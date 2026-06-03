package com.molostream.feature.player.presentation

/** Immutable snapshot of playback, rendered by the custom controls. */
data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val ended: Boolean = false,
    /** Position/duration of the feature content (excludes ad time). */
    val contentPositionMs: Long = 0L,
    val contentDurationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    /** Ad break state — drives the "Ad" overlay; controls hide while true. */
    val isPlayingAd: Boolean = false,
    val adPositionMs: Long = 0L,
    val adDurationMs: Long = 0L,
    /** Whether ads are enabled at all (free user). */
    val adsEnabled: Boolean = false,
    /** Live stream — drives the live scrubber + "no resume / no save" behavior. */
    val isLive: Boolean = false,
    /**
     * True until the resume decision is made (auto-play vs. show the prompt).
     * Controls stay hidden while preparing so the play button never flashes.
     */
    val preparing: Boolean = true,
    /**
     * When non-null, a previously-saved position exists and playback is paused
     * awaiting a Resume / Start-over choice (Netflix-style).
     */
    val resumePromptMs: Long? = null,
)
