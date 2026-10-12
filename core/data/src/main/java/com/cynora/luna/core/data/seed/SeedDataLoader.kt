package com.cynora.luna.core.data.seed

import android.content.Context
import com.cynora.luna.core.data.mapper.toEntity
import com.cynora.luna.core.database.dao.FortuneDao
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDataLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fortuneDao: FortuneDao
) {
    private val gson = Gson()

    suspend fun loadSeedDataIfNeeded() {
        val count = fortuneDao.count()
        if (count == 0) {
            loadSeedData()
        } else {
            Timber.d("Seed data already loaded: $count fortunes")
        }
    }

    private suspend fun loadSeedData() = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("seed_fortunes.json")
                .bufferedReader()
                .use { it.readText() }

            val seedData: SeedDataWrapper = gson.fromJson(jsonString, SeedDataWrapper::class.java)
            val entities = seedData.fortunes.map { it.toEntity() }
            fortuneDao.insertAll(entities)
            
            Timber.d("✅ Loaded ${entities.size} seed fortunes")
        } catch (e: Exception) {
            Timber.e(e, "❌ Seed data load failed")
        }
    }
}

data class SeedDataWrapper(
    @SerializedName("fortunes") val fortunes: List<SeedFortuneDto>
)

data class SeedFortuneDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String,
    @SerializedName("dailyIntention") val dailyIntention: String,
    @SerializedName("moodTags") val moodTags: List<String>,
    @SerializedName("focusTags") val focusTags: List<String>,
    @SerializedName("keywordTags") val keywordTags: List<String>,
    @SerializedName("rarity") val rarity: String,
    @SerializedName("createdAt") val createdAt: Long
)
