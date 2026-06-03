package com.molostream.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.molostream.core.datastore.SubscriptionPreferences
import com.molostream.core.datastore.crypto.KeystoreCrypto
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

// Single DataStore instance per process, scoped to the application context.
private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "molostream_prefs")

val dataStoreModule = module {
    single { KeystoreCrypto() }
    single { androidContext().preferencesDataStore }
    single { SubscriptionPreferences(get(), get()) }
}
