package com.molostream.core.database.di

import androidx.room.Room
import com.molostream.core.database.MoloDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** Koin module exposing the Room database and its DAOs. */
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            MoloDatabase::class.java,
            "molostream.db",
        ).build()
    }
    single { get<MoloDatabase>().watchProgressDao() }
}
