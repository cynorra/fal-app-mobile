package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.LunaUser
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val dataStore: UserPreferencesDataStore,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: Flow<LunaUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser?.takeIf { !it.isAnonymous }?.toLunaUser()
            trySend(user)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun getCurrentUser(): LunaUser? {
        return auth.currentUser?.takeIf { !it.isAnonymous }?.toLunaUser()
    }

    override suspend fun signInWithGoogle(idToken: String): Result<LunaUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val current = auth.currentUser
            // Anonim hesap varsa Google'a bağla: UID (ve bu UID'ye ait beğeni/gönderiler) korunur
            val authResult = if (current != null && current.isAnonymous) {
                try {
                    current.linkWithCredential(credential).await()
                } catch (e: FirebaseAuthUserCollisionException) {
                    // Bu Google hesabı daha önce kullanılmış: mevcut hesabına geç
                    auth.signInWithCredential(credential).await()
                }
            } else {
                auth.signInWithCredential(credential).await()
            }
            val user = authResult.user?.toLunaUser()
                ?: return Result.failure(Exception("Google Sign-In returned null user"))

            // DataStore'daki kullanıcı ID'sini de Firebase UID ile güncelle (senkronizasyon için)
            dataStore.saveUserId(user.uid)

            Timber.d("✅ Google Sign-In successful: ${user.email} (${user.uid})")
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "❌ Google Sign-In failed")
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
            Timber.d("User signed out from Firebase Auth")
        } catch (e: Exception) {
            Timber.e(e, "Sign out error")
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                // Posts first, while the auth token is still valid for the Firestore rules.
                deleteOwnPosts(user.uid)
                user.delete().await()
            }
            dataStore.clearAll()
            Timber.d("Account and community posts deleted")
            Result.success(Unit)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Timber.w(e, "Delete account needs a recent sign-in")
            Result.failure(IllegalStateException(ERROR_RECENT_LOGIN_REQUIRED))
        } catch (e: Exception) {
            Timber.e(e, "Delete account failed")
            Result.failure(e)
        }
    }

    private suspend fun deleteOwnPosts(uid: String) {
        val snapshot = firestore.collection("posts").whereEqualTo("authorUid", uid).get().await()
        snapshot.documents.chunked(400).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
    }

    private fun FirebaseUser.toLunaUser(): LunaUser {
        return LunaUser(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl?.toString()
        )
    }
}

