package com.cynora.luna.core.data.mapper

import com.cynora.luna.core.data.model.FocusArea
import com.cynora.luna.core.data.model.Fortune
import com.cynora.luna.core.data.model.FortuneType
import com.cynora.luna.core.data.model.Mood
import com.cynora.luna.core.data.model.Rarity
import com.cynora.luna.core.data.remote.FortuneDto
import com.cynora.luna.core.data.seed.SeedFortuneDto
import com.cynora.luna.core.database.entity.FortuneEntity
import timber.log.Timber

fun FortuneDto.toDomain(): Fortune {
    val validMoods = moodTags.mapNotNull { id ->
        Mood.fromId(id).takeIf { it.id == id } ?: run {
            Timber.w("Invalid mood ID: $id")
            null
        }
    }
    
    val validFocus = focusTags.mapNotNull { id ->
        FocusArea.fromId(id).takeIf { it.id == id } ?: run {
            Timber.w("Invalid focus ID: $id")
            null
        }
    }

    return Fortune(
        id = id,
        type = FortuneType.fromId(type),
        text = text.ifBlank { "The universe has a message for you..." },
        dailyIntention = dailyIntention.ifBlank { "Today I choose peace." },
        moodTags = validMoods,
        focusTags = validFocus,
        keywordTags = keywordTags,
        rarity = Rarity.fromId(rarity),
        createdAt = createdAt
    )
}

fun Fortune.toEntity(): FortuneEntity {
    return FortuneEntity(
        id = id,
        type = type.id,
        text = text,
        dailyIntention = dailyIntention,
        moodTags = moodTags.map { it.id },
        focusTags = focusTags.map { it.id },
        keywordTags = keywordTags,
        rarity = rarity.id,
        createdAt = createdAt,
        isSeen = false
    )
}

fun FortuneEntity.toDomain(): Fortune {
    return Fortune(
        id = id,
        type = FortuneType.fromId(type),
        text = text,
        dailyIntention = dailyIntention,
        moodTags = moodTags.mapNotNull { Mood.fromId(it) },
        focusTags = focusTags.mapNotNull { FocusArea.fromId(it) },
        keywordTags = keywordTags,
        rarity = Rarity.fromId(rarity),
        createdAt = createdAt
    )
}

fun SeedFortuneDto.toEntity(): FortuneEntity {
    return FortuneEntity(
        id = id,
        type = type,
        text = text,
        dailyIntention = dailyIntention,
        moodTags = moodTags,
        focusTags = focusTags,
        keywordTags = keywordTags,
        rarity = rarity,
        createdAt = createdAt,
        isSeen = false
    )
}
