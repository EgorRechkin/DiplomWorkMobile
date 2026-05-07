package com.example.diplomwork.platform

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import java.time.LocalTime

actual class WifiChecker(private val context: Context) {

    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    // Список рабочих сетей — TODO: загружать из настроек/БД
    private val workNetworks = listOf("Office_5G", "Office_2.4G")

    actual suspend fun getCurrentSsid(): String? {
        val networkInfo = connectivityManager.activeNetwork ?: return null
        val capabilities = connectivityManager.getNetworkCapabilities(networkInfo) ?: return null

        if (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return null

        // Android 10+ — через WifiInfo из NetworkCapabilities
        val wifiInfo = wifiManager.connectionInfo ?: return null
        val ssid = wifiInfo.ssid

        // Android возвращает SSID в кавычках: "Office_5G" → убираем
        return ssid?.removePrefix("\"")?.removeSuffix("\"")
            ?.takeIf { it.isNotEmpty() && it != "<unknown ssid>" }
    }

    actual fun isOnWorkNetwork(ssid: String): Boolean {
        return workNetworks.contains(ssid)
    }

    actual fun startMonitoring(onNetworkChanged: (String?) -> Unit) {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val ssid = wifiManager.connectionInfo?.ssid
                    ?.removePrefix("\"")?.removeSuffix("\"")
                    ?.takeIf { it.isNotEmpty() && it != "<unknown ssid>" }
                onNetworkChanged(ssid)
            }

            override fun onLost(network: Network) {
                onNetworkChanged(null)
            }
        }

        connectivityManager.registerNetworkCallback(request, networkCallback!!)
    }

    actual fun stopMonitoring() {
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
        }
        networkCallback = null
    }
}