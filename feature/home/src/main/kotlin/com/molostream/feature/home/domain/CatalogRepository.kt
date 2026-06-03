package com.molostream.feature.home.domain

import com.molostream.core.model.Movie

/**
 * Source of catalog titles. Owned by the Home feature (its mock data source
 * implements it); use cases below depend on this abstraction.
 */
interface CatalogRepository {
    suspend fun getCatalog(): List<Movie>
    suspend fun getMovie(id: String): Movie?
}
