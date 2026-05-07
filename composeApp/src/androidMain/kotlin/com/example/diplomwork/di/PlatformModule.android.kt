package com.example.diplomwork.di

import android.app.Activity
import com.example.diplomwork.platform.NfcScanner
import com.example.diplomwork.platform.WifiChecker
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // WifiChecker требует Context — берём из androidContext()
    single { WifiChecker(androidContext()) }
    factory { (activity: Activity) -> NfcScanner(activity) }
}