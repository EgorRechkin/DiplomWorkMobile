package com.example.diplomwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplomwork.data.ApiClient
import com.example.diplomwork.data.TimeIntervalDto
import com.example.diplomwork.platform.DateTimeProvider
import com.example.diplomwork.platform.SessionStorage
import com.example.diplomwork.platform.WifiChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.minus

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

class WifiViewModel(private val wifiChecker: WifiChecker, private val apiClient: ApiClient, private val sessionStorage: SessionStorage) : ViewModel() {

    private val _state = MutableStateFlow(WifiState())
    val state: StateFlow<WifiState> = _state.asStateFlow()

    //private val employeeId = "139ee633-5543-40e5-a4f7-4d845efb5443"
    private var allowedSsids = listOf("Office_5G", "Office_2.4G", "AndroidWifi")

    private var employeeId: String = ""

    init {
        viewModelScope.launch {
            employeeId = sessionStorage.getEmployeeId() ?: ""
            println("WifiViewModel: employeeId загружен = $employeeId")
            loadAllowedNetworks()
            startMonitoring()
        }
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
        viewModelScope.launch {
            try {
                val ssid = apiClient.getEmployeeSsid(employeeId)
                if (ssid != null) {
                    println("WifiViewModel: загружена сеть $ssid")
                    allowedSsids = listOf(ssid)
                    _state.update {
                        it.copy(
                            allowedNetworks = listOf(WifiNetwork(ssid = ssid, isAllowed = true))
                        )
                    }
                    // Перепроверяем текущую сеть
                    val currentSsid = wifiChecker.getCurrentSsid()
                    onNetworkChanged(currentSsid)
                } else {
                    println("WifiViewModel: SSID не найден для сотрудника")
                }
            } catch (e: Exception) {
                println("WifiViewModel: ошибка загрузки SSID ${e.message}")
            }
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
        val time = DateTimeProvider().currentTimeString()

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
                val now = DateTimeProvider()
                val dateStr = now.currentDateString()
                val timeStr = now.currentTimeString()

                println("События за сегодня ${_state.value.todayEvents}")

                // Рассчитываем табель за вчера при первом входе сегодня
                val connectedEventsToday = _state.value.todayEvents
                    .count { it.type == WifiEventType.CONNECTED }
                val isFirstEventToday = connectedEventsToday <= 1
                if (isFirstEventToday) {
                    val yesterday = getYesterdayDateString()
                    println("Вчера: ${yesterday}")
                    println("WifiViewModel: рассчитываем табель за $yesterday")
                    try {
                        apiClient.checkAbsence(employeeId, yesterday)
                        apiClient.calculateTabel(employeeId, yesterday)
                        println("WifiViewModel: табель за $yesterday рассчитан")
                    } catch (e: Exception) {
                        println("WifiViewModel: ошибка расчёта табеля: ${e.message}")
                    }
                }

                // Записываем приход
                apiClient.recordTimeInterval(
                    TimeIntervalDto(
                        employeeId = employeeId,
                        date = dateStr,
                        recordSource = "WIFI",
                        startTime = "${dateStr}T${timeStr}:00",
                        endTime = null,
                        updateTime = "${dateStr}T${timeStr}:00"
                    )
                )

                println("WifiViewModel: recordCheckIn успешно записано")

            } catch (e: Exception) {
                println("WifiViewModel: recordCheckIn ОШИБКА ${e.message}")
                _state.update { it.copy(error = "Ошибка записи прихода: ${e.message}") }
            }
        }
    }

    private fun recordCheckOut() {
        viewModelScope.launch {
            try {
                val now = DateTimeProvider()
                val dateStr = now.currentDateString()
                val timeStr = now.currentTimeString()

                println("WifiViewModel: recordCheckOut date=$dateStr time=$timeStr")

                val intervals = apiClient.getTimeIntervals(
                    employeeId = employeeId,
                    date = dateStr
                )

                println("WifiViewModel: найдено интервалов=${intervals.size}")
                intervals.forEach { println("WifiViewModel: интервал id=${it.id} endTime=${it.endTime}") }

                val lastOpen = intervals.lastOrNull { it.endTime == null }
                println("WifiViewModel: lastOpen=$lastOpen")

                if (lastOpen != null && lastOpen.id != null) {
                    val endTimeStr = "${dateStr}T${timeStr}:00"
                    println("WifiViewModel: закрываю интервал id=${lastOpen.id} endTime=$endTimeStr")
                    apiClient.updateTimeInterval(
                        id = lastOpen.id,
                        endTime = endTimeStr
                    )
                    println("WifiViewModel: интервал закрыт")
                } else {
                    println("WifiViewModel: открытый интервал не найден")
                }

            } catch (e: Exception) {
                println("WifiViewModel: recordCheckOut ОШИБКА ${e::class.simpleName}: ${e.message}")
                _state.update { it.copy(error = "Ошибка записи ухода: ${e.message}") }
            }
        }
    }

    // Временные методы для тестирования — УДАЛИТЬ ПОСЛЕ ТЕСТОВ
    fun simulateCheckIn() {
        val fakeSsid = allowedSsids.first()
        _state.update {
            it.copy(
                currentSsid = fakeSsid,
                isWorkNetwork = true
            )
        }
        addEvent(
            ssid = fakeSsid,
            type = WifiEventType.CONNECTED,
            description = "приход"
        )
        recordCheckIn(fakeSsid)
    }

    fun simulateCheckOut() {
        val previousSsid = _state.value.currentSsid ?: allowedSsids.first()
        _state.update {
            it.copy(
                currentSsid = null,
                isWorkNetwork = false
            )
        }
        addEvent(
            ssid = previousSsid,
            type = WifiEventType.DISCONNECTED,
            description = "уход / пауза"
        )
        recordCheckOut()
    }

    private fun getYesterdayDateString(): String {
        val provider = DateTimeProvider()
        val today = provider.currentDateString()
        println("WifiViewModel: getYesterdayDateString today='$today' provider=$provider")

        val parts = today.split("-")
        if (parts.size < 3) return today
        val date = kotlinx.datetime.LocalDate(
            parts[0].toInt(),
            parts[1].toInt(),
            parts[2].toInt()
        )
        val yesterday = date.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
        return "${yesterday.year}-${yesterday.monthNumber.toString().padStart(2, '0')}-${yesterday.dayOfMonth.toString().padStart(2, '0')}"
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