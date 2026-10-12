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
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(
    onBack: () -> Unit,
    viewModel: CompatibilityViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zodiac Compatibility 💑") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LunaColors.DeepBlack,
                    titleContentColor = LunaColors.Gold
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LunaColors.DeepBlack)
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
                
                Button(
                    onClick = { viewModel.onIntent(CompatibilityIntent.Calculate) },
                    enabled = state.sign1 != null && state.sign2 != null,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LunaColors.Gold)
                ) {
                    Text("Calculate Compatibility 💕", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                CompatibilityResultView(state.result!!)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.onIntent(CompatibilityIntent.Reset) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LunaColors.Gold)
                ) {
                    Text("Try Another", color = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun ZodiacGrid(selected: ZodiacSign?, onSelect: (ZodiacSign) -> Unit) {
    // ✅ Sabit height - verticalScroll içinde crash önlendi
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxWidth().height(240.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ZodiacSign.entries.size) { index ->
            val sign = ZodiacSign.entries[index]
            Card(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable { onSelect(sign) }
                    .then(
                        if (selected == sign) Modifier.border(
                            2.dp, LunaColors.Gold, RoundedCornerShape(12.dp)
                        ) else Modifier
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected == sign) LunaColors.Gold.copy(alpha = 0.2f) else LunaColors.SurfaceDark
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(sign.symbol, fontSize = 24.sp)
                    Text(sign.displayName, fontSize = 10.sp, color = Color.White, textAlign = TextAlign.Center)
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
        colors = CardDefaults.cardColors(containerColor = LunaColors.SurfaceDark.copy(alpha = 0.8f)),
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
        colors = CardDefaults.cardColors(containerColor = LunaColors.SurfaceDark),
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
        colors = CardDefaults.cardColors(containerColor = LunaColors.SurfaceDark),
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
        colors = CardDefaults.cardColors(containerColor = LunaColors.SurfaceDark),
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
