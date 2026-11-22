package com.cynora.luna.feature.fortune

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.cynora.luna.core.data.model.FocusArea
import com.cynora.luna.core.data.model.Mood
import com.cynora.luna.core.data.model.FortuneType
import com.cynora.luna.core.ui.theme.LunaColors
import com.cynora.luna.core.ui.modern.BalloonsBurst
import com.cynora.luna.core.ui.modern.DotMatrixLoader
import com.cynora.luna.core.ui.modern.GlassCard
import com.cynora.luna.core.ui.modern.KineticReveal
import com.cynora.luna.core.ui.modern.LunaPrimaryButton
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.text.TextStyle
import com.cynora.luna.core.utils.ShareUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FortuneScreen(
    initialFortuneId: String? = null,
    viewModel: FortuneViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Deep link handling
    LaunchedEffect(initialFortuneId) {
        if (!initialFortuneId.isNullOrEmpty()) {
            viewModel.loadFortuneById(initialFortuneId)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FortuneEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = LunaColors.SurfaceDark,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                
        ) {
            if (state.fortune == null) {
                FortuneSelectionContent(state, viewModel::onIntent)
            } else {
                FortuneResultContent(state, viewModel::onIntent)
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        DotMatrixLoader(loaderSize = 80.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Reading the cosmos...",
                            color = LunaColors.Gold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FortuneSelectionContent(
    state: FortuneState,
    onIntent: (FortuneIntent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        BoxWithConstraints(modifier = Modifier.widthIn(max = 640.dp).fillMaxSize()) {
            val typeColumns = if (maxWidth >= 520.dp) 4 else 2

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                KineticReveal(
                    text = "What do you seek today?",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                    staggerMillis = 60,
                    centered = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Choose your mood and focus, receive the universe's message",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.62f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                FortuneSectionLabel("Fortune Type")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = typeColumns,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FortuneType.entries.take(8).forEach { type ->
                        FortuneTypeCard(type, state.selectedType == type, Modifier.weight(1f)) {
                            onIntent(FortuneIntent.SelectType(type))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FortuneSectionLabel("Current Mood")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Mood.entries.forEach { mood ->
                        MoodChip(mood, state.selectedMood == mood) {
                            onIntent(FortuneIntent.SelectMood(mood))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FortuneSectionLabel("Focus Area")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FocusArea.entries.forEach { focus ->
                        FocusChip(focus, state.selectedFocus == focus) {
                            onIntent(FortuneIntent.SelectFocus(focus))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                LunaPrimaryButton(
                    text = "✨ Read My Fortune",
                    onClick = { onIntent(FortuneIntent.GetFortune) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FortuneResultContent(
    state: FortuneState,
    onIntent: (FortuneIntent) -> Unit
) {
    val fortune = state.fortune ?: return
    val context = LocalContext.current
    val fullText = fortune.text

    // Keyed by fortune.id so every new fortune restarts the reveal
    var targetLength by remember(fortune.id) { mutableIntStateOf(0) }
    var reported by remember(fortune.id) { mutableStateOf(false) }

    // The reveal itself is drawn word by word (KineticReveal). This timer only tells us when the
    // reading has been fully shown, to mark it as seen and show the actions.
    val animatedLength by animateIntAsState(
        targetValue = targetLength,
        animationSpec = tween(
            durationMillis = (fullText.length * 25).coerceIn(400, 2500),
            easing = LinearEasing
        ),
        label = "revealTimer"
    )

    LaunchedEffect(fortune.id) {
        targetLength = 0
        reported = false
        delay(150)
        targetLength = fullText.length
    }

    LaunchedEffect(animatedLength, fullText.length) {
        if (!reported && animatedLength >= fullText.length && fullText.isNotEmpty()) {
            reported = true
            onIntent(FortuneIntent.MarkAsSeen)
            onIntent(FortuneIntent.OnTypewriterComplete)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = fortune.type.icon + " " + fortune.type.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = LunaColors.Gold
            )

            Spacer(modifier = Modifier.height(20.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                selected = true,
                cornerRadius = 24.dp,
                contentPadding = 24.dp
            ) {
                KineticReveal(
                    text = fullText,
                    style = TextStyle(
                        fontSize = 19.sp,
                        lineHeight = 30.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    ),
                    staggerMillis = 60
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = state.isTypewriterComplete,
                enter = fadeIn() + slideInVertically { it }
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    accent = LunaColors.MysticPurple,
                    cornerRadius = 18.dp,
                    contentPadding = 20.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✨ Daily Intention",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LunaColors.Gold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = fortune.dailyIntention,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.92f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            AnimatedVisibility(
                visible = state.isTypewriterComplete,
                enter = fadeIn()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        cornerRadius = 28.dp,
                        contentPadding = 0.dp,
                        onClick = { onIntent(FortuneIntent.GetFortune) }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Try Again",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LunaColors.Gold
                            )
                        }
                    }

                    LunaPrimaryButton(
                        text = "Share",
                        onClick = {
                            ShareUtils.shareFortune(
                                context = context,
                                fortuneText = fortune.text,
                                dailyIntention = fortune.dailyIntention,
                                fortuneType = fortune.type.displayName
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        BalloonsBurst(trigger = if (state.isTypewriterComplete) fortune.id else null)
    }
}

@Composable
private fun FortuneTypeCard(
    type: FortuneType,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.height(88.dp),
        selected = isSelected,
        cornerRadius = 18.dp,
        contentPadding = 8.dp,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = type.icon, fontSize = 26.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = type.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) LunaColors.Gold else Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun MoodChip(mood: Mood, isSelected: Boolean, onClick: () -> Unit) {
    FortuneGlassChip(label = mood.displayName, selected = isSelected, onClick = onClick)
}

@Composable
private fun FocusChip(focus: FocusArea, isSelected: Boolean, onClick: () -> Unit) {
    FortuneGlassChip(label = focus.displayName, selected = isSelected, onClick = onClick)
}


@Composable
private fun FortuneGlassChip(label: String, selected: Boolean, onClick: () -> Unit) {
    GlassCard(
        selected = selected,
        cornerRadius = 22.dp,
        contentPadding = 0.dp,
        onClick = onClick
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) LunaColors.Gold else Color.White.copy(alpha = 0.86f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun FortuneSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = LunaColors.Gold,
        letterSpacing = 1.sp,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    )
}
