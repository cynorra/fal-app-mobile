package com.cynora.luna.core.ui.modern

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private class Balloon(
    val x: Float,
    val sizeDp: Float,
    val speed: Float,
    val delay: Float,
    val wobble: Float,
    val color: Color
)

private val BalloonPalette = listOf(
    Color(0xFFF3D77E), // gold
    Color(0xFF9D76C1), // mystic purple
    Color(0xFFEC6FA5), // pink
    Color(0xFF5B8DEF), // blue
    Color(0xFF4DD0B4)  // teal
)

/**
 * Celebration burst in the spirit of balloons-js: balloons rise from the bottom, sway and fade out.
 * Plays once whenever [trigger] changes to a non-null value. Draws on top and ignores touches.
 */
@Composable
fun BalloonsBurst(
    trigger: Any?,
    modifier: Modifier = Modifier,
    count: Int = 14
) {
    val progress = remember(trigger) { Animatable(0f) }
    val balloons = remember(trigger) {
        List(count) { index ->
            Balloon(
                x = 0.08f + Random.nextFloat() * 0.84f,
                sizeDp = 26f + Random.nextFloat() * 26f,
                speed = 0.75f + Random.nextFloat() * 0.5f,
                delay = Random.nextFloat() * 0.3f,
                wobble = Random.nextFloat() * (2f * PI).toFloat(),
                color = BalloonPalette[index % BalloonPalette.size]
            )
        }
    }

    LaunchedEffect(trigger) {
        if (trigger != null) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(durationMillis = 5200, easing = LinearEasing))
        }
    }

    val p = progress.value
    if (trigger == null || p <= 0f || p >= 1f) return

    Canvas(modifier = modifier.fillMaxSize()) {
        balloons.forEach { balloon ->
            val local = ((p - balloon.delay) / (1f - balloon.delay)).coerceIn(0f, 1f)
            if (local <= 0f) return@forEach

            val bodyH = balloon.sizeDp.dp.toPx()
            val bodyW = bodyH * 0.8f
            val travel = (size.height + bodyH * 3f) * local * balloon.speed
            val centerY = size.height + bodyH - travel
            val centerX = balloon.x * size.width + sin(local * 6f + balloon.wobble) * 14.dp.toPx()
            val alpha = if (local > 0.8f) (1f - local) / 0.2f else 1f

            // string
            drawLine(
                color = Color.White.copy(alpha = 0.35f * alpha),
                start = Offset(centerX, centerY + bodyH / 2f),
                end = Offset(centerX + sin(local * 6f + balloon.wobble + 1f) * 6.dp.toPx(), centerY + bodyH / 2f + bodyH * 0.9f),
                strokeWidth = 1.dp.toPx()
            )
            // knot
            val knot = Path().apply {
                moveTo(centerX, centerY + bodyH / 2f - 1f)
                lineTo(centerX - bodyW * 0.08f, centerY + bodyH / 2f + bodyH * 0.1f)
                lineTo(centerX + bodyW * 0.08f, centerY + bodyH / 2f + bodyH * 0.1f)
                close()
            }
            drawPath(knot, balloon.color.copy(alpha = alpha))
            // body with a soft highlight
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.85f * alpha), balloon.color.copy(alpha = alpha)),
                    center = Offset(centerX - bodyW * 0.18f, centerY - bodyH * 0.18f),
                    radius = bodyH * 0.7f
                ),
                topLeft = Offset(centerX - bodyW / 2f, centerY - bodyH / 2f),
                size = Size(bodyW, bodyH)
            )
        }
    }
}
