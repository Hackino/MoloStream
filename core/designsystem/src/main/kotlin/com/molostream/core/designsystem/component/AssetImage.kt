package com.molostream.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.molostream.core.designsystem.theme.MoloTheme

/**
 * Loads a bundled poster from `assets/` (e.g. "posters/frame-01.jpg") via Coil.
 * Shows a surface-toned placeholder while decoding.
 */
@Composable
fun AssetImage(
    assetPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    AsyncImage(
        model = "file:///android_asset/$assetPath",
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier.background(MoloTheme.colors.surface2),
    )
}
