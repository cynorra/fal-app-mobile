package com.cynora.luna.core.ui.modern

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cynora.luna.core.ui.theme.LunaColors

/**
 * Frosted "glass" surface: translucent white wash, light sheen and a gradient hairline border.
 * When [selected] the border breathes in the [accent] colour (Halo / Border Beam card feel).
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    accent: Color = LunaColors.Gold,
    selected: Boolean = false,
    cornerRadius: Dp = 20.dp,
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val borderAlpha = rememberGlassPulse(selected)
    val fillTop = if (selected) 0.13f else 0.075f

    val surface = modifier
        .clip(shape)
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = fillTop),
                    Color.White.copy(alpha = 0.025f)
                )
            ),
            shape = shape
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.22f),
                    accent.copy(alpha = borderAlpha),
                    Color.White.copy(alpha = 0.05f)
                )
            ),
            shape = shape
        )

    val clickable = if (onClick != null) surface.clickable(onClick = onClick) else surface
    Column(modifier = clickable.padding(contentPadding), content = content)
}

@Composable
private fun rememberGlassPulse(selected: Boolean): Float {
    if (!selected) return 0.30f
    val transition = rememberInfiniteTransition(label = "glassPulse")
    val value by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    return value
}
