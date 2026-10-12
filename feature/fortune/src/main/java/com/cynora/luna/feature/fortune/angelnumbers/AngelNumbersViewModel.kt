package com.cynora.luna.feature.fortune.angelnumbers

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.repository.AngelNumber
import com.cynora.luna.core.data.repository.AngelNumbersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class AngelNumbersState(
    val allNumbers: List<AngelNumber> = emptyList(),
    val selectedNumber: AngelNumber? = null,
    val searchQuery: String = ""
)

sealed interface AngelNumbersIntent {
    data class Search(val query: String) : AngelNumbersIntent
    data class SelectNumber(val number: AngelNumber) : AngelNumbersIntent
    data object ClearSelection : AngelNumbersIntent
}

@HiltViewModel
class AngelNumbersViewModel @Inject constructor(
    private val repository: AngelNumbersRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(AngelNumbersState())
    val state: StateFlow<AngelNumbersState> = _state.asStateFlow()
    
    init {
        viewModelScope.launch {
            repository.getAllAngelNumbers().collect { numbers ->
                _state.update { it.copy(allNumbers = numbers) }
            }
        }
    }
    
    fun onIntent(intent: AngelNumbersIntent) {
        when (intent) {
            is AngelNumbersIntent.Search -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }
            is AngelNumbersIntent.SelectNumber -> {
                _state.update { it.copy(selectedNumber = intent.number) }
            }
            is AngelNumbersIntent.ClearSelection -> {
                _state.update { it.copy(selectedNumber = null) }
            }
        }
    }
    
    fun getFilteredNumbers(): List<AngelNumber> {
        val query = _state.value.searchQuery
        return if (query.isBlank()) {
            _state.value.allNumbers
        } else {
            repository.searchAngelNumber(query)
        }
    }
}
