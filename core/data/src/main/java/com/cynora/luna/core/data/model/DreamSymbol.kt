package com.cynora.luna.core.data.model

enum class DreamCategory(val displayName: String, val icon: String) {
    LOVE("Love & Bonds", "💕"),
    WARNINGS("Warnings & Shadow", "⚡"),
    ABUNDANCE("Wealth & Prosperity", "🌟"),
    SPIRITUAL("Spiritual & Transformation", "🔮"),
    NATURE("Nature & Elements", "🌊"),
    ANIMALS("Creatures & Animals", "🦅")
}

data class DreamSymbol(
    val id: String,
    val keyword: String,
    val turkishKeyword: String,
    val icon: String,
    val category: DreamCategory,
    val shortSummary: String,
    val detailedInterpretation: String,
    val cosmicAdvice: String
)
