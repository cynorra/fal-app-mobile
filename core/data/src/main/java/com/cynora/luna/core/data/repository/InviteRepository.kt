package com.cynora.luna.core.data.repository

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * "Invite a friend" loop.
 *
 * - Every user gets a short invite code; the Play link carries it in the install referrer.
 * - On the friend's first launch the referrer is read and a join record is written.
 * - Both sides unlock the friend perk (tomorrow's horoscope + 3-card spread).
 */
@Singleton
class InviteRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val dataStore: UserPreferencesDataStore
) {
    private val invites get() = firestore.collection("invites")

    private suspend fun uid(): String? {
        auth.currentUser?.uid?.let { return it }
        return try {
            auth.signInAnonymously().await().user?.uid
        } catch (e: Exception) {
            Timber.w(e, "Anonymous sign-in failed (invite)")
            null
        }
    }

    /** Returns this user's invite code, creating and registering it on first use. */
    suspend fun getOrCreateInviteCode(): String? {
        dataStore.getInviteCode().first()?.let { return it }
        val me = uid() ?: return null
        repeat(5) {
            val code = randomCode()
            try {
                val ref = invites.document(code)
                if (ref.get().await().exists()) return@repeat
                ref.set(
                    mapOf(
                        "ownerUid" to me,
                        "createdAt" to System.currentTimeMillis()
                    )
                ).await()
                dataStore.setInviteCode(code)
                return code
            } catch (e: Exception) {
                Timber.w(e, "Creating invite code failed")
                return null
            }
        }
        return null
    }

    /** Refreshes how many friends joined through this user's code; grants the perk on the first one. */
    suspend fun refreshJoinedCount(): Int {
        val code = dataStore.getInviteCode().first() ?: return dataStore.getInviteJoined().first()
        return try {
            val count = invites.document(code).collection("joins")
                .count().get(AggregateSource.SERVER).await().count.toInt()
            dataStore.setInviteJoined(count)
            if (count > 0) dataStore.setInvitePerk()
            count
        } catch (e: Exception) {
            Timber.w(e, "Reading invite count failed")
            dataStore.getInviteJoined().first()
        }
    }

    /** First launch after install: read the Play referrer and register the join, once. */
    suspend fun claimInviteIfAny() {
        if (dataStore.isInviteClaimDone()) return
        val referrer = readInstallReferrer() ?: return  // null = service unavailable, try next launch
        val code = parseInviteCode(referrer)
        if (code == null) {
            dataStore.setInviteClaimDone()
            return
        }
        val me = uid() ?: return
        try {
            val invite = invites.document(code).get().await()
            if (invite.exists() && invite.getString("ownerUid") != me) {
                invites.document(code).collection("joins").document(me)
                    .set(mapOf("joinedAt" to System.currentTimeMillis())).await()
                dataStore.setInvitePerk()
            }
            dataStore.setInviteClaimDone()
        } catch (e: Exception) {
            Timber.w(e, "Claiming invite failed")
        }
    }

    /** Referrer string, "" when none, or null when Play's service could not be reached right now. */
    private suspend fun readInstallReferrer(): String? = suspendCancellableCoroutine { cont ->
        val client = InstallReferrerClient.newBuilder(context).build()
        client.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                val result: String? = try {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> client.installReferrer.installReferrer ?: ""
                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> ""
                        else -> null
                    }
                } catch (e: Exception) {
                    null
                } finally {
                    try { client.endConnection() } catch (_: Exception) {}
                }
                if (cont.isActive) cont.resume(result)
            }

            override fun onInstallReferrerServiceDisconnected() {
                if (cont.isActive) cont.resume(null)
            }
        })
        cont.invokeOnCancellation { try { client.endConnection() } catch (_: Exception) {} }
    }

    companion object {
        private const val ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

        fun randomCode(): String {
            val rnd = java.security.SecureRandom()
            return (1..6).map { ALPHABET[rnd.nextInt(ALPHABET.length)] }.joinToString("")
        }

        /** Referrer looks like "utm_source=luna_invite&utm_content=ABC234". */
        fun parseInviteCode(referrer: String): String? {
            val params = referrer.split("&").mapNotNull {
                val i = it.indexOf('=')
                if (i > 0) it.substring(0, i) to java.net.URLDecoder.decode(it.substring(i + 1), "UTF-8") else null
            }.toMap()
            if (params["utm_source"] != "luna_invite") return null
            val code = params["utm_content"]?.uppercase() ?: return null
            return code.takeIf { c -> c.length == 6 && c.all { it in ALPHABET } }
        }
    }
}
