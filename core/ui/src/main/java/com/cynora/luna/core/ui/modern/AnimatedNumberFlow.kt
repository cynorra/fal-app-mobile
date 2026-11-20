package com.cynora.luna.core.ui.modern

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.cynora.luna.core.ui.theme.LunaColors

/**
 * NumberFlow component inspired by Barvian's NumberFlow.
 * Smoothly animates digit transitions with vertical slot-machine / odometer tumbling.
 */
@Composable
fun NumberFlow(
    value: Int,
    modifier: Modifier = Modifier,
    suffix: String = "",
    prefix: String = "",
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = Color.White
) {
    var previousValue by remember { mutableIntStateOf(value) }

    SideEffect {
        previousValue = value
    }

    val isIncreasing = value >= previousValue
    val digits = value.toString().toList()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefix.isNotEmpty()) {
            Text(
                text = prefix,
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = color
            )
        }

        digits.forEachIndexed { index, digit ->
            AnimatedContent(
                targetState = digit,
                transitionSpec = {
                    if (isIncreasing) {
                        slideInVertically { height -> height } togetherWith
                                slideOutVertically { height -> -height }
                    } else {
                        slideInVertically { height -> -height } togetherWith
                                slideOutVertically { height -> height }
                    }
                },
                label = "digit_$index"
            ) { char ->
                Text(
                    text = char.toString(),
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    color = color
                )
            }
        }

        if (suffix.isNotEmpty()) {
            Text(
                text = suffix,
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = color
            )
        }
    }
}
