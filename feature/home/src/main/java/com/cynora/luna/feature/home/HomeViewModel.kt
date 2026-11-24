package com.cynora.luna.feature.home

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cynora.luna.core.data.model.PlanetaryHourInfo
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
    val userName: String = "Seeker",
    val bigThree: String = "♌ Leo • ♏ Scorpio • ♎ Libra",
    val moonPhase: String = "🌑 New Moon",
    val planetInfo: String = "☿ Mercury Direct",
    val dailyEnergy: String = "Transformation & Renewal",
    val luckyHour: String = "06:00",
    val planetaryHour: PlanetaryHourInfo = PlanetaryHourInfo.getCurrent(),
    val loveScore: Int = 88,
    val careerScore: Int = 92,
    val energyScore: Int = 78,
    val zodiacSign: ZodiacSign = ZodiacSign.ARIES,
    val selectedHoroscopeDay: Int = 1, // 0 = Yesterday, 1 = Today, 2 = Tomorrow
    val isTomorrowUnlocked: Boolean = false,
    val horoscopeToday: String = "Today, don't be afraid to take bold steps.",
    val horoscopeYesterday: String = "Yesterday's lessons established the firm foundation for current breakthroughs.",
    val horoscopeTomorrow: String = "Tomorrow's planetary alignment opens an unexpected financial or creative opportunity.",
    val luckyNumber: Int = 7,
    val luckyColor: String = "Gold",
    val compatibleSign: String = "Leo"
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
        // Friend perk: tomorrow's horoscope stays unlocked
        viewModelScope.launch {
            dataStore.hasInvitePerk().collect { perk ->
                if (perk) _state.update { it.copy(isTomorrowUnlocked = true) }
            }
        }

        viewModelScope.launch {
            dataStore.getUserName().collect { name ->
                _state.update { it.copy(userName = name) }
            }
        }

        viewModelScope.launch {
            dataStore.getBigThree().collect { (sun, moon, rising) ->
                val sunSign = ZodiacSign.fromDisplayName(sun)
                val moonSign = ZodiacSign.fromDisplayName(moon)
                val risingSign = ZodiacSign.fromDisplayName(rising)
                val bigThreeText = "${sunSign.symbol} $sun • ${moonSign.symbol} $moon • ${risingSign.symbol} $rising"
                _state.update { it.copy(bigThree = bigThreeText) }
            }
        }

        viewModelScope.launch {
            dataStore.getUserZodiac().collect { zodiacName ->
                val zodiac = zodiacName?.let { 
                    ZodiacSign.entries.find { z -> z.name.equals(it, ignoreCase = true) } 
                } ?: ZodiacSign.ARIES
                
                val (love, career, energy) = calculateDailyScores(zodiac)

                _state.update { 
                    it.copy(
                        zodiacSign = zodiac,
                        horoscopeToday = getHoroscopeForZodiac(zodiac),
                        horoscopeYesterday = context.getString(R.string.ui_home_horo_yesterday),
                        horoscopeTomorrow = context.getString(R.string.ui_home_horo_tomorrow),
                        luckyNumber = getLuckyNumber(zodiac),
                        luckyColor = getLuckyColor(zodiac),
                        compatibleSign = getCompatibleSign(zodiac),
                        loveScore = love,
                        careerScore = career,
                        energyScore = energy,
                        planetaryHour = PlanetaryHourInfo.getCurrent()
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

    fun selectHoroscopeDay(dayIndex: Int) {
        _state.update { it.copy(selectedHoroscopeDay = dayIndex) }
    }

    fun unlockTomorrowHoroscope() {
        _state.update { it.copy(isTomorrowUnlocked = true, selectedHoroscopeDay = 2) }
    }

    private fun calculateDailyScores(zodiac: ZodiacSign): Triple<Int, Int, Int> {
        val base = (zodiac.ordinal * 7 + 65) % 30
        return Triple(70 + base, 72 + ((base + 5) % 25), 68 + ((base + 11) % 28))
    }

    private fun getCompatibleSign(zodiac: ZodiacSign): String {
        val compatibleIndex = (zodiac.ordinal + 4) % ZodiacSign.entries.size
        return ZodiacSign.entries[compatibleIndex].displayName
    }

    private fun getHoroscopeForZodiac(zodiac: ZodiacSign): String {
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
        val colors = listOf(
            R.string.ui_home_color_gold,
            R.string.ui_home_color_lavender,
            R.string.ui_home_color_blue,
            R.string.ui_home_color_green,
            R.string.ui_home_color_crimson,
            R.string.ui_home_color_white,
            R.string.ui_home_color_silver
        )
        return context.getString(colors[zodiac.ordinal % colors.size])
    }
}
