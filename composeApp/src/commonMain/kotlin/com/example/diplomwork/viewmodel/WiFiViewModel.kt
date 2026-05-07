package com.example.diplomwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplomwork.platform.WifiChecker
import com.example.diplomwork.platform.currentTimeString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class WifiNetwork(
    val ssid: String,
    val isAllowed: Boolean
)

data class WifiEvent(
    val time: String,
    val type: WifiEventType,
    val ssid: String,
    val description: String
)

enum class WifiEventType { CONNECTED, DISCONNECTED }

data class WifiState(
    val currentSsid: String? = null,
    val isWorkNetwork: Boolean = false,
    val isMonitoring: Boolean = false,
    val allowedNetworks: List<WifiNetwork> = emptyList(),
    val todayEvents: List<WifiEvent> = emptyList(),
    val error: String? = null
)

// --- ViewModel ---

class WifiViewModel(private val wifiChecker: WifiChecker) : ViewModel() {

    private val _state = MutableStateFlow(WifiState())
    val state: StateFlow<WifiState> = _state.asStateFlow()

    // Список разрешённых рабочих сетей
    // TODO: загружать из настроек / базы
    private val allowedSsids = listOf("Office_5G", "Office_2.4G", "AndroidWifi")

    init {
        loadAllowedNetworks()
        startMonitoring()
    }

    fun startMonitoring() {
        _state.update { it.copy(isMonitoring = true) }
        // Подписываемся на изменения сети через платформенный класс
        wifiChecker.startMonitoring { ssid ->
            onNetworkChanged(ssid)
        }
        // Проверяем текущую сеть сразу при запуске
        viewModelScope.launch {
            val currentSsid = wifiChecker.getCurrentSsid()
            onNetworkChanged(currentSsid)
        }
    }

    private fun loadAllowedNetworks() {
        _state.update {
            it.copy(
                allowedNetworks = allowedSsids.map { ssid ->
                    WifiNetwork(ssid = ssid, isAllowed = true)
                }
            )
        }
    }

    fun stopMonitoring() {
        _state.update { it.copy(isMonitoring = false) }
        wifiChecker.stopMonitoring()
    }


    // Вызывается платформенным WifiChecker когда сеть изменилась
    fun onNetworkChanged(ssid: String?) {
        val isWork = ssid != null && allowedSsids.contains(ssid)
        val previous = _state.value.currentSsid
        val previousIsWork = _state.value.isWorkNetwork

        _state.update {
            it.copy(
                currentSsid = ssid,
                isWorkNetwork = isWork
            )
        }

        // Фиксируем событие если сеть изменилась
        if (ssid != previous) {
            when {
                // Подключились к рабочей сети — приход
                isWork && !previousIsWork -> {
                    addEvent(
                        ssid = ssid ?: "",
                        type = WifiEventType.CONNECTED,
                        description = "приход"
                    )
                    recordCheckIn(ssid!!)
                }
                // Отключились от рабочей сети — уход / пауза
                !isWork && previousIsWork -> {
                    addEvent(
                        ssid = previous ?: "",
                        type = WifiEventType.DISCONNECTED,
                        description = "уход / пауза"
                    )
                    recordCheckOut()
                }
            }
        }
    }

    private fun checkCurrentNetwork() {
        // TODO: вызвать платформенный WifiChecker.getCurrentSsid()
        // и передать результат в onNetworkChanged()
    }


    private fun addEvent(ssid: String, type: WifiEventType, description: String) {
        val time = currentTimeString()

        val event = WifiEvent(
            time = time,
            type = type,
            ssid = ssid,
            description = description
        )

        _state.update {
            it.copy(todayEvents = it.todayEvents + event)
        }
    }

    private fun recordCheckIn(ssid: String) {
        viewModelScope.launch {
            try {
                // TODO: attendanceRepository.recordEvent(
                //     employeeId = currentEmployeeId,
                //     source = RecordSource.WIFI,
                //     ssid = ssid
                // )
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun recordCheckOut() {
        viewModelScope.launch {
            try {
                // TODO: attendanceRepository.recordCheckOut(
                //     employeeId = currentEmployeeId,
                //     source = RecordSource.WIFI
                // )
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val currentSsid = wifiChecker.getCurrentSsid()
            onNetworkChanged(currentSsid)
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}