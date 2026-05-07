package com.example.diplomwork

import android.Manifest
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.diplomwork.platform.NfcScanner
import com.example.diplomwork.viewmodel.WifiViewModel
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {
    private val wifiViewModel: WifiViewModel by inject()
    private val nfcScanner by lazy { NfcScanner(this) }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (granted) {
            wifiViewModel.refresh()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            App(
                onStartNfcScan = { callback -> nfcScanner.startScan(callback) },
                onStopNfcScan = { nfcScanner.stopScan() }
            )
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}