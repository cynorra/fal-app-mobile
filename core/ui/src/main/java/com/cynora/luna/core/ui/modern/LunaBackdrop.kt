package com.cynora.luna.core.ui.modern

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cynora.luna.core.ui.effects.CosmicBackground
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

/**
 * Full-screen animated backdrop used behind every screen:
 * deep cosmic gradient and stars, slow aurora blobs (Silk Aurora style) and a dot grid with a
 * travelling wave (Canvas Fractal Grid style: dots of 3, spacing 20, opacity 0.4, wave radius 250).
 */
@Composable
fun LunaBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        CosmicBackground(starCount = 60, particleCount = 8, nebulaEnabled = false) {}
        AuroraLayer()
        DotGridLayer()
        content()
    }
}

@Composable
private fun AuroraLayer() {
    val transition = rememberInfiniteTransition(label = "aurora")
    val p1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(32000, easing = LinearEasing)),
        label = "aurora1"
    )
    val p2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(41000, easing = LinearEasing)),
        label = "aurora2"
    )
    val p3 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(53000, easing = LinearEasing)),
        label = "aurora3"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val reach = max(w, h)

        fun blob(cx: Float, cy: Float, radius: Float, color: Color) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color, Color.Transparent),
                    center = Offset(cx, cy),
                    radius = radius
                ),
                radius = radius,
                center = Offset(cx, cy)
            )
        }

        val a1 = (p1 * 2f * PI).toFloat()
        val a2 = (p2 * 2f * PI).toFloat()
        val a3 = (p3 * 2f * PI).toFloat()
        // violet
        blob(w * (0.22f + 0.14f * cos(a1)), h * (0.18f + 0.10f * sin(a1)), reach * 0.55f, Color(0xFF7C4DFF).copy(alpha = 0.20f))
        // gold
        blob(w * (0.82f + 0.10f * sin(a2)), h * (0.35f + 0.12f * cos(a2)), reach * 0.45f, Color(0xFFD4AF37).copy(alpha = 0.11f))
        // deep blue
        blob(w * (0.55f + 0.16f * cos(a3)), h * (0.85f + 0.08f * sin(a3)), reach * 0.60f, Color(0xFF2563EB).copy(alpha = 0.14f))
    }
}

@Composable
private fun DotGridLayer() {
    val transition = rememberInfiniteTransition(label = "dotGrid")
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "wave"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacing = 20.dp.toPx()
        val dotRadius = 1.5.dp.toPx()
        val waveRadius = 250.dp.toPx()
        val reach = hypot(size.width, size.height)
        val ringRadius = wave * (reach + waveRadius)
        val center = Offset(size.width * 0.5f, size.height * 0.35f)
        val twoSigmaSq = 2f * (waveRadius * 0.35f) * (waveRadius * 0.35f)

        var y = spacing / 2f
        while (y < size.height) {
            var x = spacing / 2f
            while (x < size.width) {
                val distance = hypot(x - center.x, y - center.y)
                val delta = distance - ringRadius
                val lift = exp(-(delta * delta) / twoSigmaSq)
                val alpha = 0.035f + 0.15f * lift
                drawCircle(color = Color.White.copy(alpha = alpha), radius = dotRadius, center = Offset(x, y))
                x += spacing
            }
            y += spacing
        }
    }
}
