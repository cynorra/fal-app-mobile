package com.cynora.luna.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cynora.luna.core.database.dao.FortuneDao
import com.cynora.luna.core.database.entity.FortuneConverters
import com.cynora.luna.core.database.entity.FortuneEntity

@Database(
    entities = [FortuneEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(FortuneConverters::class)
abstract class LunaDatabase : RoomDatabase() {
    abstract fun fortuneDao(): FortuneDao

    companion object {
        const val DATABASE_NAME = "luna_db"
    }
}
