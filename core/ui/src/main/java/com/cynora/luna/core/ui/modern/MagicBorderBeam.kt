package com.cynora.luna.core.ui.modern

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cynora.luna.core.ui.theme.LunaColors

/**
 * Magic UI - Border Beam Effect
 * Traces an animated radiant beam along the perimeter of any card or container.
 */
fun Modifier.borderBeam(
    durationMillis: Int = 4000,
    borderWidth: Dp = 1.5.dp,
    cornerRadius: Dp = 20.dp,
    beamColor: Color = LunaColors.Gold,
    trailColor: Color = LunaColors.MysticPurple,
    baseBorderColor: Color = Color.White.copy(alpha = 0.08f)
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "borderBeam")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing)
        ),
        label = "beamAngle"
    )

    this
        .border(0.5.dp, baseBorderColor, RoundedCornerShape(cornerRadius))
        .drawWithContent {
            drawContent()

            val center = Offset(size.width / 2f, size.height / 2f)
            val strokePx = borderWidth.toPx()
            val radiusPx = cornerRadius.toPx()

            val sweepBrush = Brush.sweepGradient(
                colors = listOf(
                    Color.Transparent,
                    trailColor.copy(alpha = 0.3f),
                    beamColor.copy(alpha = 0.85f),
                    Color.White.copy(alpha = 0.95f),
                    beamColor.copy(alpha = 0.85f),
                    Color.Transparent,
                    Color.Transparent
                ),
                center = center
            )

            // Draw animated traveling border beam
            withTransform({
                rotate(degrees = angle, pivot = center)
            }) {
                drawRoundRect(
                    brush = sweepBrush,
                    topLeft = Offset(strokePx / 2f, strokePx / 2f),
                    size = androidx.compose.ui.geometry.Size(
                        size.width - strokePx,
                        size.height - strokePx
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(radiusPx, radiusPx),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }
}
