package com.cynora.luna.core.ui.effects

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cynora.luna.core.ui.theme.LunaColors
import kotlinx.coroutines.delay

@Composable
fun InAppNotification(
    isVisible: Boolean,
    title: String,
    message: String,
    icon: String = "✨",
    onDismiss: () -> Unit,
    onClick: (() -> Unit)? = null,
    duration: Long = 4000L
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(duration)
            onDismiss()
        }
    }
    
    androidx.compose.animation.AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(300))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = LunaColors.Gold.copy(alpha = 0.3f),
                    spotColor = LunaColors.Gold.copy(alpha = 0.5f)
                )
                .clickable { 
                    onClick?.invoke()
                    onDismiss()
                },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                LunaColors.SurfaceDark.copy(alpha = 0.95f),
                                LunaColors.SurfaceVariant.copy(alpha = 0.95f)
                            )
                        )
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    LunaColors.Gold.copy(alpha = 0.3f),
                                    LunaColors.MysticPurple.copy(alpha = 0.3f),
                                    LunaColors.Gold.copy(alpha = 0.3f)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Text(
                        text = icon,
                        fontSize = 32.sp,
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        androidx.compose.material3.Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaColors.Gold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        androidx.compose.material3.Text(
                            text = message,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun rememberInAppNotification(): InAppNotificationManager {
    return androidx.compose.runtime.remember { InAppNotificationManager() }
}

class InAppNotificationManager {
    private val _notifications = mutableStateListOf<NotificationData>()
    val notifications: List<NotificationData> = _notifications
    
    fun show(
        title: String,
        message: String,
        icon: String = "✨",
        duration: Long = 4000L,
        onClick: (() -> Unit)? = null
    ) {
        val notification = NotificationData(
            id = System.currentTimeMillis(),
            title = title,
            message = message,
            icon = icon,
            duration = duration,
            onClick = onClick
        )
        _notifications.add(notification)
    }
    
    fun dismiss(id: Long) {
        _notifications.removeAll { it.id == id }
    }
}

data class NotificationData(
    val id: Long,
    val title: String,
    val message: String,
    val icon: String,
    val duration: Long,
    val onClick: (() -> Unit)?
)
