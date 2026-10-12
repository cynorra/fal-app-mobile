package com.cynora.luna.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "fortunes")
@TypeConverters(FortuneConverters::class)
data class FortuneEntity(
    @PrimaryKey val id: String,
    val type: String,
    val text: String,
    val dailyIntention: String,
    val moodTags: List<String>,
    val focusTags: List<String>,
    val keywordTags: List<String>,
    val rarity: String,
    val createdAt: Long,
    val isSeen: Boolean = false
)

class FortuneConverters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split(",")
}
