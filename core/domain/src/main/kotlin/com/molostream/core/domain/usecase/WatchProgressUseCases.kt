package com.molostream.core.domain.usecase

import com.molostream.core.domain.repository.WatchProgressRepository
import com.molostream.core.model.WatchProgress

/** Position is "resumable" only if meaningfully into, but not at the end of, the title. */
private const val RESUME_MIN_MS = 5_000L
private const val FINISHED_FRACTION = 0.95f

/** Saves the current playback position for a movie. */
class SaveWatchProgressUseCase(
    private val repository: WatchProgressRepository,
) {
    suspend operator fun invoke(progress: WatchProgress) = repository.save(progress)
}

/** Clears all continue-watching history (called on unsubscribe). */
class ClearContinueWatchingUseCase(
    private val repository: WatchProgressRepository,
) {
    suspend operator fun invoke() = repository.clearAll()
}

/**
 * Returns the position (ms) to resume [movieId] from — or 0 if there's nothing
 * meaningful to resume (never watched, barely started, or essentially finished).
 */
class GetResumePositionUseCase(
    private val repository: WatchProgressRepository,
) {
    suspend operator fun invoke(movieId: String): Long {
        val progress = repository.get(movieId) ?: return 0L
        if (progress.positionMs < RESUME_MIN_MS) return 0L
        if (progress.fraction >= FINISHED_FRACTION) return 0L
        return progress.positionMs
    }
}
