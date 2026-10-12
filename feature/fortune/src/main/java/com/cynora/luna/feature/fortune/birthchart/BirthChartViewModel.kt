package com.cynora.luna.feature.fortune.birthchart

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.repository.BirthChartRepository
import com.cynora.luna.core.data.repository.BirthChartData
import com.cynora.luna.core.data.repository.BirthInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import javax.inject.Inject

@Immutable
data class BirthChartState(
    val birthInfo: BirthInfo? = null,
    val birthChart: BirthChartData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface BirthChartIntent {
    data class SubmitBirthInfo(val date: LocalDate, val time: LocalTime?, val place: String) : BirthChartIntent
    data object Reset : BirthChartIntent
}

@HiltViewModel
class BirthChartViewModel @Inject constructor(
    private val repository: BirthChartRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(BirthChartState())
    val state: StateFlow<BirthChartState> = _state.asStateFlow()
    
    fun onIntent(intent: BirthChartIntent) {
        when (intent) {
            is BirthChartIntent.SubmitBirthInfo -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    try {
                        val birthInfo = BirthInfo(intent.date, intent.time, intent.place)
                        val chart = repository.calculateBirthChart(birthInfo)
                        _state.update { 
                            it.copy(birthInfo = birthInfo, birthChart = chart, isLoading = false)
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
            }
            is BirthChartIntent.Reset -> _state.update { BirthChartState() }
        }
    }
}
