package com.cynora.luna.feature.fortune.compatibility

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.cynora.luna.core.data.model.ZodiacSign
import com.cynora.luna.core.data.repository.CompatibilityRepository
import com.cynora.luna.core.data.repository.CompatibilityResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Immutable
data class CompatibilityState(
    val sign1: ZodiacSign? = null,
    val sign2: ZodiacSign? = null,
    val result: CompatibilityResult? = null
)

sealed interface CompatibilityIntent {
    data class SelectSign1(val sign: ZodiacSign) : CompatibilityIntent
    data class SelectSign2(val sign: ZodiacSign) : CompatibilityIntent
    data object Calculate : CompatibilityIntent
    data object Reset : CompatibilityIntent
}

@HiltViewModel
class CompatibilityViewModel @Inject constructor(
    private val repository: CompatibilityRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CompatibilityState())
    val state: StateFlow<CompatibilityState> = _state.asStateFlow()
    
    fun onIntent(intent: CompatibilityIntent) {
        when (intent) {
            is CompatibilityIntent.SelectSign1 -> _state.update { it.copy(sign1 = intent.sign) }
            is CompatibilityIntent.SelectSign2 -> _state.update { it.copy(sign2 = intent.sign) }
            is CompatibilityIntent.Calculate -> {
                val s1 = _state.value.sign1 ?: return
                val s2 = _state.value.sign2 ?: return
                val result = repository.calculateCompatibility(s1, s2)
                _state.update { it.copy(result = result) }
            }
            is CompatibilityIntent.Reset -> _state.update { CompatibilityState() }
        }
    }
}
