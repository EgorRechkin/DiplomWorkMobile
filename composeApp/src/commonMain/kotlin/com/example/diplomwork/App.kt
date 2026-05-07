package com.example.diplomwork

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource

import diplomwork.composeapp.generated.resources.Res
import diplomwork.composeapp.generated.resources.compose_multiplatform

import androidx.compose.runtime.Composable
import com.example.diplomwork.navigation.AppNavigation

@Composable
fun App(
    onStartNfcScan: ((String) -> Unit) -> Unit = {},
    onStopNfcScan: () -> Unit = {}
) {
    MaterialTheme {
        AppNavigation(
            onStartNfcScan = onStartNfcScan,
            onStopNfcScan = onStopNfcScan
        )
    }
}