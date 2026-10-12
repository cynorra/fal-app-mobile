package com.cynora.luna.core.data.repository

import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
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
    private val dataStore: UserPreferencesDataStore
) {
    private val postsCollection = firestore.collection("posts")
    
    // ✅ Thread-safe atomic reference
    private val userIdRef = AtomicReference<String?>(null)
    
    suspend fun ensureUserId() {
        if (userIdRef.get() == null) {
            userIdRef.set(dataStore.getOrCreateUserId())
        }
    }
    
    // ✅ Compose'dan çağrılabilir (suspend değil)
    fun currentUserIdSync(): String = userIdRef.get() ?: "anonymous"
    
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
            val postData = mapOf(
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
        ensureUserId()
        val uid = userIdRef.get() ?: return Result.failure(IllegalStateException("User ID not initialized"))
        
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
}
