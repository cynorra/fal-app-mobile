package com.cynora.luna.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

data class Affirmation(
    val id: String,
    val text: String,
    val category: AffirmationCategory
)

enum class AffirmationCategory(val id: String, val displayName: String, val icon: String) {
    LOVE("love", "Love & Relationships", "💕"),
    ABUNDANCE("abundance", "Abundance & Wealth", "💰"),
    HEALTH("health", "Health & Healing", "🌿"),
    CONFIDENCE("confidence", "Confidence & Power", "💪"),
    PEACE("peace", "Peace & Calm", "🕊️"),
    SUCCESS("success", "Success & Career", "🏆"),
    SPIRITUAL("spiritual", "Spiritual Awakening", "✨"),
    GRATITUDE("gratitude", "Gratitude", "🙏")
}

@Singleton
class AffirmationRepository @Inject constructor() {
    
    private val affirmations = listOf(
        // Love & Relationships
        Affirmation("love_1", "I am worthy of love and it flows into my life.", AffirmationCategory.LOVE),
        Affirmation("love_2", "My heart is open to love and filled with trust.", AffirmationCategory.LOVE),
        Affirmation("love_3", "I attract my soulmate's energy.", AffirmationCategory.LOVE),
        Affirmation("love_4", "My relationships are filled with love, respect, and passion.", AffirmationCategory.LOVE),
        Affirmation("love_5", "I love and accept myself unconditionally.", AffirmationCategory.LOVE),
        Affirmation("love_6", "My love life grows more beautiful every day.", AffirmationCategory.LOVE),
        Affirmation("love_7", "I deserve a healthy and happy relationship.", AffirmationCategory.LOVE),
        Affirmation("love_8", "I release the past and open myself to new love.", AffirmationCategory.LOVE),
        
        // Abundance & Wealth
        Affirmation("abundance_1", "Abundance and prosperity flow into my life.", AffirmationCategory.ABUNDANCE),
        Affirmation("abundance_2", "Money comes to me easily and joyfully.", AffirmationCategory.ABUNDANCE),
        Affirmation("abundance_3", "I deserve financial freedom.", AffirmationCategory.ABUNDANCE),
        Affirmation("abundance_4", "I am open to the universe's abundance.", AffirmationCategory.ABUNDANCE),
        Affirmation("abundance_5", "More prosperity enters my life every day.", AffirmationCategory.ABUNDANCE),
        Affirmation("abundance_6", "I have a wealth consciousness.", AffirmationCategory.ABUNDANCE),
        Affirmation("abundance_7", "I am in harmony with money energy.", AffirmationCategory.ABUNDANCE),
        Affirmation("abundance_8", "I give generously and receive generously.", AffirmationCategory.ABUNDANCE),
        
        // Health & Healing
        Affirmation("health_1", "My body is healthy, my mind is clear, my soul is at peace.", AffirmationCategory.HEALTH),
        Affirmation("health_2", "Healing energy flows through my body.", AffirmationCategory.HEALTH),
        Affirmation("health_3", "I treat myself with compassion.", AffirmationCategory.HEALTH),
        Affirmation("health_4", "I listen to my body's wisdom.", AffirmationCategory.HEALTH),
        Affirmation("health_5", "Every cell of mine is filled with health and vitality.", AffirmationCategory.HEALTH),
        Affirmation("health_6", "My energy is high, my spirit is light.", AffirmationCategory.HEALTH),
        Affirmation("health_7", "I love myself and take care of my body.", AffirmationCategory.HEALTH),
        Affirmation("health_8", "I am healing and growing stronger every day.", AffirmationCategory.HEALTH),
        
        // Confidence & Power
        Affirmation("confidence_1", "I am powerful, confident, and capable.", AffirmationCategory.CONFIDENCE),
        Affirmation("confidence_2", "I believe in myself and my abilities.", AffirmationCategory.CONFIDENCE),
        Affirmation("confidence_3", "My inner strength grows every day.", AffirmationCategory.CONFIDENCE),
        Affirmation("confidence_4", "I face challenges with courage and grace.", AffirmationCategory.CONFIDENCE),
        Affirmation("confidence_5", "I am enough, exactly as I am.", AffirmationCategory.CONFIDENCE),
        Affirmation("confidence_6", "My voice matters and my opinion counts.", AffirmationCategory.CONFIDENCE),
        Affirmation("confidence_7", "I radiate confidence and attract success.", AffirmationCategory.CONFIDENCE),
        Affirmation("confidence_8", "I am the creator of my own reality.", AffirmationCategory.CONFIDENCE),
        
        // Peace & Calm
        Affirmation("peace_1", "I am at peace with myself and the world.", AffirmationCategory.PEACE),
        Affirmation("peace_2", "Calm flows through me like a gentle river.", AffirmationCategory.PEACE),
        Affirmation("peace_3", "I release what I cannot control.", AffirmationCategory.PEACE),
        Affirmation("peace_4", "My mind is quiet, my heart is still.", AffirmationCategory.PEACE),
        Affirmation("peace_5", "I choose peace over worry.", AffirmationCategory.PEACE),
        Affirmation("peace_6", "I breathe in calm, I breathe out tension.", AffirmationCategory.PEACE),
        Affirmation("peace_7", "I am safe, I am loved, I am at peace.", AffirmationCategory.PEACE),
        Affirmation("peace_8", "Serenity is my natural state.", AffirmationCategory.PEACE),
        
        // Success & Career
        Affirmation("success_1", "I am aligned with my purpose and passion.", AffirmationCategory.SUCCESS),
        Affirmation("success_2", "Success flows to me effortlessly.", AffirmationCategory.SUCCESS),
        Affirmation("success_3", "I am a magnet for opportunities.", AffirmationCategory.SUCCESS),
        Affirmation("success_4", "My work brings value and fulfillment.", AffirmationCategory.SUCCESS),
        Affirmation("success_5", "I am constantly growing and evolving.", AffirmationCategory.SUCCESS),
        Affirmation("success_6", "I attract success in all areas of my life.", AffirmationCategory.SUCCESS),
        Affirmation("success_7", "My potential is limitless.", AffirmationCategory.SUCCESS),
        Affirmation("success_8", "I create my own luck through action.", AffirmationCategory.SUCCESS),
        
        // Spiritual Awakening
        Affirmation("spiritual_1", "I am connected to the divine source.", AffirmationCategory.SPIRITUAL),
        Affirmation("spiritual_2", "My intuition guides me perfectly.", AffirmationCategory.SPIRITUAL),
        Affirmation("spiritual_3", "I am awakening to my highest self.", AffirmationCategory.SPIRITUAL),
        Affirmation("spiritual_4", "The universe supports me in every way.", AffirmationCategory.SPIRITUAL),
        Affirmation("spiritual_5", "I trust the journey of my soul.", AffirmationCategory.SPIRITUAL),
        Affirmation("spiritual_6", "I am a spiritual being having a human experience.", AffirmationCategory.SPIRITUAL),
        Affirmation("spiritual_7", "Divine wisdom flows through me.", AffirmationCategory.SPIRITUAL),
        Affirmation("spiritual_8", "I am one with the infinite universe.", AffirmationCategory.SPIRITUAL),
        
        // Gratitude
        Affirmation("gratitude_1", "I am grateful for this beautiful day.", AffirmationCategory.GRATITUDE),
        Affirmation("gratitude_2", "My heart overflows with gratitude.", AffirmationCategory.GRATITUDE),
        Affirmation("gratitude_3", "I appreciate the blessings in my life.", AffirmationCategory.GRATITUDE),
        Affirmation("gratitude_4", "Every moment is a gift I cherish.", AffirmationCategory.GRATITUDE),
        Affirmation("gratitude_5", "I am thankful for my journey and growth.", AffirmationCategory.GRATITUDE),
        Affirmation("gratitude_6", "Gratitude transforms my life.", AffirmationCategory.GRATITUDE),
        Affirmation("gratitude_7", "I see beauty in everything around me.", AffirmationCategory.GRATITUDE),
        Affirmation("gratitude_8", "My life is filled with reasons to be thankful.", AffirmationCategory.GRATITUDE)
    )
    
    fun getDailyAffirmation(): Affirmation {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dayOfYear = today.dayOfYear
        return affirmations[dayOfYear % affirmations.size]
    }
    
    fun getAffirmationsByCategory(category: AffirmationCategory): List<Affirmation> {
        return affirmations.filter { it.category == category }
    }
    
    fun getAllAffirmations(): Flow<List<Affirmation>> {
        return flowOf(affirmations)
    }
    
    fun getRandomAffirmation(): Affirmation {
        return affirmations.random()
    }
}
