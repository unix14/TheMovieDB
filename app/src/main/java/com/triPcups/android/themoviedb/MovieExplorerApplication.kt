package com.triPcups.android.themoviedb

import android.app.Application
import com.triPcups.android.themoviedb.di.appModule
import com.triPcups.android.themoviedb.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MovieExplorerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        setupKoin()
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@MovieExplorerApplication)
            androidLogger()

            modules(appModule, networkModule)
        }
    }
}