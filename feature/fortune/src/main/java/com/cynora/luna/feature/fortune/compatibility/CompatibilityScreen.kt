package com.cynora.luna.feature.fortune.compatibility

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.data.model.ZodiacSign
import com.cynora.luna.core.data.repository.CompatibilityResult
import com.cynora.luna.core.ui.theme.LunaColors
import com.cynora.luna.core.ui.modern.LunaPrimaryButton
import com.cynora.luna.core.ui.modern.GlassCard
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(
    onBack: () -> Unit,
    viewModel: CompatibilityViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Zodiac Compatibility 💑") },
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
            if (state.result == null) {
                Text("Your Sign", color = LunaColors.Gold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                ZodiacGrid(selected = state.sign1) { viewModel.onIntent(CompatibilityIntent.SelectSign1(it)) }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Partner's Sign", color = LunaColors.Gold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                ZodiacGrid(selected = state.sign2) { viewModel.onIntent(CompatibilityIntent.SelectSign2(it)) }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                LunaPrimaryButton(
                    text = "Calculate Compatibility 💕",
                    onClick = { viewModel.onIntent(CompatibilityIntent.Calculate) },
                    enabled = state.sign1 != null && state.sign2 != null,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            } else {
                CompatibilityResultView(state.result!!)
                Spacer(modifier = Modifier.height(16.dp))
                val shareContext = androidx.compose.ui.platform.LocalContext.current
                LunaPrimaryButton(
                    text = "Share with your partner",
                    onClick = {
                        val res = state.result!!
                        val a = state.sign1?.displayName ?: ""
                        val b = state.sign2?.displayName ?: ""
                        com.cynora.luna.core.utils.ShareUtils.shareCard(
                            context = shareContext,
                            title = "$a + $b: ${res.overallScore}% compatible",
                            body = "Love ${res.loveScore}%  |  Friendship ${res.friendshipScore}%  |  Trust ${res.trustScore}%\n\n${res.advice}",
                            footer = "How compatible are you two?",
                            medium = "compatibility"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                LunaPrimaryButton(
                    text = "Try Another",
                    onClick = { viewModel.onIntent(CompatibilityIntent.Reset) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ZodiacGrid(selected: ZodiacSign?, onSelect: (ZodiacSign) -> Unit) {
    // Fixed-size glass tiles in a flowing layout: compact on tablets, no nested scrolling
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ZodiacSign.entries.forEach { sign ->
            GlassCard(
                modifier = Modifier.size(76.dp),
                selected = selected == sign,
                cornerRadius = 14.dp,
                contentPadding = 4.dp,
                onClick = { onSelect(sign) }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(sign.symbol, fontSize = 24.sp)
                        Text(
                            sign.displayName,
                            fontSize = 10.sp,
                            color = if (selected == sign) LunaColors.Gold else Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompatibilityResultView(result: CompatibilityResult) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .border(1.dp, Brush.linearGradient(listOf(LunaColors.Gold, LunaColors.MysticPurple)), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Overall Compatibility", color = LunaColors.Gold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${result.overallScore}%", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(result.element, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            ScoreRow("💕 Love", result.loveScore)
            ScoreRow("🤝 Friendship", result.friendshipScore)
            ScoreRow("💬 Communication", result.communicationScore)
            ScoreRow("🤞 Trust", result.trustScore)
            ScoreRow("🔥 Passion", result.passionScore)
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("💪 Strengths", color = LunaColors.Gold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            result.strengths.forEach { Text("• $it", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp) }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("🌱 Challenges", color = LunaColors.Gold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            result.challenges.forEach { Text("• $it", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp) }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LunaColors.Gold.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("💡 Advice", color = LunaColors.Gold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(result.advice, color = Color.White, fontSize = 14.sp, lineHeight = 22.sp)
        }
    }
}

@Composable
private fun ScoreRow(label: String, score: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier.width(80.dp).height(6.dp),
                color = LunaColors.Gold,
                trackColor = LunaColors.SurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("$score%", color = LunaColors.Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
