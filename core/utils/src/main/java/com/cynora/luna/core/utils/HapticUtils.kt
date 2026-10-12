package com.cynora.luna.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Premium haptic feedback manager
 * ✅ Watermelon UI esintili mikro-etkileşimler
 */
@Composable
fun rememberHapticFeedback(): HapticFeedbackManager {
    val haptic = LocalHapticFeedback.current
    return HapticFeedbackManager(haptic)
}

class HapticFeedbackManager(private val haptic: HapticFeedback) {
    
    fun buttonClick() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    fun toggle() {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    
    fun success() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    fun error() {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    
    fun selection() {
        haptic.performHapticFeedback(HapticFeedbackType.KeyboardPress)
    }
}
