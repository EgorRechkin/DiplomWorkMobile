package com.example.diplomwork.platform

import platform.Foundation.NSBundle
import platform.NetworkExtension.NEHotspotNetwork

actual class WifiChecker {

    private var monitoring = false
    private var monitoringCallback: ((String?) -> Unit)? = null

    actual suspend fun getCurrentSsid(): String? {
        // На iOS SSID доступен только через NEHotspotNetwork
        // Требует entitlement: com.apple.developer.networking.wifi-info
        var result: String? = null
        NEHotspotNetwork.fetchCurrentWithCompletionHandler { network ->
            result = network?.SSID
        }
        return result
    }

    actual fun isOnWorkNetwork(ssid: String): Boolean {
        val workNetworks = listOf("Office_5G", "Office_2.4G")
        return workNetworks.contains(ssid)
    }

    actual fun startMonitoring(onNetworkChanged: (String?) -> Unit) {
        monitoringCallback = onNetworkChanged
        monitoring = true
        // iOS не даёт фонового мониторинга WiFi без entitlement
        // Вызываем при каждом foreground через AppDelegate/SceneDelegate
    }

    actual fun stopMonitoring() {
        monitoring = false
        monitoringCallback = null
    }
}