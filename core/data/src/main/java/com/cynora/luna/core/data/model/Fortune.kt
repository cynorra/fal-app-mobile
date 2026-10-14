package com.cynora.luna.core.data.model

enum class FortuneType(val id: String, val displayName: String, val icon: String) {
    COFFEE("coffee", "Coffee Reading", "☕"),
    TAROT("tarot", "Tarot", "🎴"),
    LENORMAND("lenormand", "Lenormand", "🃏"),
    ANGEL_CARDS("angel", "Angel Cards", "👼"),
    DAILY_HOROSCOPE("horoscope", "Horoscope", "⭐"),
    DREAM("dream", "Dream Interpretation", "💭"),
    PALMISTRY("palm", "Palmistry", "✋"),
    LOVE("love", "Love Fortune", "💕"),
    NUMEROLOGY("numerology", "Numerology", "🔢"),
    COMBINATION("combo", "Combination", "✨"),
    BIRTH_CHART("birth_chart", "Birth Chart", "🌌"),
    ZODIAC_COMPATIBILITY("compatibility", "Compatibility", "💑"),
    DAILY_CARD("daily_card", "Daily Card", "🎯"),
    ANGEL_NUMBERS("angel_numbers", "Angel Numbers", "🔮"),
    AFFIRMATION("affirmation", "Affirmations", "💫"),
    LUCKY_NUMBERS("lucky_numbers", "Lucky Numbers", "🍀");

    companion object {
        fun fromId(id: String): FortuneType {
            return entries.find { it.id == id } ?: COFFEE
        }
    }
}

enum class Mood(val id: String, val displayName: String) {
    STRESSED("stressed", "Stressed"),
    HOPEFUL("hopeful", "Hopeful"),
    CONFUSED("confused", "Confused"),
    EXCITED("excited", "Excited"),
    TIRED("tired", "Tired"),
    PEACEFUL("peaceful", "Peaceful");

    companion object {
        fun fromId(id: String): Mood {
            return entries.find { it.id == id } ?: HOPEFUL
        }
    }
}

enum class FocusArea(val id: String, val displayName: String) {
    LOVE("love", "Love & Relationships"),
    CAREER("career", "Career & Work"),
    MONEY("money", "Money & Abundance"),
    HEALTH("health", "Health & Energy"),
    GENERAL("general", "General Energy"),
    SPIRITUAL("spiritual", "Spiritual Growth");

    companion object {
        fun fromId(id: String): FocusArea {
            return entries.find { it.id == id } ?: GENERAL
        }
    }
}

enum class Rarity(val id: String, val displayName: String, val colorHex: Long) {
    COMMON("common", "Common", 0xFF9E9E9E),
    RARE("rare", "Rare", 0xFF2196F3),
    EPIC("epic", "Epic", 0xFF9C27B0),
    LEGENDARY("legendary", "Legendary", 0xFFFFD700);

    companion object {
        fun fromId(id: String): Rarity {
            return entries.find { it.id == id } ?: COMMON
        }
    }
}

data class Fortune(
    val id: String,
    val type: FortuneType,
    val text: String,
    val dailyIntention: String,
    val moodTags: List<Mood>,
    val focusTags: List<FocusArea>,
    val keywordTags: List<String>,
    val rarity: Rarity,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun calculateMatchScore(userMood: Mood, userFocus: FocusArea): Int {
        var score = 0
        if (moodTags.contains(userMood)) score += 40
        if (focusTags.contains(userFocus)) score += 40
        score += when (rarity) {
            Rarity.COMMON -> 5
            Rarity.RARE -> 10
            Rarity.EPIC -> 15
            Rarity.LEGENDARY -> 20
        }
        return score
    }
}
