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
import androidx.compose.ui.unit.dp
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.designsystem.R as DsR

/**
 * Custom progress bar: a base track, a buffered track (so loading shows as the
 * bar filling rather than a spinner), the played track, and a draggable thumb.
 * Supports drag-to-scrub and tap-to-seek.
 *
 * For live (`isLive = true`) the buffered position is treated as the "live edge":
 * the user can only drag back within `[0, bufferedMs]`, and a red LIVE pill
 * replaces the total-duration affordance (tap it to jump back to the edge).
 */
@Composable
internal fun Scrubber(
    positionMs: Long,
    bufferedMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isLive: Boolean = false,
) {
    val colors = MoloTheme.colors
    // Live: the buffered position is the live edge; VOD: the full content duration.
    val span = (if (isLive) bufferedMs else durationMs).coerceAtLeast(1L)
    var dragFraction by remember { mutableStateOf<Float?>(null) }

    val played = if (isLive) 1f else (dragFraction ?: (positionMs.toFloat() / span)).coerceIn(0f, 1f)
    val buffered = if (isLive) 1f else (bufferedMs.toFloat() / span).coerceIn(0f, 1f)
    val atLiveEdge = !isLive || positionMs >= bufferedMs - LIVE_EDGE_TOLERANCE_MS

    // Live can never seek past what's loaded / past the live edge.
    val emitSeek: (Long) -> Unit = { target ->
        onSeek(if (isLive) minOf(target, bufferedMs) else target)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .height(28.dp)
                .then(
                    if (!isLive) Modifier
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
                    else Modifier
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            val trackHeight = 5.dp
            // Base track
            Box(Modifier.fillMaxWidth().height(trackHeight).clip(CircleShape).background(colors.text.copy(alpha = 0.22f)))
            // Buffered
            Box(Modifier.fillMaxWidth(buffered).height(trackHeight).clip(CircleShape).background(colors.text.copy(alpha = 0.4f)))
            // Played
            Box(
                Modifier.fillMaxWidth(played).height(trackHeight).clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(colors.accentHot, colors.accentSoft))),
            )
            // Thumb
            Box(
                Modifier
                    .offset(x = (maxWidth - 14.dp) * played)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(colors.accent),
            )
        }

        if (isLive) {
            LivePill(atLiveEdge = atLiveEdge, onGoLive = { onSeek(bufferedMs) })
        }
    }
}

/** Red LIVE pill: solid at the live edge, dimmed (and tappable to go live) when seeked back. */
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
