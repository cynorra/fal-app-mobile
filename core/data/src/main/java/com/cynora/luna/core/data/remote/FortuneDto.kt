package com.cynora.luna.core.data.remote

import com.google.firebase.firestore.DocumentId

data class FortuneDto(
    @DocumentId val id: String = "",
    val type: String = "",
    val text: String = "",
    val dailyIntention: String = "",
    val moodTags: List<String> = emptyList(),
    val focusTags: List<String> = emptyList(),
    val keywordTags: List<String> = emptyList(),
    val rarity: String = "common",
    val createdAt: Long = 0L
)
