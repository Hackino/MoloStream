package com.molostream.feature.home.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.molostream.core.designsystem.component.AssetImage
import com.molostream.core.designsystem.component.MoloPrimaryButton
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.model.Movie
import com.molostream.core.designsystem.R as DsR

/** Full-bleed featured hero: artwork, eyebrow, title/tagline, meta, and CTAs. */
@Composable
internal fun FeaturedHero(movie: Movie, onPlay: (Movie) -> Unit, onMoreInfo: (Movie) -> Unit) {
    val colors = MoloTheme.colors
    Box(modifier = Modifier.fillMaxWidth().height(520.dp)) {
        AssetImage(assetPath = movie.posterPath, contentDescription = movie.title, modifier = Modifier.fillMaxSize())
        // Top scrim keeps the transparent app bar legible; bottom scrim blends into the page.
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.45f),
                    0.25f to Color.Transparent,
                    0.55f to Color.Transparent,
                    1f to colors.background,
                ),
            ),
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FeaturedEyebrow()
            Text(movie.title, color = colors.text, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold)
            Text(movie.tagline, color = colors.textDim, style = MaterialTheme.typography.bodyLarge)
            HeroMeta(movie)
            Spacer(Modifier.size(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MoloPrimaryButton(
                    text = stringResource(DsR.string.action_play),
                    leadingIcon = Icons.Filled.PlayArrow,
                    onClick = { onPlay(movie) },
                )
                GhostButton(
                    text = stringResource(DsR.string.action_more_info),
                    leadingIcon = Icons.Filled.Info,
                    onClick = { onMoreInfo(movie) },
                )
            }
        }
    }
}

/** "Featured today" with the animated pulsing dot (matches the React hero). */
@Composable
private fun FeaturedEyebrow() {
    val colors = MoloTheme.colors
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(0.55f, 1f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "scale")
    val alpha by transition.animateFloat(0.4f, 1f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "alpha")
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
                .clip(CircleShape)
                .background(colors.accent),
        )
        Text(
            stringResource(DsR.string.home_featured_today).uppercase(),
            color = colors.accentSoft,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

/** Rating in a bordered container, then genre · runtime (or a LIVE label for live). */
@Composable
private fun HeroMeta(movie: Movie) {
    val colors = MoloTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .border(1.dp, colors.text.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            Text(movie.rating, color = colors.text, style = MaterialTheme.typography.labelSmall)
        }
        if (movie.isLive) {
            // Live has no runtime — show a red LIVE label instead of "genre · runtime".
            Text(
                stringResource(DsR.string.media_live),
                color = colors.live,
                style = MaterialTheme.typography.labelMedium,
            )
        } else {
            Text(
                stringResource(DsR.string.movie_genre_meta, movie.genre, movie.meta),
                color = colors.textDim,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
