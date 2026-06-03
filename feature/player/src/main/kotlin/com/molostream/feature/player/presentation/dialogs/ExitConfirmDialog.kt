package com.molostream.feature.player.presentation.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.molostream.core.designsystem.component.MoloPrimaryButton
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.designsystem.R as DsR

/** Confirms leaving playback. Only VOD subscribers are promised a saved resume point. */
@Composable
internal fun ExitConfirmDialog(subscribed: Boolean, isLive: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val colors = MoloTheme.colors
    // Live streams are never saved/resumed, so only VOD subscribers can resume.
    val canResume = subscribed && !isLive
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface1)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                stringResource(if (isLive) DsR.string.player_exit_title_live else DsR.string.player_exit_title),
                color = colors.text,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                // Only VOD subscribers resume; free users and live streams don't, so don't promise it.
                stringResource(if (canResume) DsR.string.player_exit_body else DsR.string.player_exit_body_free),
                color = colors.textDim,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            MoloPrimaryButton(
                text = stringResource(DsR.string.player_action_close),
                leadingIcon = Icons.Filled.Replay,
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                stringResource(DsR.string.player_action_keep_watching),
                color = colors.textDim,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
    }
}
