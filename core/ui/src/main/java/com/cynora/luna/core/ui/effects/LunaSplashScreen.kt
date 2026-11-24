package com.cynora.luna.core.ui.effects

import com.cynora.luna.core.ui.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cynora.luna.core.ui.theme.LunaColors
import kotlinx.coroutines.delay

@Composable
fun LunaSplashScreen(
    onComplete: () -> Unit,
    duration: Int = 2500
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800),
        label = "logoAlpha"
    )
    
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 400),
        label = "textAlpha"
    )
    
    val textOffset by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 30f,
        animationSpec = tween(800, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "textOffset"
    )
    
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(duration.toLong())
        onComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0A0A1F),
                        LunaColors.DeepBlack
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        CosmicBackground(
            starCount = 80,
            particleCount = 15,
            nebulaEnabled = true
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🌙",
                    fontSize = 96.sp,
                    modifier = Modifier
                        .scale(logoScale)
                        .alpha(logoAlpha)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Luna",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaColors.Gold,
                    modifier = Modifier
                        .alpha(textAlpha)
                        .offset(y = textOffset.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.ui_core_tagline),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = textAlpha * 0.7f),
                    modifier = Modifier
                        .alpha(textAlpha)
                        .offset(y = textOffset.dp)
                )
            }
        }
    }
}
