package com.cynora.luna.feature.fortune.dreams

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.data.model.DreamCategory
import com.cynora.luna.core.data.model.DreamSymbol
import com.cynora.luna.core.ui.theme.LunaColors

@Composable
fun DreamInterpreterScreen(
    onBack: () -> Unit,
    viewModel: DreamInterpreterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

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
                text = "Dream Interpreter 💭",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Input
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            placeholder = {
                Text("Search symbols (e.g. water, snake, teeth...)", color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = LunaColors.Gold)
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.6f))
                    }
                }
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

        Spacer(modifier = Modifier.height(14.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = state.selectedCategory == null,
                onClick = { viewModel.onCategorySelected(null) },
                label = { Text("All Symbols") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LunaColors.Gold,
                    selectedLabelColor = Color.Black,
                    containerColor = LunaColors.SurfaceDark,
                    labelColor = Color.White
                )
            )

            DreamCategory.entries.forEach { category ->
                FilterChip(
                    selected = state.selectedCategory == category,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = { Text("${category.icon} ${category.displayName}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LunaColors.Gold,
                        selectedLabelColor = Color.Black,
                        containerColor = LunaColors.SurfaceDark,
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Symbols List
        if (state.symbols.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No dream symbols found matching \"${state.searchQuery}\"",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(state.symbols, key = { it.id }) { symbol ->
                    DreamSymbolCard(
                        symbol = symbol,
                        onClick = { viewModel.selectSymbol(symbol) }
                    )
                }
            }
        }
    }

    // Detail Dialog
    state.selectedSymbolForDetail?.let { symbol ->
        AlertDialog(
            onDismissRequest = { viewModel.selectSymbol(null) },
            containerColor = LunaColors.SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(symbol.icon, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${symbol.keyword} (${symbol.turkishKeyword})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaColors.Gold
                        )
                        Text(
                            text = symbol.category.displayName,
                            fontSize = 12.sp,
                            color = LunaColors.MysticPurple
                        )
                    }
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = symbol.detailedInterpretation,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "✨ Cosmic Guidance:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LunaColors.Gold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = symbol.cosmicAdvice,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.selectSymbol(null) }) {
                    Text("Close", color = LunaColors.Gold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun DreamSymbolCard(
    symbol: DreamSymbol,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LunaColors.GlassFill),
        border = LunaColors.GlassBorder
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = symbol.icon, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = symbol.keyword,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${symbol.turkishKeyword}",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = symbol.shortSummary,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 2
                )
            }
        }
    }
}
