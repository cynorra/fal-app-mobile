package com.cynora.luna.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

data class MysticCalendarData(
    val moonPhase: String,
    val planetInfo: String,
    val dailyEnergy: String,
    val luckyHour: String
)

@Singleton
class MysticCalendarRepository @Inject constructor() {
    
    fun observeDailyData(): Flow<MysticCalendarData> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        return flowOf(
            MysticCalendarData(
                moonPhase = calculateMoonPhase(today),
                planetInfo = getDailyPlanetInfo(today),
                dailyEnergy = getDailyEnergy(today),
                luckyHour = getLuckyHour(today)
            )
        )
    }
    
    private fun calculateMoonPhase(date: LocalDate): String {
        val knownNewMoon = LocalDate(2000, 1, 6)
        val daysSinceNewMoon = date.toEpochDays() - knownNewMoon.toEpochDays()
        val cyclePosition = (daysSinceNewMoon % 30).toInt()
        
        return when {
            cyclePosition < 2 -> "🌑 New Moon"
            cyclePosition < 7 -> "🌒 Waxing Crescent"
            cyclePosition < 10 -> "🌓 First Quarter"
            cyclePosition < 14 -> "🌔 Waxing Gibbous"
            cyclePosition < 16 -> "🌕 Full Moon"
            cyclePosition < 20 -> "🌖 Waning Gibbous"
            cyclePosition < 23 -> "🌗 Last Quarter"
            cyclePosition < 27 -> "🌘 Waning Crescent"
            else -> "🌑 New Moon"
        }
    }
    
    private fun getDailyPlanetInfo(date: LocalDate): String {
        val planets = listOf(
            "☿ Mercury Direct",
            "♀ Venus in Aries",
            "♂ Mars in Taurus",
            "♃ Jupiter in Gemini",
            "♄ Saturn in Pisces",
            "♅ Uranus in Taurus",
            "♆ Neptune in Pisces",
            "♇ Pluto in Aquarius"
        )
        return planets[date.dayOfYear % planets.size]
    }
    
    private fun getDailyEnergy(date: LocalDate): String {
        val energies = listOf(
            "Transformation & Renewal",
            "Creativity & Expression",
            "Communication & Connection",
            "Discipline & Focus",
            "Abundance & Prosperity",
            "Intuition & Insight",
            "Courage & Action"
        )
        // ✅ dayOfWeek.isoDayNumber (1-7, Pazartesi=1)
        return energies[(date.dayOfWeek.isoDayNumber - 1) % energies.size]
    }
    
    private fun getLuckyHour(date: LocalDate): String {
        val hours = listOf("06:00", "09:00", "12:00", "15:00", "18:00", "21:00")
        return hours[date.dayOfMonth % hours.size]
    }
}
