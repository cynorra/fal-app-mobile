package com.cynora.luna.core.ui.effects

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cynora.luna.core.ui.theme.LunaColors
import kotlin.random.Random

@Composable
fun SuccessConfetti(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    duration: Int = 2500
) {
    if (!isActive) return
    
    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                startX = 0.5f,
                startY = 0.5f,
                velocityX = (Random.nextFloat() - 0.5f) * 2f,
                velocityY = Random.nextFloat() * -2f - 1f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                size = Random.nextFloat() * 8f + 4f,
                color = listOf(
                    LunaColors.Gold,
                    LunaColors.GoldLight,
                    LunaColors.MysticPurple,
                    Color.White
                ).random(),
                shape = Random.nextInt(3)
            )
        }
    }
    
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(duration, easing = FastOutLinearInEasing),
        label = "confetti"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (progress > 0.7f) (1f - (progress - 0.7f) / 0.3f) else 1f,
        label = "alpha"
    )
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        particles.forEach { particle ->
            val t = progress
            val gravity = 2f
            
            val x = particle.startX * size.width + 
                    particle.velocityX * t * size.width * 0.3f
            val y = particle.startY * size.height + 
                    particle.velocityY * t * size.height * 0.3f + 
                    gravity * t * t * size.height * 0.2f
            
            when (particle.shape) {
                0 -> drawCircle(
                    color = particle.color.copy(alpha = alpha),
                    radius = particle.size,
                    center = Offset(x, y)
                )
                1 -> drawRect(
                    color = particle.color.copy(alpha = alpha),
                    topLeft = Offset(x - particle.size, y - particle.size),
                    size = Size(particle.size * 2, particle.size * 2)
                )
                else -> drawCircle(
                    color = particle.color.copy(alpha = alpha * 0.7f),
                    radius = particle.size * 1.5f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

private data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val size: Float,
    val color: Color,
    val shape: Int
)
