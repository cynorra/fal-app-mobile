package com.cynora.luna.core.ui.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.cynora.luna.core.ui.theme.LunaColors
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier,
    starCount: Int = 100,
    particleCount: Int = 20,
    nebulaEnabled: Boolean = true,
    shootingStarInterval: Long = 8000L,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LunaColors.DeepBlack,
                        LunaColors.DeepBlack.copy(alpha = 0.95f),
                        Color(0xFF050510)
                    ),
                    center = Offset(size.width * 0.3f, size.height * 0.3f),
                    radius = size.maxDimension
                )
            )
        }
        
        if (nebulaEnabled) {
            NebulaLayer()
        }
        
        StarField(starCount = starCount)
        ShootingStars(interval = shootingStarInterval)
        FloatingParticles(count = particleCount)
        
        content()
    }
}

@Composable
private fun NebulaLayer() {
    val infiniteTransition = rememberInfiniteTransition(label = "nebula")
    
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "nebulaX"
    )
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "nebulaY"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    LunaColors.MysticPurple.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(
                    size.width * (0.3f + offsetX * 0.2f),
                    size.height * (0.4f + offsetY * 0.1f)
                ),
                radius = size.minDimension * 0.6f
            ),
            radius = size.minDimension * 0.6f,
            center = Offset(
                size.width * (0.3f + offsetX * 0.2f),
                size.height * (0.4f + offsetY * 0.1f)
            )
        )
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    LunaColors.Gold.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(
                    size.width * (0.7f - offsetX * 0.15f),
                    size.height * (0.6f - offsetY * 0.1f)
                ),
                radius = size.minDimension * 0.5f
            ),
            radius = size.minDimension * 0.5f,
            center = Offset(
                size.width * (0.7f - offsetX * 0.15f),
                size.height * (0.6f - offsetY * 0.1f)
            )
        )
    }
}

@Composable
private fun StarField(starCount: Int) {
    val stars = remember(starCount) {
        List(starCount) {
            Star(
                position = Offset(Random.nextFloat(), Random.nextFloat()),
                size = Random.nextFloat() * 2f + 0.5f,
                baseAlpha = Random.nextFloat() * 0.5f + 0.3f,
                twinkleSpeed = Random.nextFloat() * 2f + 1f,
                twinkleOffset = Random.nextFloat() * 1000f
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "twinkle")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { star ->
            // ✅ sin() için Double cast
            val twinkle = sin(
                ((time + star.twinkleOffset) * star.twinkleSpeed * 0.01f).toDouble()
            ).toFloat()
            val alpha = star.baseAlpha + (twinkle * 0.3f).coerceIn(-0.3f, 0.3f)
            
            drawCircle(
                color = Color.White.copy(alpha = alpha.coerceIn(0f, 1f)),
                radius = star.size * 2f,
                center = Offset(star.position.x * size.width, star.position.y * size.height)
            )
            
            if (star.size > 1.5f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = alpha * 0.3f),
                            Color.Transparent
                        ),
                        radius = star.size * 8f
                    ),
                    radius = star.size * 8f,
                    center = Offset(star.position.x * size.width, star.position.y * size.height)
                )
            }
        }
    }
}

@Composable
private fun ShootingStars(interval: Long) {
    var shootingStar by remember { mutableStateOf<ShootingStar?>(null) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(interval + Random.nextLong(3000))
            shootingStar = ShootingStar(
                start = Offset(Random.nextFloat() * 0.5f, Random.nextFloat() * 0.3f),
                end = Offset(Random.nextFloat() * 0.5f + 0.5f, Random.nextFloat() * 0.3f + 0.5f)
            )
            delay(1500)
            shootingStar = null
        }
    }
    
    shootingStar?.let { star ->
        val progress by androidx.compose.animation.core.animateFloatAsState(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(1200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            label = "shooting"
        )
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val currentX = star.start.x + (star.end.x - star.start.x) * progress
            val currentY = star.start.y + (star.end.y - star.start.y) * progress
            
            val trailLength = 0.15f
            val trailStartX = (currentX - trailLength).coerceAtLeast(0f)
            val trailStartY = (currentY - trailLength * 0.5f).coerceAtLeast(0f)
            
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.8f),
                        Color.White
                    ),
                    start = Offset(trailStartX * size.width, trailStartY * size.height),
                    end = Offset(currentX * size.width, currentY * size.height)
                ),
                start = Offset(trailStartX * size.width, trailStartY * size.height),
                end = Offset(currentX * size.width, currentY * size.height),
                strokeWidth = 3f
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        LunaColors.Gold.copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    radius = 20f
                ),
                radius = 20f,
                center = Offset(currentX * size.width, currentY * size.height)
            )
        }
    }
}

@Composable
private fun FloatingParticles(count: Int) {
    val particles = remember(count) {
        List(count) {
            FloatingParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 0.0005f + 0.0002f,
                alpha = Random.nextFloat() * 0.4f + 0.1f,
                color = if (Random.nextBoolean()) LunaColors.Gold else LunaColors.MysticPurple
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val yOffset = (time * particle.speed) % 1f
            val y = (particle.y - yOffset + 1f) % 1f
            // ✅ sin() için Double cast
            val wobble = sin(
                (time * 0.01f + particle.y * 10f).toDouble()
            ).toFloat()
            val x = particle.x + wobble * 0.02f
            
            drawCircle(
                color = particle.color.copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(x * size.width, y * size.height)
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        particle.color.copy(alpha = particle.alpha * 0.5f),
                        Color.Transparent
                    ),
                    radius = particle.size * 4f
                ),
                radius = particle.size * 4f,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

private data class Star(
    val position: Offset,
    val size: Float,
    val baseAlpha: Float,
    val twinkleSpeed: Float,
    val twinkleOffset: Float
)

private data class ShootingStar(
    val start: Offset,
    val end: Offset
)

private data class FloatingParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float,
    val color: Color
)
