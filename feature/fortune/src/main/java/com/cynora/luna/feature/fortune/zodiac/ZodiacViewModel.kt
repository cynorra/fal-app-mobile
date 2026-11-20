package com.cynora.luna.feature.fortune.zodiac

import androidx.lifecycle.ViewModel
import com.cynora.luna.core.data.model.ZodiacDetail
import com.cynora.luna.core.data.model.ZodiacElement
import com.cynora.luna.core.data.model.ZodiacSign
import com.cynora.luna.core.data.repository.ZodiacRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ZodiacGuideUiState(
    val selectedElementFilter: ZodiacElement? = null,
    val details: List<ZodiacDetail> = emptyList(),
    val selectedSignDetail: ZodiacDetail? = null
)

@HiltViewModel
class ZodiacViewModel @Inject constructor(
    private val zodiacRepository: ZodiacRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ZodiacGuideUiState(details = zodiacRepository.getAllDetails())
    )
    val uiState: StateFlow<ZodiacGuideUiState> = _uiState.asStateFlow()

    fun filterByElement(element: ZodiacElement?) {
        _uiState.update { current ->
            val newElement = if (current.selectedElementFilter == element) null else element
            val all = zodiacRepository.getAllDetails()
            val filtered = if (newElement == null) all else all.filter { it.element == newElement }
            current.copy(selectedElementFilter = newElement, details = filtered)
        }
    }

    fun selectSign(sign: ZodiacSign?) {
        _uiState.update { current ->
            val detail = sign?.let { zodiacRepository.getDetailForSign(it) }
            current.copy(selectedSignDetail = detail)
        }
    }
}
