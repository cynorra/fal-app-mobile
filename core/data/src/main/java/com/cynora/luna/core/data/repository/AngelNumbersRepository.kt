package com.cynora.luna.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

data class AngelNumber(
    val number: String,
    val meaning: String,
    val loveMessage: String,
    val careerMessage: String,
    val spiritualMessage: String,
    val action: String
)

@Singleton
class AngelNumbersRepository @Inject constructor() {
    
    private val angelNumbers = mapOf(
        "000" to AngelNumber(
            number = "000",
            meaning = "Infinite potential and new beginnings. The universe invites you to recreate yourself from scratch.",
            loveMessage = "A new page opens in your love life. Let go of the past, be ready for a fresh start.",
            careerMessage = "Time for a completely new direction in your career. Be bold.",
            spiritualMessage = "You're at the threshold of spiritual awakening. Meditate.",
            action = "Get a journal today and write down your new beginnings."
        ),
        "111" to AngelNumber(
            number = "111",
            meaning = "Manifestation portal is open. Your thoughts are turning into reality instantly. Think positive.",
            loveMessage = "Your thoughts about love are manifesting. Decide what you want.",
            careerMessage = "Your career goals have been heard by the universe. Keep going.",
            spiritualMessage = "Your spiritual connection is very strong. Listen to your inner voice.",
            action = "Spend 11 minutes today visualizing your biggest wish."
        ),
        "222" to AngelNumber(
            number = "222",
            meaning = "Balance, harmony and trust. Everything happens in divine timing.",
            loveMessage = "Time for balance in your relationship. Balance giving and receiving.",
            careerMessage = "Perfect time for collaborations and partnerships.",
            spiritualMessage = "Maintain your inner peace. Everything is fine.",
            action = "Do a kind gesture for someone today and feel the universe's balance."
        ),
        "333" to AngelNumber(
            number = "333",
            meaning = "Ascended masters are with you. Your creativity and expression are blessed.",
            loveMessage = "Your communication is strengthening. Open your heart.",
            careerMessage = "Perfect time for creative projects. Step into the spotlight.",
            spiritualMessage = "Your spiritual guides are very close. Your prayers are heard.",
            action = "Do something creative today: write, draw, sing."
        ),
        "444" to AngelNumber(
            number = "444",
            meaning = "Angels are around you. You're protected, you're safe.",
            loveMessage = "You're in a safe space. You can open your heart.",
            careerMessage = "Your foundations are solid. Work with discipline.",
            spiritualMessage = "Your guardian angels are very close. You're not alone.",
            action = "Thank your angels today and ask for protection."
        ),
        "555" to AngelNumber(
            number = "555",
            meaning = "Big change is coming. The old is ending, the new is beginning.",
            loveMessage = "Time for transformation in your relationship. Be open to change.",
            careerMessage = "A major change is at your career door. Be ready.",
            spiritualMessage = "Your spiritual transformation is accelerating. Let go and flow.",
            action = "Write down something you want to change in your life today."
        ),
        "666" to AngelNumber(
            number = "666",
            meaning = "Find balance. Balance your thoughts and focus.",
            loveMessage = "Time for sacrifice and balance in your relationship.",
            careerMessage = "Re-establish work-life balance.",
            spiritualMessage = "Find balance between material and spiritual.",
            action = "Think about which area of your life you're focusing too much on today."
        ),
        "777" to AngelNumber(
            number = "777",
            meaning = "Luck and divine guidance. You're on the right path.",
            loveMessage = "Lucky period in love. Your soulmate is near.",
            careerMessage = "Career luck is shifting in your direction. Don't miss opportunities.",
            spiritualMessage = "You're at a very high spiritual frequency.",
            action = "Make a wish today and trust the universe."
        ),
        "888" to AngelNumber(
            number = "888",
            meaning = "Abundance and prosperity are flowing. Time of financial blessing.",
            loveMessage = "Abundance of love in your life. Give and receive.",
            careerMessage = "Financial abundance is at your door. Evaluate opportunities.",
            spiritualMessage = "The universe's abundance flows to you. Accept it.",
            action = "Write a gratitude list today and invite abundance."
        ),
        "999" to AngelNumber(
            number = "999",
            meaning = "Completion and release. A cycle is ending.",
            loveMessage = "A past relationship is completing. Let go.",
            careerMessage = "Old job completing, new one starting.",
            spiritualMessage = "A chapter of your spiritual journey is ending.",
            action = "Release something you need to let go of today."
        ),
        "1010" to AngelNumber(
            number = "1010",
            meaning = "Spiritual awakening and divine guidance. You're ascending.",
            loveMessage = "Spiritual connections are strengthening.",
            careerMessage = "Time to find your mission.",
            spiritualMessage = "Ascension call. Are you ready?",
            action = "Meditate today and ask for guidance."
        ),
        "1111" to AngelNumber(
            number = "1111",
            meaning = "Manifestation portal is open. You're in the most powerful moment.",
            loveMessage = "Connection moment with your soulmate.",
            careerMessage = "Perfect time for your dream job.",
            spiritualMessage = "In perfect harmony with the universe. Set intention.",
            action = "Make a wish at 11:11 and visualize."
        ),
        "1212" to AngelNumber(
            number = "1212",
            meaning = "Completion and new cycle. Time of ascension.",
            loveMessage = "New level in relationship.",
            careerMessage = "Completion and new beginning in career.",
            spiritualMessage = "Spiritual ascension to the 12th dimension.",
            action = "Close an old cycle today, set a new intention."
        ),
        "1234" to AngelNumber(
            number = "1234",
            meaning = "Step by step progress. Simple and clear path.",
            loveMessage = "Progress step by step in relationship.",
            careerMessage = "Progress in career with simple steps.",
            spiritualMessage = "Spiritual journey is simple and clear.",
            action = "Take a small but clear step today."
        ),
        "2222" to AngelNumber(
            number = "2222",
            meaning = "Master builder. Big dreams are becoming reality.",
            loveMessage = "Work for your dream relationship.",
            careerMessage = "Master plan for big projects.",
            spiritualMessage = "Master level spiritually.",
            action = "Write down a big dream today and plan it."
        )
    )
    
    fun getAngelNumber(number: String): AngelNumber? {
        return angelNumbers[number]
    }
    
    fun getAllAngelNumbers(): Flow<List<AngelNumber>> {
        return flowOf(angelNumbers.values.toList())
    }
    
    fun getRandomAngelNumber(): AngelNumber {
        return angelNumbers.values.toList().random()
    }
    
    fun searchAngelNumber(query: String): List<AngelNumber> {
        return angelNumbers.values.filter { 
            it.number.contains(query) || 
            it.meaning.contains(query, ignoreCase = true) 
        }
    }
}
