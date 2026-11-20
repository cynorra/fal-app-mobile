package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.DrawnTarotCard
import com.cynora.luna.core.data.model.SpreadPosition
import com.cynora.luna.core.data.model.TarotCard
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class TarotRepository @Inject constructor() {

    private val random = Random.Default

    val majorArcana = listOf(
        TarotCard(
            id = 0, name = "The Fool", romanNumeral = "0", icon = "🎒", element = "Air",
            uprightKeywords = listOf("New Beginnings", "Innocence", "Leap of Faith", "Spontaneity"),
            reversedKeywords = listOf("Recklessness", "Hesitation", "Naivety", "Risk-taking"),
            uprightMeaning = "The Fool signals the start of an exhilarating new chapter. Embrace the unknown with childlike wonder and leap boldly forward.",
            reversedMeaning = "Beware of careless impulses or refusing to look before you leap. Gather essential facts before acting.",
            loveAdvice = "Be open to unexpected encounters and let go of past baggage.",
            careerAdvice = "A bold creative initiative or career pivot is blessed with favorable winds."
        ),
        TarotCard(
            id = 1, name = "The Magician", romanNumeral = "I", icon = "🪄", element = "Mercury",
            uprightKeywords = listOf("Manifestation", "Resourcefulness", "Power", "Skill"),
            reversedKeywords = listOf("Manipulation", "Wasted Talent", "Untapped Potential"),
            uprightMeaning = "You hold all tools, elements, and willpower necessary to turn your highest vision into tangible reality.",
            reversedMeaning = "You may be doubting your own abilities or directing your power into superficial manipulation. Refocus your intent.",
            loveAdvice = "Communicate your authentic desires clearly; your personal magnetism is at an all-time peak.",
            careerAdvice = "Take lead on projects; your problem-solving brilliance commands authority."
        ),
        TarotCard(
            id = 2, name = "The High Priestess", romanNumeral = "II", icon = "🌙", element = "Water",
            uprightKeywords = listOf("Intuition", "Sacred Knowledge", "Divine Feminine", "Subconscious"),
            reversedKeywords = listOf("Secrets", "Disconnected Intuition", "Superficiality"),
            uprightMeaning = "Look past surface noise. The answers you seek are already whispering in your deep inner stillness.",
            reversedMeaning = "You may be ignoring gut red flags or being overwhelmed by hidden gossip. Quiet your mind.",
            loveAdvice = "Trust your intuitive feelings about your partner rather than overanalyzing their words.",
            careerAdvice = "Observe organizational dynamics quietly before making your strategic move."
        ),
        TarotCard(
            id = 3, name = "The Empress", romanNumeral = "III", icon = "👑", element = "Earth",
            uprightKeywords = listOf("Abundance", "Sensuality", "Creativity", "Fertility"),
            reversedKeywords = listOf("Creative Block", "Codependency", "Neglect"),
            uprightMeaning = "Lavish abundance, emotional warmth, and creative fertility. Your projects and relationships bloom with grace.",
            reversedMeaning = "Give yourself the nurturing you give so freely to others. Overcoming burnout is your top priority.",
            loveAdvice = "Cultivate sensuality, gentle patience, and heartwarming moments together.",
            careerAdvice = "Creative collaborations, branding, and design initiatives thrive effortlessly."
        ),
        TarotCard(
            id = 4, name = "The Emperor", romanNumeral = "IV", icon = "🏛️", element = "Fire",
            uprightKeywords = listOf("Authority", "Structure", "Stability", "Protection"),
            reversedKeywords = listOf("Tyranny", "Rigidity", "Lack of Discipline"),
            uprightMeaning = "A call for solid structures, strategic discipline, and firm personal boundaries. You are the sovereign ruler of your world.",
            reversedMeaning = "Loosen the grip of micromanagement. Being overly rigid blocks creative breakthroughs.",
            loveAdvice = "Provide consistency and dependable emotional safety for those you love.",
            careerAdvice = "Organize workflows, establish boundaries, and lead with steady confidence."
        ),
        TarotCard(
            id = 5, name = "The Hierophant", romanNumeral = "V", icon = "📜", element = "Earth",
            uprightKeywords = listOf("Spiritual Wisdom", "Tradition", "Mentorship", "Institutions"),
            reversedKeywords = listOf("Rebellion", "New Beliefs", "Dogma"),
            uprightMeaning = "Seek wisdom from proven principles, spiritual lineages, or experienced mentors. Honor sacred agreements.",
            reversedMeaning = "Question outdated dogmas or traditions that no longer resonate with your authentic truth.",
            loveAdvice = "Shared core values and honest communication forge unshakable bonds.",
            careerAdvice = "Align with reputable organizations or seek the counsel of a seasoned elder."
        ),
        TarotCard(
            id = 6, name = "The Lovers", romanNumeral = "VI", icon = "💞", element = "Air",
            uprightKeywords = listOf("Sacred Union", "Harmony", "Values Alignment", "Choice"),
            reversedKeywords = listOf("Disharmony", "Mismatch", "Inner Conflict"),
            uprightMeaning = "A profound union of hearts and minds. Represents crucial moral choices guided by authentic love.",
            reversedMeaning = "A disconnect between your actions and your values. Resolve inner turmoil before pointing fingers.",
            loveAdvice = "Choose vulnerability and deep communion; emotional honesty opens the door to ecstasy.",
            careerAdvice = "Choose business partners whose integrity and principles mirror your own."
        ),
        TarotCard(
            id = 7, name = "The Chariot", romanNumeral = "VII", icon = "🛡️", element = "Water",
            uprightKeywords = listOf("Triumph", "Willpower", "Focus", "Victory"),
            reversedKeywords = listOf("Lack of Control", "Aggression", "Obstacles"),
            uprightMeaning = "Unyielding drive and victorious determination. Harness conflicting impulses and steer your life straight to victory.",
            reversedMeaning = "Channel frustrated aggression into strategic planning. Stop fighting every small battle.",
            loveAdvice = "Pursue what your heart truly desires with clear, unapologetic confidence.",
            careerAdvice = "Overcome competitors through relentless focus and flawless execution."
        ),
        TarotCard(
            id = 8, name = "Strength", romanNumeral = "VIII", icon = "🦁", element = "Fire",
            uprightKeywords = listOf("Inner Fortitude", "Compassion", "Patience", "Courage"),
            reversedKeywords = listOf("Self-Doubt", "Raw Emotion", "Weakness"),
            uprightMeaning = "True power is gentle. Master life's fierce storms through compassionate endurance, patience, and soft grace.",
            reversedMeaning = "Do not mistake self-doubt for weakness. Forgive yourself and reclaim your inner fire.",
            loveAdvice = "Gentle kindness and emotional patience tame even the most guarded hearts.",
            careerAdvice = "Diplomacy and quiet resilience will win where aggressive force fails."
        ),
        TarotCard(
            id = 9, name = "The Hermit", romanNumeral = "IX", icon = "🏮", element = "Earth",
            uprightKeywords = listOf("Soul Searching", "Solitude", "Inner Guidance", "Truth"),
            reversedKeywords = listOf("Isolation", "Loneliness", "Withdrawal"),
            uprightMeaning = "Step away from the noisy world. In quiet solitude, your inner lantern reveals the path forward.",
            reversedMeaning = "Do not isolate yourself out of fear or pride. Connect with a trusted soul.",
            loveAdvice = "Understand your own emotional needs deeply before seeking validation from others.",
            careerAdvice = "Spend time conducting thorough research, auditing, and mastering your craft."
        ),
        TarotCard(
            id = 10, name = "Wheel of Fortune", romanNumeral = "X", icon = "☸️", element = "Fire",
            uprightKeywords = listOf("Good Luck", "Karma", "Destiny", "Turning Point"),
            reversedKeywords = listOf("Bad Luck", "Resistance to Change", "Cycles"),
            uprightMeaning = "A dramatic cosmic shift is turning your luck around. Destined opportunities arrive out of nowhere.",
            reversedMeaning = "Accept that life has natural ebbs and flows. What goes down is already on its way up.",
            loveAdvice = "Serendipitous encounters or a refreshing turning point in relationship dynamics.",
            careerAdvice = "Seize unexpected breaks and ride the wave of momentum immediately."
        ),
        TarotCard(
            id = 11, name = "Justice", romanNumeral = "XI", icon = "⚖️", element = "Air",
            uprightKeywords = listOf("Fairness", "Truth", "Cause & Effect", "Law"),
            reversedKeywords = listOf("Dishonesty", "Unfairness", "Evading Truth"),
            uprightMeaning = "Cosmic truth prevails. Legal, moral, and karmic accounts balance in strict fairness.",
            reversedMeaning = "Examine where you have been denying personal responsibility. Clean up your side of the street.",
            loveAdvice = "Seek absolute equality and mutual respect in your partnerships.",
            careerAdvice = "Contracts, negotiations, and formal agreements will resolve justly."
        ),
        TarotCard(
            id = 12, name = "The Hanged Man", romanNumeral = "XII", icon = "⏳", element = "Water",
            uprightKeywords = listOf("Surrender", "New Perspective", "Pause", "Release"),
            reversedKeywords = listOf("Stalling", "Resistance", "Martyrdom"),
            uprightMeaning = "Voluntary surrender of control. Viewing your life upside down uncovers the hidden truth.",
            reversedMeaning = "Stop playing the martyr. Either take decisive action or release the complaint completely.",
            loveAdvice = "Let go of expectations and view your partner's viewpoint with fresh eyes.",
            careerAdvice = "Patience is required. Forcing a stagnant project will only break it."
        ),
        TarotCard(
            id = 13, name = "Death", romanNumeral = "XIII", icon = "🦋", element = "Water",
            uprightKeywords = listOf("Transformation", "Ending", "Rebirth", "Transition"),
            reversedKeywords = listOf("Fear of Change", "Holding On", "Stagnation"),
            uprightMeaning = "The clearing of the old to birth the magnificent new. A definitive ending makes room for profound rebirth.",
            reversedMeaning = "Clinging to dead chapters only prolongs emotional suffering. Open your hands and let go.",
            loveAdvice = "Release old relationship patterns and past lovers to invite true love in.",
            careerAdvice = "An obsolete role or project concludes. Step gracefully into the next phase."
        ),
        TarotCard(
            id = 14, name = "Temperance", romanNumeral = "XIV", icon = "🏺", element = "Fire",
            uprightKeywords = listOf("Balance", "Moderation", "Alchemy", "Patience"),
            reversedKeywords = listOf("Imbalance", "Excess", "Discord"),
            uprightMeaning = "The divine blending of opposites. Finding exquisite harmony, emotional serenity, and golden moderation.",
            reversedMeaning = "Extreme behavior or impatience is unsettling your peace. Restore balance immediately.",
            loveAdvice = "Blend your distinct personalities into a peaceful, supportive rhythm.",
            careerAdvice = "Mediate disputes and bring balanced, harmonious solutions to the table."
        ),
        TarotCard(
            id = 15, name = "The Devil", romanNumeral = "XV", icon = "⛓️", element = "Earth",
            uprightKeywords = listOf("Shadow Self", "Addiction", "Attachment", "Illusions"),
            reversedKeywords = listOf("Freedom", "Breaking Chains", "Reclaiming Power"),
            uprightMeaning = "Shine light on unhealthy attachments or self-imposed mental chains. You are freer than you think.",
            reversedMeaning = "You are breaking free from toxic habits, negative patterns, and manipulative bonds.",
            loveAdvice = "Beware of possessiveness, jealousy, or superficial attraction masquerading as love.",
            careerAdvice = "Do not sell your soul or compromise core morals for quick monetary gain."
        ),
        TarotCard(
            id = 16, name = "The Tower", romanNumeral = "XVI", icon = "⚡", element = "Fire",
            uprightKeywords = listOf("Sudden Awakening", "Truth Revelation", "Breakthrough"),
            reversedKeywords = listOf("Averting Disaster", "Fear of Suffering", "Delayed Inevitable"),
            uprightMeaning = "Lightning strikes false illusions. While startling, it shatters brittle foundations so you can build on bedrock truth.",
            reversedMeaning = "You have narrowly dodged a crisis or are delaying an inevitable necessary transformation.",
            loveAdvice = "Shocking honesty will clear the toxic air and allow true intimacy to breathe.",
            careerAdvice = "Sudden structural shakeups will ultimately liberate you into your true calling."
        ),
        TarotCard(
            id = 17, name = "The Star", romanNumeral = "XVII", icon = "⭐", element = "Air",
            uprightKeywords = listOf("Hope", "Faith", "Inspiration", "Healing", "Serenity"),
            reversedKeywords = listOf("Despair", "Discouragement", "Faithlessness"),
            uprightMeaning = "Pure divine light, restored optimism, and celestial blessing. Healing rains wash away all sorrow.",
            reversedMeaning = "Do not lose faith in your dreams right before dawn. Replenish your spiritual well.",
            loveAdvice = "Renewed hope, romantic reconciliation, or finding an uplifting soulmate connection.",
            careerAdvice = "Your creative talents are recognized; dream boldly and inspire others."
        ),
        TarotCard(
            id = 18, name = "The Moon", romanNumeral = "XVIII", icon = "🐺", element = "Water",
            uprightKeywords = listOf("Illusion", "Subconscious Fears", "Intuition", "Mystery"),
            reversedKeywords = listOf("Release of Fear", "Unveiling Secrets", "Clarity"),
            uprightMeaning = "Things are not as they appear in the moonlight. Trust your intuition to navigate through murky illusions.",
            reversedMeaning = "The fog is lifting. Lies and confusing illusions dissolve into clear daylight.",
            loveAdvice = "Do not let irrational anxieties or jealousy fabricate stories that don't exist.",
            careerAdvice = "Read all fine print carefully. Look beneath polished promises."
        ),
        TarotCard(
            id = 19, name = "The Sun", romanNumeral = "XIX", icon = "☀️", element = "Fire",
            uprightKeywords = listOf("Joy", "Success", "Radiance", "Vitality", "Celebration"),
            reversedKeywords = listOf("Temporary Cloudiness", "Over-optimism", "Ego"),
            uprightMeaning = "Unabashed radiance, vibrant health, joyful triumph, and crystal-clear clarity. The universe smiles upon you.",
            reversedMeaning = "The warmth is still present behind passing clouds. Count your blessings today.",
            loveAdvice = "Pure happiness, warm affection, and joyful laughter fill your partnership.",
            careerAdvice = "Massive triumph, public recognition, and celebratory financial success."
        ),
        TarotCard(
            id = 20, name = "Judgement", romanNumeral = "XX", icon = "🎺", element = "Fire",
            uprightKeywords = listOf("Reckoning", "Awakening", "Higher Calling", "Absolution"),
            reversedKeywords = listOf("Self-Doubt", "Harsh Self-Criticism", "Hesitation"),
            uprightMeaning = "The trumpet calls you into your highest destiny. Forgive the past and step boldly into your rebirth.",
            reversedMeaning = "Cease punishing yourself for past mistakes. Receive the grace to begin anew.",
            loveAdvice = "A significant decision about the future of your romantic life is at hand.",
            careerAdvice = "Heed your authentic vocation; you are called to a higher purpose."
        ),
        TarotCard(
            id = 21, name = "The World", romanNumeral = "XXI", icon = "🌍", element = "Earth",
            uprightKeywords = listOf("Fulfillment", "Wholeness", "Completion", "Mastery"),
            reversedKeywords = listOf("Unfinished Business", "Shortcuts", "Delayed Closure"),
            uprightMeaning = "A grand cycle reaches triumphant, beautiful completion. You stand in wholeness, mastery, and cosmic peace.",
            reversedMeaning = "Tie up loose ends and find closure so you can step cleanly into the next cycle.",
            loveAdvice = "A deep sense of emotional wholeness, unity, and harmonious fulfillment.",
            careerAdvice = "Major milestone achieved. Global reach, celebration, and proud rewards."
        )
    )

    fun drawDailyCard(): DrawnTarotCard {
        val card = majorArcana[random.nextInt(majorArcana.size)]
        val isReversed = random.nextBoolean()
        return DrawnTarotCard(card = card, isReversed = isReversed)
    }

    fun drawThreeCardSpread(): List<DrawnTarotCard> {
        val shuffled = majorArcana.shuffled(random)
        return listOf(
            DrawnTarotCard(card = shuffled[0], isReversed = random.nextBoolean(), position = SpreadPosition.PAST),
            DrawnTarotCard(card = shuffled[1], isReversed = random.nextBoolean(), position = SpreadPosition.PRESENT),
            DrawnTarotCard(card = shuffled[2], isReversed = random.nextBoolean(), position = SpreadPosition.FUTURE)
        )
    }
}
