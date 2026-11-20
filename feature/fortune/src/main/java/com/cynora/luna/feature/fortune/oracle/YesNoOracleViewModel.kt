package com.cynora.luna.feature.fortune.oracle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.model.OracleReading
import com.cynora.luna.core.data.repository.OracleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OracleUiState(
    val isConsulting: Boolean = false,
    val reading: OracleReading? = null,
    val questionInput: String = ""
)

@HiltViewModel
class YesNoOracleViewModel @Inject constructor(
    private val oracleRepository: OracleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OracleUiState())
    val uiState: StateFlow<OracleUiState> = _uiState.asStateFlow()

    fun onQuestionChange(text: String) {
        _uiState.update { it.copy(questionInput = text) }
    }

    fun consultOracle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isConsulting = true, reading = null) }
            // Dramatic cosmic delay
            delay(1600)
            val result = oracleRepository.consultOracle()
            _uiState.update { it.copy(isConsulting = false, reading = result) }
        }
    }

    fun reset() {
        _uiState.update { it.copy(reading = null, questionInput = "") }
    }
}
