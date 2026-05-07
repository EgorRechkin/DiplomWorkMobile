package com.example.diplomwork.platform

expect class WifiChecker {
    suspend fun getCurrentSsid(): String?
    fun isOnWorkNetwork(ssid: String): Boolean
    fun startMonitoring(onNetworkChanged: (String?) -> Unit)
    fun stopMonitoring()
}