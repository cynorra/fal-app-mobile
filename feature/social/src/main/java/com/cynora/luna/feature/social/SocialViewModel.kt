package com.cynora.luna.feature.social

import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.repository.IntentionPost
import com.cynora.luna.core.data.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

const val MAX_POST_LENGTH = 280

@Immutable
data class SocialState(
    val posts: List<IntentionPost> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val notice: String? = null
)

sealed interface SocialIntent {
    data class PostIntention(val text: String) : SocialIntent
    data class LikePost(val postId: String, val currentLiked: Boolean) : SocialIntent
    data class ReportPost(val post: IntentionPost, val reason: String) : SocialIntent
    data class HidePost(val post: IntentionPost) : SocialIntent
    data class BlockAuthor(val post: IntentionPost) : SocialIntent
    data object DismissNotice : SocialIntent
}

@HiltViewModel
class SocialViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SocialRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SocialState())
    val state: StateFlow<SocialState> = _state.asStateFlow()

    init {
        // Make sure we have a signed-in (anonymous) user before listening, then observe the feed.
        viewModelScope.launch {
            repository.ensureUserId()
            observePosts()
        }
    }

    private fun observePosts() {
        viewModelScope.launch {
            combine(
                repository.observePosts(),
                repository.observeBlockedUserIds(),
                repository.observeHiddenPostIds()
            ) { posts, blockedAuthors, hiddenPosts ->
                posts.filter { post ->
                    post.id !in hiddenPosts &&
                        (post.authorUid.isEmpty() || post.authorUid !in blockedAuthors)
                }
            }.catch { error ->
                // A failed Firestore listener must not crash the app: show the empty/error state.
                Timber.e(error, "Observing posts failed")
                _state.update { it.copy(isLoading = false, error = context.getString(R.string.ui_social_err_load)) }
            }.collect { visiblePosts ->
                _state.update { it.copy(posts = visiblePosts, isLoading = false, error = null) }
            }
        }
    }

    fun onIntent(intent: SocialIntent) {
        when (intent) {
            is SocialIntent.PostIntention -> postIntention(intent.text)
            is SocialIntent.LikePost -> likePost(intent.postId, intent.currentLiked)
            is SocialIntent.ReportPost -> reportPost(intent.post, intent.reason)
            is SocialIntent.HidePost -> hidePost(intent.post)
            is SocialIntent.BlockAuthor -> blockAuthor(intent.post)
            is SocialIntent.DismissNotice -> _state.update { it.copy(notice = null) }
        }
    }

    // Sync so it can be called from Compose
    fun getCurrentUserIdSync(): String = repository.currentUserIdSync()

    private fun postIntention(text: String) {
        val clean = text.trim().take(MAX_POST_LENGTH)
        if (clean.isEmpty()) return
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
                    intentionText = clean,
                    timestamp = System.currentTimeMillis()
                )
                repository.addPost(newPost).onFailure {
                    _state.update { s -> s.copy(error = context.getString(R.string.ui_social_err_post)) }
                }
            } catch (e: Exception) {
                Timber.e(e, "Post intention failed")
                _state.update { it.copy(error = context.getString(R.string.ui_social_err_post)) }
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
                _state.update { it.copy(error = context.getString(R.string.ui_social_err_like)) }
            }
        }
    }

    private fun reportPost(post: IntentionPost, reason: String) {
        viewModelScope.launch {
            val result = repository.reportPost(post, reason)
            val message = if (result.isSuccess) {
                context.getString(R.string.ui_social_report_thanks)
            } else {
                context.getString(R.string.ui_social_report_failed)
            }
            _state.update { it.copy(notice = message) }
        }
    }

    private fun hidePost(post: IntentionPost) {
        viewModelScope.launch {
            repository.hidePost(post.id)
            _state.update { it.copy(notice = context.getString(R.string.ui_social_post_hidden)) }
        }
    }

    private fun blockAuthor(post: IntentionPost) {
        viewModelScope.launch {
            if (post.authorUid.isBlank()) {
                repository.hidePost(post.id)
                _state.update { it.copy(notice = context.getString(R.string.ui_social_post_hidden)) }
            } else {
                repository.blockAuthor(post.authorUid)
                _state.update { it.copy(notice = context.getString(R.string.ui_social_user_blocked)) }
            }
        }
    }
}
