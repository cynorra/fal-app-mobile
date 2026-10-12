package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.FocusArea
import com.cynora.luna.core.data.model.Fortune
import com.cynora.luna.core.data.model.FortuneType
import com.cynora.luna.core.data.model.Mood
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SmartPoolSelector @Inject constructor(
    private val random: Random = Random.Default
) {

    fun selectFortune(
        pool: List<Fortune>,
        seenIds: Set<String>,
        userMood: Mood,
        userFocus: FocusArea,
        fortuneType: FortuneType
    ): Fortune? {
        val candidates = pool.filter { fortune ->
            fortune.type == fortuneType && !seenIds.contains(fortune.id)
        }

        val availablePool = if (candidates.isEmpty()) {
            pool.filter { it.type == fortuneType }
        } else {
            candidates
        }

        if (availablePool.isEmpty()) return null

        val scoredFortunes = availablePool.map { fortune ->
            fortune to fortune.calculateMatchScore(userMood, userFocus)
        }

        val maxScore = scoredFortunes.maxOfOrNull { it.second } ?: 0
        val topCandidates = scoredFortunes
            .filter { it.second >= maxScore * 0.8 }
            .map { it.first }

        return if (topCandidates.isNotEmpty()) {
            topCandidates[random.nextInt(topCandidates.size)]
        } else {
            availablePool[random.nextInt(availablePool.size)]
        }
    }
}
