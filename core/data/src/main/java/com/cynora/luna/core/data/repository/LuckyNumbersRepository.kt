package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.ZodiacSign
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

data class LuckyNumbersResult(
    val mainNumbers: List<Int>,
    val bonusNumber: Int,
    val luckyColor: String,
    val luckyDay: String,
    val luckyElement: String,
    val luckyPlanet: String,
    val luckyCrystal: String,
    val dailyMessage: String
)

@Singleton
class LuckyNumbersRepository @Inject constructor(
    private val random: Random = Random.Default
) {
    
    fun getDailyLuckyNumbers(sign: ZodiacSign): LuckyNumbersResult {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val seed = today.toEpochDays() + sign.ordinal
        val seededRandom = Random(seed.toLong())
        
        // 6 ana sayı (1-49 arası)
        val mainNumbers = (1..49)
            .shuffled(seededRandom)
            .take(6)
            .sorted()
        
        // Bonus sayı (1-10)
        val bonusNumber = seededRandom.nextInt(10) + 1
        
        // Şanslı renk
        val colors = listOf("Gold ✨", "Purple 💜", "Blue 💙", "Green 💚", "Red ❤️", "White 🤍", "Silver 🩶", "Pink 💗")
        val luckyColor = colors[seededRandom.nextInt(colors.size)]
        
        // Şanslı gün (bugünün günü - her gün aynı)
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val luckyDay = days[(today.dayOfWeek.isoDayNumber - 1) % days.size]
        
        // Şanslı element
        val elements = listOf("Fire 🔥", "Earth 🌍", "Air 💨", "Water 💧", "Aether ✨")
        val luckyElement = elements[seededRandom.nextInt(elements.size)]
        
        // Şanslı gezegen
        val planets = listOf("Sun ☀️", "Moon 🌙", "Mercury ☿", "Venus ♀", "Mars ♂", "Jupiter ♃", "Saturn ♄")
        val luckyPlanet = planets[seededRandom.nextInt(planets.size)]
        
        // Şanslı kristal
        val crystals = listOf("Amethyst 🔮", "Rose Quartz 💗", "Citrine 💛", "Clear Crystal 💎", "Black Tourmaline 🖤", "Moonstone 🌙", "Tiger's Eye 🐯")
        val luckyCrystal = crystals[seededRandom.nextInt(crystals.size)]
        
        // Günlük mesaj
        val messages = listOf(
            "Today the universe aligns in your favor. Trust your instincts.",
            "A moment of luck awaits you. Stay open to unexpected opportunities.",
            "Your energy is magnetic today. What you focus on expands.",
            "The stars whisper secrets of fortune. Listen carefully.",
            "Your lucky star shines bright. Take that bold step.",
            "Synchronicities surround you. Pay attention to signs.",
            "The cosmos conspires in your favor. Act with confidence.",
            "A door of opportunity opens. Walk through with courage."
        )
        val dailyMessage = messages[seededRandom.nextInt(messages.size)]
        
        return LuckyNumbersResult(
            mainNumbers = mainNumbers,
            bonusNumber = bonusNumber,
            luckyColor = luckyColor,
            luckyDay = luckyDay,
            luckyElement = luckyElement,
            luckyPlanet = luckyPlanet,
            luckyCrystal = luckyCrystal,
            dailyMessage = dailyMessage
        )
    }
    
    fun getWeeklyLuckyNumbers(sign: ZodiacSign): List<LuckyNumbersResult> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return (0..6).map { dayOffset ->
            val date = today.plusDays(dayOffset.toLong())
            val seed = date.toEpochDays() + sign.ordinal
            val seededRandom = Random(seed.toLong())
            
            LuckyNumbersResult(
                mainNumbers = (1..49).shuffled(seededRandom).take(6).sorted(),
                bonusNumber = seededRandom.nextInt(10) + 1,
                luckyColor = listOf("Gold ✨", "Purple 💜", "Blue 💙", "Green 💚", "Red ❤️", "White 🤍", "Silver 🩶", "Pink 💗")[seededRandom.nextInt(8)],
                luckyDay = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")[dayOffset],
                luckyElement = listOf("Fire 🔥", "Earth 🌍", "Air 💨", "Water 💧", "Aether ✨")[seededRandom.nextInt(5)],
                luckyPlanet = listOf("Sun ☀️", "Moon 🌙", "Mercury ☿", "Venus ♀", "Mars ♂", "Jupiter ♃", "Saturn ♄")[seededRandom.nextInt(7)],
                luckyCrystal = listOf("Amethyst 🔮", "Rose Quartz 💗", "Citrine 💛", "Clear Crystal 💎", "Black Tourmaline 🖤", "Moonstone 🌙", "Tiger's Eye 🐯")[seededRandom.nextInt(7)],
                dailyMessage = "Weekly fortune for day ${dayOffset + 1}"
            )
        }
    }
}
