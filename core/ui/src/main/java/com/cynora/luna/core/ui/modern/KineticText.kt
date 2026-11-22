package com.cynora.luna.core.ui.modern

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Word-by-word reveal (Componentry "Kinetic Text Reveal"): each word slides up by [distance],
 * fades in and loses its blur, staggered by [staggerMillis] (library default 0.075 s).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KineticReveal(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(fontSize = 18.sp, color = Color.White),
    staggerMillis: Int = 75,
    distance: Dp = 20.dp,
    blurRadius: Dp = 8.dp,
    centered: Boolean = false
) {
    val words = remember(text) { text.split(" ").filter { it.isNotEmpty() } }
    key(text) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = if (centered) {
                Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
            } else {
                Arrangement.spacedBy(6.dp)
            }
        ) {
            words.forEachIndexed { index, word ->
                KineticWord(
                    word = word,
                    delayMillis = index * staggerMillis,
                    style = style,
                    distance = distance,
                    blurRadius = blurRadius
                )
            }
        }
    }
}

@Composable
private fun KineticWord(
    word: String,
    delayMillis: Int,
    style: TextStyle,
    distance: Dp,
    blurRadius: Dp
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        progress.animateTo(1f, tween(durationMillis = 520, easing = FastOutSlowInEasing))
    }
    Text(
        text = word,
        style = style,
        modifier = Modifier
            .graphicsLayer {
                alpha = progress.value
                translationY = (1f - progress.value) * distance.toPx()
            }
            .blur(blurRadius * (1f - progress.value))
    )
}

/**
 * Split-flap style heading (Componentry "Letter Cascade"): every letter flips in with a spring
 * (stiffness 220, damping ratio ~0.54 = library damping 16), staggered by [staggerMillis] (0.04 s).
 * Use for short headings only, the letters are laid out in a single row.
 */
@Composable
fun CascadeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(fontSize = 28.sp, color = Color.White),
    staggerMillis: Int = 40
) {
    key(text) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
            text.forEachIndexed { index, ch ->
                CascadeChar(ch = ch, delayMillis = index * staggerMillis, style = style)
            }
        }
    }
}

@Composable
private fun CascadeChar(ch: Char, delayMillis: Int, style: TextStyle) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        progress.animateTo(1f, spring(dampingRatio = 0.54f, stiffness = 220f))
    }
    Text(
        text = ch.toString(),
        style = style,
        modifier = Modifier.graphicsLayer {
            rotationX = (1f - progress.value) * -90f
            alpha = progress.value.coerceIn(0f, 1f)
            cameraDistance = 12f * density
        }
    )
}
