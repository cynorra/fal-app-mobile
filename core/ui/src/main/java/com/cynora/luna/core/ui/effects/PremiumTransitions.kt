package com.cynora.luna.core.ui.effects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun PremiumFadeScaleSlide(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) +
                slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ),
        exit = fadeOut(animationSpec = tween(300)) +
               scaleOut(targetScale = 0.92f) +
               slideOutVertically(targetOffsetY = { it / 4 }),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun Card3DFlip(
    flipped: Boolean,
    modifier: Modifier = Modifier,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "flip3d"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        if (rotation <= 90f) {
            front()
        } else {
            Box(
                modifier = Modifier.graphicsLayer {
                    rotationY = 180f
                }
            ) {
                back()
            }
        }
    }
}

@Composable
fun ParallaxScroll(
    scrollOffset: Float,
    modifier: Modifier = Modifier,
    parallaxFactor: Float = 0.5f,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.graphicsLayer {
            translationY = scrollOffset * parallaxFactor
        }
    ) {
        content()
    }
}

@Composable
fun <T> StaggeredList(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T, Int) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val delay = index * 80
            val itemVisible by animateFloatAsState(
                targetValue = if (visible) 1f else 0f,
                animationSpec = tween(500, delayMillis = delay, easing = FastOutSlowInEasing),
                label = "stagger"
            )
            
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = itemVisible
                        translationY = (1f - itemVisible) * 30f
                        scaleX = 0.95f + itemVisible * 0.05f
                        scaleY = 0.95f + itemVisible * 0.05f
                    }
            ) {
                itemContent(item, index)
            }
        }
    }
}
