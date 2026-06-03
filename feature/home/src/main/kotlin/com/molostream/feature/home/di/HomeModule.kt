package com.molostream.feature.home.di

import com.molostream.feature.home.data.CatalogRepositoryImpl
import com.molostream.feature.home.data.MockCatalogDataSource
import com.molostream.feature.home.domain.CatalogRepository
import com.molostream.feature.home.domain.GetCatalogUseCase
import com.molostream.feature.home.domain.GetMovieUseCase
import com.molostream.feature.home.domain.ObserveContinueWatchingUseCase
import com.molostream.feature.home.presentation.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Home wires its own data → domain → presentation graph. The catalog repository
 * and its use cases are feature-local; ObserveContinueWatching also pulls the
 * cross-cutting WatchProgressRepository (provided by :core:data).
 */
val homeModule = module {
    // data
    single { MockCatalogDataSource(androidContext()) }
    single<CatalogRepository> { CatalogRepositoryImpl(get()) }

    // domain
    factory { GetCatalogUseCase(get()) }
    factory { GetMovieUseCase(get()) }
    factory { ObserveContinueWatchingUseCase(get(), get()) }

    // presentation
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
}
