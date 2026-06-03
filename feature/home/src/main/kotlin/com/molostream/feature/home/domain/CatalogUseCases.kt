package com.molostream.feature.home.domain

import com.molostream.core.model.Movie

/** Loads the full catalog (Home splits it into the vertical/horizontal rails). */
class GetCatalogUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(): List<Movie> = repository.getCatalog()
}

/** Resolves a single movie by id. */
class GetMovieUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(id: String): Movie? = repository.getMovie(id)
}
