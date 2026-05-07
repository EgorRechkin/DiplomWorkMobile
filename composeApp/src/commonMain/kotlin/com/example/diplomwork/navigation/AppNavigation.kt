// commonMain/kotlin/com/example/diplomwork/navigation/AppNavigation.kt
package com.example.diplomwork.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diplomwork.ui.checkin.CheckInScreen
import com.example.diplomwork.ui.checkin.NfcScanScreen
import com.example.diplomwork.ui.checkin.WifiStatusScreen
import com.example.diplomwork.viewmodel.CheckInViewModel
import com.example.diplomwork.viewmodel.NfcViewModel
import com.example.diplomwork.viewmodel.WifiViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(
    onStartNfcScan: ((String) -> Unit) -> Unit,
    onStopNfcScan: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        composable("home") {
            val viewModel = koinViewModel<CheckInViewModel>()
            CheckInScreen(
                viewModel = viewModel,
                onNfcClick = { navController.navigate("nfc") },
                onWifiClick = { navController.navigate("wifi") }
            )
        }
        composable("nfc") {
            val viewModel = koinViewModel<NfcViewModel>()
            NfcScanScreen(
                viewModel = viewModel,
                onBack = navController::popBackStack,
                onStartScan = onStartNfcScan,
                onStopScan = onStopNfcScan
            )
        }
        composable("wifi") {
            val viewModel = koinViewModel<WifiViewModel>()
            WifiStatusScreen(
                viewModel = viewModel,
                onBack = navController::popBackStack
            )
        }
    }
}