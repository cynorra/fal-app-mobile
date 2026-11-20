package com.cynora.luna.core.data.model

import java.util.Calendar

enum class Planet(
    val displayName: String,
    val symbol: String,
    val theme: String,
    val description: String,
    val bestFor: String
) {
    SUN("Sun", "☉", "Vitality & Confidence", "Radiant creative energy, leadership, and personal power.", "Presenting ideas, making decisions, self-expression"),
    MOON("Moon", "☽", "Intuition & Emotion", "Subtle receptive energy, inner feelings, and instinctive knowing.", "Meditation, family time, home projects, dreamwork"),
    MARS("Mars", "♂", "Courage & Action", "High drive, determination, ambition, and physical energy.", "Workouts, tackling hard tasks, courage, breaking barriers"),
    MERCURY("Mercury", "☿", "Intellect & Communication", "Mental agility, conversation, commerce, and analytical thought.", "Negotiations, writing, contracts, learning new skills"),
    JUPITER("Jupiter", "♃", "Abundance & Expansion", "Good fortune, optimism, wisdom, and spiritual prosperity.", "Financial planning, learning, generous acts, gratitude"),
    VENUS("Venus", "♀", "Love & Harmony", "Beauty, artistic creation, romantic connections, and social warmth.", "Dates, self-care, artistic projects, socializing"),
    SATURN("Saturn", "♄", "Discipline & Structure", "Focus, grounding, long-term mastery, and structured endurance.", "Deep focus, budgeting, organizing, completing obligations");

    companion object {
        val CHALDEAN_ORDER = listOf(SATURN, JUPITER, MARS, SUN, VENUS, MERCURY, MOON)

        fun getDayRuler(dayOfWeek: Int): Planet {
            return when (dayOfWeek) {
                Calendar.SUNDAY -> SUN
                Calendar.MONDAY -> MOON
                Calendar.TUESDAY -> MARS
                Calendar.WEDNESDAY -> MERCURY
                Calendar.THURSDAY -> JUPITER
                Calendar.FRIDAY -> VENUS
                Calendar.SATURDAY -> SATURN
                else -> SUN
            }
        }
    }
}

data class PlanetaryHourInfo(
    val planet: Planet,
    val hourStart: Int,
    val hourEnd: Int,
    val timeFormatted: String,
    val cosmicPulseMessage: String
) {
    companion object {
        fun getCurrent(calendar: Calendar = Calendar.getInstance()): PlanetaryHourInfo {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

            val dayRuler = Planet.getDayRuler(dayOfWeek)
            val startIndex = Planet.CHALDEAN_ORDER.indexOf(dayRuler)

            val planetIndex = (startIndex + hourOfDay) % Planet.CHALDEAN_ORDER.size
            val activePlanet = Planet.CHALDEAN_ORDER[planetIndex]

            val nextHour = (hourOfDay + 1) % 24
            val timeFormatted = String.format("%02d:00 - %02d:00", hourOfDay, nextHour)
            val pulseMessage = "${activePlanet.symbol} ${activePlanet.displayName} Hour: ${activePlanet.theme}. Ideal for ${activePlanet.bestFor.lowercase()}."

            return PlanetaryHourInfo(
                planet = activePlanet,
                hourStart = hourOfDay,
                hourEnd = nextHour,
                timeFormatted = timeFormatted,
                cosmicPulseMessage = pulseMessage
            )
        }
    }
}
