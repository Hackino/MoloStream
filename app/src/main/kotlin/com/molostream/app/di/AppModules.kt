package com.molostream.app.di

import com.molostream.core.data.di.dataModule
import com.molostream.core.database.di.databaseModule
import com.molostream.core.datastore.di.dataStoreModule
import com.molostream.feature.home.di.homeModule
import com.molostream.feature.player.di.playerModule

/**
 * Every module contributes its own Koin module; the app composes them. Note
 * [dataModule]'s catalog use cases resolve [com.molostream.core.domain.repository.CatalogRepository]
 * which is bound by [homeModule].
 */
val appModules = listOf(
    dataStoreModule,
    databaseModule,
    dataModule,
    homeModule,
    playerModule,
)
