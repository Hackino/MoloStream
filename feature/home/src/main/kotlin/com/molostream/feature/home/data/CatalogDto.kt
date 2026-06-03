package com.molostream.feature.home.data

import kotlinx.serialization.Serializable

/**
 * Wire shape of `assets/catalog.json`. Each title has its own HLS [MovieDto.url]
 * and a [MovieDto.type] (live / movie / series); the IMA ad tag is shared.
 */
@Serializable
data class CatalogDto(
    val adTagUrl: String,
    val items: List<MovieDto>,
)

@Serializable
data class MovieDto(
    val id: String,
    val title: String,
    val genre: String,
    val tagline: String,
    val description: String,
    val year: Int,
    val rating: String,
    val meta: String,
    val orientation: String,
    /** "LIVE" | "MOVIE" | "SERIES" (defaults to MOVIE). */
    val type: String = "MOVIE",
    val poster: String,
    val url: String,
)
