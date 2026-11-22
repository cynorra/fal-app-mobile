package com.cynora.luna.feature.fortune.dailycard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.ui.theme.LunaColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCardScreen(
    onBack: () -> Unit,
    viewModel: DailyCardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isFlipped by remember { mutableStateOf(false) }
    // ✅ Double-tap koruması
    var isAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "flip"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Card of the Day 🎯") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    titleContentColor = LunaColors.Gold
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isFlipped) {
                Card(
                    modifier = Modifier
                        .size(280.dp)
                        .rotate(rotation)
                        .clickable(enabled = !isAnimating) {
                            if (!isFlipped) {
                                isAnimating = true
                                isFlipped = true
                                viewModel.onIntent(DailyCardIntent.RevealCard)
                                scope.launch {
                                    delay(800)
                                    isAnimating = false
                                }
                            }
                        }
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(listOf(LunaColors.Gold, LunaColors.MysticPurple)),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("✨", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Tap to reveal\nyour card",
                                fontSize = 18.sp,
                                color = LunaColors.Gold,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Take a deep breath and tap the card", fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f))
            } else {
                state.card?.let { card ->
                    Card(
                        modifier = Modifier
                            .size(280.dp)
                            .rotate(rotation)
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(listOf(LunaColors.Gold, LunaColors.MysticPurple)),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(card.emoji, fontSize = 72.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(card.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                card.keywords.joinToString(" • "),
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    MeaningSection("✨ General", card.generalMeaning)
                    MeaningSection("💕 Love", card.loveMeaning)
                    MeaningSection("💼 Career", card.careerMeaning)
                    MeaningSection("🙏 Spiritual", card.spiritualMeaning)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LunaColors.Gold.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("💡 Today's Advice", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(card.advice, fontSize = 14.sp, color = Color.White)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LunaColors.MysticPurple.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🌟 Affirmation", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LunaColors.MysticPurple)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "\"${card.affirmation}\"",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            isAnimating = true
                            isFlipped = false
                            viewModel.onIntent(DailyCardIntent.DrawNewCard)
                            scope.launch {
                                delay(800)
                                isAnimating = false
                            }
                        },
                        enabled = !isAnimating,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LunaColors.Gold)
                    ) {
                        Text("Draw Another Card", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun MeaningSection(title: String, meaning: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(meaning, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f), lineHeight = 20.sp)
        }
    }
}
