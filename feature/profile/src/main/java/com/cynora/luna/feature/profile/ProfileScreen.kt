package com.cynora.luna.feature.profile

import android.Manifest
import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cynora.luna.core.ui.theme.LunaColors
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

// The language picker is hidden until the UI strings are translated; switching languages
// currently changes nothing visible because most screens still use English text.
private const val LANGUAGE_SWITCHER_ENABLED = false

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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
                is ProfileEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        } catch (e: Exception) {
            ""
        }
    }

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
                    Text("Allow", color = LunaColors.Gold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(ProfileIntent.DismissPermissionDialog) }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.6f))
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
                    SupportedLanguages.forEach { (displayName, code) ->
                        TextButton(onClick = {
                            viewModel.onIntent(ProfileIntent.ChangeLanguage(displayName, code))
                            showLanguageDialog = false
                        }) {
                            Text(displayName, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Cancel", color = LunaColors.Gold)
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Luna ✨") },
            text = {
                Column {
                    Text(
                        text = "Luna v$versionName",
                        fontWeight = FontWeight.Bold,
                        color = LunaColors.Gold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your personal cosmic guide for tarot, astrology, birth charts, daily card readings, and angel numbers.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "© 2026 Cynora Studio. All rights reserved.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = LunaColors.Gold)
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete account and data?") },
            text = {
                Text(
                    "This permanently deletes your community posts and your Luna account, and clears " +
                        "everything stored on this device (preferences, history and streak). " +
                        "This cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.onIntent(ProfileIntent.DeleteAccount)
                    },
                    enabled = !state.isDeletingAccount
                ) {
                    Text("Delete", color = Color(0xFFEF5350))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = LunaColors.DeepBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LunaColors.DeepBlack)
                .verticalScroll(rememberScrollState())
        ) {
            // Profil ve Burç Başlığı
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat("Fortunes Read", state.fortuneCount.toString())
                        ProfileStat("Daily Streak", "${state.streak} days")
                        ProfileStat("Rare Cards", state.rareCards.toString())
                    }
                }
            }

            // Google Hesabı Kartı
            Text(
                text = "Account",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = LunaColors.Gold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = LunaColors.SurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val user = state.currentUser
                    if (user != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (!user.photoUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = user.photoUrl,
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(LunaColors.Gold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = LunaColors.Gold,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.displayName ?: "Cosmic Traveler",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = user.email ?: "",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }

                            TextButton(
                                onClick = { viewModel.onIntent(ProfileIntent.SignOut) }
                            ) {
                                Text("Sign Out", color = LunaColors.Gold, fontSize = 13.sp)
                            }
                        }
                    } else {
                        Column {
                            Text(
                                text = "Save & Sync Your Cosmic Journey",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sign in with Google to sync your fortune readings, streaks, and preferences across devices.",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            if (state.isAuthLoading) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = LunaColors.Gold,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        triggerGoogleSignIn(
                                            context = context,
                                            coroutineScope = coroutineScope,
                                            onIdTokenReceived = { idToken ->
                                                viewModel.onIntent(ProfileIntent.SignInWithGoogleToken(idToken))
                                            },
                                            onError = { errorMsg ->
                                                viewModel.onIntent(ProfileIntent.SetAuthError(errorMsg))
                                            }
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LunaColors.Gold
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text(
                                        text = "G  Sign in with Google",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ayarlar Bölümü
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
                    title = "Daily Notifications (08:30)",
                    isChecked = state.notificationsEnabled,
                    onCheckedChange = { viewModel.onIntent(ProfileIntent.ToggleNotifications(it)) }
                )

                SettingsToggleItem(
                    icon = Icons.Default.Star,
                    title = "Hourly Cosmic Pulse (09:00 - 22:00)",
                    isChecked = state.hourlyNotificationsEnabled,
                    onCheckedChange = { viewModel.onIntent(ProfileIntent.ToggleHourlyNotifications(it)) }
                )

                SettingsToggleItem(
                    icon = Icons.Default.Lock,
                    title = "Quiet Hours (Mute 22:00 - 09:00)",
                    isChecked = state.quietHoursEnabled,
                    onCheckedChange = { viewModel.onIntent(ProfileIntent.ToggleQuietHours(it)) }
                )

                SettingsToggleItem(
                    icon = Icons.Default.Settings,
                    title = "Dark Mode",
                    isChecked = state.darkModeEnabled,
                    onCheckedChange = { viewModel.onIntent(ProfileIntent.ToggleDarkMode(it)) }
                )

                if (LANGUAGE_SWITCHER_ENABLED) {
                    SettingsClickItem(
                        icon = Icons.Default.Info,
                        title = "Language",
                        subtitle = state.language,
                        onClick = { showLanguageDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hakkında Bölümü
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
                    icon = Icons.Default.Lock,
                    title = "Privacy Policy",
                    onClick = {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://cynorra.github.io/luna/privacy-policy.html")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Timber.e(e, "Could not open privacy policy URL")
                        }
                    }
                )

                SettingsClickItem(
                    icon = Icons.Default.Delete,
                    title = "Delete account and data",
                    subtitle = if (state.isDeletingAccount) "Deleting..." else null,
                    onClick = { showDeleteDialog = true }
                )

                SettingsClickItem(
                    icon = Icons.Default.Info,
                    title = "About App",
                    onClick = { showAboutDialog = true }
                )

                SettingsInfoItem(
                    icon = Icons.Default.Star,
                    title = "Version",
                    subtitle = versionName
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun triggerGoogleSignIn(
    context: Context,
    coroutineScope: CoroutineScope,
    onIdTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
) {
    val credentialManager = CredentialManager.create(context)

    // Firebase google-services tarafından üretilen default_web_client_id aranır
    val webClientIdResId = context.resources.getIdentifier(
        "default_web_client_id",
        "string",
        context.packageName
    )

    if (webClientIdResId == 0) {
        onError("Google Client ID not found. Please ensure google-services.json is configured.")
        return
    }

    val serverClientId = context.getString(webClientIdResId)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(serverClientId)
        .setAutoSelectEnabled(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    coroutineScope.launch {
        try {
            val response: GetCredentialResponse = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                onIdTokenReceived(googleIdTokenCredential.idToken)
            } else {
                onError("Unexpected credential format received.")
            }
        } catch (e: GetCredentialCancellationException) {
            // Kullanıcı seçimi iptal etti
            Timber.d("Google Sign-In was cancelled by user")
        } catch (e: GetCredentialException) {
            Timber.e(e, "Google Sign-In Credential error")
            onError(e.message ?: "Sign-in error")
        } catch (e: Exception) {
            Timber.e(e, "Google Sign-In failed")
            onError(e.message ?: "Sign-in error")
        }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(16.dp),
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = LunaColors.Gold, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp, color = Color.White, modifier = Modifier.weight(1f))
        Text(text = subtitle, fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f))
    }
}
