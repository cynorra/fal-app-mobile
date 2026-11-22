package com.cynora.luna.feature.fortune.tarot

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.data.model.DrawnTarotCard
import com.cynora.luna.core.ui.ads.rememberRewardedAdManager
import com.cynora.luna.core.ui.modern.ShimmerGlowButton
import com.cynora.luna.core.ui.modern.borderBeam
import com.cynora.luna.core.ui.theme.LunaColors
import com.cynora.luna.core.utils.rememberHapticFeedback
import com.cynora.luna.core.utils.ShareUtils

@Composable
fun TarotReadingScreen(
    onBack: () -> Unit,
    viewModel: TarotReadingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val rewardedAdManager = rememberRewardedAdManager()
    val haptic = rememberHapticFeedback()

    Column(
        modifier = Modifier
            .fillMaxSize()
            
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tarot Divination 🎴",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Spread Mode Tabs
        TabRow(
            selectedTabIndex = if (state.spreadMode == TarotSpreadMode.DAILY_ONE_CARD) 0 else 1,
            containerColor = LunaColors.GlassFill,
            contentColor = LunaColors.Gold,
            indicator = { tabPositions ->
                val index = if (state.spreadMode == TarotSpreadMode.DAILY_ONE_CARD) 0 else 1
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                    color = LunaColors.Gold
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = state.spreadMode == TarotSpreadMode.DAILY_ONE_CARD,
                onClick = { viewModel.setSpreadMode(TarotSpreadMode.DAILY_ONE_CARD) },
                text = { Text("Daily 1-Card", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = state.spreadMode == TarotSpreadMode.THREE_CARD_SPREAD,
                onClick = { viewModel.setSpreadMode(TarotSpreadMode.THREE_CARD_SPREAD) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("3-Card Spread", fontWeight = FontWeight.SemiBold)
                        if (!state.isThreeCardUnlocked) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(14.dp), tint = LunaColors.Gold)
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Draw / Shuffle Action
        val needsUnlock = state.spreadMode == TarotSpreadMode.THREE_CARD_SPREAD && !state.isThreeCardUnlocked

        if (needsUnlock) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LunaColors.Gold.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔒 3-Card Spread (Past / Present / Future)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Unlock the full past, present, and emerging destiny spread by viewing a short sponsor message.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (rewardedAdManager.isAdLoaded()) {
                                rewardedAdManager.showAd {
                                    viewModel.unlockThreeCardSpread()
                                }
                            } else {
                                viewModel.unlockThreeCardSpread()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LunaColors.Gold, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Watch Sponsor to Unlock", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            ShimmerGlowButton(
                onClick = {
                    haptic.buttonClick()
                    viewModel.drawCards()
                },
                enabled = !state.isShuffling,
                modifier = Modifier.fillMaxWidth(),
                height = 54.dp,
                cornerRadius = 16.dp,
                containerColor = LunaColors.Gold
            ) {
                if (state.isShuffling) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Shuffling 22 Major Arcana...", fontWeight = FontWeight.Bold, color = Color.Black)
                } else {
                    Text(
                        if (state.isCardRevealed) "✨ Shuffle & Draw Again" else "✨ Shuffle & Draw Cards",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Cards Display
        AnimatedVisibility(
            visible = state.isCardRevealed && state.drawnCards.isNotEmpty(),
            enter = fadeIn() + slideInVertically(initialOffsetY = { 60 })
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                state.drawnCards.forEach { drawn ->
                    TarotCard3DView(drawn = drawn)
                }

                // Share Button
                Button(
                    onClick = {
                        val cardDetails = state.drawnCards.joinToString("\n\n") { d ->
                            val pos = d.position?.let { "[${it.title}] " } ?: ""
                            "$pos${d.card.name} (${d.orientationLabel})\n\"${d.activeMeaning}\""
                        }
                        ShareUtils.shareFortune(
                            context = context,
                            fortuneText = cardDetails,
                            dailyIntention = "Walk in alignment with today's drawn energies.",
                            fortuneType = "Tarot Reading"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.1f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Tarot Reading", fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun TarotCard3DView(drawn: DrawnTarotCard) {
    var flipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "cardFlip"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .borderBeam(durationMillis = 4000, beamColor = LunaColors.Gold, cornerRadius = 20.dp),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Position Header
            drawn.position?.let { pos ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = pos.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
                    Text(text = pos.subtitle, fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Interactive 3D Card Visual
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E1638),
                                Color(0xFF0F0B1E)
                            )
                        )
                    )
                    .border(1.5.dp, LunaColors.Gold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .clickable { flipped = !flipped },
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    // Front Face
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = drawn.card.icon, fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${drawn.card.romanNumeral} • ${drawn.card.name}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaColors.Gold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (drawn.isReversed) "🔄 Reversed" else "✨ Upright",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (drawn.isReversed) Color(0xFFFFB74D) else Color(0xFF81C784)
                        )
                    }
                } else {
                    // Back Face (flips 180)
                    Box(
                        modifier = Modifier
                            .graphicsLayer { rotationY = 180f }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔮", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Element: ${drawn.card.element}",
                                fontSize = 13.sp,
                                color = LunaColors.MysticPurple,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap to view front",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Keywords
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                drawn.activeKeywords.take(3).forEach { kw ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = kw, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Meaning
            Text(
                text = drawn.activeMeaning,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Advice
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .padding(12.dp)
            ) {
                Text(text = "💕 Love: ${drawn.card.loveAdvice}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "💼 Career: ${drawn.card.careerAdvice}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}
