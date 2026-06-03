package com.molostream.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.model.Movie
import com.molostream.feature.home.presentation.components.ContinueWatchingRail
import com.molostream.feature.home.presentation.components.FeaturedHero
import com.molostream.feature.home.presentation.components.HomeFooter
import com.molostream.feature.home.presentation.components.HomeTopBar
import com.molostream.feature.home.presentation.components.MovieCarousel
import com.molostream.feature.home.presentation.components.SubscriptionCard
import com.molostream.feature.home.presentation.dialogs.DetailSheet
import com.molostream.feature.home.presentation.dialogs.SubscribeDialog
import com.molostream.feature.home.presentation.dialogs.UnsubscribeDialog
import org.koin.androidx.compose.koinViewModel
import com.molostream.core.designsystem.R as DsR

/**
 * Home screen. The featured hero is full-bleed (starts at the very top, behind
 * a transparent app bar whose background fades in on scroll); below it sit the
 * subscription switch, an optional Continue Watching rail, the two carousels and
 * a footer. Tapping a title opens the player; "More info" opens the detail sheet.
 */
@Composable
fun HomeScreen(
    onOpenMovie: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var detailMovie by remember { mutableStateOf<Movie?>(null) }
    // null = no dialog; true = subscribe (benefits); false = unsubscribe (confirm).
    var pendingSubscribe by remember { mutableStateOf<Boolean?>(null) }

    // App-bar background opacity: transparent over the hero, fading to opaque as it scrolls away.
    val appBarAlpha by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) 1f
            else (listState.firstVisibleItemScrollOffset / 500f).coerceIn(0f, 1f)
        }
    }

    HomeContent(
        state = state,
        listState = listState,
        appBarAlpha = appBarAlpha,
        modifier = modifier,
        onOpenMovie = onOpenMovie,
        onShowDetail = { detailMovie = it },
        onRequestSubscribe = { pendingSubscribe = it },
    )

    HomeDialogs(
        subscribed = state.subscribed,
        detailMovie = detailMovie,
        pendingSubscribe = pendingSubscribe,
        onOpenMovie = onOpenMovie,
        onDismissDetail = { detailMovie = null },
        onSubscriptionChange = { confirmed ->
            confirmed?.let { viewModel.setSubscribed(it) }
            pendingSubscribe = null
        },
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    listState: LazyListState,
    appBarAlpha: Float,
    modifier: Modifier,
    onOpenMovie: (Movie) -> Unit,
    onShowDetail: (Movie) -> Unit,
    onRequestSubscribe: (Boolean?) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(MoloTheme.colors.background)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        ) {
            state.featured?.let { featured ->
                item { FeaturedHero(movie = featured, onPlay = onOpenMovie, onMoreInfo = onShowDetail) }
            }
            item {
                SubscriptionCard(
                    subscribed = state.subscribed,
                    onToggle = onRequestSubscribe,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            if (state.subscribed && state.continueWatching.isNotEmpty()) {
                item { ContinueWatchingRail(items = state.continueWatching, onOpenMovie = onOpenMovie) }
            }
            item {
                MovieCarousel(
                    heading = stringResource(DsR.string.home_rail_top_picks),
                    caption = stringResource(DsR.string.home_rail_top_picks_caption),
                    movies = state.verticalRail,
                    vertical = true,
                    onOpenMovie = onOpenMovie,
                )
            }
            item {
                MovieCarousel(
                    heading = stringResource(DsR.string.home_rail_movies),
                    caption = stringResource(DsR.string.home_rail_movies_caption),
                    movies = state.horizontalRail,
                    vertical = false,
                    onOpenMovie = onOpenMovie,
                )
            }
            item { HomeFooter() }
        }
        HomeTopBar(backgroundAlpha = appBarAlpha)
    }
}

@Composable
private fun HomeDialogs(
    subscribed: Boolean,
    detailMovie: Movie?,
    pendingSubscribe: Boolean?,
    onOpenMovie: (Movie) -> Unit,
    onDismissDetail: () -> Unit,
    onSubscriptionChange: (Boolean?) -> Unit,
) {
    detailMovie?.let { movie ->
        DetailSheet(
            movie = movie,
            subscribed = subscribed,
            onPlay = { onDismissDetail(); onOpenMovie(it) },
            onDismiss = onDismissDetail,
        )
    }

    when (pendingSubscribe) {
        true -> SubscribeDialog(
            onConfirm = { onSubscriptionChange(true) },
            onDismiss = { onSubscriptionChange(null) },
        )
        false -> UnsubscribeDialog(
            onConfirm = { onSubscriptionChange(false) },
            onDismiss = { onSubscriptionChange(null) },
        )
        null -> Unit
    }
}
