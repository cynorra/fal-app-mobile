package com.cynora.luna.feature.fortune.tarot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.model.DrawnTarotCard
import com.cynora.luna.core.data.repository.TarotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TarotSpreadMode {
    DAILY_ONE_CARD,
    THREE_CARD_SPREAD
}

data class TarotUiState(
    val spreadMode: TarotSpreadMode = TarotSpreadMode.DAILY_ONE_CARD,
    val isShuffling: Boolean = false,
    val isCardRevealed: Boolean = false,
    val drawnCards: List<DrawnTarotCard> = emptyList(),
    val isThreeCardUnlocked: Boolean = false
)

@HiltViewModel
class TarotReadingViewModel @Inject constructor(
    private val tarotRepository: TarotRepository,
    dataStore: com.cynora.luna.core.database.datastore.UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(TarotUiState())
    val uiState: StateFlow<TarotUiState> = _uiState.asStateFlow()

    init {
        // Friend perk: the 3-card spread stays unlocked
        viewModelScope.launch {
            dataStore.hasInvitePerk().collect { perk ->
                if (perk) _uiState.update { it.copy(isThreeCardUnlocked = true) }
            }
        }
    }

    fun setSpreadMode(mode: TarotSpreadMode) {
        if (mode == TarotSpreadMode.THREE_CARD_SPREAD && !_uiState.value.isThreeCardUnlocked) {
            // Needs reward unlock
            _uiState.update { it.copy(spreadMode = mode) }
        } else {
            _uiState.update { it.copy(spreadMode = mode, isCardRevealed = false, drawnCards = emptyList()) }
        }
    }

    fun unlockThreeCardSpread() {
        _uiState.update { it.copy(isThreeCardUnlocked = true) }
        drawCards()
    }

    fun drawCards() {
        viewModelScope.launch {
            _uiState.update { it.copy(isShuffling = true, isCardRevealed = false) }
            delay(1200)

            val cards = when (_uiState.value.spreadMode) {
                TarotSpreadMode.DAILY_ONE_CARD -> listOf(tarotRepository.drawDailyCard())
                TarotSpreadMode.THREE_CARD_SPREAD -> tarotRepository.drawThreeCardSpread()
            }

            _uiState.update {
                it.copy(
                    isShuffling = false,
                    isCardRevealed = true,
                    drawnCards = cards
                )
            }
        }
    }
}
