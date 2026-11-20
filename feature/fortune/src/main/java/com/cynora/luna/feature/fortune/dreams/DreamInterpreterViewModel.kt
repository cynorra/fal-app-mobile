package com.cynora.luna.feature.fortune.dreams

import androidx.lifecycle.ViewModel
import com.cynora.luna.core.data.model.DreamCategory
import com.cynora.luna.core.data.model.DreamSymbol
import com.cynora.luna.core.data.repository.DreamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class DreamUiState(
    val searchQuery: String = "",
    val selectedCategory: DreamCategory? = null,
    val symbols: List<DreamSymbol> = emptyList(),
    val selectedSymbolForDetail: DreamSymbol? = null
)

@HiltViewModel
class DreamInterpreterViewModel @Inject constructor(
    private val dreamRepository: DreamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DreamUiState(symbols = dreamRepository.getAllSymbols())
    )
    val uiState: StateFlow<DreamUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                symbols = dreamRepository.searchSymbols(query, current.selectedCategory)
            )
        }
    }

    fun onCategorySelected(category: DreamCategory?) {
        _uiState.update { current ->
            val newCategory = if (current.selectedCategory == category) null else category
            current.copy(
                selectedCategory = newCategory,
                symbols = dreamRepository.searchSymbols(current.searchQuery, newCategory)
            )
        }
    }

    fun selectSymbol(symbol: DreamSymbol?) {
        _uiState.update { it.copy(selectedSymbolForDetail = symbol) }
    }
}
