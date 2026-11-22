package com.cynora.luna.core.data.repository

import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

data class IntentionPost(
    val id: String = "",
    val authorUid: String = "",
    val mysticName: String = "",
    val avatarEmoji: String = "",
    val intentionText: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val likedBy: List<String> = emptyList()
)

@Singleton
class SocialRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val dataStore: UserPreferencesDataStore
) {
    private val postsCollection = firestore.collection("posts")
    
    // Çevrimdışı/anonim giriş başarısız olursa okuma için yerel yedek kimlik
    private val userIdRef = AtomicReference<String?>(null)

    /**
     * Firestore kuralları request.auth.uid gerektirir. Oturum yoksa anonim giriş yapar
     * (Google ile giriş sonradan bu hesaba bağlanır, UID korunur).
     */
    private suspend fun ensureSignedIn(): String? {
        auth.currentUser?.uid?.let { return it }
        return try {
            auth.signInAnonymously().await().user?.uid
        } catch (e: Exception) {
            Timber.e(e, "Anonymous sign-in failed")
            null
        }
    }

    suspend fun ensureUserId() {
        val uid = ensureSignedIn() ?: dataStore.getOrCreateUserId()
        userIdRef.set(uid)
    }

    // Compose'dan çağrılabilir (suspend değil)
    fun currentUserIdSync(): String = auth.currentUser?.uid ?: userIdRef.get() ?: "anonymous"

    fun observePosts(): Flow<List<IntentionPost>> = callbackFlow {
        val registration: ListenerRegistration = postsCollection
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Firestore listen failed")
                    close(error)
                    return@addSnapshotListener
                }
                
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        @Suppress("UNCHECKED_CAST")
                        val likedBy = doc.get("likedBy") as? List<String> ?: emptyList()
                        
                        IntentionPost(
                            id = doc.id,
                            authorUid = doc.getString("authorUid") ?: "",
                            mysticName = doc.getString("mysticName") ?: "",
                            avatarEmoji = doc.getString("avatarEmoji") ?: "",
                            intentionText = doc.getString("intentionText") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            likeCount = doc.getLong("likeCount")?.toInt() ?: 0,
                            likedBy = likedBy
                        )
                    } catch (e: Exception) {
                        Timber.e(e, "Post parse failed")
                        null
                    }
                } ?: emptyList()
                
                trySend(posts)
            }
        
        awaitClose { registration.remove() }
    }
    
    suspend fun addPost(post: IntentionPost): Result<Unit> {
        return try {
            val uid = ensureSignedIn()
                ?: return Result.failure(IllegalStateException("Not signed in"))
            val postData = mapOf(
                "authorUid" to uid,
                "mysticName" to post.mysticName,
                "avatarEmoji" to post.avatarEmoji,
                "intentionText" to post.intentionText,
                "timestamp" to post.timestamp,
                "likeCount" to 0,
                "likedBy" to emptyList<String>()
            )
            postsCollection.add(postData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Add post failed")
            Result.failure(e)
        }
    }
    
    suspend fun toggleLike(postId: String): Result<Boolean> {
        val uid = ensureSignedIn()
            ?: return Result.failure(IllegalStateException("Not signed in"))
        
        return try {
            val postRef = postsCollection.document(postId)
            
            val newIsLiked = firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                @Suppress("UNCHECKED_CAST")
                val likedBy = (snapshot.get("likedBy") as? List<String>) ?: emptyList()
                val currentLikes = snapshot.getLong("likeCount") ?: 0L
                
                val isLiked = uid in likedBy
                val newLikedBy = if (isLiked) {
                    likedBy - uid
                } else {
                    likedBy + uid
                }
                val newLikeCount = if (isLiked) currentLikes - 1 else currentLikes + 1
                
                transaction.update(postRef, "likedBy", newLikedBy)
                transaction.update(postRef, "likeCount", newLikeCount)
                
                !isLiked
            }.await()
            
            Result.success(newIsLiked)
        } catch (e: Exception) {
            Timber.e(e, "Toggle like failed")
            Result.failure(e)
        }
    }

    // --- Community safety (user generated content) ---

    fun observeBlockedUserIds(): Flow<Set<String>> = dataStore.getBlockedUserIds()

    fun observeHiddenPostIds(): Flow<Set<String>> = dataStore.getHiddenPostIds()

    /** Hides one post on this device only. */
    suspend fun hidePost(postId: String) = dataStore.hidePost(postId)

    /** Hides every post of this author on this device only. */
    suspend fun blockAuthor(authorUid: String) = dataStore.blockUser(authorUid)

    /**
     * Reports a post for moderation. The post is hidden locally first, so the reporter never has to
     * see it again even if the network call fails or the post was already reported.
     */
    suspend fun reportPost(post: IntentionPost, reason: String): Result<Unit> {
        dataStore.hidePost(post.id)
        val uid = ensureSignedIn()
            ?: return Result.failure(IllegalStateException("Not signed in"))
        return try {
            val report = mapOf(
                "postId" to post.id,
                "authorUid" to post.authorUid,
                "reporterUid" to uid,
                "reason" to reason,
                "createdAt" to System.currentTimeMillis()
            )
            // One report per reporter and post: the document id enforces it (see firestore.rules).
            firestore.collection("reports").document("${uid}_${post.id}").set(report).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Report post failed")
            Result.failure(e)
        }
    }
}
