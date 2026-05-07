package com.example.diplomwork.di

import com.example.diplomwork.platform.WifiChecker
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // iOS не требует Context
    single { WifiChecker() }
}