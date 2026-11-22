package com.cynora.luna.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import kotlin.math.abs
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cynora.luna.core.ui.theme.LunaColors

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun LunaBottomNavigation(
    items: List<BottomNavItem>,
    selectedRoute: String?,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = items.indexOfFirst { it.route == selectedRoute }
    val shape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating glass dock (Cult UI "MacOS Dock" idea): the selected item and its neighbours magnify.
        Row(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xE61C1635), Color(0xF20B0919))
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.22f),
                            LunaColors.Gold.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.06f)
                        )
                    ),
                    shape = shape
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                BottomNavItemView(
                    item = item,
                    isSelected = index == selectedIndex,
                    distance = if (selectedIndex < 0) 99 else abs(index - selectedIndex),
                    onClick = { onItemSelected(item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    distance: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isSelected -> 1.3f
            distance == 1 -> 1.1f
            else -> 1.0f
        },
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f),
        label = "scale"
    )

    val tint by animateColorAsState(
        targetValue = if (isSelected) LunaColors.Gold else Color.White.copy(alpha = 0.6f),
        label = "tint"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = LunaColors.Gold.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(LunaColors.Gold.copy(alpha = 0.28f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
            }
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = tint,
                modifier = Modifier.size(24.dp).scale(scale)
            )
        }

        Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = tint
        )
    }
}
