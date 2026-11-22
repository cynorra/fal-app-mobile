package com.cynora.luna.feature.fortune.angelnumbers

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.data.repository.AngelNumber
import com.cynora.luna.core.ui.theme.LunaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AngelNumbersScreen(
    onBack: () -> Unit,
    viewModel: AngelNumbersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Angel Numbers 🔮") },
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
                
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onIntent(AngelNumbersIntent.Search(it)) },
                placeholder = { Text("Search (e.g. 111, 222...)") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = LunaColors.Gold) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = LunaColors.SurfaceDark,
                    unfocusedContainerColor = LunaColors.SurfaceDark
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (state.selectedNumber != null) {
                AngelNumberDetail(state.selectedNumber!!) {
                    viewModel.onIntent(AngelNumbersIntent.ClearSelection)
                }
            } else {
                // ✅ key eklendi - performans
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(viewModel.getFilteredNumbers(), key = { it.number }) { number ->
                        AngelNumberCard(number) {
                            viewModel.onIntent(AngelNumbersIntent.SelectNumber(number))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AngelNumberCard(number: AngelNumber, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                number.number,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = LunaColors.Gold,
                modifier = Modifier.width(80.dp)
            )
            Text(
                number.meaning.take(80) + "...",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AngelNumberDetail(number: AngelNumber, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TextButton(onClick = onBack) {
            Text("← Back to list", color = LunaColors.Gold)
        }
        
        Text(
            number.number,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = LunaColors.Gold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DetailCard("✨ Meaning", number.meaning)
        DetailCard("💕 Love", number.loveMessage)
        DetailCard("💼 Career", number.careerMessage)
        DetailCard("🙏 Spiritual", number.spiritualMessage)
        DetailCard("🎯 Action", number.action)
    }
}

@Composable
private fun DetailCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = LunaColors.Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(content, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, lineHeight = 22.sp)
        }
    }
}
