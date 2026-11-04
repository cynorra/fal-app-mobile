package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.FocusArea
import com.cynora.luna.core.data.model.Fortune
import com.cynora.luna.core.data.model.FortuneType
import com.cynora.luna.core.data.model.Mood
import kotlinx.coroutines.flow.Flow

interface FortuneRepository {
    suspend fun getFortune(
        type: FortuneType,
        mood: Mood,
        focus: FocusArea,
        seenIds: Set<String>
    ): Result<Fortune>

    suspend fun getFortuneById(fortuneId: String): Result<Fortune>

    suspend fun syncFromFirestore(): Result<Unit>

    fun observeSeenIds(): Flow<Set<String>>

    suspend fun markAsSeen(fortuneId: String)
}
