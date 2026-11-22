package com.cynora.luna.core.ui.modern

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.cynora.luna.core.ui.theme.LunaColors
import kotlin.math.sqrt

/**
 * Dot-matrix loader in the spirit of Dot Matrix "Lunar Breathe": a square grid of dots whose
 * opacity breathes outwards from the centre. Opacity stops follow the library defaults
 * (base 0.12, mid 0.42, peak 1.0) and speed 1.1.
 */
@Composable
fun DotMatrixLoader(
    modifier: Modifier = Modifier,
    loaderSize: Dp = 88.dp,
    grid: Int = 5,
    speed: Float = 1.1f,
    color: Color = LunaColors.Gold,
    opacityBase: Float = 0.12f,
    opacityMid: Float = 0.42f,
    opacityPeak: Float = 1f
) {
    val transition = rememberInfiniteTransition(label = "dotMatrix")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween((2400 / speed).toInt(), easing = LinearEasing)),
        label = "phase"
    )

    Canvas(modifier = modifier.size(loaderSize)) {
        val cell = this.size.minDimension / grid
        val radius = cell * 0.2f
        val center = (grid - 1) / 2f
        val maxDist = center * 1.4142f

        for (row in 0 until grid) {
            for (col in 0 until grid) {
                val dr = row - center
                val dc = col - center
                val dist = if (maxDist == 0f) 0f else sqrt(dr * dr + dc * dc) / maxDist
                // a ring that travels from the centre to the corners and repeats
                val raw = phase * 1.4f - dist * 0.9f
                val wave = ((raw % 1f) + 1f) % 1f
                val tri = if (wave < 0.5f) wave * 2f else (1f - wave) * 2f
                val alpha = if (tri < 0.5f) {
                    lerp(opacityBase, opacityMid, tri * 2f)
                } else {
                    lerp(opacityMid, opacityPeak, (tri - 0.5f) * 2f)
                }
                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(cell * (col + 0.5f), cell * (row + 0.5f))
                )
            }
        }
    }
}
