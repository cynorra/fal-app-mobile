package com.cynora.luna.feature.fortune.birthchart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.cynora.luna.core.data.repository.BirthChartData
import com.cynora.luna.core.ui.theme.LunaColors
import com.cynora.luna.core.ui.modern.LunaPrimaryButton
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthChartScreen(
    onBack: () -> Unit,
    viewModel: BirthChartViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Birth Chart 🌌") },
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
                .padding(16.dp)
        ) {
            if (state.birthInfo == null) {
                BirthInfoForm { date, time, place ->
                    viewModel.onIntent(BirthChartIntent.SubmitBirthInfo(date, time, place))
                }
            } else if (state.birthChart != null) {
                BirthChartResult(state.birthChart!!)
                Spacer(modifier = Modifier.height(16.dp))
                val shareContext = androidx.compose.ui.platform.LocalContext.current
                LunaPrimaryButton(
                    text = "Share my Big Three",
                    onClick = {
                        val chart = state.birthChart!!
                        com.cynora.luna.core.utils.ShareUtils.shareCard(
                            context = shareContext,
                            title = "My Big Three",
                            body = "Sun: ${chart.sunSign.symbol} ${chart.sunSign.displayName}\n" +
                                "Moon: ${chart.moonSign.symbol} ${chart.moonSign.displayName}\n" +
                                "Rising: ${chart.risingSign.symbol} ${chart.risingSign.displayName}\n\n" +
                                chart.personality,
                            footer = "What is yours?",
                            medium = "big_three"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                LunaPrimaryButton(
                    text = "Calculate Another",
                    onClick = { viewModel.onIntent(BirthChartIntent.Reset) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthInfoForm(
    onSubmit: (LocalDate, LocalTime?, String) -> Unit
) {
    var year by remember { mutableStateOf("1990") }
    var month by remember { mutableStateOf("1") }
    var day by remember { mutableStateOf("1") }
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🌌 Your Birth Chart",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = LunaColors.Gold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Discover the cosmic blueprint of your soul",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Birth Date", color = LunaColors.Gold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = day, onValueChange = { day = it },
                    label = { Text("Day") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent
                    )
                )
                OutlinedTextField(
                    value = month, onValueChange = { month = it },
                    label = { Text("Month") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent
                    )
                )
                OutlinedTextField(
                    value = year, onValueChange = { year = it },
                    label = { Text("Year") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Birth Time (optional)", color = LunaColors.Gold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = hour, onValueChange = { hour = it },
                    label = { Text("Hour") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent
                    )
                )
                OutlinedTextField(
                    value = minute, onValueChange = { minute = it },
                    label = { Text("Minute") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Birth Place (optional)", color = LunaColors.Gold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = place, onValueChange = { place = it },
                label = { Text("City, Country") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    try {
                        val birthDate = LocalDate(year.toInt(), month.toInt(), day.toInt())
                        val birthTime = if (hour.isNotBlank() && minute.isNotBlank()) {
                            LocalTime(hour.toInt(), minute.toInt())
                        } else null
                        onSubmit(birthDate, birthTime, place)
                    } catch (e: Exception) {
                        // Invalid date - kullanıcıya gösterilebilir
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LunaColors.Gold),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Reveal My Chart ✨", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BirthChartResult(birthChart: BirthChartData) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(LunaColors.Gold, LunaColors.MysticPurple, LunaColors.Gold)),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Your Big Three", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                BigThreeItem("☀️ Sun", birthChart.sunSign.symbol + " " + birthChart.sunSign.displayName)
                BigThreeItem("🌙 Moon", birthChart.moonSign.symbol + " " + birthChart.moonSign.displayName)
                BigThreeItem("⬆️ Rising", birthChart.risingSign.symbol + " " + birthChart.risingSign.displayName)
            }
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
            Text("Planets", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
            Spacer(modifier = Modifier.height(12.dp))
            PlanetRow("☿ Mercury", birthChart.mercurySign.symbol + " " + birthChart.mercurySign.displayName)
            PlanetRow("♀ Venus", birthChart.venusSign.symbol + " " + birthChart.venusSign.displayName)
            PlanetRow("♂ Mars", birthChart.marsSign.symbol + " " + birthChart.marsSign.displayName)
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
            Text("Additional Insights", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow("🔢 Life Path", birthChart.numerologyLifePath.toString())
            InfoRow("🐉 Chinese Zodiac", birthChart.chineseZodiac)
            InfoRow("🎴 Birth Card", birthChart.birthCard)
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
            Text("✨ Your Personality", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(birthChart.personality, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f), lineHeight = 22.sp)
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💪 Strengths", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
                Spacer(modifier = Modifier.height(8.dp))
                birthChart.strengths.forEach { Text("• $it", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f)) }
            }
        }
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🌱 Growth", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
                Spacer(modifier = Modifier.height(8.dp))
                birthChart.challenges.forEach { Text("• $it", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f)) }
            }
        }
    }
}

@Composable
private fun BigThreeItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun PlanetRow(planet: String, sign: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(planet, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
        Text(sign, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = LunaColors.Gold)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = LunaColors.Gold)
    }
}
