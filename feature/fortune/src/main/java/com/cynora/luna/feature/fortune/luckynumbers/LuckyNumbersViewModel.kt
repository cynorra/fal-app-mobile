package com.cynora.luna.feature.fortune.luckynumbers

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.model.ZodiacSign
import com.cynora.luna.core.data.repository.LuckyNumbersRepository
import com.cynora.luna.core.data.repository.LuckyNumbersResult
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class LuckyNumbersState(
    val userSign: ZodiacSign = ZodiacSign.ARIES,
    val result: LuckyNumbersResult? = null
)

sealed interface LuckyNumbersIntent {
    data class ChangeSign(val sign: ZodiacSign) : LuckyNumbersIntent
    data object Refresh : LuckyNumbersIntent
}

@HiltViewModel
class LuckyNumbersViewModel @Inject constructor(
    private val repository: LuckyNumbersRepository,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(LuckyNumbersState())
    val state: StateFlow<LuckyNumbersState> = _state.asStateFlow()
    
    init {
        viewModelScope.launch {
            dataStore.getUserZodiac().collect { zodiacName ->
                val sign = zodiacName?.let { 
                    ZodiacSign.entries.find { z -> z.name.equals(it, ignoreCase = true) } 
                } ?: ZodiacSign.ARIES
                _state.update { it.copy(userSign = sign) }
                refresh()
            }
        }
    }
    
    fun onIntent(intent: LuckyNumbersIntent) {
        when (intent) {
            is LuckyNumbersIntent.ChangeSign -> {
                _state.update { it.copy(userSign = intent.sign) }
                refresh()
            }
            is LuckyNumbersIntent.Refresh -> refresh()
        }
    }
    
    private fun refresh() {
        val result = repository.getDailyLuckyNumbers(_state.value.userSign)
        _state.update { it.copy(result = result) }
    }
}
