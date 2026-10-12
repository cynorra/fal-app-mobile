package com.cynora.luna.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

object LunaColors {
    val DeepBlack = Color(0xFF0A0A0A)
    val SurfaceDark = Color(0xFF1E1E1E)
    val SurfaceVariant = Color(0xFF2C2C2C)
    val Gold = Color(0xFFD4AF37)
    val GoldLight = Color(0xFFFFD700)
    val MysticPurple = Color(0xFF9D76C1)
    val TextPrimary = Color(0xFFE0E0E0)
    val TextSecondary = Color(0xFFB0B0B0)
    val Error = Color(0xFFCF6679)
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFFC107)

    // Eski kodlardaki kısa isimler için alias
    val Surface: Color get() = SurfaceDark
    val SurfaceVar: Color get() = SurfaceVariant
    val Purple: Color get() = MysticPurple
    val Text: Color get() = TextPrimary
    val TextSec: Color get() = TextSecondary
}

val LunaDark = darkColorScheme(
    primary = LunaColors.Gold,
    onPrimary = Color(0xFF121212),
    primaryContainer = LunaColors.GoldLight,
    onPrimaryContainer = Color(0xFF121212),
    secondary = LunaColors.MysticPurple,
    onSecondary = Color.White,
    secondaryContainer = LunaColors.MysticPurple.copy(alpha = 0.3f),
    onSecondaryContainer = Color.White,
    background = LunaColors.DeepBlack,
    onBackground = LunaColors.TextPrimary,
    surface = LunaColors.SurfaceDark,
    onSurface = LunaColors.TextPrimary,
    surfaceVariant = LunaColors.SurfaceVariant,
    onSurfaceVariant = LunaColors.TextSecondary,
    error = LunaColors.Error,
    onError = Color.Black
)
