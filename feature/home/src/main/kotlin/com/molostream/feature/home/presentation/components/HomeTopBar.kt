package com.molostream.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.molostream.core.designsystem.component.accentGradient
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.designsystem.R as DsR

/** Transparent app bar that overlays the hero; its background fades in on scroll. */
@Composable
internal fun HomeTopBar(backgroundAlpha: Float) {
    val colors = MoloTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background.copy(alpha = backgroundAlpha))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(DsR.string.brand_molo),
                color = colors.text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                stringResource(DsR.string.brand_stream),
                color = colors.accent,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Box(
            modifier = Modifier.size(34.dp).clip(CircleShape).background(accentGradient()),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(DsR.string.brand_avatar_initial), color = colors.onAccent, style = MaterialTheme.typography.labelLarge)
        }
    }
}
