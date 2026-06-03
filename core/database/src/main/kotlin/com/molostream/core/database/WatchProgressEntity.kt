package com.molostream.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room row holding the last playback position for a given movie id. */
@Entity(tableName = "watch_progress")
data class WatchProgressEntity(
    @PrimaryKey val movieId: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long,
)
