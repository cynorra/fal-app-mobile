package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.ZodiacSign
import javax.inject.Inject
import javax.inject.Singleton

data class CompatibilityResult(
    val overallScore: Int,
    val loveScore: Int,
    val friendshipScore: Int,
    val communicationScore: Int,
    val trustScore: Int,
    val passionScore: Int,
    val strengths: List<String>,
    val challenges: List<String>,
    val advice: String,
    val element: String
)

enum class Element(val displayName: String, val emoji: String) {
    FIRE("Fire", "🔥"),
    EARTH("Earth", "🌍"),
    AIR("Air", "💨"),
    WATER("Water", "💧")
}

@Singleton
class CompatibilityRepository @Inject constructor() {
    
    fun calculateCompatibility(sign1: ZodiacSign, sign2: ZodiacSign): CompatibilityResult {
        val element1 = getElement(sign1)
        val element2 = getElement(sign2)
        
        val elementScore = calculateElementCompatibility(element1, element2)
        val modalityScore = calculateModalityCompatibility(sign1, sign2)
        val polarityScore = calculatePolarityCompatibility(sign1, sign2)
        
        val overallScore = ((elementScore * 0.5) + (modalityScore * 0.3) + (polarityScore * 0.2)).toInt()
        
        return CompatibilityResult(
            overallScore = overallScore,
            loveScore = calculateLoveScore(element1, element2),
            friendshipScore = calculateFriendshipScore(element1, element2),
            communicationScore = calculateCommunicationScore(element1, element2),
            trustScore = calculateTrustScore(element1, element2),
            passionScore = calculatePassionScore(element1, element2),
            strengths = getStrengths(element1, element2),
            challenges = getChallenges(element1, element2),
            advice = getAdvice(element1, element2),
            element = "${element1.emoji} ${element1.displayName} + ${element2.emoji} ${element2.displayName}"
        )
    }
    
    private fun getElement(sign: ZodiacSign): Element {
        return when (sign) {
            ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.SAGITTARIUS -> Element.FIRE
            ZodiacSign.TAURUS, ZodiacSign.VIRGO, ZodiacSign.CAPRICORN -> Element.EARTH
            ZodiacSign.GEMINI, ZodiacSign.LIBRA, ZodiacSign.AQUARIUS -> Element.AIR
            ZodiacSign.CANCER, ZodiacSign.SCORPIO, ZodiacSign.PISCES -> Element.WATER
        }
    }
    
    private val elementScores = mapOf(
        "${Element.FIRE}+${Element.FIRE}" to 85,
        "${Element.FIRE}+${Element.AIR}" to 95,
        "${Element.FIRE}+${Element.EARTH}" to 60,
        "${Element.FIRE}+${Element.WATER}" to 55,
        "${Element.EARTH}+${Element.EARTH}" to 80,
        "${Element.EARTH}+${Element.WATER}" to 95,
        "${Element.EARTH}+${Element.AIR}" to 55,
        "${Element.AIR}+${Element.AIR}" to 85,
        "${Element.AIR}+${Element.WATER}" to 60,
        "${Element.WATER}+${Element.WATER}" to 80
    )
    
    private fun calculateElementCompatibility(elem1: Element, elem2: Element): Int {
        val key1 = "$elem1+$elem2"
        val key2 = "$elem2+$elem1"
        return elementScores[key1] ?: elementScores[key2] ?: 70
    }
    
    private fun calculateModalityCompatibility(sign1: ZodiacSign, sign2: ZodiacSign): Int {
        val modality1 = getModality(sign1)
        val modality2 = getModality(sign2)
        return if (modality1 == modality2) 70 else 85
    }
    
    private fun getModality(sign: ZodiacSign): String {
        return when (sign) {
            ZodiacSign.ARIES, ZodiacSign.CANCER, ZodiacSign.LIBRA, ZodiacSign.CAPRICORN -> "Cardinal"
            ZodiacSign.TAURUS, ZodiacSign.LEO, ZodiacSign.SCORPIO, ZodiacSign.AQUARIUS -> "Fixed"
            ZodiacSign.GEMINI, ZodiacSign.VIRGO, ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES -> "Mutable"
        }
    }
    
    private fun calculatePolarityCompatibility(sign1: ZodiacSign, sign2: ZodiacSign): Int {
        val polarity1 = getPolarity(sign1)
        val polarity2 = getPolarity(sign2)
        return if (polarity1 != polarity2) 90 else 75
    }
    
