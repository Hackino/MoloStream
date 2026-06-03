package com.molostream.core.data.di

import com.molostream.core.common.DefaultDispatcherProvider
import com.molostream.core.common.DispatcherProvider
import com.molostream.core.data.SubscriptionRepositoryImpl
import com.molostream.core.data.WatchProgressRepositoryImpl
import com.molostream.core.domain.repository.SubscriptionRepository
import com.molostream.core.domain.repository.WatchProgressRepository
import com.molostream.core.domain.usecase.ClearContinueWatchingUseCase
import com.molostream.core.domain.usecase.GetResumePositionUseCase
import com.molostream.core.domain.usecase.ObserveSubscriptionUseCase
import com.molostream.core.domain.usecase.SaveWatchProgressUseCase
import com.molostream.core.domain.usecase.SetSubscriptionUseCase
import org.koin.dsl.module

/**
 * Cross-cutting repositories + use cases (subscription, watch progress,
 * dispatchers). Catalog/continue-watching domain lives in :feature:home.
 */
val dataModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }

    single<SubscriptionRepository> { SubscriptionRepositoryImpl(get()) }
    single<WatchProgressRepository> { WatchProgressRepositoryImpl(get()) }

    factory { ObserveSubscriptionUseCase(get()) }
    factory { SetSubscriptionUseCase(get()) }
    factory { SaveWatchProgressUseCase(get()) }
    factory { GetResumePositionUseCase(get()) }
    factory { ClearContinueWatchingUseCase(get()) }
}
