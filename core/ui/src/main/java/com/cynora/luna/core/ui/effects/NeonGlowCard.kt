package com.cynora.luna.core.ui.effects

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cynora.luna.core.ui.theme.LunaColors

@Composable
fun NeonGlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = LunaColors.Gold,
    cornerRadius: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon")
    
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    Box(
        modifier = modifier
            .drawBehind {
                // ✅ Size + operator yerine manuel hesaplama
                val expandedSize = Size(size.width + 40f, size.height + 40f)
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.3f * glowIntensity),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.maxDimension * 0.7f
                    ),
                    cornerRadius = CornerRadius(cornerRadius.toPx() + 20f),
                    topLeft = Offset(-20f, -20f),
                    size = expandedSize
                )
            }
    ) {
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(cornerRadius))
                .border(
                    width = 1.5.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            glowColor.copy(alpha = glowIntensity),
                            glowColor.copy(alpha = 0.3f),
                            glowColor.copy(alpha = glowIntensity),
                            glowColor.copy(alpha = 0.3f),
                            glowColor.copy(alpha = glowIntensity)
                        ),
                        center = Offset(100f, 100f)
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                ),
            colors = CardDefaults.cardColors(
                containerColor = LunaColors.SurfaceDark.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(cornerRadius)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.02f)
                            )
                        )
                    )
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    content()
                }
            }
        }
    }
}
