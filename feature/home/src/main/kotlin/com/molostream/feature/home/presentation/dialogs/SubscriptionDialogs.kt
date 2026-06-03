package com.molostream.feature.home.presentation.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.molostream.core.designsystem.component.MoloPrimaryButton
import com.molostream.core.designsystem.component.accentGradient
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.designsystem.R as DsR

/** Benefits dialog shown when the user opts in to Premium. */
@Composable
internal fun SubscribeDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val colors = MoloTheme.colors
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface1)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(accentGradient()),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = colors.onAccent, modifier = Modifier.size(32.dp))
            }
            Text(
                stringResource(DsR.string.sub_dialog_title),
                color = colors.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(stringResource(DsR.string.sub_dialog_subtitle), color = colors.textDim, style = MaterialTheme.typography.bodyMedium)

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Benefit(stringResource(DsR.string.sub_benefit_adfree))
                Benefit(stringResource(DsR.string.sub_benefit_quality))
                Benefit(stringResource(DsR.string.sub_benefit_resume))
            }

            MoloPrimaryButton(
                text = stringResource(DsR.string.sub_dialog_confirm),
                leadingIcon = Icons.Filled.WorkspacePremium,
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            )
            DialogText(stringResource(DsR.string.sub_dialog_cancel), onClick = onDismiss)
        }
    }
}

/** Confirmation shown before cancelling Premium — styled to match SubscribeDialog. */
@Composable
internal fun UnsubscribeDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val colors = MoloTheme.colors
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface1)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(colors.surface3),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = colors.textMute, modifier = Modifier.size(32.dp))
            }
            Text(
                stringResource(DsR.string.unsub_dialog_title),
                color = colors.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                stringResource(DsR.string.unsub_dialog_body),
                color = colors.textDim,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            MoloPrimaryButton(
                text = stringResource(DsR.string.unsub_dialog_confirm),
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            )
            DialogText(stringResource(DsR.string.unsub_dialog_dismiss), onClick = onDismiss)
        }
    }
}

@Composable
private fun Benefit(text: String) {
    val colors = MoloTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(Icons.Filled.Check, contentDescription = null, tint = colors.success, modifier = Modifier.size(18.dp))
        Text(text, color = colors.text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DialogText(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = MoloTheme.colors.textDim,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}
