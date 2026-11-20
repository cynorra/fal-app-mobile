package com.cynora.luna.feature.home

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.data.model.PlanetaryHourInfo
import com.cynora.luna.core.ui.ads.rememberRewardedAdManager
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
    onNavigateToYesNoOracle: () -> Unit = {},
    onNavigateToDreamInterpreter: () -> Unit = {},
    onNavigateToTarotReading: () -> Unit = {},
    onNavigateToZodiacGuide: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val windowSize = windowSize()
    val rewardedAdManager = rememberRewardedAdManager()

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
            Spacer(modifier = Modifier.height(28.dp))

            // Greeting + Big 3 Badge
            Text(
                text = "Welcome, ${state.userName} ✨",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF15192B))
                    .border(0.5.dp, LunaColors.Gold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = state.bigThree,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LunaColors.Gold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cosmic Metrics (Love, Career, Energy)
            CosmicMetricsCard(
                loveScore = state.loveScore,
                careerScore = state.careerScore,
                energyScore = state.energyScore
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Current Planetary Hour Live Card
            PlanetaryHourCard(planetaryHour = state.planetaryHour)

            Spacer(modifier = Modifier.height(20.dp))

            // Daily Horoscope Card with Yesterday / Today / Tomorrow Tabs
            HoroscopeTabsCard(
                state = state,
                onSelectTab = { index ->
                    if (index == 2 && !state.isTomorrowUnlocked) {
                        if (rewardedAdManager.isAdLoaded()) {
                            rewardedAdManager.showAd {
                                viewModel.unlockTomorrowHoroscope()
                            }
                        } else {
                            viewModel.unlockTomorrowHoroscope()
                        }
                    } else {
                        viewModel.selectHoroscopeDay(index)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Daily Lucky Strip
            DailyLuckyStrip(
                luckyNumber = state.luckyNumber,
                luckyColor = state.luckyColor,
                compatibleSign = state.compatibleSign
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Mystic Calendar Widget
            MysticCalendarWidget(
                moonPhase = state.moonPhase,
                planetInfo = state.planetInfo,
                dailyEnergy = state.dailyEnergy,
                luckyHour = state.luckyHour
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Divination & Quick Actions
            Text(
                text = "Cosmic Divination & Tools",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard("Tarot Spread", "🎴", Modifier.weight(1f), onNavigateToTarotReading)
                    QuickActionCard("Yes / No Oracle", "🔮", Modifier.weight(1f), onNavigateToYesNoOracle)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard("Dream Guide", "💭", Modifier.weight(1f), onNavigateToDreamInterpreter)
                    QuickActionCard("12 Zodiac Signs", "♈", Modifier.weight(1f), onNavigateToZodiacGuide)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard("Birth Chart", "🌌", Modifier.weight(1f), onNavigateToBirthChart)
                    QuickActionCard("Daily Card", "🎯", Modifier.weight(1f), onNavigateToDailyCard)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard("Compatibility", "💑", Modifier.weight(1f), onNavigateToCompatibility)
                    QuickActionCard("Angel Numbers", "🔢", Modifier.weight(1f), onNavigateToAngelNumbers)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard("Lucky Numbers", "🍀", Modifier.weight(1f), onNavigateToLuckyNumbers)
                    QuickActionCard("Affirmations", "💫", Modifier.weight(1f), onNavigateToAffirmations)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard("Full Reading", "☕", Modifier.weight(1f), onNavigateToFortune)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun CosmicMetricsCard(
    loveScore: Int,
    careerScore: Int,
    energyScore: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, LunaColors.Gold.copy(alpha = 0.25f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15192B)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "✨ Daily Cosmic Metrics",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = LunaColors.Gold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricGauge(label = "Love", score = loveScore, color = Color(0xFFE91E63))
                MetricGauge(label = "Career", score = careerScore, color = LunaColors.Gold)
                MetricGauge(label = "Energy", score = energyScore, color = LunaColors.MysticPurple)
            }
        }
    }
}

@Composable
private fun MetricGauge(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
            CircularProgressIndicator(
                progress = { score / 100f },
                color = color,
                trackColor = Color.White.copy(alpha = 0.1f),
                strokeWidth = 4.dp,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "$score%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
private fun PlanetaryHourCard(planetaryHour: PlanetaryHourInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, LunaColors.MysticPurple.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15192B)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(LunaColors.MysticPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(planetaryHour.planet.symbol, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${planetaryHour.planet.displayName} Hour",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = planetaryHour.timeFormatted,
                        fontSize = 12.sp,
                        color = LunaColors.Gold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${planetaryHour.planet.theme} • Best for ${planetaryHour.planet.bestFor}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun HoroscopeTabsCard(
    state: HomeState,
    onSelectTab: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, LunaColors.Gold.copy(alpha = 0.25f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15192B)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.zodiacSign.symbol} ${state.zodiacSign.displayName} Daily Guidance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LunaColors.Gold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = state.selectedHoroscopeDay,
                containerColor = Color(0xFF0F121E),
                contentColor = LunaColors.Gold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedHoroscopeDay]),
                        color = LunaColors.Gold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = state.selectedHoroscopeDay == 0,
                    onClick = { onSelectTab(0) },
                    text = { Text("Yesterday", fontSize = 12.sp) }
                )
                Tab(
                    selected = state.selectedHoroscopeDay == 1,
                    onClick = { onSelectTab(1) },
                    text = { Text("Today", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = state.selectedHoroscopeDay == 2,
                    onClick = { onSelectTab(2) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Tomorrow", fontSize = 12.sp)
                            if (!state.isTomorrowUnlocked) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(11.dp), tint = LunaColors.Gold)
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val currentText = when (state.selectedHoroscopeDay) {
                0 -> state.horoscopeYesterday
                1 -> state.horoscopeToday
                else -> state.horoscopeTomorrow
            }

            Text(
                text = currentText,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun DailyLuckyStrip(
    luckyNumber: Int,
    luckyColor: String,
    compatibleSign: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15192B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Lucky Number", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                Text("$luckyNumber", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
            }
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.1f)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Lucky Color", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                Text(luckyColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.1f)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Compatible Sign", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                Text(compatibleSign, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LunaColors.MysticPurple)
            }
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
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF15192B), Color(0xFF1E1E2E))
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "🌙 Mystic Calendar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LunaColors.Gold
                )
                Spacer(modifier = Modifier.height(14.dp))
                CalendarInfoRow("🌕", "Moon Phase", moonPhase)
                Spacer(modifier = Modifier.height(10.dp))
                CalendarInfoRow("🪐", "Planet", planetInfo)
                Spacer(modifier = Modifier.height(10.dp))
                CalendarInfoRow("✨", "Energy", dailyEnergy)
                Spacer(modifier = Modifier.height(10.dp))
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
            fontSize = 18.sp,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(LunaColors.Gold.copy(alpha = 0.1f))
                .padding(4.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
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
            .height(76.dp)
            .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15192B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 24.sp,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
