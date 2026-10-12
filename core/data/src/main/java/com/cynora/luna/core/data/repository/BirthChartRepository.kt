package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.ZodiacSign
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

data class BirthChartData(
    val sunSign: ZodiacSign,
    val moonSign: ZodiacSign,
    val risingSign: ZodiacSign,
    val mercurySign: ZodiacSign,
    val venusSign: ZodiacSign,
    val marsSign: ZodiacSign,
    val numerologyLifePath: Int,
    val chineseZodiac: String,
    val birthCard: String,
    val personality: String,
    val strengths: List<String>,
    val challenges: List<String>
)

data class BirthInfo(
    val birthDate: LocalDate,
    val birthTime: LocalTime?,
    val birthPlace: String?
)

@Singleton
class BirthChartRepository @Inject constructor() {
    
    fun calculateBirthChart(birthInfo: BirthInfo): BirthChartData {
        val sunSign = calculateSunSign(birthInfo.birthDate)
        val moonSign = calculateMoonSign(birthInfo.birthDate)
        val risingSign = calculateRisingSign(birthInfo.birthDate, birthInfo.birthTime)
        val mercurySign = calculateMercurySign(birthInfo.birthDate)
        val venusSign = calculateVenusSign(birthInfo.birthDate)
        val marsSign = calculateMarsSign(birthInfo.birthDate)
        val lifePath = calculateLifePathNumber(birthInfo.birthDate)
        val chineseZodiac = calculateChineseZodiac(birthInfo.birthDate.year)
        val birthCard = calculateBirthCard(birthInfo.birthDate)
        
        return BirthChartData(
            sunSign = sunSign,
            moonSign = moonSign,
            risingSign = risingSign,
            mercurySign = mercurySign,
            venusSign = venusSign,
            marsSign = marsSign,
            numerologyLifePath = lifePath,
            chineseZodiac = chineseZodiac,
            birthCard = birthCard,
            personality = getPersonality(sunSign, moonSign, risingSign),
            strengths = getStrengths(sunSign),
            challenges = getChallenges(sunSign)
        )
    }
    
    private fun calculateSunSign(date: LocalDate): ZodiacSign {
        val month = date.monthNumber
        val day = date.dayOfMonth
        return when {
            (month == 3 && day >= 21) || (month == 4 && day <= 19) -> ZodiacSign.ARIES
            (month == 4 && day >= 20) || (month == 5 && day <= 20) -> ZodiacSign.TAURUS
            (month == 5 && day >= 21) || (month == 6 && day <= 20) -> ZodiacSign.GEMINI
            (month == 6 && day >= 21) || (month == 7 && day <= 22) -> ZodiacSign.CANCER
            (month == 7 && day >= 23) || (month == 8 && day <= 22) -> ZodiacSign.LEO
            (month == 8 && day >= 23) || (month == 9 && day <= 22) -> ZodiacSign.VIRGO
            (month == 9 && day >= 23) || (month == 10 && day <= 22) -> ZodiacSign.LIBRA
            (month == 10 && day >= 23) || (month == 11 && day <= 21) -> ZodiacSign.SCORPIO
            (month == 11 && day >= 22) || (month == 12 && day <= 21) -> ZodiacSign.SAGITTARIUS
            (month == 12 && day >= 22) || (month == 1 && day <= 19) -> ZodiacSign.CAPRICORN
            (month == 1 && day >= 20) || (month == 2 && day <= 18) -> ZodiacSign.AQUARIUS
            else -> ZodiacSign.PISCES
        }
    }
    
    private fun calculateMoonSign(date: LocalDate): ZodiacSign {
        val knownMoonInAries = LocalDate(2000, 1, 6)
        val daysSince = date.toEpochDays() - knownMoonInAries.toEpochDays()
        val daysPerSign = 27.32 / 12.0
        val signIndex = ((daysSince / daysPerSign) % 12).toInt()
        return ZodiacSign.entries[(signIndex + 12) % 12]
    }
    
    private fun calculateRisingSign(date: LocalDate, time: LocalTime?): ZodiacSign {
        if (time == null) return calculateSunSign(date)
        val hour = time.hour
        val sunSign = calculateSunSign(date)
        val offset = hour / 2
        val risingIndex = (sunSign.ordinal + offset) % 12
        return ZodiacSign.entries[risingIndex]
    }
    
    private fun calculateMercurySign(date: LocalDate): ZodiacSign {
        val sunSign = calculateSunSign(date)
        val dayOfYear = date.dayOfYear
        val offset = if (dayOfYear % 2 == 0) 1 else -1
        val index = (sunSign.ordinal + offset + 12) % 12
        return ZodiacSign.entries[index]
    }
    
    private fun calculateVenusSign(date: LocalDate): ZodiacSign {
        val dayOfYear = date.dayOfYear
        val index = (dayOfYear / 15) % 12
        return ZodiacSign.entries[index]
    }
    
    private fun calculateMarsSign(date: LocalDate): ZodiacSign {
        val dayOfYear = date.dayOfYear
        val index = (dayOfYear / 20) % 12
        return ZodiacSign.entries[index]
    }
    
    private fun calculateLifePathNumber(date: LocalDate): Int {
        val allDigits = buildString {
            append(date.year)
            append(date.monthNumber)
            append(date.dayOfMonth)
        }.map { it.toString().toInt() }
        
        var sum = allDigits.sum()
        
        fun reduce(n: Int): Int {
            if (n == 11 || n == 22 || n == 33) return n
            if (n <= 9) return n
            return reduce(n.toString().map { it.toString().toInt() }.sum())
        }
        
        return reduce(sum)
    }
    
