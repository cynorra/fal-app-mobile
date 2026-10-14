package com.cynora.luna.feature.onboarding

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.cynora.luna.core.data.model.ZodiacSign
import com.cynora.luna.core.ui.theme.LunaColors
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // ✅ rememberSaveable - configuration change'te korunur
    var currentPage by rememberSaveable { mutableStateOf(0) }

    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(LunaColors.DeepBlack, Color(0xFF1A0A2E))
                )
            )
    ) {
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it } + fadeOut())
            },
            label = "onboarding_page"
        ) { page ->
            when (page) {
                0 -> WelcomePage(onNext = { currentPage = 1 })
                1 -> ZodiacPage(
                    selectedZodiac = state.selectedZodiac,
                    onZodiacSelected = { viewModel.onIntent(OnboardingIntent.SelectZodiac(it)) },
                    onNext = { currentPage = 2 }
                )
                2 -> PermissionPage(
                    onAllow = {
                        notificationPermission?.launchPermissionRequest()
                        currentPage = 3
                    },
                    onSkip = { currentPage = 3 }
                )
                3 -> FinalPage(
                    onFinish = {
                        viewModel.onIntent(OnboardingIntent.CompleteOnboarding)
                        onComplete()
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentPage) LunaColors.Gold
                            else Color.White.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Composable
private fun WelcomePage(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "✨", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Welcome to Luna",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your personal cosmic guide\nto the universe's whispers",
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LunaColors.Gold),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Begin Journey ✨", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
private fun ZodiacPage(
    selectedZodiac: ZodiacSign?,
    onZodiacSelected: (ZodiacSign) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "What's Your Sign?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Select your zodiac for personalized readings",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(ZodiacSign.entries.toList()) { zodiac ->
                ZodiacCard(zodiac, selectedZodiac == zodiac) { onZodiacSelected(zodiac) }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onNext,
            enabled = selectedZodiac != null,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LunaColors.Gold,
                disabledContainerColor = LunaColors.Gold.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
private fun ZodiacCard(zodiac: ZodiacSign, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.aspectRatio(1f).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LunaColors.Gold.copy(alpha = 0.2f) else LunaColors.SurfaceDark
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, LunaColors.Gold) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = zodiac.symbol, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = zodiac.displayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) LunaColors.Gold else Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PermissionPage(onAllow: () -> Unit, onSkip: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🔔", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Stay Connected", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Receive daily fortune readings\nand intention messages",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onAllow,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LunaColors.Gold),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Allow Notifications", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onSkip) {
            Text("Not Now", fontSize = 16.sp, color = Color.White.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun FinalPage(onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🌟", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "You're Ready!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Discover the universe's\npersonalized messages for you",
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LunaColors.Gold),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Start Exploring ✨", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}
