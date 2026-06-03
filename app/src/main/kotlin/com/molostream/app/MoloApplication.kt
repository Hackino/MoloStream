package com.molostream.app

import android.app.Application
import com.molostream.app.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/** Application entry point — boots the Koin graph for every module. */
class MoloApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MoloApplication)
            modules(appModules)
        }
    }
}
