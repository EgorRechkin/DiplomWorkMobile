package com.example.diplomwork

import android.app.Application
import com.example.diplomwork.di.appModule
import com.example.diplomwork.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AndroidApp)
            modules(appModule, platformModule)
        }
    }
}