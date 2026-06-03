package com.molostream.feature.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.molostream.core.designsystem.component.AssetImage
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.model.ContinueWatching
import com.molostream.core.model.Movie
import com.molostream.core.designsystem.R as DsR

/** Continue Watching rail: resume cards with a progress bar. */
@Composable
internal fun ContinueWatchingRail(items: List<ContinueWatching>, onOpenMovie: (Movie) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            stringResource(DsR.string.home_rail_continue),
            color = MoloTheme.colors.text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items, key = { it.movie.id }) { entry ->
                ContinueCard(entry = entry, onClick = { onOpenMovie(entry.movie) })
            }
        }
    }
}

@Composable
private fun ContinueCard(entry: ContinueWatching, onClick: () -> Unit) {
    val colors = MoloTheme.colors
    Column(modifier = Modifier.width(248.dp).clickable(onClick = onClick), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        AssetImage(
            assetPath = entry.movie.posterPath,
            contentDescription = entry.movie.title,
            modifier = Modifier.width(248.dp).height(140.dp).clip(RoundedCornerShape(16.dp)),
        )
        LinearProgressIndicator(
            progress = { entry.progress.fraction },
            modifier = Modifier.fillMaxWidth().height(3.dp),
            color = colors.accent,
            trackColor = colors.surface3,
        )
        Text(
            entry.movie.title,
            color = colors.textDim,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
