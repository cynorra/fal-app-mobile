package com.cynora.luna.core.data.model

enum class OracleOutcome(val displayName: String, val emoji: String, val colorHex: Long) {
    YES("YES", "✨", 0xFF4CAF50),
    NO("NO", "🌑", 0xFFE57373),
    MAYBE("UNCERTAIN / WAIT", "🔮", 0xFFFFD700)
}

data class OracleReading(
    val outcome: OracleOutcome,
    val headline: String,
    val cosmicAlignment: String,
    val spiritualAdvice: String,
    val luckyAspect: String,
    val timestamp: Long = System.currentTimeMillis()
)
