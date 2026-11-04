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
                .background(LunaColors.DeepBlack)
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
                        CircularProgressIndicator(color = LunaColors.Gold)
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

@Composable
private fun FortuneSelectionContent(
    state: FortuneState,
    onIntent: (FortuneIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "What do you seek today?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose your mood and focus, receive the universe's message",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Fortune Type", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = LunaColors.Gold)
        Spacer(modifier = Modifier.height(12.dp))

        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(240.dp)
        ) {
            items(FortuneType.entries.take(8)) { type ->
                FortuneTypeCard(type, state.selectedType == type) {
                    onIntent(FortuneIntent.SelectType(type))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Current Mood", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = LunaColors.Gold)
        Spacer(modifier = Modifier.height(12.dp))

        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(120.dp)
        ) {
            items(Mood.entries.toList()) { mood ->
                MoodChip(mood, state.selectedMood == mood) {
                    onIntent(FortuneIntent.SelectMood(mood))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Focus Area", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = LunaColors.Gold)
        Spacer(modifier = Modifier.height(12.dp))

        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(160.dp)
        ) {
            items(FocusArea.entries.toList()) { focus ->
                FocusChip(focus, state.selectedFocus == focus) {
                    onIntent(FortuneIntent.SelectFocus(focus))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onIntent(FortuneIntent.GetFortune) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(LunaColors.Gold, LunaColors.GoldLight, LunaColors.Gold)
                    )
                ),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text(
                text = "✨ Read My Fortune",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF121212)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
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

    // ✅ fortune.id ile key - yeni fortune gelince sıfırlansın
    var targetLength by remember(fortune.id) { mutableIntStateOf(0) }
    var reported by remember(fortune.id) { mutableStateOf(false) }

    val animatedLength by animateIntAsState(
        targetValue = targetLength,
        animationSpec = tween(
            durationMillis = (fullText.length * 25).coerceIn(400, 2500),
            easing = LinearEasing
        ),
        label = "typewriter"
    )

    // Animasyonu başlat
    LaunchedEffect(fortune.id) {
        targetLength = 0
        reported = false
        delay(150)
        targetLength = fullText.length
    }

    // ✅ KRİTİK: Bitiş tespiti + seen işaretleme
    LaunchedEffect(animatedLength, fullText.length) {
        if (!reported && animatedLength >= fullText.length && fullText.isNotEmpty()) {
            reported = true
            onIntent(FortuneIntent.MarkAsSeen)
            onIntent(FortuneIntent.OnTypewriterComplete)
        }
    }

    val visibleText = fullText.take(animatedLength)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = fortune.type.icon + " " + fortune.type.displayName,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = LunaColors.Gold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(LunaColors.SurfaceDark.copy(alpha = 0.6f))
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            LunaColors.Gold.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = visibleText,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                if (!state.isTypewriterComplete) {
                    Text(text = "▌", color = LunaColors.Gold, fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = state.isTypewriterComplete,
            enter = fadeIn() + slideInVertically { it }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LunaColors.SurfaceVariant)
                    .padding(20.dp),
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
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(
            visible = state.isTypewriterComplete,
            enter = fadeIn()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onIntent(FortuneIntent.GetFortune) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LunaColors.Gold)
                ) {
                    Text("Try Again")
                }

                Button(
                    onClick = {
                        ShareUtils.shareFortune(
                            context = context,
                            fortuneText = fortune.text,
                            dailyIntention = fortune.dailyIntention,
                            fortuneType = fortune.type.displayName
                        )
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LunaColors.Gold)
                ) {
                    Text("Share", color = Color(0xFF121212))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FortuneTypeCard(type: FortuneType, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(2.dp, LunaColors.Gold, RoundedCornerShape(16.dp))
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LunaColors.Gold.copy(alpha = 0.1f) else LunaColors.SurfaceDark
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = type.icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = type.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) LunaColors.Gold else Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MoodChip(mood: Mood, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text = mood.displayName, fontSize = 12.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = LunaColors.Gold.copy(alpha = 0.2f),
            selectedLabelColor = LunaColors.Gold
        )
    )
}

@Composable
private fun FocusChip(focus: FocusArea, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text = focus.displayName, fontSize = 12.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = LunaColors.Gold.copy(alpha = 0.2f),
            selectedLabelColor = LunaColors.Gold
        )
    )
}
