package com.molostream.feature.player.domain

import androidx.media3.common.AdViewProvider
import androidx.media3.common.Player
import com.molostream.feature.player.presentation.PlayerUiState
import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction over the playback engine. The presentation layer talks to this;
 * the data layer ([com.molostream.feature.player.data.ExoPlayerController])
 * implements it with ExoPlayer + Google IMA.
 */
interface PlayerController {
    /** Reactive playback state for the UI. */
    val uiState: StateFlow<PlayerUiState>

    /** The underlying media player, for binding to the render surface. */
    val player: Player?

    /** Build + start playback once a render surface (IMA ad container) exists. */
    fun initialize(adViewProvider: AdViewProvider)

    fun togglePlayPause()
    fun seekBy(deltaMs: Long)
    fun seekTo(positionMs: Long)

    /** Resume-prompt choices (only relevant when uiState.resumePromptMs != null). */
    fun resumeFromSaved()
    fun startFromBeginning()

    /** Persist final position and release all resources (idempotent). */
    fun release()
}
