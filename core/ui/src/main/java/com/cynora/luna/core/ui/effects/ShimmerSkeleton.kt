package com.cynora.luna.core.ui.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cynora.luna.core.ui.theme.LunaColors

@Composable
fun ShimmerSkeleton(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "translate"
    )
    
    val shimmerColors = listOf(
        LunaColors.SurfaceDark,
        LunaColors.SurfaceVariant,
        LunaColors.Gold.copy(alpha = 0.15f),
        LunaColors.SurfaceVariant,
        LunaColors.SurfaceDark
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim)
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

@Composable
fun SkeletonTextLine(
    modifier: Modifier = Modifier,
    widthPercent: Float = 1f
) {
    ShimmerSkeleton(
        modifier = modifier
            .fillMaxWidth(widthPercent)
            .height(14.dp),
        cornerRadius = 4.dp
    )
}

@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerSkeleton(
                modifier = Modifier.size(48.dp),
                cornerRadius = 24.dp
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonTextLine(widthPercent = 0.5f)
                SkeletonTextLine(widthPercent = 0.3f)
            }
        }
        SkeletonTextLine(widthPercent = 1f)
        SkeletonTextLine(widthPercent = 0.8f)
        SkeletonTextLine(widthPercent = 0.6f)
    }
}

@Composable
fun SkeletonFortuneCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShimmerSkeleton(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally),
            cornerRadius = 40.dp
        )
        SkeletonTextLine(widthPercent = 0.7f)
        SkeletonTextLine(widthPercent = 1f)
        SkeletonTextLine(widthPercent = 0.9f)
        SkeletonTextLine(widthPercent = 0.5f)
    }
}
