package com.cynora.luna.feature.social

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.repository.IntentionPost
import com.cynora.luna.core.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Immutable
data class SocialState(
    val posts: List<IntentionPost> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface SocialIntent {
    data class PostIntention(val text: String) : SocialIntent
    data class LikePost(val postId: String, val currentLiked: Boolean) : SocialIntent
}

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val repository: SocialRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SocialState())
    val state: StateFlow<SocialState> = _state.asStateFlow()

    init {
        // ✅ ViewModel başlangıcında userId initialize et
        viewModelScope.launch {
            repository.ensureUserId()
            observePosts()
        }
    }

    private fun observePosts() {
        viewModelScope.launch {
            repository.observePosts().collect { posts ->
                _state.update { it.copy(posts = posts, isLoading = false, error = null) }
            }
        }
    }

    fun onIntent(intent: SocialIntent) {
        when (intent) {
            is SocialIntent.PostIntention -> postIntention(intent.text)
            is SocialIntent.LikePost -> likePost(intent.postId, intent.currentLiked)
        }
    }

    // ✅ SYNC metod - Compose'dan çağrılabilir
    fun getCurrentUserIdSync(): String = repository.currentUserIdSync()

    private fun postIntention(text: String) {
        viewModelScope.launch {
            try {
                val mysticNames = listOf(
                    "Stardust", "Moonlight", "Sunflower", "Crystal Heart",
                    "Blue Angel", "Golden Butterfly", "Silver Wolf", "Purple Lotus"
                )
                val avatarEmojis = listOf("🌟", "🌙", "☀️", "💎", "👼", "🦋", "🐺", "🪷")
                val nameIndex = (0 until mysticNames.size).random()
                
                val newPost = IntentionPost(
                    mysticName = mysticNames[nameIndex],
                    avatarEmoji = avatarEmojis[nameIndex],
                    intentionText = text,
                    timestamp = System.currentTimeMillis()
                )
                repository.addPost(newPost)
            } catch (e: Exception) {
                Timber.e(e, "Post intention failed")
                _state.update { it.copy(error = "Post failed") }
            }
        }
    }

    private fun likePost(postId: String, currentLiked: Boolean) {
        viewModelScope.launch {
            try {
                val userId = repository.currentUserIdSync()
                
                // Optimistic update
                _state.update { currentState ->
                    currentState.copy(
                        posts = currentState.posts.map { post ->
                            if (post.id == postId) {
                                post.copy(
                                    likeCount = if (currentLiked) post.likeCount - 1 else post.likeCount + 1,
                                    likedBy = if (currentLiked) {
                                        post.likedBy - userId
                                    } else {
                                        post.likedBy + userId
                                    }
                                )
                            } else post
                        }
                    )
                }
                repository.toggleLike(postId)
            } catch (e: Exception) {
                Timber.e(e, "Like post failed")
                _state.update { it.copy(error = "Like failed") }
            }
        }
    }
}
