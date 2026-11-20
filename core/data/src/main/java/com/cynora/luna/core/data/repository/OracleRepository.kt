package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.OracleOutcome
import com.cynora.luna.core.data.model.OracleReading
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class OracleRepository @Inject constructor() {

    private val random = Random.Default

    private val yesReadings = listOf(
        Pair("The celestial currents flow in your favor.", "Proceed with quiet confidence. The stars align to support decisive and sincere action."),
        Pair("A resounding affirmation from the cosmos.", "Doubts are only shadows; your path is illuminated with clarity and divine encouragement."),
        Pair("Doors open where walls once stood.", "Trust your inner intuition. Take the step forward without second-guessing yourself."),
        Pair("Harmonic resonance confirmed.", "The universe mirrors your highest intention. Say yes to the possibility unfolding.")
    )

    private val noReadings = listOf(
        Pair("The stars urge stillness and reflection.", "Not at this moment. The current timing carries hidden friction; pause and protect your peace."),
        Pair("A gentle cosmic redirection.", "What you seek right now may distract you from a far more rewarding path arriving shortly."),
        Pair("Preserve your energy; do not force.", "When things feel forced, the cosmos is asking you to wait. Step back and observe."),
        Pair("Unfavorable celestial aspect.", "Rushing into this now creates complications. Allow more information to surface first.")
    )

    private val maybeReadings = listOf(
        Pair("The outcome hinges on your inner clarity.", "The balance is delicate. Align your true intent before seeking external confirmation."),
        Pair("A veil conceals the final answer for now.", "Wait until after the next moon shift or sleep on it. The energetic tide is still settling."),
        Pair("Multiple timelines intersect here.", "Your decision itself will shape the outcome. Do not look for guarantees—choose with integrity."),
        Pair("Patience is your strongest ally.", "The universe is rearranging pieces behind the scenes. Give it 24 hours before acting.")
    )

    fun consultOracle(): OracleReading {
        // Weighted roll: 45% Yes, 35% No, 20% Maybe
        val roll = random.nextInt(100)
        return when {
            roll < 45 -> {
                val item = yesReadings[random.nextInt(yesReadings.size)]
                OracleReading(
                    outcome = OracleOutcome.YES,
                    headline = item.first,
                    cosmicAlignment = "94% Harmonic Resonance",
                    spiritualAdvice = item.second,
                    luckyAspect = "Best accompanied by bold action and gratitude."
                )
            }
            roll < 80 -> {
                val item = noReadings[random.nextInt(noReadings.size)]
                OracleReading(
                    outcome = OracleOutcome.NO,
                    headline = item.first,
                    cosmicAlignment = "Planetary Friction Detected",
                    spiritualAdvice = item.second,
                    luckyAspect = "Focus inward and guard your emotional reserves."
                )
            }
            else -> {
                val item = maybeReadings[random.nextInt(maybeReadings.size)]
                OracleReading(
                    outcome = OracleOutcome.MAYBE,
                    headline = item.first,
                    cosmicAlignment = "Energetic Shift In Progress",
                    spiritualAdvice = item.second,
                    luckyAspect = "Clarity will arrive when you release urgency."
                )
            }
        }
    }
}
