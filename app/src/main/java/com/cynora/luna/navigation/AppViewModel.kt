package com.cynora.luna.navigation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class AppState(
    val isOnboardingComplete: Boolean = false
)

sealed interface AppIntent {
    data object CompleteOnboarding : AppIntent
}

@HiltViewModel
class AppViewModel @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.isOnboardingComplete().collect { isComplete ->
                _state.update { it.copy(isOnboardingComplete = isComplete) }
            }
        }
    }

    fun onIntent(intent: AppIntent) {
        when (intent) {
            is AppIntent.CompleteOnboarding -> {
                viewModelScope.launch {
                    dataStore.setOnboardingComplete(true)
                    _state.update { it.copy(isOnboardingComplete = true) }
                }
            }
        }
    }
}