    private fun getPolarity(sign: ZodiacSign): String {
        return when (sign) {
            ZodiacSign.ARIES, ZodiacSign.GEMINI, ZodiacSign.LEO, 
            ZodiacSign.LIBRA, ZodiacSign.SAGITTARIUS, ZodiacSign.AQUARIUS -> "Masculine"
            else -> "Feminine"
        }
    }
    
    private fun calculateLoveScore(elem1: Element, elem2: Element): Int {
        return (calculateElementCompatibility(elem1, elem2) * 1.05).toInt().coerceAtMost(100)
    }
    
    private fun calculateFriendshipScore(elem1: Element, elem2: Element): Int {
        return (calculateElementCompatibility(elem1, elem2) * 0.95).toInt()
    }
    
    private fun calculateCommunicationScore(elem1: Element, elem2: Element): Int {
        return when {
            elem1 == elem2 -> 85
            (elem1 == Element.AIR && elem2 == Element.FIRE) || 
            (elem2 == Element.AIR && elem1 == Element.FIRE) -> 95
            else -> 70
        }
    }
    
    private fun calculateTrustScore(elem1: Element, elem2: Element): Int {
        return (calculateElementCompatibility(elem1, elem2) * 0.9).toInt()
    }
    
    private fun calculatePassionScore(elem1: Element, elem2: Element): Int {
        return when {
            elem1 == Element.FIRE && elem2 == Element.FIRE -> 95
            (elem1 == Element.FIRE && elem2 == Element.AIR) || 
            (elem2 == Element.FIRE && elem1 == Element.AIR) -> 90
            elem1 == Element.WATER && elem2 == Element.WATER -> 85
            else -> 70
        }
    }
    
    private fun getStrengths(elem1: Element, elem2: Element): List<String> {
        return when {
            elem1 == elem2 -> listOf("Deep understanding", "Shared values", "Natural harmony")
            (elem1 == Element.FIRE && elem2 == Element.AIR) || 
            (elem2 == Element.FIRE && elem1 == Element.AIR) -> 
                listOf("Inspiring communication", "Shared passion", "Dynamic energy")
            (elem1 == Element.EARTH && elem2 == Element.WATER) || 
            (elem2 == Element.EARTH && elem1 == Element.WATER) -> 
                listOf("Emotional security", "Practical support", "Deep bond")
            (elem1 == Element.FIRE && elem2 == Element.FIRE) -> 
                listOf("Shared passion", "Mutual understanding", "Dynamic energy")
            (elem1 == Element.EARTH && elem2 == Element.EARTH) -> 
                listOf("Stability", "Reliability", "Practical support")
            (elem1 == Element.AIR && elem2 == Element.AIR) -> 
                listOf("Intellectual connection", "Great communication", "Shared ideas")
            (elem1 == Element.WATER && elem2 == Element.WATER) -> 
                listOf("Emotional depth", "Intuitive bond", "Compassion")
            else -> listOf("Complementary traits", "Growth opportunity", "New perspectives")
        }
    }
    
    private fun getChallenges(elem1: Element, elem2: Element): List<String> {
        return when {
            (elem1 == Element.FIRE && elem2 == Element.WATER) || 
            (elem2 == Element.FIRE && elem1 == Element.WATER) -> 
                listOf("Emotional conflict", "Different needs", "Requires patience")
            (elem1 == Element.AIR && elem2 == Element.EARTH) || 
            (elem2 == Element.AIR && elem1 == Element.EARTH) -> 
                listOf("Communication gaps", "Different priorities", "Needs understanding")
            else -> listOf("Areas requiring compromise", "Different approaches")
        }
    }
    
    private fun getAdvice(elem1: Element, elem2: Element): String {
        return when {
            elem1 == elem2 -> 
                "You share the power of the same element. You understand each other deeply but may share the same blind spots. Stay open to different perspectives."
            (elem1 == Element.FIRE && elem2 == Element.AIR) || 
            (elem2 == Element.FIRE && elem1 == Element.AIR) -> 
                "Air feeds fire! Your communication is strong, your energy high. You can achieve great things together. Just remember to be patient."
            (elem1 == Element.EARTH && elem2 == Element.WATER) || 
            (elem2 == Element.EARTH && elem1 == Element.WATER) -> 
                "Water nourishes earth. Emotional depth and practical security in perfect harmony. Embrace each other's differences."
            else -> 
                "Different elements can be challenging but rewarding. You have much to learn from each other. Patience and open communication are your keys."
        }
    }
}
