package com.molostream.feature.player.presentation.components

/** Playback progress data consumed by [Scrubber]. */
internal data class ScrubberState(
    val positionMs: Long,
    val bufferedMs: Long,
    val durationMs: Long,
    val isLive: Boolean = false,
    val adBreakFractions: List<Float> = emptyList(),
)
