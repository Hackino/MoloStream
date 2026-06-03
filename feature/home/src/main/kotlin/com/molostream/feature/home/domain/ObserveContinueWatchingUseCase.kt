package com.molostream.feature.home.domain

import com.molostream.core.domain.repository.WatchProgressRepository
import com.molostream.core.model.ContinueWatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Continue Watching rail: watch-progress rows (a cross-cutting :core concern)
 * joined with this feature's catalog titles, dropping finished items.
 */
class ObserveContinueWatchingUseCase(
    private val catalogRepository: CatalogRepository,
    private val watchProgressRepository: WatchProgressRepository,
) {
    operator fun invoke(): Flow<List<ContinueWatching>> =
        watchProgressRepository.observeAll().map { rows ->
            val byId = catalogRepository.getCatalog().associateBy { it.id }
            rows.mapNotNull { progress ->
                val movie = byId[progress.movieId] ?: return@mapNotNull null
                if (progress.fraction >= FINISHED_FRACTION) null else ContinueWatching(movie, progress)
            }
        }

    private companion object {
        const val FINISHED_FRACTION = 0.95f
    }
}
