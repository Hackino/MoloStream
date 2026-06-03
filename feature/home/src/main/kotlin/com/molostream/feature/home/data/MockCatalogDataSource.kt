package com.molostream.feature.home.data

import android.content.Context
import com.molostream.core.model.MediaType
import com.molostream.core.model.Movie
import com.molostream.core.model.Orientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Reads the bundled mock catalog from `assets/catalog.json`. This is the Home
 * feature's own data source; swapping it for a network source later is just a
 * new [com.molostream.feature.home.domain.CatalogRepository] implementation.
 */
class MockCatalogDataSource(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun load(): List<Movie> = withContext(Dispatchers.IO) {
        val raw = context.assets.open(ASSET_FILE).bufferedReader().use { it.readText() }
        val catalog = json.decodeFromString<CatalogDto>(raw)
        catalog.items.map { it.toMovie(catalog.adTagUrl) }
    }

    private fun MovieDto.toMovie(adTagUrl: String) = Movie(
        id = id,
        title = title,
        genre = genre,
        tagline = tagline,
        description = description,
        year = year,
        rating = rating,
        meta = meta,
        orientation = orientation.toOrientation(),
        type = type.toMediaType(),
        posterPath = poster,
        hlsUrl = url,
        adTagUrl = adTagUrl,
    )

    private fun String.toOrientation() =
        if (equals("HORIZONTAL", ignoreCase = true)) Orientation.HORIZONTAL else Orientation.VERTICAL

    private fun String.toMediaType() = when (uppercase()) {
        "LIVE" -> MediaType.LIVE
        "SERIES" -> MediaType.SERIES
        else -> MediaType.MOVIE
    }

    private companion object {
        const val ASSET_FILE = "catalog.json"
    }
}