    private fun calculateChineseZodiac(year: Int): String {
        val animals = listOf("Rat 🐀", "Ox 🐂", "Tiger 🐅", "Rabbit 🐇", 
                           "Dragon 🐉", "Snake 🐍", "Horse 🐎", "Goat 🐐",
                           "Monkey 🐒", "Rooster 🐓", "Dog 🐕", "Pig 🐖")
        return animals[(year - 4) % 12]
    }
    
    private fun calculateBirthCard(date: LocalDate): String {
        val sum = date.dayOfMonth + date.monthNumber
        val cardNumber = if (sum > 22) sum - 22 else sum
        val cards = listOf(
            "The Fool", "The Magician", "The High Priestess", "The Empress",
            "The Emperor", "The Hierophant", "The Lovers", "The Chariot",
            "Strength", "The Hermit", "Wheel of Fortune", "Justice",
            "The Hanged Man", "Death", "Temperance", "The Devil",
            "The Tower", "The Star", "The Moon", "The Sun",
            "Judgement", "The World"
        )
        return cards[(cardNumber - 1) % 22]
    }
    
    private fun getPersonality(sun: ZodiacSign, moon: ZodiacSign, rising: ZodiacSign): String {
        return "With ${sun.displayName} Sun, ${moon.displayName} Moon, and ${rising.displayName} Rising, " +
               "you have a ${getPersonalityTrait(sun)} soul. " +
               "Your emotional world is shaped by ${moon.displayName}'s depth, " +
               "while you appear to the world with ${rising.displayName}'s energy."
    }
    
    private fun getPersonalityTrait(sign: ZodiacSign): String {
        return when (sign) {
            ZodiacSign.ARIES -> "bold and pioneering"
            ZodiacSign.TAURUS -> "steady and reliable"
            ZodiacSign.GEMINI -> "curious and communicative"
            ZodiacSign.CANCER -> "nurturing and protective"
            ZodiacSign.LEO -> "charismatic and creative"
            ZodiacSign.VIRGO -> "analytical and perfectionist"
            ZodiacSign.LIBRA -> "diplomatic and harmonious"
            ZodiacSign.SCORPIO -> "passionate and deep"
            ZodiacSign.SAGITTARIUS -> "adventurous and free-spirited"
            ZodiacSign.CAPRICORN -> "disciplined and ambitious"
            ZodiacSign.AQUARIUS -> "innovative and visionary"
            ZodiacSign.PISCES -> "intuitive and empathetic"
        }
    }
    
    private fun getStrengths(sign: ZodiacSign): List<String> {
        return when (sign) {
            ZodiacSign.ARIES -> listOf("Courage", "Leadership", "Energy", "Honesty")
            ZodiacSign.TAURUS -> listOf("Patience", "Reliability", "Practicality", "Loyalty")
            ZodiacSign.GEMINI -> listOf("Intelligence", "Adaptability", "Communication", "Curiosity")
            ZodiacSign.CANCER -> listOf("Empathy", "Compassion", "Intuition", "Protectiveness")
            ZodiacSign.LEO -> listOf("Charisma", "Creativity", "Generosity", "Confidence")
            ZodiacSign.VIRGO -> listOf("Analytical", "Attention to Detail", "Organization", "Service")
            ZodiacSign.LIBRA -> listOf("Diplomacy", "Justice", "Harmony", "Romance")
            ZodiacSign.SCORPIO -> listOf("Passion", "Determination", "Intuition", "Transformation")
            ZodiacSign.SAGITTARIUS -> listOf("Optimism", "Adventure", "Philosophy", "Honesty")
            ZodiacSign.CAPRICORN -> listOf("Discipline", "Ambition", "Responsibility", "Strategy")
            ZodiacSign.AQUARIUS -> listOf("Innovation", "Independence", "Humanity", "Vision")
            ZodiacSign.PISCES -> listOf("Intuition", "Empathy", "Creativity", "Spirituality")
        }
    }
    
    private fun getChallenges(sign: ZodiacSign): List<String> {
        return when (sign) {
            ZodiacSign.ARIES -> listOf("Impatience", "Impulsiveness", "Stubbornness")
            ZodiacSign.TAURUS -> listOf("Stubbornness", "Comfort Zone", "Jealousy")
            ZodiacSign.GEMINI -> listOf("Indecisiveness", "Superficiality", "Restlessness")
            ZodiacSign.CANCER -> listOf("Over-emotionality", "Holding onto past", "Moodiness")
            ZodiacSign.LEO -> listOf("Pride", "Need for attention", "Stubbornness")
            ZodiacSign.VIRGO -> listOf("Perfectionism", "Worry", "Critical nature")
            ZodiacSign.LIBRA -> listOf("Indecisiveness", "Conflict avoidance", "Dependency")
            ZodiacSign.SCORPIO -> listOf("Jealousy", "Revenge", "Trust issues")
            ZodiacSign.SAGITTARIUS -> listOf("Carelessness", "Bluntness", "Impatience")
            ZodiacSign.CAPRICORN -> listOf("Workaholism", "Rigidity", "Pessimism")
            ZodiacSign.AQUARIUS -> listOf("Emotional distance", "Rebelliousness", "Detachment")
            ZodiacSign.PISCES -> listOf("Escapism", "Oversensitivity", "Victim mentality")
        }
    }
}
