package com.cynora.luna.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun adaptiveTextSize(
    baseSize: TextUnit,
    minSize: TextUnit = 12.sp,
    maxSize: TextUnit = 32.sp
): TextUnit {
    val fontScale = LocalDensity.current.fontScale
    val scaledSize = (baseSize.value * fontScale).sp
    
    return when {
        scaledSize < minSize -> minSize
        scaledSize > maxSize -> maxSize
        else -> scaledSize
    }
}
