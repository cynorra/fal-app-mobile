package com.cynora.luna.feature.home

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.model.ZodiacSign
import com.cynora.luna.core.data.repository.MysticCalendarRepository
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class HomeState(
    val moonPhase: String = "🌑 New Moon",
    val planetInfo: String = "☿ Mercury Direct",
    val dailyEnergy: String = "Transformation & Renewal",
    val luckyHour: String = "06:00",
    val zodiacSign: ZodiacSign = ZodiacSign.ARIES,
    val horoscopeText: String = "Today, don't be afraid to take bold steps.",
    val luckyNumber: Int = 7,
    val luckyColor: String = "Gold"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: UserPreferencesDataStore,
    private val calendarRepository: MysticCalendarRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.getUserZodiac().collect { zodiacName ->
                val zodiac = zodiacName?.let { 
                    ZodiacSign.entries.find { z -> z.name.equals(it, ignoreCase = true) } 
                } ?: ZodiacSign.ARIES
                
                _state.update { 
                    it.copy(
                        zodiacSign = zodiac,
                        horoscopeText = getHoroscopeForZodiac(zodiac),
                        luckyNumber = getLuckyNumber(zodiac),
                        luckyColor = getLuckyColor(zodiac)
                    ) 
                }
            }
        }

        viewModelScope.launch {
            calendarRepository.observeDailyData().collect { data ->
                _state.update { 
                    it.copy(
                        moonPhase = data.moonPhase,
                        planetInfo = data.planetInfo,
                        dailyEnergy = data.dailyEnergy,
                        luckyHour = data.luckyHour
                    ) 
                }
            }
        }
    }

    private fun getHoroscopeForZodiac(zodiac: ZodiacSign): String {
        // ✅ Feature modülünün kendi R'ı
        val resId = when (zodiac) {
            ZodiacSign.ARIES -> R.string.horoscope_aries
            ZodiacSign.TAURUS -> R.string.horoscope_taurus
            ZodiacSign.GEMINI -> R.string.horoscope_gemini
            ZodiacSign.CANCER -> R.string.horoscope_cancer
            ZodiacSign.LEO -> R.string.horoscope_leo
            ZodiacSign.VIRGO -> R.string.horoscope_virgo
            ZodiacSign.LIBRA -> R.string.horoscope_libra
            ZodiacSign.SCORPIO -> R.string.horoscope_scorpio
            ZodiacSign.SAGITTARIUS -> R.string.horoscope_sagittarius
            ZodiacSign.CAPRICORN -> R.string.horoscope_capricorn
            ZodiacSign.AQUARIUS -> R.string.horoscope_aquarius
            ZodiacSign.PISCES -> R.string.horoscope_pisces
        }
        return context.getString(resId)
    }

    private fun getLuckyNumber(zodiac: ZodiacSign): Int {
        return (zodiac.ordinal + 1) * 3 % 10 + 1
    }

    private fun getLuckyColor(zodiac: ZodiacSign): String {
        val colors = listOf("Gold", "Purple", "Blue", "Green", "Red", "White", "Silver")
        return colors[zodiac.ordinal % colors.size]
    }
}
