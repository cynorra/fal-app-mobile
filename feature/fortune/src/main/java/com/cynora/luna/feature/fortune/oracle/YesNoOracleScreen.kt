package com.cynora.luna.feature.fortune.oracle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.data.model.OracleOutcome
import com.cynora.luna.core.ui.theme.LunaColors
import com.cynora.luna.core.utils.rememberHapticFeedback
import com.cynora.luna.core.utils.ShareUtils

@Composable
fun YesNoOracleScreen(
    onBack: () -> Unit,
    viewModel: YesNoOracleViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()

    val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LunaColors.DeepBlack)
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
                text = "Yes / No Oracle",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Focus your intention on a single question. The cosmos will reveal whether the currents flow in alignment.",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Question Input
        OutlinedTextField(
            value = state.questionInput,
            onValueChange = viewModel::onQuestionChange,
            placeholder = {
                Text("What does your soul seek to know?", color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LunaColors.SurfaceDark,
                unfocusedContainerColor = LunaColors.SurfaceDark,
                focusedBorderColor = LunaColors.Gold,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Cosmic Orb
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            val scaleFactor = if (state.isConsulting) pulseScale * 1.1f else 1.0f

            // Outer Aura
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(scaleFactor)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                LunaColors.Gold.copy(alpha = if (state.isConsulting) 0.5f else 0.25f),
                                LunaColors.MysticPurple.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Inner Orb
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scaleFactor)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2E1B4E),
                                Color(0xFF15192B),
                                LunaColors.DeepBlack
                            )
                        )
                    )
                    .border(2.dp, LunaColors.Gold.copy(alpha = 0.6f), CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        haptic.buttonClick()
                        viewModel.consultOracle()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.isConsulting) {
                    CircularProgressIndicator(
                        color = LunaColors.Gold,
                        modifier = Modifier.size(42.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🔮",
                            fontSize = 36.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Touch Orb",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = LunaColors.Gold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Button
        Button(
            onClick = {
                haptic.success()
                viewModel.consultOracle()
            },
            enabled = !state.isConsulting,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LunaColors.Gold,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = if (state.isConsulting) "Channelling the Stars..." else "Consult the Oracle",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Result Card
        AnimatedVisibility(
            visible = state.reading != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 80 })
        ) {
            state.reading?.let { reading ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LunaColors.Gold.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = LunaColors.SurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Outcome Badge
                        val badgeColor = when (reading.outcome) {
                            OracleOutcome.YES -> Color(0xFF4CAF50)
                            OracleOutcome.NO -> Color(0xFFEF5350)
                            OracleOutcome.MAYBE -> LunaColors.Gold
                        }

                        Text(
                            text = "${reading.outcome.emoji} ${reading.outcome.displayName}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = reading.headline,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = reading.cosmicAlignment,
                                fontSize = 12.sp,
                                color = LunaColors.MysticPurple,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = reading.spiritualAdvice,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "💡 ${reading.luckyAspect}",
                            fontSize = 12.sp,
                            color = LunaColors.Gold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    ShareUtils.shareFortune(
                                        context = context,
                                        fortuneText = "Question: \"${state.questionInput.ifBlank { "My Path" }}\"\nAnswer: ${reading.outcome.displayName} — ${reading.headline}\n\n${reading.spiritualAdvice}",
                                        dailyIntention = reading.luckyAspect,
                                        fortuneType = "Yes/No Oracle"
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.1f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share")
                            }

                            Button(
                                onClick = { viewModel.reset() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LunaColors.Gold.copy(alpha = 0.2f),
                                    contentColor = LunaColors.Gold
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("New Question")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
