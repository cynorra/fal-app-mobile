package com.cynora.luna.core.ui.effects

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun LiquidSwipeTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 48.dp,
    content: @Composable () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "liquid"
    )
    
    Box(
        modifier = modifier
            .clip(LiquidShape(progress, cornerRadius))
            .drawWithContent {
                drawContent()
            }
    ) {
        content()
    }
}

private class LiquidShape(
    private val progress: Float,
    private val cornerRadius: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val waveAmplitude = size.height * 0.1f * (1f - progress)
        val controlX = size.width * progress
        
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(controlX, 0f)
            cubicTo(
                controlX + waveAmplitude, size.height * 0.25f,
                controlX - waveAmplitude, size.height * 0.75f,
                controlX, size.height
            )
            lineTo(0f, size.height)
            close()
        }
        
        return Outline.Generic(path)
    }
}
