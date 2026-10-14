package com.cynora.luna.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.ui.effects.CosmicBackground
import com.cynora.luna.core.ui.effects.adaptiveParticleCount
import com.cynora.luna.core.ui.effects.adaptiveStarCount
import com.cynora.luna.core.ui.effects.shouldEnableNebula
import com.cynora.luna.core.ui.effects.shouldEnableShootingStars
import com.cynora.luna.core.ui.theme.LunaColors
import com.cynora.luna.core.ui.util.WindowSize
import com.cynora.luna.core.ui.util.windowSize

@Composable
fun HomeScreen(
    onNavigateToFortune: () -> Unit,
    onNavigateToBirthChart: () -> Unit,
    onNavigateToDailyCard: () -> Unit,
    onNavigateToCompatibility: () -> Unit,
    onNavigateToAngelNumbers: () -> Unit,
    onNavigateToLuckyNumbers: () -> Unit,
    onNavigateToAffirmations: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val windowSize = windowSize()
    
    // ✅ Adaptive effects - cihaza göre optimize
    val starCount = adaptiveStarCount()
    val particleCount = adaptiveParticleCount()
    val nebulaEnabled = shouldEnableNebula()
    val shootingStarInterval = if (shouldEnableShootingStars()) 8000L else Long.MAX_VALUE
    
    CosmicBackground(
        starCount = starCount,
        particleCount = particleCount,
        nebulaEnabled = nebulaEnabled,
        shootingStarInterval = shootingStarInterval
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LunaColors.DeepBlack)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = when (windowSize) {
                        WindowSize.Compact -> 16.dp
                        WindowSize.Medium -> 24.dp
                        WindowSize.Expanded -> 48.dp
                    }
                )
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Your Personal Cosmic Guide ✨",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Premium tarot, birth charts & offline fortune telling.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            MysticCalendarWidget(
                moonPhase = state.moonPhase,
                planetInfo = state.planetInfo,
                dailyEnergy = state.dailyEnergy,
                luckyHour = state.luckyHour
            )

            Spacer(modifier = Modifier.height(24.dp))

            DailyHoroscopeCard(
                zodiacSign = state.zodiacSign.displayName,
                zodiacSymbol = state.zodiacSign.symbol,
                horoscopeText = state.horoscopeText,
                luckyNumber = state.luckyNumber,
                luckyColor = state.luckyColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard("Birth Chart", "🌌", Modifier.weight(1f), onNavigateToBirthChart)
                    QuickActionCard("Daily Card", "🎯", Modifier.weight(1f), onNavigateToDailyCard)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard("Compatibility", "💑", Modifier.weight(1f), onNavigateToCompatibility)
                    QuickActionCard("Angel Numbers", "🔮", Modifier.weight(1f), onNavigateToAngelNumbers)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard("Lucky Numbers", "🍀", Modifier.weight(1f), onNavigateToLuckyNumbers)
                    QuickActionCard("Affirmations", "💫", Modifier.weight(1f), onNavigateToAffirmations)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MysticCalendarWidget(
    moonPhase: String,
    planetInfo: String,
    dailyEnergy: String,
    luckyHour: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(LunaColors.SurfaceDark, LunaColors.SurfaceVariant)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "🌙 Mystic Calendar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LunaColors.Gold
                )
                Spacer(modifier = Modifier.height(16.dp))
                CalendarInfoRow("🌕", "Moon Phase", moonPhase)
                Spacer(modifier = Modifier.height(12.dp))
                CalendarInfoRow("🪐", "Planet", planetInfo)
                Spacer(modifier = Modifier.height(12.dp))
                CalendarInfoRow("✨", "Energy", dailyEnergy)
                Spacer(modifier = Modifier.height(12.dp))
                CalendarInfoRow("⏰", "Lucky Hour", luckyHour)
            }
        }
    }
}

@Composable
private fun CalendarInfoRow(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(LunaColors.Gold.copy(alpha = 0.1f))
                .padding(6.dp),
            color = Color.White
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }
}

@Composable
private fun DailyHoroscopeCard(
    zodiacSign: String,
    zodiacSymbol: String,
    horoscopeText: String,
    luckyNumber: Int,
    luckyColor: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LunaColors.SurfaceDark),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "$zodiacSymbol $zodiacSign",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = LunaColors.Gold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = horoscopeText,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HoroscopeStat("Lucky Number", luckyNumber.toString())
                HoroscopeStat("Lucky Color", luckyColor)
            }
        }
    }
}

@Composable
private fun RowScope.HoroscopeStat(label: String, value: String) {
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = LunaColors.SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
