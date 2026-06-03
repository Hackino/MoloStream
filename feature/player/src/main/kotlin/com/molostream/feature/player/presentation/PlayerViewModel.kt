package com.molostream.feature.player.presentation

import androidx.lifecycle.ViewModel
import androidx.media3.common.AdViewProvider
import androidx.media3.common.Player
import com.molostream.feature.player.domain.PlayerController
import kotlinx.coroutines.flow.StateFlow

/**
 * Thin presentation wrapper around [PlayerController]. Holds no playback logic
 * itself — it delegates to the injected controller and releases it on clear.
 */
class PlayerViewModel(private val controller: PlayerController) : ViewModel() {

    val uiState: StateFlow<PlayerUiState> get() = controller.uiState
    val player: Player? get() = controller.player

    fun initialize(adViewProvider: AdViewProvider) = controller.initialize(adViewProvider)
    fun togglePlayPause() = controller.togglePlayPause()
    fun seekBy(deltaMs: Long) = controller.seekBy(deltaMs)
    fun seekTo(positionMs: Long) = controller.seekTo(positionMs)
    fun resumeFromSaved() = controller.resumeFromSaved()
    fun startFromBeginning() = controller.startFromBeginning()
    fun release() = controller.release()

    override fun onCleared() = controller.release()
}
