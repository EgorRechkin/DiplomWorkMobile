package com.example.diplomwork

import androidx.compose.ui.window.ComposeUIViewController
import com.example.diplomwork.di.appModule
import com.example.diplomwork.di.platformModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(appModule, platformModule)
    }
    App(
        onStartNfcScan = {},
        onStopNfcScan = {}
    )
}