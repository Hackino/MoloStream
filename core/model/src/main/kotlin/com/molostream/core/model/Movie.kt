package com.molostream.core.model

/** Poster orientation — drives which carousel a title appears in. */
enum class Orientation { VERTICAL, HORIZONTAL }

/**
 * A catalog title. Every movie has a unique [id]; in this prototype they all
 * share the same HLS source ([hlsUrl]) and IMA ad tag ([adTagUrl]) from the
 * brief, but the model is shaped as if each could differ.
 */
data class Movie(
    val id: String,
    val title: String,
    val genre: String,
    val tagline: String,
    val description: String,
    val year: Int,
    val rating: String,
    val meta: String,
    val orientation: Orientation,
    val type: MediaType,
    /** Asset-relative path to the poster image, e.g. "posters/frame-01.jpg". */
    val posterPath: String,
    val hlsUrl: String,
    val adTagUrl: String,
) {
    val isLive: Boolean get() = type == MediaType.LIVE
}
