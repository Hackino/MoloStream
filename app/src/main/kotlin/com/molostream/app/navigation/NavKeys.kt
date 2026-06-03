package com.molostream.app.navigation

import androidx.navigation3.runtime.NavKey
import com.molostream.core.model.MediaType
import com.molostream.core.model.Movie
import com.molostream.feature.player.domain.PlayerArgs
import kotlinx.serialization.Serializable

/** Navigation 3 destination keys. Must be @Serializable for state saving. */
@Serializable
data object SplashKey : NavKey

@Serializable
data object HomeKey : NavKey

@Serializable
data class PlayerKey(
    val movieId: String,
    val title: String,
    val subtitle: String,
    val hlsUrl: String,
    val adTagUrl: String,
    val isLive: Boolean,
) : NavKey

/** Home hands the player everything it needs through the nav key. */
fun Movie.toPlayerKey() = PlayerKey(
    movieId = id,
    title = title,
    // Live has no runtime, so the subtitle is just the genre.
    subtitle = if (type == MediaType.LIVE) genre else "$genre · $meta",
    hlsUrl = hlsUrl,
    adTagUrl = adTagUrl,
    isLive = type == MediaType.LIVE,
)

fun PlayerKey.toArgs() = PlayerArgs(
    movieId = movieId,
    title = title,
    subtitle = subtitle,
    hlsUrl = hlsUrl,
    adTagUrl = adTagUrl,
    isLive = isLive,
)
