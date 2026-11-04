package com.cynora.luna.feature.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.model.LunaUser
import com.cynora.luna.core.data.model.ZodiacSign
import com.cynora.luna.core.data.repository.AuthRepository
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class ProfileState(
    val zodiacName: String = "Aries",
    val zodiacSymbol: String = "♈",
    val fortuneCount: Int = 0,
    val streak: Int = 0,
    val rareCards: Int = 0,
    val notificationsEnabled: Boolean = false,
    val darkModeEnabled: Boolean = true,
    val language: String = "English",
    val showPermissionDialog: Boolean = false,
    val currentUser: LunaUser? = null,
    val isAuthLoading: Boolean = false,
    val authErrorMessage: String? = null
)

sealed interface ProfileIntent {
    data class ToggleNotifications(val enabled: Boolean) : ProfileIntent
    data class ToggleDarkMode(val enabled: Boolean) : ProfileIntent
    data class ChangeZodiac(val zodiac: ZodiacSign) : ProfileIntent
    data class ChangeLanguage(val displayName: String, val code: String) : ProfileIntent
    data object DismissPermissionDialog : ProfileIntent
    data class SignInWithGoogleToken(val idToken: String) : ProfileIntent
    data object SignOut : ProfileIntent
    data class SetAuthError(val error: String) : ProfileIntent
}

sealed interface ProfileEffect {
    data object RequestPermission : ProfileEffect
    data object OpenSettings : ProfileEffect
    data class ShowSnackbar(val message: String) : ProfileEffect
}

val SupportedLanguages: List<Pair<String, String>> = listOf(
    "English" to "en",
    "Türkçe" to "tr",
    "Español" to "es",
    "العربية" to "ar",
    "Deutsch" to "de",
    "Français" to "fr",
    "Português" to "pt",
    "日本語" to "ja"
)

private fun currentLanguageDisplayName(): String {
    val tag = AppCompatDelegate.getApplicationLocales().get(0)?.language
        ?: java.util.Locale.getDefault().language
    return SupportedLanguages.firstOrNull { it.second == tag }?.first ?: "English"
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: UserPreferencesDataStore,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState(language = currentLanguageDisplayName()))
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    // ✅ Buffer eklendi
    private val _effects = MutableSharedFlow<ProfileEffect>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effects: SharedFlow<ProfileEffect> = _effects.asSharedFlow()

    init {
        // Oturum durumunu dinle
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _state.update { it.copy(currentUser = user, isAuthLoading = false) }
            }
        }

        viewModelScope.launch {
            dataStore.getUserZodiac().collect { zodiacName ->
                zodiacName?.let {
                    val zodiac = ZodiacSign.entries.find { z -> z.name.equals(it, ignoreCase = true) } ?: ZodiacSign.ARIES
                    _state.update { s -> s.copy(zodiacName = zodiac.displayName, zodiacSymbol = zodiac.symbol) }
                }
            }
        }

        viewModelScope.launch {
            dataStore.getFortuneCount().collect { count ->
                _state.update { 
                    it.copy(
                        fortuneCount = count,
                        rareCards = count / 50
                    ) 
                }
            }
        }

        viewModelScope.launch {
            dataStore.getStreakCount().collect { streak ->
                _state.update { it.copy(streak = streak) }
            }
        }

        viewModelScope.launch {
            dataStore.areNotificationsEnabled().collect { enabled ->
                _state.update { it.copy(notificationsEnabled = enabled) }
            }
        }

        viewModelScope.launch {
            dataStore.isDarkModeEnabled().collect { enabled ->
                _state.update { it.copy(darkModeEnabled = enabled) }
            }
        }
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.ToggleNotifications -> {
                if (intent.enabled) {
                    if (hasNotificationPermission()) {
                        viewModelScope.launch {
                            dataStore.setNotificationsEnabled(true)
                            _state.update { it.copy(notificationsEnabled = true) }
                        }
                    } else {
                        _state.update { it.copy(showPermissionDialog = true) }
                    }
                } else {
                    viewModelScope.launch {
                        dataStore.setNotificationsEnabled(false)
                        _state.update { it.copy(notificationsEnabled = false) }
                    }
                }
            }
            is ProfileIntent.ToggleDarkMode -> {
                viewModelScope.launch {
                    dataStore.setDarkModeEnabled(intent.enabled)
                    _state.update { it.copy(darkModeEnabled = intent.enabled) }
                }
            }
            is ProfileIntent.ChangeZodiac -> {
                viewModelScope.launch {
                    dataStore.saveUserZodiac(intent.zodiac.name)
                    _state.update { 
                        it.copy(zodiacName = intent.zodiac.displayName, zodiacSymbol = intent.zodiac.symbol) 
                    }
                }
            }
            is ProfileIntent.ChangeLanguage -> {
                _state.update { it.copy(language = intent.displayName) }
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(intent.code))
            }
            is ProfileIntent.DismissPermissionDialog -> {
                _state.update { it.copy(showPermissionDialog = false) }
            }
            is ProfileIntent.SignInWithGoogleToken -> {
                viewModelScope.launch {
                    _state.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
                    val result = authRepository.signInWithGoogle(intent.idToken)
                    result.fold(
                        onSuccess = { user ->
                            _state.update { it.copy(isAuthLoading = false, currentUser = user) }
                            _effects.tryEmit(ProfileEffect.ShowSnackbar("Welcome ${user.displayName ?: ""}! ✨"))
                        },
                        onFailure = { error ->
                            _state.update { it.copy(isAuthLoading = false, authErrorMessage = error.message) }
                            _effects.tryEmit(ProfileEffect.ShowSnackbar("Sign in failed: ${error.localizedMessage}"))
                        }
                    )
                }
            }
            is ProfileIntent.SignOut -> {
                viewModelScope.launch {
                    authRepository.signOut()
                    _effects.tryEmit(ProfileEffect.ShowSnackbar("Signed out successfully."))
                }
            }
            is ProfileIntent.SetAuthError -> {
                _state.update { it.copy(isAuthLoading = false, authErrorMessage = intent.error) }
                _effects.tryEmit(ProfileEffect.ShowSnackbar(intent.error))
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            if (granted) {
                dataStore.setNotificationsEnabled(true)
                _state.update { it.copy(notificationsEnabled = true, showPermissionDialog = false) }
            } else {
                _state.update { it.copy(showPermissionDialog = false) }
                _effects.tryEmit(ProfileEffect.OpenSettings)
            }
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }
}
