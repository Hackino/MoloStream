package com.molostream.feature.player.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.designsystem.R as DsR

/**
 * Custom progress bar with drag-to-scrub, tap-to-seek, ad break dots, and live-edge pill.
 * Pass [onSeekLocked] (non-null) to intercept gestures without changing position.
 */
@Composable
internal fun Scrubber(
    state: ScrubberState,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onSeekLocked: (() -> Unit)? = null,
) {
    val atLiveEdge = !state.isLive || state.positionMs >= state.bufferedMs - LIVE_EDGE_TOLERANCE_MS
    val emitSeek: (Long) -> Unit = { target ->
        onSeek(if (state.isLive) minOf(target, state.bufferedMs) else target)
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ScrubberTrack(
            state = state,
            emitSeek = emitSeek,
            onSeekLocked = onSeekLocked,
            modifier = Modifier.weight(1f),
        )
        if (state.isLive) {
            LivePill(atLiveEdge = atLiveEdge, onGoLive = { onSeek(state.bufferedMs) })
        }
    }
}

@Composable
private fun ScrubberTrack(
    state: ScrubberState,
    emitSeek: (Long) -> Unit,
    onSeekLocked: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val span = (if (state.isLive) state.bufferedMs else state.durationMs).coerceAtLeast(1L)
    var dragFraction by remember { mutableStateOf<Float?>(null) }
    val played = if (state.isLive) 1f else (dragFraction ?: (state.positionMs.toFloat() / span)).coerceIn(0f, 1f)
    val buffered = if (state.isLive) 1f else (state.bufferedMs.toFloat() / span).coerceIn(0f, 1f)

    BoxWithConstraints(
        modifier = modifier.height(28.dp).then(
            when {
                state.isLive -> Modifier
                onSeekLocked != null -> Modifier
                    .pointerInput(onSeekLocked) {
                        detectHorizontalDragGestures(
                            onDragStart = { onSeekLocked() },
                            onHorizontalDrag = { _, _ -> },
                            onDragEnd = {},
                            onDragCancel = {},
                        )
                    }
                    .pointerInput(onSeekLocked) { detectTapGestures { onSeekLocked() } }
                else -> Modifier
                    .pointerInput(span) {
                        detectHorizontalDragGestures(
                            onDragStart = { offset -> dragFraction = (offset.x / size.width).coerceIn(0f, 1f) },
                            onHorizontalDrag = { change, _ -> dragFraction = (change.position.x / size.width).coerceIn(0f, 1f) },
                            onDragEnd = { dragFraction?.let { emitSeek((it * span).toLong()) }; dragFraction = null },
                            onDragCancel = { dragFraction = null },
                        )
                    }
                    .pointerInput(span) {
                        detectTapGestures { offset -> emitSeek(((offset.x / size.width).coerceIn(0f, 1f) * span).toLong()) }
                    }
            },
        ),
        contentAlignment = Alignment.CenterStart,
    ) {
        ScrubberLayers(
            played = played,
            buffered = buffered,
            adBreakFractions = if (!state.isLive) state.adBreakFractions else emptyList(),
            maxWidth = maxWidth,
        )
    }
}

@Composable
private fun ScrubberLayers(played: Float, buffered: Float, adBreakFractions: List<Float>, maxWidth: Dp) {
    val colors = MoloTheme.colors
    val trackHeight = 5.dp
    Box(Modifier.fillMaxWidth().height(trackHeight).clip(CircleShape).background(colors.text.copy(alpha = 0.22f)))
    Box(Modifier.fillMaxWidth(buffered).height(trackHeight).clip(CircleShape).background(colors.text.copy(alpha = 0.4f)))
    Box(
        Modifier.fillMaxWidth(played).height(trackHeight).clip(CircleShape)
            .background(Brush.horizontalGradient(listOf(colors.accentHot, colors.accentSoft))),
    )
    adBreakFractions.forEach { fraction ->
        Box(
            Modifier
                .offset(x = (maxWidth - AD_DOT_SIZE) * fraction)
                .size(AD_DOT_SIZE)
                .clip(CircleShape)
                .background(colors.ad),
        )
    }
    Box(
        Modifier
            .offset(x = (maxWidth - 14.dp) * played)
            .size(14.dp)
            .clip(CircleShape)
            .background(colors.accent),
    )
}

@Composable
private fun LivePill(atLiveEdge: Boolean, onGoLive: () -> Unit) {
    val colors = MoloTheme.colors
    Text(
        text = stringResource(DsR.string.media_live),
        color = colors.onAccent,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(colors.live.copy(alpha = if (atLiveEdge) 1f else 0.45f))
            .then(if (atLiveEdge) Modifier else Modifier.clickable(onClick = onGoLive))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

private const val LIVE_EDGE_TOLERANCE_MS = 10_000L
private val AD_DOT_SIZE = 8.dp
