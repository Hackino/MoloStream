package com.molostream.feature.home.data

import com.molostream.feature.home.domain.CatalogRepository
import com.molostream.core.model.Movie

/**
 * [CatalogRepository] backed by the bundled mock data source, with a simple
 * in-memory cache (the catalog is static for the session).
 */
class CatalogRepositoryImpl(
    private val dataSource: MockCatalogDataSource,
) : CatalogRepository {

    @Volatile
    private var cache: List<Movie>? = null

    override suspend fun getCatalog(): List<Movie> =
        cache ?: dataSource.load().also { cache = it }

    override suspend fun getMovie(id: String): Movie? =
        getCatalog().firstOrNull { it.id == id }
}
