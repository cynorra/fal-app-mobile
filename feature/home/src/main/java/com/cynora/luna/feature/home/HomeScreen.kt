package com.cynora.luna.feature.home

import androidx.compose.ui.res.stringResource
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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.cynora.luna.core.ui.modern.CmdkAction
import com.cynora.luna.core.ui.modern.CmdkPalette
import com.cynora.luna.core.ui.modern.NumberFlow
import com.cynora.luna.core.ui.modern.borderBeam
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
    var isCmdkOpen by remember { mutableStateOf(false) }

    val starCount = adaptiveStarCount()
    val particleCount = adaptiveParticleCount()
    val nebulaEnabled = shouldEnableNebula()
    val shootingStarInterval = if (shouldEnableShootingStars()) 8000L else Long.MAX_VALUE

    val cmdkActions = listOf(
        CmdkAction(stringResource(R.string.ui_home_cmdk_tarot_title), stringResource(R.string.ui_home_cmdk_tarot_sub), "🎴", stringResource(R.string.ui_home_cat_divination), onNavigateToTarotReading),
        CmdkAction(stringResource(R.string.ui_home_cmdk_oracle_title), stringResource(R.string.ui_home_cmdk_oracle_sub), "🔮", stringResource(R.string.ui_home_cat_oracle), onNavigateToYesNoOracle),
        CmdkAction(stringResource(R.string.ui_home_cmdk_dream_title), stringResource(R.string.ui_home_cmdk_dream_sub), "💭", stringResource(R.string.ui_home_cat_dreams), onNavigateToDreamInterpreter),
        CmdkAction(stringResource(R.string.ui_home_cmdk_zodiac_title), stringResource(R.string.ui_home_cmdk_zodiac_sub), "♈", stringResource(R.string.ui_home_cat_astrology), onNavigateToZodiacGuide),
        CmdkAction(stringResource(R.string.ui_home_birth_chart), stringResource(R.string.ui_home_cmdk_birth_sub), "🌌", stringResource(R.string.ui_home_cat_astrology), onNavigateToBirthChart),
        CmdkAction(stringResource(R.string.ui_home_daily_card), stringResource(R.string.ui_home_cmdk_daily_sub), "🎯", stringResource(R.string.ui_home_cat_daily), onNavigateToDailyCard),
        CmdkAction(stringResource(R.string.ui_home_cmdk_compat_title), stringResource(R.string.ui_home_cmdk_compat_sub), "💑", stringResource(R.string.ui_home_cat_love), onNavigateToCompatibility),
        CmdkAction(stringResource(R.string.ui_home_angel_numbers), stringResource(R.string.ui_home_cmdk_angel_sub), "🔢", stringResource(R.string.ui_home_cat_numerology), onNavigateToAngelNumbers),
        CmdkAction(stringResource(R.string.ui_home_lucky_numbers), stringResource(R.string.ui_home_cmdk_lucky_sub), "🍀", stringResource(R.string.ui_home_cat_daily), onNavigateToLuckyNumbers),
        CmdkAction(stringResource(R.string.ui_home_cmdk_affirm_title), stringResource(R.string.ui_home_cmdk_affirm_sub), "💫", stringResource(R.string.ui_home_cat_daily), onNavigateToAffirmations),
        CmdkAction(stringResource(R.string.ui_home_cmdk_full_title), stringResource(R.string.ui_home_cmdk_full_sub), "☕", stringResource(R.string.ui_home_cat_divination), onNavigateToFortune)
    )

    // The animated LunaBackdrop (aurora, stars, dot grid) is drawn by the navigation host.
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                
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

            // Greeting + Big 3 Badge + cmdk Quick Command Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.ui_home_welcome, state.userName),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(LunaColors.GlassFill)
                            .border(0.5.dp, LunaColors.Gold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = state.bigThree,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LunaColors.Gold
                        )
                    }
                }

                // cmdk Spotlight Search Pill Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LunaColors.GlassFill)
                        .border(0.5.dp, LunaColors.Gold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { isCmdkOpen = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.ui_home_search), tint = LunaColors.Gold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.ui_home_search_pill), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cosmic Metrics (Love, Career, Energy) with Magic UI Border Beam & NumberFlow
            CosmicMetricsCard(
                loveScore = state.loveScore,
                careerScore = state.careerScore,
                energyScore = state.energyScore
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Current Planetary Hour Live Card with Border Beam
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
                text = stringResource(R.string.ui_home_tools_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(stringResource(R.string.ui_home_qa_tarot), "🎴", Modifier.weight(1f), onNavigateToTarotReading)
                    QuickActionCard(stringResource(R.string.ui_home_cmdk_oracle_title), "🔮", Modifier.weight(1f), onNavigateToYesNoOracle)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(stringResource(R.string.ui_home_qa_dream), "💭", Modifier.weight(1f), onNavigateToDreamInterpreter)
                    QuickActionCard(stringResource(R.string.ui_home_cmdk_zodiac_title), "♈", Modifier.weight(1f), onNavigateToZodiacGuide)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(stringResource(R.string.ui_home_birth_chart), "🌌", Modifier.weight(1f), onNavigateToBirthChart)
                    QuickActionCard(stringResource(R.string.ui_home_daily_card), "🎯", Modifier.weight(1f), onNavigateToDailyCard)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(stringResource(R.string.ui_home_qa_compat), "💑", Modifier.weight(1f), onNavigateToCompatibility)
                    QuickActionCard(stringResource(R.string.ui_home_angel_numbers), "🔢", Modifier.weight(1f), onNavigateToAngelNumbers)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(stringResource(R.string.ui_home_lucky_numbers), "🍀", Modifier.weight(1f), onNavigateToLuckyNumbers)
                    QuickActionCard(stringResource(R.string.ui_home_qa_affirm), "💫", Modifier.weight(1f), onNavigateToAffirmations)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(stringResource(R.string.ui_home_qa_full), "☕", Modifier.weight(1f), onNavigateToFortune)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        // cmdk Spotlight Palette Overlay
        CmdkPalette(
            isOpen = isCmdkOpen,
            onDismiss = { isCmdkOpen = false },
            actions = cmdkActions
        )
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
            .borderBeam(durationMillis = 3500, beamColor = LunaColors.Gold, cornerRadius = 20.dp),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.ui_home_metrics_title),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = LunaColors.Gold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricGauge(label = stringResource(R.string.ui_home_love), score = loveScore, color = Color(0xFFE91E63))
                MetricGauge(label = stringResource(R.string.ui_home_career), score = careerScore, color = LunaColors.Gold)
                MetricGauge(label = stringResource(R.string.ui_home_energy), score = energyScore, color = LunaColors.MysticPurple)
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
            // NumberFlow by Barvian - Odometer Tumbling Number
            NumberFlow(
                value = score,
                suffix = "%",
                fontSize = 12.sp,
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
            .borderBeam(durationMillis = 5000, beamColor = LunaColors.MysticPurple, cornerRadius = 18.dp),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
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
                        text = stringResource(R.string.ui_home_planet_hour, planetaryHour.planet.displayName),
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
                    text = stringResource(R.string.ui_home_planet_theme, planetaryHour.planet.theme, planetaryHour.planet.bestFor),
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
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.ui_home_daily_guidance, state.zodiacSign.symbol, state.zodiacSign.displayName),
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
                    text = { Text(stringResource(R.string.ui_home_yesterday), fontSize = 12.sp) }
                )
                Tab(
                    selected = state.selectedHoroscopeDay == 1,
                    onClick = { onSelectTab(1) },
                    text = { Text(stringResource(R.string.ui_home_today), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = state.selectedHoroscopeDay == 2,
                    onClick = { onSelectTab(2) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.ui_home_tomorrow), fontSize = 12.sp)
                            if (!state.isTomorrowUnlocked) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Lock, contentDescription = stringResource(R.string.ui_home_locked), modifier = Modifier.size(11.dp), tint = LunaColors.Gold)
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
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
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
                Text(stringResource(R.string.ui_home_lucky_number), fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                Text("$luckyNumber", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
            }
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.1f)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.ui_home_lucky_color), fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                Text(luckyColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.1f)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.ui_home_compatible_sign), fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
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
                        colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.03f))
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.ui_home_calendar_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LunaColors.Gold
                )
                Spacer(modifier = Modifier.height(14.dp))
                CalendarInfoRow("🌕", stringResource(R.string.ui_home_moon_phase), moonPhase)
                Spacer(modifier = Modifier.height(10.dp))
                CalendarInfoRow("🪐", stringResource(R.string.ui_home_planet), planetInfo)
                Spacer(modifier = Modifier.height(10.dp))
                CalendarInfoRow("✨", stringResource(R.string.ui_home_energy), dailyEnergy)
                Spacer(modifier = Modifier.height(10.dp))
                CalendarInfoRow("⏰", stringResource(R.string.ui_home_lucky_hour), luckyHour)
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
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder
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
