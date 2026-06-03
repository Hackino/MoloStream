package com.molostream.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.molostream.core.designsystem.component.AssetImage
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.model.Movie
import com.molostream.core.designsystem.R as DsR

/** A titled, horizontally-scrolling rail of poster cards. */
@Composable
internal fun MovieCarousel(
    heading: String,
    caption: String,
    movies: List<Movie>,
    vertical: Boolean,
    onOpenMovie: (Movie) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(heading, color = MoloTheme.colors.text, style = MaterialTheme.typography.titleLarge)
            Text(caption, color = MoloTheme.colors.textMute, style = MaterialTheme.typography.bodyMedium)
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(movies, key = { it.id }) { movie ->
                PosterCard(movie = movie, vertical = vertical, onClick = { onOpenMovie(movie) })
            }
        }
    }
}

/**
 * Poster card with the same overlays as the React design: a genre chip (or LIVE
 * badge for live) at top-start, a bottom scrim, and the title + year · rating.
 */
@Composable
private fun PosterCard(movie: Movie, vertical: Boolean, onClick: () -> Unit) {
    val colors = MoloTheme.colors
    val cardModifier = if (vertical) Modifier.width(132.dp).height(198.dp) else Modifier.width(248.dp).height(140.dp)
    Box(
        modifier = cardModifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
    ) {
        AssetImage(assetPath = movie.posterPath, contentDescription = "${movie.title} — ${movie.genre}", modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(0.45f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.82f)),
            ),
        )
        // Live → red LIVE badge; otherwise the genre chip.
        Text(
            text = if (movie.isLive) stringResource(DsR.string.media_live) else movie.genre,
            color = colors.text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .clip(CircleShape)
                .background(if (movie.isLive) colors.live else Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 7.dp, vertical = 3.dp),
        )
        // Title + meta
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                movie.title,
                color = colors.text,
                style = if (vertical) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                stringResource(DsR.string.movie_year_rating, movie.year, movie.rating),
                color = colors.textDim,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
