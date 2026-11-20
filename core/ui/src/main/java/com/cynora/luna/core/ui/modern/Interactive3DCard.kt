package com.cynora.luna.core.ui.modern

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cynora.luna.core.ui.theme.LunaColors

/**
 * Aceternity UI - 3D Perspective Card
 * Tilts dynamically in 3D space with tactile drag physics and an interactive specular glare.
 */
@Composable
fun Interactive3DCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    maxTiltDegrees: Float = 12f,
    containerColor: Color = Color(0xFF15192B),
    borderColor: Color = LunaColors.Gold.copy(alpha = 0.3f),
    content: @Composable BoxScope.() -> Unit
) {
    var rawTiltX by remember { mutableFloatStateOf(0f) }
    var rawTiltY by remember { mutableFloatStateOf(0f) }
    var touchPoint by remember { mutableStateOf<Offset?>(null) }

    val tiltX by animateFloatAsState(
        targetValue = rawTiltX,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "tiltX"
    )

    val tiltY by animateFloatAsState(
        targetValue = rawTiltY,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "tiltY"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationX = tiltX
                rotationY = tiltY
                cameraDistance = 18f * density
            }
            .clip(RoundedCornerShape(cornerRadius))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        touchPoint = offset
                        val normX = (offset.x / size.width - 0.5f) * 2f
                        val normY = (offset.y / size.height - 0.5f) * 2f
                        rawTiltY = normX * maxTiltDegrees
                        rawTiltX = -normY * maxTiltDegrees
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        touchPoint = change.position
                        val normX = (change.position.x / size.width - 0.5f) * 2f
                        val normY = (change.position.y / size.height - 0.5f) * 2f
                        rawTiltY = normX.coerceIn(-1f, 1f) * maxTiltDegrees
                        rawTiltX = -normY.coerceIn(-1f, 1f) * maxTiltDegrees
                    },
                    onDragEnd = {
                        rawTiltX = 0f
                        rawTiltY = 0f
                        touchPoint = null
                    },
                    onDragCancel = {
                        rawTiltX = 0f
                        rawTiltY = 0f
                        touchPoint = null
                    }
                )
            }
            .drawBehind {
                // Dynamic Specular Glare following touch position
                touchPoint?.let { pos ->
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                LunaColors.Gold.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = pos,
                            radius = size.width * 0.6f
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                    )
                }
            }
    ) {
        content()
    }
}
