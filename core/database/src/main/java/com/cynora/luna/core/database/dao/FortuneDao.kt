package com.cynora.luna.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cynora.luna.core.database.entity.FortuneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FortuneDao {

    @Query("SELECT * FROM fortunes")
    fun getAll(): Flow<List<FortuneEntity>>

    @Query("SELECT * FROM fortunes WHERE type = :typeId")
    fun getByType(typeId: String): Flow<List<FortuneEntity>>

    @Query("SELECT * FROM fortunes WHERE id = :fortuneId")
    suspend fun getById(fortuneId: String): FortuneEntity?

    @Query("UPDATE fortunes SET isSeen = 1 WHERE id = :fortuneId")
    suspend fun markSeen(fortuneId: String)

    @Query("UPDATE fortunes SET isSeen = 0")
    suspend fun resetSeenStatus()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fortunes: List<FortuneEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fortune: FortuneEntity)

    @Query("DELETE FROM fortunes")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM fortunes")
    suspend fun count(): Int
}
