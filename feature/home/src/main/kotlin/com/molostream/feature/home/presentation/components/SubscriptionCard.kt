package com.molostream.feature.home.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.molostream.core.designsystem.component.accentGradient
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.designsystem.R as DsR

/** Premium plan card with a custom pill switch to toggle subscription. */
@Composable
internal fun SubscriptionCard(subscribed: Boolean, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val colors = MoloTheme.colors
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.linearGradient(listOf(colors.surface2, colors.surface1)))
            .border(1.dp, if (subscribed) colors.accent.copy(alpha = 0.4f) else colors.hairline, shape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(accentGradient()),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = colors.onAccent)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(stringResource(DsR.string.home_premium_title), color = colors.text, style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(if (subscribed) DsR.string.home_premium_on else DsR.string.home_premium_off),
                color = colors.textMute,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        PremiumSwitch(checked = subscribed, onCheckedChange = onToggle)
    }
}

/** Custom pill switch matching the React design (gradient track + sliding thumb). */
@Composable
private fun PremiumSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = MoloTheme.colors
    val thumbOffset by animateDpAsState(targetValue = if (checked) 22.dp else 0.dp, label = "thumb")
    val track = if (checked) Modifier.background(accentGradient()) else Modifier.background(colors.surface3)
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 30.dp)
            .clip(CircleShape)
            .then(track)
            .border(1.dp, if (checked) Color.Transparent else colors.hairline, CircleShape)
            .clickable { onCheckedChange(!checked) }
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(modifier = Modifier.offset(x = thumbOffset).size(24.dp).clip(CircleShape).background(colors.text))
    }
}
