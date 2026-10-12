package com.cynora.luna.feature.fortune.affirmations

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.cynora.luna.core.data.repository.Affirmation
import com.cynora.luna.core.data.repository.AffirmationCategory
import com.cynora.luna.core.data.repository.AffirmationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Immutable
data class AffirmationsState(
    val selectedCategory: AffirmationCategory? = null,
    val dailyAffirmation: Affirmation? = null,
    val allAffirmations: List<Affirmation> = emptyList()
)

sealed interface AffirmationsIntent {
    data class SelectCategory(val category: AffirmationCategory?) : AffirmationsIntent
}

@HiltViewModel
class AffirmationsViewModel @Inject constructor(
    private val repository: AffirmationRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(
        AffirmationsState(dailyAffirmation = repository.getDailyAffirmation())
    )
    val state: StateFlow<AffirmationsState> = _state.asStateFlow()
    
    fun onIntent(intent: AffirmationsIntent) {
        when (intent) {
            is AffirmationsIntent.SelectCategory -> {
                val category = intent.category
                val affirmations = if (category == null) {
                    emptyList()
                } else {
                    repository.getAffirmationsByCategory(category)
                }
                _state.update { 
                    it.copy(selectedCategory = category, allAffirmations = affirmations) 
                }
            }
        }
    }
}
