package com.cynora.luna.feature.profile

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.ui.theme.LunaColors
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) { granted ->
            viewModel.onPermissionResult(granted)
        }
    } else null
    
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ProfileEffect.RequestPermission -> {
                    notificationPermission?.launchPermissionRequest()
                }
                is ProfileEffect.OpenSettings -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
    
    // Dil seçimi dialog
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    if (state.showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(ProfileIntent.DismissPermissionDialog) },
            title = { Text("Notification Permission Required") },
            text = { Text("Daily fortune and intention messages require notification permission. You can allow it from settings.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onIntent(ProfileIntent.DismissPermissionDialog)
                    notificationPermission?.launchPermissionRequest()
                }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(ProfileIntent.DismissPermissionDialog) }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
            text = {
                Column {
                    listOf(
                        "English" to "en",
                        "Türkçe" to "tr",
                        "Español" to "es",
                        "العربية" to "ar",
                        "Deutsch" to "de",
                        "Français" to "fr",
                        "Português" to "pt",
                        "日本語" to "ja"
                    ).forEach { (displayName, code) ->
                        TextButton(onClick = {
                            viewModel.onIntent(ProfileIntent.ChangeLanguage(displayName))
                            showLanguageDialog = false
                            // TODO: AppCompatDelegate.setApplicationLocales()
                        }) {
                            Text(displayName, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LunaColors.DeepBlack)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(LunaColors.Gold, LunaColors.MysticPurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.zodiacSymbol, fontSize = 36.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = state.zodiacName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProfileStat("Fortunes Read", state.fortuneCount.toString())
                    ProfileStat("Daily Streak", "${state.streak} days")
                    ProfileStat("Rare Cards", state.rareCards.toString())
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = LunaColors.Gold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsSection {
            SettingsToggleItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                isChecked = state.notificationsEnabled,
                onCheckedChange = { viewModel.onIntent(ProfileIntent.ToggleNotifications(it)) }
            )

            SettingsToggleItem(
                // ✅ Icons.Default.DarkMode YOK → Settings kullan
                icon = Icons.Default.Settings,
                title = "Dark Mode",
                isChecked = state.darkModeEnabled,
                onCheckedChange = { viewModel.onIntent(ProfileIntent.ToggleDarkMode(it)) }
            )

            SettingsClickItem(
                // ✅ Icons.Default.Language YOK → Info kullan
                icon = Icons.Default.Info,
                title = "Language",
                subtitle = state.language,
                onClick = { showLanguageDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "About",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = LunaColors.Gold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsSection {
            SettingsClickItem(
                // ✅ Icons.Default.Policy YOK → Lock kullan
                icon = Icons.Default.Lock,
                title = "Privacy Policy",
                onClick = { /* URL aç */ }
            )

            SettingsClickItem(
                icon = Icons.Default.Info,
                title = "About App",
                onClick = { /* Dialog */ }
            )

            SettingsInfoItem(
                // ✅ Icons.Default.Tag YOK → Star kullan
                icon = Icons.Default.Star,
                title = "Version",
                subtitle = "1.0.0"
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LunaColors.Gold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
    }
}

@Composable
private fun SettingsSection(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = LunaColors.SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!isChecked) }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = LunaColors.Gold, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp, color = Color.White, modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = LunaColors.Gold,
                checkedTrackColor = LunaColors.Gold.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = LunaColors.Gold, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = Color.White)
            subtitle?.let {
                Text(text = it, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
            }
        }
        // ✅ Icons.Default.ChevronRight YOK → KeyboardArrowRight kullan
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = LunaColors.Gold, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp, color = Color.White, modifier = Modifier.weight(1f))
        Text(text = subtitle, fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f))
    }
}
