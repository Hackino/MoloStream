package com.molostream.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.molostream.core.designsystem.theme.MoloTheme

/** The signature mango gradient used on primary actions and accents. */
@Composable
fun accentGradient(): Brush = Brush.linearGradient(
    colors = listOf(MoloTheme.colors.accentSoft, MoloTheme.colors.accentHot),
)
