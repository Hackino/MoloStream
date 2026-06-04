package com.molostream.feature.player.presentation.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.molostream.core.designsystem.component.MoloPrimaryButton
import com.molostream.core.designsystem.component.accentGradient
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.designsystem.R as DsR

/**
 * Shown when a free user attempts to drag or tap the scrubber.
 *
 * Concept: "The timeline is yours — with Premium." The lock icon at the top
 * reinforces the idea that seeking is a gated capability, not a missing feature.
 * The three benefit lines are specific to what scrubbing unlocks (not generic
 * Premium copy), so the dialog feels contextual rather than a generic paywall.
 *
 * [onGoToPremium] — exits the player so the user can subscribe on the Home screen.
 * [onDismiss]    — closes the dialog; playback continues from where it was.
 */
@Composable
internal fun SeekUpsellDialog(onGoToPremium: () -> Unit, onDismiss: () -> Unit) {
    val colors = MoloTheme.colors
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(colors.surface1)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            SeekUpsellIcon()

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(DsR.string.seek_upsell_title),
                color = colors.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(DsR.string.seek_upsell_body),
                color = colors.textDim,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))

            SeekUpsellBenefits()

            Spacer(Modifier.height(24.dp))

            MoloPrimaryButton(
                text = stringResource(DsR.string.seek_upsell_cta),
                leadingIcon = Icons.Filled.WorkspacePremium,
                onClick = onGoToPremium,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(DsR.string.seek_upsell_dismiss),
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

/** Stacked lock-over-gradient-circle — visually distinct from the generic Premium icon. */
@Composable
private fun SeekUpsellIcon() {
    val colors = MoloTheme.colors
    Box(contentAlignment = Alignment.Center) {
        // Outer ambient glow ring
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(colors.accentHot.copy(alpha = 0.18f), colors.accentInk),
                    ),
                ),
        )
        // Inner gradient circle
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(accentGradient()),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = null,
                tint = colors.onAccent,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
private fun SeekUpsellBenefits() {
    val colors = MoloTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface2)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SeekBenefitRow(stringResource(DsR.string.seek_benefit_scrub))
        SeekBenefitRow(stringResource(DsR.string.seek_benefit_adfree))
        SeekBenefitRow(stringResource(DsR.string.seek_benefit_resume))
    }
}

@Composable
private fun SeekBenefitRow(text: String) {
    val colors = MoloTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(colors.accentInk),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = colors.accentSoft,
                modifier = Modifier.size(13.dp),
            )
        }
        Text(text, color = colors.text, style = MaterialTheme.typography.bodyMedium)
    }
}
