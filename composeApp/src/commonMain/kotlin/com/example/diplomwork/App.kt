package com.example.diplomwork

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.diplomwork.navigation.AppNavigation
import com.example.diplomwork.ui.LoginScreen
import com.example.diplomwork.viewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    onStartNfcScan: ((String) -> Unit) -> Unit = {},
    onStopNfcScan: () -> Unit = {}
) {
    val colors = lightColorScheme(
        primary = Color(0xFF1A1A1A),
        onPrimary = Color.White,
        primaryContainer = Color(0xFF1A1A1A),
        onPrimaryContainer = Color.White,
        secondary = Color(0xFF1A1A1A),
        onSecondary = Color.White,
        background = Color.White,
        onBackground = Color(0xFF1A1A1A),
        surface = Color.White,
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = Color(0xFFF5F5F5),
        onSurfaceVariant = Color(0xFF666666)
    )

    MaterialTheme(colorScheme = colors) {
        val authViewModel = koinViewModel<AuthViewModel>()
        val authState by authViewModel.state.collectAsState()

        when {
            authState.isAuthorized -> AppNavigation(
                onStartNfcScan = onStartNfcScan,
                onStopNfcScan = onStopNfcScan,
                employeeId = authState.employeeId ?: "",
                onLogout = { authViewModel.logout() }
            )
            else -> LoginScreen(viewModel = authViewModel)
        }
    }
}