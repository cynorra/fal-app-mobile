package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.PlanetaryHourInfo
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

data class HourlyCosmicPulse(
    val title: String,
    val message: String,
    val category: String,
    val planetaryHour: PlanetaryHourInfo
)

@Singleton
class HourlyCosmicRepository @Inject constructor() {

    private val random = Random.Default

    private val hourlyAffirmations = listOf(
        "✨ Cosmic Pulse: I am deeply connected to the infinite abundance of the universe.",
        "🔮 Hourly Alignment: Trust the timing of your life; what is meant for you will not pass you.",
        "💫 Star Guidance: Release what you cannot control. Ground your energy in gratitude.",
        "🌟 Celestial Pulse: You carry the quiet strength of the cosmos within you.",
        "✨ Hourly Grace: Every breath aligns you with clarity, peace, and inner purpose.",
        "🪐 Cosmic Frequency: Today's planetary flow opens new doors of unexpected favor."
    )

    private val luckyCheckIns = listOf(
        "🍀 Lucky Portal: Your energetic window is heightened right now. A good moment for important calls.",
        "✨ Celestial Check-in: Take 3 deep breaths and set a 1-sentence intention for this hour.",
        "🔮 Oracle Whisper: Wondering about a choice? Open your Yes/No Oracle for instant cosmic clarity.",
        "🌙 Lunar Shift: Observe your thoughts without judgment; clarity is unfolding."
    )

    fun getHourlyPulse(calendar: Calendar = Calendar.getInstance()): HourlyCosmicPulse {
        val planetaryHour = PlanetaryHourInfo.getCurrent(calendar)
        val variant = random.nextInt(3)

        return when (variant) {
            0 -> HourlyCosmicPulse(
                title = "${planetaryHour.planet.symbol} ${planetaryHour.planet.displayName} Hour Active",
                message = "${planetaryHour.planet.theme}. Ideal for ${planetaryHour.planet.bestFor.lowercase()}.",
                category = "Planetary Hour",
                planetaryHour = planetaryHour
            )
            1 -> HourlyCosmicPulse(
                title = "✨ Cosmic Pulse Check-in",
                message = luckyCheckIns[random.nextInt(luckyCheckIns.size)],
                category = "Cosmic Sync",
                planetaryHour = planetaryHour
            )
            else -> HourlyCosmicPulse(
                title = "💫 Hourly Celestial Affirmation",
                message = hourlyAffirmations[random.nextInt(hourlyAffirmations.size)],
                category = "Affirmation",
                planetaryHour = planetaryHour
            )
        }
    }
}
