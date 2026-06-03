package com.molostream.feature.player.presentation.components

/** Groups all transport callbacks so [PlayerControls] stays within parameter limits. */
internal data class PlayerActions(
    val onBack: () -> Unit,
    val onTogglePlay: () -> Unit,
    val onSeekBy: (Long) -> Unit,
    val onSeekTo: (Long) -> Unit,
)
