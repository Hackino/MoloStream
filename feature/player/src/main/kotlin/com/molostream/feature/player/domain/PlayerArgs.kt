package com.molostream.feature.player.domain

/**
 * Everything the player needs to stream a title. Passed in from Home via the
 * navigation key, so :feature:player stays decoupled from the catalog.
 */
data class PlayerArgs(
    val movieId: String,
    val title: String,
    val subtitle: String,
    val hlsUrl: String,
    val adTagUrl: String,
    /** Live streams have no fixed duration, can't resume, and aren't saved. */
    val isLive: Boolean = false,
)
