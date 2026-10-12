package com.cynora.luna.feature.onboarding

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.model.ZodiacSign
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class OnboardingState(
    val selectedZodiac: ZodiacSign? = null,
    val isComplete: Boolean = false
)

sealed interface OnboardingIntent {
    data class SelectZodiac(val zodiac: ZodiacSign) : OnboardingIntent
    data object CompleteOnboarding : OnboardingIntent
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.SelectZodiac -> {
                _state.update { it.copy(selectedZodiac = intent.zodiac) }
                viewModelScope.launch {
                    dataStore.saveUserZodiac(intent.zodiac.name)
                }
            }
            is OnboardingIntent.CompleteOnboarding -> {
                viewModelScope.launch {
                    dataStore.setOnboardingComplete(true)
                    _state.update { it.copy(isComplete = true) }
                }
            }
        }
    }
}
