package com.cynora.luna.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

data class TarotCard(
    val id: Int,
    val name: String,
    val emoji: String,
    val keywords: List<String>,
    val generalMeaning: String,
    val loveMeaning: String,
    val careerMeaning: String,
    val spiritualMeaning: String,
    val advice: String,
    val affirmation: String
)

@Singleton
class DailyCardRepository @Inject constructor(
    private val random: Random = Random.Default
) {
    
    private val majorArcana = listOf(
        TarotCard(0, "The Fool", "🃏", 
            listOf("New beginnings", "Innocence", "Adventure"),
            "A new journey begins. Trust the path ahead.",
            "A fresh start in love. Be open to unexpected connections.",
            "Take a leap of faith in your career.",
            "Embrace the beginner's mind.",
            "Say yes to new experiences today.",
            "I embrace new beginnings with an open heart."),
            
        TarotCard(1, "The Magician", "🎩",
            listOf("Manifestation", "Power", "Action"),
            "You have all the tools you need. Act and create.",
            "You have the power to manifest your dream relationship.",
            "Your skills are ready to be showcased.",
            "You are the magician of your own life.",
            "Focus your intention and watch miracles unfold.",
            "I am a powerful creator of my reality."),
            
        TarotCard(2, "The High Priestess", "🌙",
            listOf("Intuition", "Mystery", "Inner wisdom"),
            "Trust your intuition. The answers are within.",
            "Listen to your heart's whispers in love.",
            "Your intuitive insights guide you to success.",
            "Connect with your inner wisdom through meditation.",
            "Be still and listen to your inner voice today.",
            "I trust the wisdom of my intuition."),
            
        TarotCard(3, "The Empress", "👑",
            listOf("Abundance", "Fertility", "Nurturing"),
            "A time of abundance and growth.",
            "Love flourishes in a nurturing environment.",
            "Creative projects bloom.",
            "Connect with nature and your feminine energy.",
            "Create something beautiful today.",
            "I am abundant in all areas of my life."),
            
        TarotCard(4, "The Emperor", "🏛️",
            listOf("Authority", "Structure", "Stability"),
            "Take charge of your life with confidence.",
            "Establish healthy boundaries in relationships.",
            "Leadership opportunities arise.",
            "Build solid foundations in your spiritual practice.",
            "Create order from chaos today.",
            "I lead my life with confidence and wisdom."),
            
        TarotCard(5, "The Hierophant", "📿",
            listOf("Tradition", "Spiritual wisdom", "Education"),
            "Seek wisdom from established traditions.",
            "Consider traditional approaches to relationships.",
            "Formal education or mentorship brings growth.",
            "Explore ancient spiritual teachings.",
            "Honor your traditions while staying open to growth.",
            "I honor wisdom from all sources."),
            
        TarotCard(6, "The Lovers", "💑",
            listOf("Love", "Harmony", "Choices"),
            "An important choice lies before you. Choose with your heart.",
            "A significant relationship milestone.",
            "Partnerships and collaborations are favored.",
            "Align your actions with your highest values.",
            "Make a choice that honors both heart and mind.",
            "I choose love in all my decisions."),
            
        TarotCard(7, "The Chariot", "🏇",
            listOf("Victory", "Willpower", "Determination"),
            "Victory is yours through determination.",
            "Take the reins in your love life.",
            "Career success through focused determination.",
            "Master your inner conflicts to move forward.",
            "Drive toward your goals with unwavering focus.",
            "I am determined and victorious."),
            
        TarotCard(8, "Strength", "🦁",
            listOf("Courage", "Inner strength", "Patience"),
            "Your inner strength is your greatest asset.",
            "Approach love with compassion and patience.",
            "Your resilience will be rewarded.",
            "Tame your inner beasts with compassion.",
            "Face challenges with gentle strength today.",
            "I am strong, compassionate, and resilient."),
            
        TarotCard(9, "The Hermit", "🕯️",
            listOf("Introspection", "Solitude", "Wisdom"),
            "Retreat inward to find answers.",
            "Take time alone to understand your heart.",
            "Solitude brings career clarity.",
            "Your soul's journey requires quiet contemplation.",
            "Spend time in reflection today.",
            "I find wisdom in solitude and silence."),
            
        TarotCard(10, "Wheel of Fortune", "🎡",
            listOf("Destiny", "Cycles", "Luck"),
            "A turning point arrives. The wheel turns in your favor.",
            "Fate brings a significant relationship change.",
            "Career luck shifts in your direction.",
            "Embrace life's cycles with grace.",
            "Trust the turning of the wheel today.",
            "I embrace the cycles of life with trust."),
            
        TarotCard(11, "Justice", "⚖️",
            listOf("Fairness", "Truth", "Balance"),
            "Truth and fairness prevail. Act with integrity.",
            "Seek balance and fairness in relationships.",
            "Legal matters resolve favorably.",
            "Align your actions with universal law.",
            "Make decisions based on truth and fairness.",
            "I act with integrity and truth."),
            
        TarotCard(12, "The Hanged Man", "🙃",
            listOf("Surrender", "New perspective", "Pause"),
            "See things from a new angle. Surrender to gain wisdom.",
            "Let go of control in love.",
            "Pause in your career to gain fresh viewpoint.",
            "Surrender your ego to spiritual growth.",
            "Look at your situation upside down today.",
            "I surrender and gain new perspective."),
            
        TarotCard(13, "Death", "🦋",
            listOf("Transformation", "Endings", "Rebirth"),
            "A major transformation is underway. Embrace change.",
            "An old relationship pattern dies so a new one is born.",
            "Career transformation. What ends makes way for better.",
            "Spiritual death and rebirth.",
            "Release what no longer serves you.",
            "I embrace transformation and rebirth."),
            
        TarotCard(14, "Temperance", "🏺",
            listOf("Balance", "Moderation", "Patience"),
            "Find the middle path. Balance is your key.",
            "Harmony in relationships through patience.",
            "Balanced approach brings career success.",
            "Blend different aspects of your spiritual practice.",
            "Seek balance in all things today.",
            "I live in perfect balance and harmony."),
            
        TarotCard(15, "The Devil", "😈",
            listOf("Bondage", "Shadow self", "Materialism"),
            "Face your shadows. Break free from what binds you.",
            "Examine unhealthy patterns in relationships.",
            "Don't let material concerns trap you.",
            "Confront your shadow self with honesty.",
            "Identify and break one unhealthy pattern today.",
            "I free myself from all that binds me."),
            
        TarotCard(16, "The Tower", "⚡",
            listOf("Upheaval", "Revelation", "Awakening"),
            "Sudden change brings necessary revelation.",
            "A shocking truth about a relationship emerges.",
            "Career upheaval leads to better alignment.",
            "Spiritual awakening through sudden insight.",
            "Welcome the storm that clears the way.",
            "I welcome necessary change with courage."),
            
        TarotCard(17, "The Star", "⭐",
            listOf("Hope", "Inspiration", "Serenity"),
            "Hope shines bright. Your wishes are heard.",
            "Healing and renewal in love.",
            "Inspired career path revealed.",
            "Divine guidance flows to you.",
            "Make a wish today. The universe is listening.",
            "I am guided by hope and inspiration."),
            
        TarotCard(18, "The Moon", "🌕",
            listOf("Illusion", "Intuition", "Dreams"),
            "Not everything is as it seems. Trust intuition.",
            "Hidden emotions surface in relationships.",
            "Beware of deception in career matters.",
            "Dreams carry important messages.",
            "Explore your subconscious through dreams today.",
            "I trust my intuition in times of uncertainty."),
            
        TarotCard(19, "The Sun", "☀️",
            listOf("Joy", "Success", "Vitality"),
            "Radiant success and joy surround you.",
            "Happiness and warmth in relationships.",
            "Career success shines bright.",
            "Your spirit radiates with divine light.",
            "Share your light with the world today.",
            "I radiate joy and success."),
            
        TarotCard(20, "Judgement", "📯",
            listOf("Rebirth", "Calling", "Awakening"),
            "A higher calling beckons. Answer with your whole heart.",
            "Relationships are evaluated. Make conscious choices.",
            "Career calling becomes clear.",
            "Spiritual rebirth. Forgive yourself and rise.",
            "Answer your soul's calling today.",
            "I answer my highest calling with courage."),
            
        TarotCard(21, "The World", "🌍",
            listOf("Completion", "Accomplishment", "Wholeness"),
            "A cycle completes successfully. Celebrate!",
            "Relationship goals achieved. Wholeness in love.",
            "Major career milestone reached.",
            "Spiritual wholeness. You are complete.",
            "Celebrate how far you've come today.",
            "I am complete and whole in every way.")
    )
    
    fun getDailyCard(): TarotCard {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dayOfYear = today.dayOfYear
        return majorArcana[dayOfYear % majorArcana.size]
    }
    
    fun getRandomCard(): TarotCard {
        return majorArcana.random(random)
    }
    
    fun getCardById(id: Int): TarotCard? {
        return majorArcana.find { it.id == id }
    }
    
    fun getAllCards(): Flow<List<TarotCard>> {
        return flowOf(majorArcana)
    }
    
    fun getThreeCardSpread(): List<TarotCard> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val seed = today.toEpochDays()
        val seededRandom = Random(seed)
        return majorArcana.shuffled(seededRandom).take(3)
    }
}
