package com.molostream.core.model

/**
 * Last-known playback position for a movie, used to resume and to build the
 * "Continue Watching" rail.
 */
data class WatchProgress(
    val movieId: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long,
) {
    /** Fraction watched in [0f, 1f]. */
    val fraction: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
}

/** A catalog title paired with its saved progress — the Continue Watching unit. */
data class ContinueWatching(
    val movie: Movie,
    val progress: WatchProgress,
)
