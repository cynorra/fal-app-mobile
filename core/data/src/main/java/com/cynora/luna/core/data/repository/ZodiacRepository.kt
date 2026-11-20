package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.ZodiacDetail
import com.cynora.luna.core.data.model.ZodiacElement
import com.cynora.luna.core.data.model.ZodiacModality
import com.cynora.luna.core.data.model.ZodiacSign
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZodiacRepository @Inject constructor() {

    private val zodiacDetails = listOf(
        ZodiacDetail(
            sign = ZodiacSign.ARIES,
            element = ZodiacElement.FIRE,
            modality = ZodiacModality.CARDINAL,
            rulingPlanet = "Mars ♂",
            rulingHouse = "1st House (Self & Vitality)",
            tarotCard = "The Emperor",
            keywords = listOf("Bold", "Pioneering", "Passionate", "Independent"),
            strengths = listOf("Fearless courage", "Infectious enthusiasm", "Natural leadership", "Decisiveness"),
            weaknesses = listOf("Impatience", "Short temper", "Impulsive decisions", "Easily bored"),
            loveProfile = "Passionate, fiery, and fiercely loyal. Aries loves the thrill of the chase and craves an equally confident partner.",
            careerProfile = "Entrepreneurial trailblazers who thrive in high-stakes environments, competitive leadership, and direct action.",
            compatibleSigns = listOf(ZodiacSign.LEO, ZodiacSign.SAGITTARIUS, ZodiacSign.GEMINI, ZodiacSign.AQUARIUS)
        ),
        ZodiacDetail(
            sign = ZodiacSign.TAURUS,
            element = ZodiacElement.EARTH,
            modality = ZodiacModality.FIXED,
            rulingPlanet = "Venus ♀",
            rulingHouse = "2nd House (Values & Wealth)",
            tarotCard = "The Hierophant",
            keywords = listOf("Reliable", "Sensual", "Devoted", "Grounded"),
            strengths = listOf("Unshakable loyalty", "Practical wisdom", "Patience", "Artistic appreciation"),
            weaknesses = listOf("Stubbornness", "Resistance to change", "Possessiveness", "Overindulgence"),
            loveProfile = "Steadfast, affectionate, and deeply sensual. Taurus builds relationships meant to endure a lifetime on solid trust.",
            careerProfile = "Methodical masters of finance, real estate, design, and building lasting value with serene consistency.",
            compatibleSigns = listOf(ZodiacSign.VIRGO, ZodiacSign.CAPRICORN, ZodiacSign.CANCER, ZodiacSign.PISCES)
        ),
        ZodiacDetail(
            sign = ZodiacSign.GEMINI,
            element = ZodiacElement.AIR,
            modality = ZodiacModality.MUTABLE,
            rulingPlanet = "Mercury ☿",
            rulingHouse = "3rd House (Mind & Communication)",
            tarotCard = "The Lovers",
            keywords = listOf("Curious", "Witty", "Adaptable", "Communicative"),
            strengths = listOf("Intellectual agility", "Charming conversationalist", "Multi-talented", "Open-minded"),
            weaknesses = listOf("Restlessness", "Inconsistency", "Overthinking", "Superficiality"),
            loveProfile = "Mental connection is essential. Gemini falls in love with intellect, banter, and a partner who keeps their curiosity alive.",
            careerProfile = "Media, writing, tech, marketing, and negotiation. Gemini excels wherever information moves fast.",
            compatibleSigns = listOf(ZodiacSign.LIBRA, ZodiacSign.AQUARIUS, ZodiacSign.ARIES, ZodiacSign.LEO)
        ),
        ZodiacDetail(
            sign = ZodiacSign.CANCER,
            element = ZodiacElement.WATER,
            modality = ZodiacModality.CARDINAL,
            rulingPlanet = "Moon ☽",
            rulingHouse = "4th House (Home & Roots)",
            tarotCard = "The Chariot",
            keywords = listOf("Intuitive", "Nurturing", "Empathetic", "Protective"),
            strengths = listOf("Profound empathy", "Fierce loyalty to loved ones", "Intuitive psychic sense", "Creative memory"),
            weaknesses = listOf("Moodiness", "Guarded shell", "Clinging to the past", "Taking things personally"),
            loveProfile = "Deeply romantic, emotional, and protective. Cancer creates a safe emotional sanctuary for the partner they adore.",
            careerProfile = "Caregiving, psychology, hospitality, architecture, and roles demanding deep human empathy and emotional intelligence.",
            compatibleSigns = listOf(ZodiacSign.SCORPIO, ZodiacSign.PISCES, ZodiacSign.TAURUS, ZodiacSign.VIRGO)
        ),
        ZodiacDetail(
            sign = ZodiacSign.LEO,
            element = ZodiacElement.FIRE,
            modality = ZodiacModality.FIXED,
            rulingPlanet = "Sun ☉",
            rulingHouse = "5th House (Joy & Creativity)",
            tarotCard = "Strength",
            keywords = listOf("Radiant", "Generous", "Magnetic", "Dramatic"),
            strengths = listOf("Big-hearted generosity", "Charismatic stage presence", "Loyalty", "Creative optimism"),
            weaknesses = listOf("Pride / Ego", "Need for constant validation", "Stubbornness", "Drama"),
            loveProfile = "Generous, devoted, and cinematic lovers. Leo showers their beloved in praise, luxury, and fierce devotion.",
            careerProfile = "Entertainment, executive leadership, arts, public speaking, and directing. Born to shine on center stage.",
            compatibleSigns = listOf(ZodiacSign.ARIES, ZodiacSign.SAGITTARIUS, ZodiacSign.GEMINI, ZodiacSign.LIBRA)
        ),
        ZodiacDetail(
            sign = ZodiacSign.VIRGO,
            element = ZodiacElement.EARTH,
            modality = ZodiacModality.MUTABLE,
            rulingPlanet = "Mercury ☿",
            rulingHouse = "6th House (Service & Wellness)",
            tarotCard = "The Hermit",
            keywords = listOf("Analytical", "Precise", "Helpful", "Dedicated"),
            strengths = listOf("Exceptional eye for detail", "Devoted service", "Practical problem solving", "Reliability"),
            weaknesses = listOf("Perfectionism", "Over-critical mind", "Worrying", "Self-imposed pressure"),
            loveProfile = "Virgo shows love through thoughtful acts of service, quiet loyalty, and constant support to help you grow.",
            careerProfile = "Healthcare, engineering, data analysis, editing, and operations. Virgo ensures perfection in complex systems.",
            compatibleSigns = listOf(ZodiacSign.TAURUS, ZodiacSign.CAPRICORN, ZodiacSign.CANCER, ZodiacSign.SCORPIO)
        ),
        ZodiacDetail(
            sign = ZodiacSign.LIBRA,
            element = ZodiacElement.AIR,
            modality = ZodiacModality.CARDINAL,
            rulingPlanet = "Venus ♀",
            rulingHouse = "7th House (Partnership & Balance)",
            tarotCard = "Justice",
            keywords = listOf("Harmonious", "Diplomatic", "Charming", "Aesthetic"),
            strengths = listOf("Innate sense of justice", "Social grace & charm", "Aesthetic brilliance", "Peace-making"),
            weaknesses = listOf("Indecisiveness", "Conflict avoidance", "People pleasing", "Codependency"),
            loveProfile = "The true romantic of the zodiac. Libra seeks an aesthetically and emotionally balanced fairytale partnership.",
            careerProfile = "Law, diplomacy, fashion, interior design, mediation, and partnerships where harmony and aesthetics rule.",
            compatibleSigns = listOf(ZodiacSign.GEMINI, ZodiacSign.AQUARIUS, ZodiacSign.LEO, ZodiacSign.SAGITTARIUS)
        ),
        ZodiacDetail(
            sign = ZodiacSign.SCORPIO,
            element = ZodiacElement.WATER,
            modality = ZodiacModality.FIXED,
            rulingPlanet = "Pluto ♇ & Mars ♂",
            rulingHouse = "8th House (Transformation & Rebirth)",
            tarotCard = "Death",
            keywords = listOf("Intense", "Transformational", "Loyal", "Magnetic"),
            strengths = listOf("Unshakable emotional depth", "Laser focus", "Fierce loyalty", "Perceptive intuition"),
            weaknesses = listOf("Secretiveness", "Jealousy", "Grudge-holding", "All-or-nothing mindset"),
            loveProfile = "All-consuming, soulful, and profoundly intimate. Scorpio bonds on a soul level or not at all.",
            careerProfile = "Investigative research, surgery, psychology, finance, crisis management, and uncovering deep truths.",
            compatibleSigns = listOf(ZodiacSign.CANCER, ZodiacSign.PISCES, ZodiacSign.VIRGO, ZodiacSign.CAPRICORN)
        ),
        ZodiacDetail(
            sign = ZodiacSign.SAGITTARIUS,
            element = ZodiacElement.FIRE,
            modality = ZodiacModality.MUTABLE,
            rulingPlanet = "Jupiter ♃",
            rulingHouse = "9th House (Philosophy & Exploration)",
            tarotCard = "Temperance",
            keywords = listOf("Adventurous", "Philosophical", "Optimistic", "Free"),
            strengths = listOf("Boundless optimism", "Love for truth and learning", "Generosity", "Sense of humor"),
            weaknesses = listOf("Restlessness", "Tactless honesty", "Commitment hesitation", "Overpromising"),
            loveProfile = "Values freedom and adventure. Sagittarius falls for fellow travelers who expand their intellectual and spiritual horizons.",
            careerProfile = "Academia, travel, international business, publishing, law, and inspirational thought leadership.",
            compatibleSigns = listOf(ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.LIBRA, ZodiacSign.AQUARIUS)
        ),
        ZodiacDetail(
            sign = ZodiacSign.CAPRICORN,
            element = ZodiacElement.EARTH,
            modality = ZodiacModality.CARDINAL,
            rulingPlanet = "Saturn ♄",
            rulingHouse = "10th House (Legacy & Mastery)",
            tarotCard = "The Devil",
            keywords = listOf("Ambitious", "Disciplined", "Strategic", "Patient"),
            strengths = listOf("Iron discipline", "Long-term vision", "Unmatched work ethic", "Integrity & wisdom"),
            weaknesses = listOf("Emotional aloofness", "Workaholism", "Pessimism", "High expectations"),
            loveProfile = "Slow to open up, but fiercely protective, steadfast, and deeply committed once trust is earned.",
            careerProfile = "CEOs, architecture, finance, government, and institutional leadership. Capricorn builds empires.",
            compatibleSigns = listOf(ZodiacSign.TAURUS, ZodiacSign.VIRGO, ZodiacSign.SCORPIO, ZodiacSign.PISCES)
        ),
        ZodiacDetail(
            sign = ZodiacSign.AQUARIUS,
            element = ZodiacElement.AIR,
            modality = ZodiacModality.FIXED,
            rulingPlanet = "Uranus ♅ & Saturn ♄",
            rulingHouse = "11th House (Community & Future)",
            tarotCard = "The Star",
            keywords = listOf("Visionary", "Original", "Humanitarian", "Independent"),
            strengths = listOf("Forward-thinking vision", "Authentic originality", "Humanitarian heart", "Logical brilliance"),
            weaknesses = listOf("Emotional detachment", "Rebelliousness for its own sake", "Unpredictability", "Stubbornness"),
            loveProfile = "Values friendship as the bedrock of love. Aquarius needs intellectual equality and personal freedom.",
            careerProfile = "Technology, AI, science, social activism, aviation, and revolutionizing outdated systems.",
            compatibleSigns = listOf(ZodiacSign.GEMINI, ZodiacSign.LIBRA, ZodiacSign.ARIES, ZodiacSign.SAGITTARIUS)
        ),
        ZodiacDetail(
            sign = ZodiacSign.PISCES,
            element = ZodiacElement.WATER,
            modality = ZodiacModality.MUTABLE,
            rulingPlanet = "Neptune ♆ & Jupiter ♃",
            rulingHouse = "12th House (Mysticism & Subconscious)",
            tarotCard = "The Moon",
            keywords = listOf("Dreamy", "Mystical", "Compassionate", "Artistic"),
            strengths = listOf("Limitless compassion", "Psychic artistic vision", "Spiritual wisdom", "Gentle grace"),
            weaknesses = listOf("Escapism", "Boundary issues", "Playing the martyr", "Over-idealizing others"),
            loveProfile = "Poetic, selfless, and unconditionally loving. Pisces views romantic love as a sacred spiritual union.",
            careerProfile = "Music, cinema, healing arts, poetry, counseling, and mystical spiritual vocations.",
            compatibleSigns = listOf(ZodiacSign.CANCER, ZodiacSign.SCORPIO, ZodiacSign.TAURUS, ZodiacSign.CAPRICORN)
        )
    )

    fun getAllDetails(): List<ZodiacDetail> = zodiacDetails

    fun getDetailForSign(sign: ZodiacSign): ZodiacDetail {
        return zodiacDetails.find { it.sign == sign } ?: zodiacDetails.first()
    }
}
