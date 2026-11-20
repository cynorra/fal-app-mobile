package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.DreamCategory
import com.cynora.luna.core.data.model.DreamSymbol
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DreamRepository @Inject constructor() {

    private val symbols = listOf(
        DreamSymbol(
            id = "water",
            keyword = "Water",
            turkishKeyword = "Su",
            icon = "🌊",
            category = DreamCategory.NATURE,
            shortSummary = "Emotional state, intuition, and spiritual cleansing.",
            detailedInterpretation = "Water in dreams mirrors the depth of your emotional subconscious. Clear, calm water signifies peace, spiritual purification, and upcoming emotional harmony. Turbulent or murky water signals unexpressed feelings, stress, or turbulent life transitions waiting to be healed.",
            cosmicAdvice = "Drink fresh water upon waking and allow suppressed emotions to flow without judgment."
        ),
        DreamSymbol(
            id = "snake",
            keyword = "Snake",
            turkishKeyword = "Yılan",
            icon = "🐍",
            category = DreamCategory.ANIMALS,
            shortSummary = "Profound rebirth, secret wisdom, or hidden envy.",
            detailedInterpretation = "The serpent is an ancient symbol of shedding old skin, spiritual kundalini awakening, and metamorphosis. If peaceful, it represents healing and hidden wisdom emerging into consciousness. If threatening, it warns of toxic individuals or envy lurking in your immediate circle.",
            cosmicAdvice = "Set firm personal boundaries and embrace the transformation unfolding in your life."
        ),
        DreamSymbol(
            id = "falling",
            keyword = "Falling",
            turkishKeyword = "Düşmek",
            icon = "💫",
            category = DreamCategory.WARNINGS,
            shortSummary = "Loss of control, high stress, or release of anxiety.",
            detailedInterpretation = "Dreams of falling signify feelings of instability in your career, relationships, or personal plans. Your subconscious is alerting you that you are gripping too tightly to outcomes you cannot control.",
            cosmicAdvice = "Ground yourself today through deep breaths. Surrendering control brings unexpected peace."
        ),
        DreamSymbol(
            id = "flying",
            keyword = "Flying",
            turkishKeyword = "Uçmak",
            icon = "🦅",
            category = DreamCategory.SPIRITUAL,
            shortSummary = "Freedom, elevated perspective, and spiritual transcendence.",
            detailedInterpretation = "Soaring in the sky indicates liberation from a restricting situation, high creative energy, and rising above petty mundane disputes. It is a sign of awakening willpower and creative triumph.",
            cosmicAdvice = "Trust your grandest vision; you now have the aerial clarity to execute your goals."
        ),
        DreamSymbol(
            id = "teeth",
            keyword = "Teeth Falling Out",
            turkishKeyword = "Diş Dökülmesi",
            icon = "🦷",
            category = DreamCategory.WARNINGS,
            shortSummary = "Vulnerability, fear of aging, or unspoken truths.",
            detailedInterpretation = "Losing teeth symbolizes anxiety over personal power, aging, or regret over words spoken in haste. It indicates a period of rapid life change where you feel momentarily exposed.",
            cosmicAdvice = "Speak your truth with gentle clarity; your inner authority cannot be shaken."
        ),
        DreamSymbol(
            id = "fire",
            keyword = "Fire",
            turkishKeyword = "Ateş",
            icon = "🔥",
            category = DreamCategory.NATURE,
            shortSummary = "Intense passion, purification, anger, or rapid transformation.",
            detailedInterpretation = "A contained, warm fire signifies warmth, passion, inspiration, and domestic comfort. A wild, raging fire warns of burnout, suppressed anger, or the complete burning away of obsolete habits to make way for new beginnings.",
            cosmicAdvice = "Channel raw emotional energy into physical activity or creative projects today."
        ),
        DreamSymbol(
            id = "house",
            keyword = "House / Rooms",
            turkishKeyword = "Ev / Odalar",
            icon = "🏠",
            category = DreamCategory.SPIRITUAL,
            shortSummary = "The self, hidden psyche, and unexplored potential.",
            detailedInterpretation = "The house is the architectural representation of your mind and soul. Discovering new or secret rooms indicates discovering latent talents, psychic abilities, or new avenues of life purpose.",
            cosmicAdvice = "Declutter your physical living space today to mirror the spiritual expansion of your mind."
        ),
        DreamSymbol(
            id = "gold",
            keyword = "Gold / Wealth",
            turkishKeyword = "Altın / Para",
            icon = "💰",
            category = DreamCategory.ABUNDANCE,
            shortSummary = "Self-worth, incoming abundance, and spiritual riches.",
            detailedInterpretation = "Holding or finding gold indicates a sudden awakening of self-esteem and the ripening of financial or creative investments. The universe acknowledges your hard work and prepares fruitful returns.",
            cosmicAdvice = "Acknowledge your own worth; do not settle for less than what you deserve."
        ),
        DreamSymbol(
            id = "mirror",
            keyword = "Mirror",
            turkishKeyword = "Ayna",
            icon = "🪞",
            category = DreamCategory.SPIRITUAL,
            shortSummary = "Self-reflection, truth, and shadow work.",
            detailedInterpretation = "Looking into a mirror reflects your relationship with your authentic self. A broken mirror urges healing fragmented thoughts, while a crystal-clear reflection shows deep alignment with your cosmic path.",
            cosmicAdvice = "Practice loving self-acceptance in the mirror today."
        ),
        DreamSymbol(
            id = "wedding",
            keyword = "Wedding",
            turkishKeyword = "Düğün",
            icon = "💍",
            category = DreamCategory.LOVE,
            shortSummary = "Sacred union, integration of masculine & feminine, commitments.",
            detailedInterpretation = "Attending or participating in a wedding signifies commitment, partnership harmonization, or the union of your inner logical and intuitive energies. A harmonious chapter in relationships is commencing.",
            cosmicAdvice = "Nurture connections that offer reciprocal respect and emotional safety."
        ),
        DreamSymbol(
            id = "keys",
            keyword = "Keys",
            turkishKeyword = "Anahtar",
            icon = "🗝️",
            category = DreamCategory.ABUNDANCE,
            shortSummary = "Access, unlocked secrets, solutions, and opportunities.",
            detailedInterpretation = "Finding or holding keys signifies that a long-standing deadlock or puzzle in your career, finance, or love life is about to unlock. You already hold the solution within your intuition.",
            cosmicAdvice = "Take the bold initiative to open conversations that have been delayed."
        ),
        DreamSymbol(
            id = "cat",
            keyword = "Cat",
            turkishKeyword = "Kedi",
            icon = "🐈",
            category = DreamCategory.ANIMALS,
            shortSummary = "Independence, feminine mystery, sensuality, and intuition.",
            detailedInterpretation = "Cats in dreams are spiritual guardians calling you to trust your gut instincts over logical arguments. They invite you to be independent, playful, and sovereign in your boundaries.",
            cosmicAdvice = "Take time for quiet solitude and honor your instinctual hunches."
        )
    )

    fun getAllSymbols(): List<DreamSymbol> = symbols

    fun searchSymbols(query: String, category: DreamCategory? = null): List<DreamSymbol> {
        val q = query.trim().lowercase()
        return symbols.filter { sym ->
            val matchesCategory = category == null || sym.category == category
            val matchesQuery = q.isEmpty() ||
                    sym.keyword.lowercase().contains(q) ||
                    sym.turkishKeyword.lowercase().contains(q) ||
                    sym.shortSummary.lowercase().contains(q) ||
                    sym.detailedInterpretation.lowercase().contains(q)
            matchesCategory && matchesQuery
        }
    }
}
