package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.mapper.toDomain
import com.cynora.luna.core.data.mapper.toEntity
import com.cynora.luna.core.data.model.FocusArea
import com.cynora.luna.core.data.model.Fortune
import com.cynora.luna.core.data.model.FortuneType
import com.cynora.luna.core.data.model.Mood
import com.cynora.luna.core.data.remote.FortuneDto
import com.cynora.luna.core.database.dao.FortuneDao
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FortuneRepositoryImpl @Inject constructor(
    private val fortuneDao: FortuneDao,
    private val firestore: FirebaseFirestore,
    private val smartPoolSelector: SmartPoolSelector,
    private val dataStore: UserPreferencesDataStore
) : FortuneRepository {

    override suspend fun getFortune(
        type: FortuneType,
        mood: Mood,
        focus: FocusArea,
        seenIds: Set<String>
    ): Result<Fortune> {
        return try {
            val localFortunes = fortuneDao.getAll().first().map { it.toDomain() }

            val selectedFortune = smartPoolSelector.selectFortune(
                pool = localFortunes,
                seenIds = seenIds,
                userMood = mood,
                userFocus = focus,
                fortuneType = type
            )

            if (selectedFortune != null) {
                Result.success(selectedFortune)
            } else {
                val syncResult = syncFromFirestore()
                
                if (syncResult.isFailure) {
                    Result.failure(Exception("Luna is working offline. Connect to sync new cosmic messages."))
                } else {
                    val refreshedFortunes = fortuneDao.getAll().first().map { it.toDomain() }
                    val retryFortune = smartPoolSelector.selectFortune(
                        pool = refreshedFortunes,
                        seenIds = seenIds,
                        userMood = mood,
                        userFocus = focus,
                        fortuneType = type
                    )
                    
                    if (retryFortune != null) Result.success(retryFortune)
                    else Result.failure(Exception("No fortune found right now. Please try again later."))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "getFortune failed")
            Result.failure(e)
        }
    }

    override suspend fun syncFromFirestore(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("fortunes").get().await()
                val fortunes = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(FortuneDto::class.java)?.toDomain()
                }
                fortuneDao.insertAll(fortunes.map { it.toEntity() })
                Timber.d("✅ Synced ${fortunes.size} fortunes from Firestore")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "❌ Firestore sync failed")
                Result.failure(e)
            }
        }
    }

    override fun observeSeenIds(): Flow<Set<String>> {
        return dataStore.getSeenFortuneIds()
    }

    override suspend fun markAsSeen(fortuneId: String) {
        fortuneDao.markSeen(fortuneId)
        dataStore.updateSeenFortuneIds(fortuneId)
    }
}
