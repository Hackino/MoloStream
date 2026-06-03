package com.molostream.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WatchProgressEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class MoloDatabase : RoomDatabase() {
    abstract fun watchProgressDao(): WatchProgressDao
}
