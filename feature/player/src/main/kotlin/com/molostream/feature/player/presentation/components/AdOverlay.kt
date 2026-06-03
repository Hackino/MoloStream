package com.molostream.feature.player.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.feature.player.presentation.PlayerUiState
import com.molostream.feature.player.shared.utils.formatTime
import com.molostream.core.designsystem.R as DsR

/** Minimal overlay shown during an IMA ad break: a back affordance and a countdown chip. */
@Composable
internal fun AdOverlay(state: PlayerUiState, onBack: () -> Unit) {
    val colors = MoloTheme.colors
    Box(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(8.dp)) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart)) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(DsR.string.player_action_close),
                tint = colors.text,
            )
        }
        val remaining = (state.adDurationMs - state.adPositionMs).coerceAtLeast(0L)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 4.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(colors.ad)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                text = stringResource(DsR.string.player_ad_with_time, formatTime(remaining)),
                color = Color(0xFF20160A),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
