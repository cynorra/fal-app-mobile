package com.cynora.luna.feature.social

import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.data.repository.IntentionPost
import com.cynora.luna.core.ui.theme.LunaColors

// Pair of (label resource, English reason code sent to moderation).
private val ReportReasons = listOf(
    R.string.ui_social_reason_spam to "Spam or advertising",
    R.string.ui_social_reason_harass to "Harassment or hate",
    R.string.ui_social_reason_sexual to "Sexual or violent content",
    R.string.ui_social_reason_personal to "Personal information",
    R.string.ui_social_reason_other to "Something else"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    viewModel: SocialViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var intentionText by remember { mutableStateOf("") }
    var reportTarget by remember { mutableStateOf<IntentionPost?>(null) }
    val currentUserId = viewModel.getCurrentUserIdSync()

    reportTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { reportTarget = null },
            title = { Text(stringResource(R.string.ui_social_report_title)) },
            text = {
                Column {
                    Text(
                        stringResource(R.string.ui_social_report_why),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ReportReasons.forEach { (reasonLabel, reason) ->
                        TextButton(
                            onClick = {
                                viewModel.onIntent(SocialIntent.ReportPost(target, reason))
                                reportTarget = null
                            }
                        ) {
                            Text(stringResource(reasonLabel), color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { reportTarget = null }) {
                    Text(stringResource(R.string.ui_social_cancel), color = LunaColors.Gold)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            
    ) {
        Text(
            text = stringResource(R.string.ui_social_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        state.notice?.let { notice ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = LunaColors.Gold.copy(alpha = 0.18f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(start = 12.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notice,
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.onIntent(SocialIntent.DismissNotice) }) {
                        Text("OK", color = LunaColors.Gold)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = intentionText,
                    onValueChange = { if (it.length <= MAX_POST_LENGTH) intentionText = it },
                    placeholder = { Text(stringResource(R.string.ui_social_hint), color = Color.White.copy(alpha = 0.4f)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = LunaColors.Gold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.ui_social_rules),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.45f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${intentionText.length}/$MAX_POST_LENGTH",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Button(
                        onClick = {
                            if (intentionText.isNotBlank()) {
                                viewModel.onIntent(SocialIntent.PostIntention(intentionText))
                                intentionText = ""
                            }
                        },
                        enabled = intentionText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = LunaColors.Gold)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(R.string.ui_social_send), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.ui_social_share))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LunaColors.Gold)
            }
        } else if (state.posts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(R.string.ui_social_empty),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.posts, key = { it.id }) { post ->
                    IntentionPostCard(
                        post = post,
                        currentUserId = currentUserId,
                        onLike = { liked ->
                            viewModel.onIntent(SocialIntent.LikePost(post.id, liked))
                        },
                        onReport = { reportTarget = post },
                        onHide = { viewModel.onIntent(SocialIntent.HidePost(post)) },
                        onBlock = { viewModel.onIntent(SocialIntent.BlockAuthor(post)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IntentionPostCard(
    post: IntentionPost,
    currentUserId: String,
    onLike: (Boolean) -> Unit,
    onReport: () -> Unit,
    onHide: () -> Unit,
    onBlock: () -> Unit
) {
    val isLiked = post.likedBy.contains(currentUserId)
    val isOwnPost = post.authorUid.isNotEmpty() && post.authorUid == currentUserId
    var menuOpen by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LunaColors.Gold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = post.avatarEmoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.mysticName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LunaColors.Gold
                    )
                    Text(
                        text = formatTimeAgo(post.timestamp),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }

                if (!isOwnPost) {
                    Box {
                        IconButton(onClick = { menuOpen = true }) {
                            Text(text = "⋮", fontSize = 20.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.ui_social_report_post)) },
                                onClick = {
                                    menuOpen = false
                                    onReport()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.ui_social_hide_post)) },
                                onClick = {
                                    menuOpen = false
                                    onHide()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.ui_social_block_user)) },
                                onClick = {
                                    menuOpen = false
                                    onBlock()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "\"${post.intentionText}\"",
                fontSize = 16.sp,
                color = Color.White,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { onLike(isLiked) }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.ui_social_like),
                        tint = if (isLiked) Color(0xFFE91E63) else Color.White.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = "${post.likeCount}",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / 60000
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> stringResource(R.string.ui_social_just_now)
        minutes < 60 -> stringResource(R.string.ui_social_min_ago, minutes.toInt())
        hours < 24 -> stringResource(R.string.ui_social_hours_ago, hours.toInt())
        else -> stringResource(R.string.ui_social_days_ago, days.toInt())
    }
}
