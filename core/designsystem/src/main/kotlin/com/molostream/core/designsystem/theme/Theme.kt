package com.molostream.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Root theme. Wraps Material 3 with the MoloStream dark palette and exposes the
 * brand colors via [MoloTheme.colors]. The app is intentionally dark-only.
 */
@Composable
fun MoloStreamTheme(content: @Composable () -> Unit) {
    val colors = DarkMoloColors
    val scheme = darkColorScheme(
        primary = colors.accent,
        onPrimary = colors.onAccent,
        secondary = colors.accentSoft,
        background = colors.background,
        onBackground = colors.text,
        surface = colors.surface1,
        onSurface = colors.text,
        surfaceVariant = colors.surface2,
        onSurfaceVariant = colors.textDim,
        error = colors.live,
    )
    CompositionLocalProvider(LocalMoloColors provides colors) {
        MaterialTheme(
            colorScheme = scheme,
            typography = MoloTypography,
            shapes = MoloShapes,
            content = content,
        )
    }
}

/** Brand-token accessor: `MoloTheme.colors.accent`, etc. */
object MoloTheme {
    val colors: MoloColors
        @Composable @ReadOnlyComposable get() = LocalMoloColors.current
}
