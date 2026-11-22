package com.cynora.luna.core.ui.modern

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cynora.luna.core.ui.theme.LunaColors

/**
 * Primary call to action: gold gradient pill with a soft glow and a light sweep that travels across
 * the surface (Cult UI "Cosmic Button" / "Border Beam Button" idea). The sweep is skipped when disabled.
 */
@Composable
fun LunaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(28.dp)
    val transition = rememberInfiniteTransition(label = "buttonSweep")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "sweep"
    )

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f)
            .drawBehind {
                // soft golden glow: concentric strokes that fade outwards
                for (i in 1..6) {
                    val grow = i * 3.5f
                    drawRoundRect(
                        color = LunaColors.Gold.copy(alpha = 0.075f * (7 - i) / 6f),
                        topLeft = Offset(-grow, -grow),
                        size = androidx.compose.ui.geometry.Size(size.width + grow * 2f, size.height + grow * 2f),
                        cornerRadius = CornerRadius(size.height / 2f + grow),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f)
                    )
                }
            }
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFF3D77E), LunaColors.Gold, Color(0xFFB8860B))
                )
            )
            .drawWithContent {
                drawContent()
                if (enabled) {
                    val x = size.width * (sweep * 1.8f - 0.4f)
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.38f), Color.Transparent),
                            startX = x,
                            endX = x + size.width * 0.35f
                        )
                    )
                }
            }
            .clickable(enabled = enabled, onClick = onClick)
            .defaultMinSize(minHeight = 56.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
