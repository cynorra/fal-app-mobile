package com.cynora.luna.feature.onboarding

import androidx.compose.ui.res.stringResource
import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.data.model.ZodiacSign
import com.cynora.luna.core.ui.modern.CascadeText
import com.cynora.luna.core.ui.modern.DotMatrixLoader
import com.cynora.luna.core.ui.modern.GlassCard
import com.cynora.luna.core.ui.modern.KineticReveal
import com.cynora.luna.core.ui.modern.LunaPrimaryButton
import com.cynora.luna.core.ui.theme.LunaColors
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

private val PageTitleStyle = TextStyle(
    fontSize = 30.sp,
    fontWeight = FontWeight.Bold,
    color = Color.White,
    textAlign = TextAlign.Center
)

private val PageBodyStyle = TextStyle(
    fontSize = 17.sp,
    color = Color.White.copy(alpha = 0.72f),
    textAlign = TextAlign.Center,
    lineHeight = 26.sp
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // rememberSaveable keeps the page across configuration changes
    var currentPage by rememberSaveable { mutableStateOf(0) }

    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    // The animated LunaBackdrop is drawn by the navigation host behind this screen.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Content is capped in width so buttons and cards do not stretch across tablets,
        // and the bottom is reserved for the page indicator so nothing overlaps it.
        Box(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .padding(bottom = 72.dp)
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
        }

        PageIndicator(
            count = 4,
            current = currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun PageIndicator(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { index ->
            val selected = index == current
            val width by animateDpAsState(
                targetValue = if (selected) 28.dp else 8.dp,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                label = "indicatorWidth"
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            Brush.horizontalGradient(listOf(Color(0xFFF3D77E), LunaColors.Gold))
                        } else {
                            Brush.horizontalGradient(
                                listOf(Color.White.copy(alpha = 0.28f), Color.White.copy(alpha = 0.28f))
                            )
                        }
                    )
            )
        }
    }
}

@Composable
private fun PageScaffold(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        content()
    }
}

@Composable
private fun WelcomePage(onNext: () -> Unit) {
    PageScaffold {
        DotMatrixLoader(loaderSize = 96.dp)
        Spacer(modifier = Modifier.height(28.dp))
        CascadeText(text = stringResource(R.string.ui_onb_welcome_title), style = PageTitleStyle)
        Spacer(modifier = Modifier.height(14.dp))
        KineticReveal(
            text = stringResource(R.string.ui_onb_welcome_body),
            style = PageBodyStyle,
            centered = true
        )
        Spacer(modifier = Modifier.height(40.dp))
        LunaPrimaryButton(text = stringResource(R.string.ui_onb_begin), onClick = onNext, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ZodiacPage(
    selectedZodiac: ZodiacSign?,
    onZodiacSelected: (ZodiacSign) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CascadeText(text = stringResource(R.string.ui_onb_sign_title), style = PageTitleStyle.copy(fontSize = 26.sp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.ui_onb_sign_body),
            style = PageBodyStyle.copy(fontSize = 15.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 74.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(ZodiacSign.entries.toList()) { zodiac ->
                ZodiacTile(zodiac, selectedZodiac == zodiac) { onZodiacSelected(zodiac) }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LunaPrimaryButton(
            text = stringResource(R.string.ui_onb_continue),
            onClick = onNext,
            enabled = selectedZodiac != null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ZodiacTile(zodiac: ZodiacSign, isSelected: Boolean, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.aspectRatio(1f),
        selected = isSelected,
        cornerRadius = 16.dp,
        contentPadding = 6.dp,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = zodiac.symbol, fontSize = 28.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = zodiac.displayName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) LunaColors.Gold else Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun PermissionPage(onAllow: () -> Unit, onSkip: () -> Unit) {
    PageScaffold {
        Text(text = "🔔", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(24.dp))
        CascadeText(text = stringResource(R.string.ui_onb_notif_title), style = PageTitleStyle)
        Spacer(modifier = Modifier.height(14.dp))
        KineticReveal(
            text = stringResource(R.string.ui_onb_notif_body),
            style = PageBodyStyle,
            centered = true
        )
        Spacer(modifier = Modifier.height(36.dp))
        LunaPrimaryButton(text = stringResource(R.string.ui_onb_allow), onClick = onAllow, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onSkip) {
            Text(stringResource(R.string.ui_onb_not_now), fontSize = 16.sp, color = Color.White.copy(alpha = 0.65f))
        }
    }
}

@Composable
private fun FinalPage(onFinish: () -> Unit) {
    PageScaffold {
        Text(text = "🌟", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(24.dp))
        CascadeText(text = stringResource(R.string.ui_onb_ready_title), style = PageTitleStyle)
        Spacer(modifier = Modifier.height(14.dp))
        KineticReveal(
            text = stringResource(R.string.ui_onb_ready_body),
            style = PageBodyStyle,
            centered = true
        )
        Spacer(modifier = Modifier.height(36.dp))
        LunaPrimaryButton(text = stringResource(R.string.ui_onb_start), onClick = onFinish, modifier = Modifier.fillMaxWidth())
    }
}
