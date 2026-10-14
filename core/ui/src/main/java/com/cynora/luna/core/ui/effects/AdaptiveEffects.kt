package com.cynora.luna.core.ui.effects

import android.app.ActivityManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
private fun rememberIsLowEnd(): Boolean {
    val context = LocalContext.current
    return remember {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        am?.isLowRamDevice == true
    }
}

@Composable
fun adaptiveStarCount(): Int = if (rememberIsLowEnd()) 40 else 100

@Composable
fun adaptiveParticleCount(): Int = if (rememberIsLowEnd()) 8 else 20

@Composable
fun shouldEnableNebula(): Boolean = !rememberIsLowEnd()

@Composable
fun shouldEnableShootingStars(): Boolean = !rememberIsLowEnd()
