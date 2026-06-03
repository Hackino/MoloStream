package com.molostream.feature.player.presentation.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.molostream.core.designsystem.component.MoloPrimaryButton
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.feature.player.presentation.components.OutlineAction
import com.molostream.feature.player.shared.utils.formatTime
import com.molostream.core.designsystem.R as DsR

/** Netflix-style choice shown when a saved position exists. */
@Composable
internal fun ResumePrompt(resumeMs: Long, onResume: () -> Unit, onRestart: () -> Unit, onBack: () -> Unit) {
    val colors = MoloTheme.colors
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f))) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(8.dp),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(DsR.string.player_action_close),
                tint = colors.text,
            )
        }
        Column(
            modifier = Modifier.align(Alignment.Center).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(DsR.string.player_resume_title), color = colors.text, style = MaterialTheme.typography.headlineSmall)
            Text(
                stringResource(DsR.string.player_resume_body, formatTime(resumeMs)),
                color = colors.textDim,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MoloPrimaryButton(
                    text = stringResource(DsR.string.player_action_resume),
                    leadingIcon = Icons.Filled.PlayArrow,
                    onClick = onResume,
                )
                OutlineAction(text = stringResource(DsR.string.player_action_start_over), onClick = onRestart)
            }
        }
    }
}
