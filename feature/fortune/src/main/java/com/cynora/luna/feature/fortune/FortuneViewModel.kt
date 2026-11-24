package com.cynora.luna.feature.fortune

import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.model.FocusArea
import com.cynora.luna.core.data.model.Fortune
import com.cynora.luna.core.data.model.FortuneType
import com.cynora.luna.core.data.model.Mood
import com.cynora.luna.core.data.repository.FortuneRepository
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@Immutable
data class FortuneState(
    val isLoading: Boolean = false,
    val fortune: Fortune? = null,
    val selectedType: FortuneType = FortuneType.COFFEE,
    val selectedMood: Mood = Mood.HOPEFUL,
    val selectedFocus: FocusArea = FocusArea.GENERAL,
    val error: String? = null,
    val isTypewriterComplete: Boolean = false
)

sealed interface FortuneIntent {
    data class SelectType(val type: FortuneType) : FortuneIntent
    data class SelectMood(val mood: Mood) : FortuneIntent
    data class SelectFocus(val focus: FocusArea) : FortuneIntent
    data object GetFortune : FortuneIntent
    data object Retry : FortuneIntent
    data object MarkAsSeen : FortuneIntent
    data object OnTypewriterComplete : FortuneIntent
}

sealed interface FortuneEffect {
    data class ShowSnackbar(val message: String) : FortuneEffect
}

@HiltViewModel
class FortuneViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: FortuneRepository,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(FortuneState())
    val state: StateFlow<FortuneState> = _state.asStateFlow()

    // ✅ Buffer eklendi - effect kaybı önlendi
    private val _effects = MutableSharedFlow<FortuneEffect>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effects: SharedFlow<FortuneEffect> = _effects.asSharedFlow()

    private val _seenIds = MutableStateFlow<Set<String>>(emptySet())

    init {
        viewModelScope.launch {
            repository.observeSeenIds().collect { ids ->
                _seenIds.value = ids
            }
        }
    }

    fun onIntent(intent: FortuneIntent) {
        when (intent) {
            is FortuneIntent.SelectType -> _state.update { it.copy(selectedType = intent.type) }
            is FortuneIntent.SelectMood -> _state.update { it.copy(selectedMood = intent.mood) }
            is FortuneIntent.SelectFocus -> _state.update { it.copy(selectedFocus = intent.focus) }
            is FortuneIntent.GetFortune -> getFortune()
            is FortuneIntent.Retry -> getFortune()
            is FortuneIntent.MarkAsSeen -> markAsSeen()
            is FortuneIntent.OnTypewriterComplete -> {
                _state.update { it.copy(isTypewriterComplete = true) }
            }
        }
    }

    fun loadFortuneById(fortuneId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isTypewriterComplete = false) }
            val result = repository.getFortuneById(fortuneId)
            result.fold(
                onSuccess = { fortune ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            fortune = fortune,
                            selectedType = fortune.type,
                            error = null,
                            isTypewriterComplete = false
                        )
                    }
                    dataStore.incrementFortuneCount()
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _effects.tryEmit(FortuneEffect.ShowSnackbar(error.message ?: context.getString(R.string.ui_fortune_err_load)))
                }
            )
        }
    }

    private fun getFortune() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isTypewriterComplete = false) }

            val result = repository.getFortune(
                type = _state.value.selectedType,
                mood = _state.value.selectedMood,
                focus = _state.value.selectedFocus,
                seenIds = _seenIds.value
            )

            result.fold(
                onSuccess = { fortune ->
                    _state.update { it.copy(isLoading = false, fortune = fortune, error = null) }
                    dataStore.incrementFortuneCount()
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    // ✅ tryEmit kullanıldı
                    _effects.tryEmit(FortuneEffect.ShowSnackbar(error.message ?: context.getString(R.string.ui_fortune_err_fortune)))
                }
            )
        }
    }

    private fun markAsSeen() {
        viewModelScope.launch {
            val fortuneId = _state.value.fortune?.id ?: return@launch
            repository.markAsSeen(fortuneId)
            _seenIds.update { it + fortuneId }
            
            // ✅ UTC ile streak güncelle - timezone bağımsız
            val todayUtc = Clock.System.now()
                .toLocalDateTime(TimeZone.UTC).date.toString()
            dataStore.updateStreak(todayUtc)
        }
    }
}
