package com.cynora.luna.core.data.model

enum class ZodiacElement(val displayName: String, val emoji: String, val colorHex: Long) {
    FIRE("Fire", "🔥", 0xFFFF5722),
    EARTH("Earth", "🌿", 0xFF8BC34A),
    AIR("Air", "💨", 0xFF03A9F4),
    WATER("Water", "🌊", 0xFF00BCD4)
}

enum class ZodiacModality(val displayName: String) {
    CARDINAL("Cardinal (Initiating)"),
    FIXED("Fixed (Sustaining)"),
    MUTABLE("Mutable (Adapting)")
}

data class ZodiacDetail(
    val sign: ZodiacSign,
    val element: ZodiacElement,
    val modality: ZodiacModality,
    val rulingPlanet: String,
    val rulingHouse: String,
    val tarotCard: String,
    val keywords: List<String>,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val loveProfile: String,
    val careerProfile: String,
    val compatibleSigns: List<ZodiacSign>
)
