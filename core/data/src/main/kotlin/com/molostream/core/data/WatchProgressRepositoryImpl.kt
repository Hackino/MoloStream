package com.molostream.core.data

import com.molostream.core.database.WatchProgressDao
import com.molostream.core.database.WatchProgressEntity
import com.molostream.core.domain.repository.WatchProgressRepository
import com.molostream.core.model.WatchProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Backs [WatchProgressRepository] with Room, mapping entity <-> domain. */
class WatchProgressRepositoryImpl(
    private val dao: WatchProgressDao,
) : WatchProgressRepository {

    override fun observe(movieId: String): Flow<WatchProgress?> =
        dao.observe(movieId).map { it?.toDomain() }

    override suspend fun get(movieId: String): WatchProgress? = dao.get(movieId)?.toDomain()

    override suspend fun save(progress: WatchProgress) = dao.upsert(progress.toEntity())

    override suspend fun clearAll() = dao.deleteAll()

    override fun observeAll(): Flow<List<WatchProgress>> =
        dao.observeAll().map { rows -> rows.map(WatchProgressEntity::toDomain) }
}

private fun WatchProgressEntity.toDomain() = WatchProgress(
    movieId = movieId,
    positionMs = positionMs,
    durationMs = durationMs,
    updatedAt = updatedAt,
)

private fun WatchProgress.toEntity() = WatchProgressEntity(
    movieId = movieId,
    positionMs = positionMs,
    durationMs = durationMs,
    updatedAt = updatedAt,
)
