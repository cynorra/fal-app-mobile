package com.cynora.luna.feature.fortune.dailycard

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.cynora.luna.core.data.repository.DailyCardRepository
import com.cynora.luna.core.data.repository.TarotCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Immutable
data class DailyCardState(
    val card: TarotCard? = null,
    val isRevealed: Boolean = false
)

sealed interface DailyCardIntent {
    data object RevealCard : DailyCardIntent
    data object DrawNewCard : DailyCardIntent
}

@HiltViewModel
class DailyCardViewModel @Inject constructor(
    private val repository: DailyCardRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(DailyCardState())
    val state: StateFlow<DailyCardState> = _state.asStateFlow()
    
    fun onIntent(intent: DailyCardIntent) {
        when (intent) {
            is DailyCardIntent.RevealCard -> {
                _state.update { it.copy(card = repository.getDailyCard(), isRevealed = true) }
            }
            is DailyCardIntent.DrawNewCard -> {
                _state.update { it.copy(card = repository.getRandomCard(), isRevealed = true) }
            }
        }
    }
}
