package com.molostream.feature.home.presentation

import com.molostream.core.model.ContinueWatching
import com.molostream.core.model.Movie

data class HomeUiState(
    val loading: Boolean = true,
    val featured: Movie? = null,
    val verticalRail: List<Movie> = emptyList(),
    val horizontalRail: List<Movie> = emptyList(),
    val continueWatching: List<ContinueWatching> = emptyList(),
    val subscribed: Boolean = false,
)
