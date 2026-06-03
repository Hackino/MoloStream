package com.molostream.core.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchProgressDao {

    /** Insert or update progress for a movie. */
    @Upsert
    suspend fun upsert(progress: WatchProgressEntity)

    /** Observe a single movie's progress (null until something is saved). */
    @Query("SELECT * FROM watch_progress WHERE movieId = :movieId")
    fun observe(movieId: String): Flow<WatchProgressEntity?>

    /** One-shot read, used to resume playback. */
    @Query("SELECT * FROM watch_progress WHERE movieId = :movieId")
    suspend fun get(movieId: String): WatchProgressEntity?

    /** Most-recently-watched first — drives the Continue Watching rail. */
    @Query("SELECT * FROM watch_progress ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<WatchProgressEntity>>

    @Query("DELETE FROM watch_progress WHERE movieId = :movieId")
    suspend fun delete(movieId: String)

    @Query("DELETE FROM watch_progress")
    suspend fun deleteAll()
}
