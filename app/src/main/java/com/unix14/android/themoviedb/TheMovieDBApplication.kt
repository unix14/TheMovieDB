package com.unix14.android.themoviedb

import android.app.Application
import com.unix14.android.themoviedb.di.appModule
import com.unix14.android.themoviedb.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TheMovieDBApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        setupKoin()
    }

    private fun setupKoin() {
        // start Koin (di) context
        startKoin {
            androidContext(this@TheMovieDBApplication)
            androidLogger()
            modules(appModule, networkModule)
        }
    }
}