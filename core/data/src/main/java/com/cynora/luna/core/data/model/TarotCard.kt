package com.cynora.luna.core.data.model

data class TarotCard(
    val id: Int,
    val name: String,
    val romanNumeral: String,
    val icon: String,
    val element: String,
    val uprightKeywords: List<String>,
    val reversedKeywords: List<String>,
    val uprightMeaning: String,
    val reversedMeaning: String,
    val loveAdvice: String,
    val careerAdvice: String
)

enum class SpreadPosition(val title: String, val subtitle: String) {
    PAST("The Past", "Foundations, recent lessons & root causes"),
    PRESENT("The Present", "Current energy, immediate choices & reality"),
    FUTURE("The Future", "Emerging outcome, destiny path & potential")
}

data class DrawnTarotCard(
    val card: TarotCard,
    val isReversed: Boolean,
    val position: SpreadPosition? = null
) {
    val activeKeywords: List<String>
        get() = if (isReversed) card.reversedKeywords else card.uprightKeywords

    val activeMeaning: String
        get() = if (isReversed) card.reversedMeaning else card.uprightMeaning

    val orientationLabel: String
        get() = if (isReversed) "Reversed" else "Upright"
}
