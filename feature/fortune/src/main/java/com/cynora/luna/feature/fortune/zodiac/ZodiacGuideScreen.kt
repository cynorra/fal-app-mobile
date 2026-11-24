package com.cynora.luna.feature.fortune.zodiac

import com.cynora.luna.feature.fortune.R
import androidx.compose.ui.res.stringResource
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.cynora.luna.core.data.model.ZodiacDetail
import com.cynora.luna.core.data.model.ZodiacElement
import com.cynora.luna.core.ui.theme.LunaColors

@Composable
fun ZodiacGuideScreen(
    onBack: () -> Unit,
    viewModel: ZodiacViewModel = hiltViewModel()
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
                    contentDescription = stringResource(R.string.ui_fortune_back),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.ui_fortune_zg_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Element Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = state.selectedElementFilter == null,
                onClick = { viewModel.filterByElement(null) },
                label = { Text(stringResource(R.string.ui_fortune_zg_all_elements)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LunaColors.Gold,
                    selectedLabelColor = Color.Black,
                    containerColor = LunaColors.GlassFill,
                    labelColor = Color.White
                )
            )

            ZodiacElement.entries.forEach { element ->
                FilterChip(
                    selected = state.selectedElementFilter == element,
                    onClick = { viewModel.filterByElement(element) },
                    label = { Text("${element.emoji} ${element.displayName}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LunaColors.Gold,
                        selectedLabelColor = Color.Black,
                        containerColor = LunaColors.GlassFill,
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 12 Signs Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(state.details, key = { it.sign.name }) { detail ->
                ZodiacSignCard(
                    detail = detail,
                    onClick = { viewModel.selectSign(detail.sign) }
                )
            }
        }
    }

    // Sign Detail Dialog
    state.selectedSignDetail?.let { detail ->
        AlertDialog(
            onDismissRequest = { viewModel.selectSign(null) },
            containerColor = LunaColors.SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(detail.sign.symbol, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = detail.sign.displayName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = LunaColors.Gold
                        )
                        Text(
                            text = "${detail.sign.dateRange} • ${detail.element.displayName}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quick Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(stringResource(R.string.ui_fortune_zg_ruler), fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                            Text(detail.rulingPlanet, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                        Column {
                            Text(stringResource(R.string.ui_fortune_zg_modality), fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                            Text(detail.modality.displayName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                        Column {
                            Text(stringResource(R.string.ui_fortune_zg_tarot), fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                            Text(detail.tarotCard, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LunaColors.MysticPurple)
                        }
                    }

                    // Strengths & Weaknesses
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.04f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(stringResource(R.string.ui_fortune_zg_strengths, detail.strengths.joinToString(", ")), fontSize = 12.sp, color = Color(0xFF81C784))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(stringResource(R.string.ui_fortune_zg_shadow, detail.weaknesses.joinToString(", ")), fontSize = 12.sp, color = Color(0xFFFF8A80))
                        }
                    }

                    // Profiles
                    Text(stringResource(R.string.ui_fortune_zg_love_bonds), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
                    Text(detail.loveProfile, fontSize = 13.sp, lineHeight = 20.sp, color = Color.White.copy(alpha = 0.85f))

                    Text(stringResource(R.string.ui_fortune_zg_career), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
                    Text(detail.careerProfile, fontSize = 13.sp, lineHeight = 20.sp, color = Color.White.copy(alpha = 0.85f))

                    Text(stringResource(R.string.ui_fortune_zg_compatible), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        detail.compatibleSigns.forEach { comp ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LunaColors.Gold.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("${comp.symbol} ${comp.displayName}", fontSize = 11.sp, color = LunaColors.Gold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.selectSign(null) }) {
                    Text(stringResource(R.string.ui_fortune_close), color = LunaColors.Gold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun ZodiacSignCard(
    detail: ZodiacDetail,
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
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = detail.sign.symbol, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = detail.sign.displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = detail.sign.dateRange,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(detail.element.colorHex).copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${detail.element.emoji} ${detail.element.displayName}",
                    fontSize = 11.sp,
                    color = Color(detail.element.colorHex),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
