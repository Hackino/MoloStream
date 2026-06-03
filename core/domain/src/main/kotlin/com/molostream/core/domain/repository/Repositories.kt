package com.molostream.core.domain.repository

import com.molostream.core.model.WatchProgress
import kotlinx.coroutines.flow.Flow

/** Subscription state, persisted (encrypted) across restarts. */
interface SubscriptionRepository {
    val subscribed: Flow<Boolean>

    suspend fun setSubscribed(value: Boolean)
}

/** Per-movie playback progress for resume + Continue Watching. */
interface WatchProgressRepository {
    fun observe(movieId: String): Flow<WatchProgress?>

    suspend fun get(movieId: String): WatchProgress?

    suspend fun save(progress: WatchProgress)

    suspend fun clearAll()

    fun observeAll(): Flow<List<WatchProgress>>
}
