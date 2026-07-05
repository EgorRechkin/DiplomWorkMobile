package com.example.diplomwork.platform

import platform.Foundation.NSTimer
import platform.NetworkExtension.NEHotspotNetwork

actual class WifiChecker {

    private var monitoring = false
    private var monitoringCallback: ((String?) -> Unit)? = null
    private var timer: NSTimer? = null

    actual suspend fun getCurrentSsid(): String? =
        suspendCoroutine { continuation ->
            NEHotspotNetwork.fetchCurrentWithCompletionHandler { network ->
                continuation.resume(network?.SSID)
            }
        }

    actual fun startMonitoring(onNetworkChanged: (String?) -> Unit) {
        monitoringCallback = onNetworkChanged
        monitoring = true

        // Проверяем сеть каждые 30 секунд
        timer = NSTimer.scheduledTimerWithTimeInterval(
            interval = 30.0,
            repeats = true
        ) {
            if (monitoring) {
                NEHotspotNetwork.fetchCurrentWithCompletionHandler { network ->
                    onNetworkChanged(network?.SSID)
                }
            }
        }

        // Первая проверка сразу
        NEHotspotNetwork.fetchCurrentWithCompletionHandler { network ->
            onNetworkChanged(network?.SSID)
        }
    }

    actual fun stopMonitoring() {
        monitoring = false
        monitoringCallback = null
        timer?.invalidate()
        timer = null
    }

    actual fun isOnWorkNetwork(ssid: String): Boolean {
        return false // загружается из БД через ViewModel
    }
}