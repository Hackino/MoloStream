package com.molostream.feature.player.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.feature.player.presentation.PlayerUiState
import com.molostream.feature.player.shared.utils.formatTime
import com.molostream.core.designsystem.R as DsR

/**
 * Fully custom transport controls drawn over the video surface: a top bar, a
 * centre play cluster with ±10s skips, and a bottom scrubber with timecodes.
 * Hidden while an ad is playing (IMA draws its own ad UI).
 */
@Composable
internal fun PlayerControls(
    title: String,
    subtitle: String,
    state: PlayerUiState,
    actions: PlayerActions,
    modifier: Modifier = Modifier,
) {
    val colors = MoloTheme.colors
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.55f),
                    0.3f to Color.Transparent,
                    0.7f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.7f),
                ),
            ),
    ) {
        PlayerTopBar(title = title, subtitle = subtitle, onBack = actions.onBack)
        PlayerCenterTransport(
            state = state,
            onTogglePlay = actions.onTogglePlay,
            onSeekBy = actions.onSeekBy,
            modifier = Modifier.align(Alignment.Center),
        )
        PlayerBottomBar(
            state = state,
            onSeekTo = actions.onSeekTo,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun PlayerTopBar(title: String, subtitle: String, onBack: () -> Unit) {
    val colors = MoloTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(DsR.string.cd_close_player),
                tint = colors.text,
            )
        }
        Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
            Text(
                title,
                color = colors.text,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    subtitle,
                    color = colors.textDim,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                HlsChip()
            }
        }
    }
}

@Composable
private fun PlayerCenterTransport(
    state: PlayerUiState,
    onTogglePlay: () -> Unit,
    onSeekBy: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MoloTheme.colors
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        IconButton(onClick = { onSeekBy(-10_000L) }) {
            Icon(
                Icons.Filled.Replay10,
                contentDescription = stringResource(DsR.string.cd_rewind_10),
                tint = colors.text,
                modifier = Modifier.size(34.dp),
            )
        }
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(Brush.linearGradient(listOf(colors.accentSoft, colors.accentHot))),
            contentAlignment = Alignment.Center,
        ) {
            if (state.isBuffering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = colors.accentHot,
                    trackColor = colors.onAccent.copy(alpha = 0.25f),
                    strokeWidth = 3.dp,
                )
            } else {
                IconButton(onClick = onTogglePlay) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = stringResource(if (state.isPlaying) DsR.string.cd_pause else DsR.string.cd_play),
                        tint = colors.onAccent,
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
        }
        IconButton(onClick = { onSeekBy(10_000L) }) {
            Icon(
                Icons.Filled.Forward10,
                contentDescription = stringResource(DsR.string.cd_forward_10),
                tint = colors.text,
                modifier = Modifier.size(34.dp),
            )
        }
    }
}

@Composable
private fun PlayerBottomBar(state: PlayerUiState, onSeekTo: (Long) -> Unit, modifier: Modifier = Modifier) {
    val colors = MoloTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Scrubber(
            positionMs = state.contentPositionMs,
            bufferedMs = state.bufferedPositionMs,
            durationMs = state.contentDurationMs,
            onSeek = onSeekTo,
            isLive = state.isLive,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (state.isLive) {
                Text(
                    stringResource(DsR.string.media_live),
                    color = colors.live,
                    style = MaterialTheme.typography.labelMedium,
                )
            } else {
                Text(formatTime(state.contentPositionMs), color = colors.textDim, style = MaterialTheme.typography.labelMedium)
                Text(formatTime(state.contentDurationMs), color = colors.textDim, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun HlsChip() {
    val colors = MoloTheme.colors
    Text(
        text = stringResource(DsR.string.player_hls),
        color = colors.accentSoft,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(colors.accentInk)
            .padding(horizontal = 7.dp, vertical = 2.dp),
    )
}
