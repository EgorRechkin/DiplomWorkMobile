package com.example.diplomwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplomwork.data.ApiClient
import com.example.diplomwork.data.TimeIntervalDto
import com.example.diplomwork.platform.DateTimeProvider
import com.example.diplomwork.platform.SessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

// --- State ---

sealed class NfcState {
    object Idle : NfcState()
    object Scanning : NfcState()
    data class Success(val result: NfcResult) : NfcState()
    data class Error(val message: String) : NfcState()
}

data class NfcResult(
    val employeeName: String,
    val tabelNumber: String,
    val time: String,
    val status: String,
    val isCheckIn: Boolean
)
class NfcViewModel(
    private val apiClient: ApiClient,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow<NfcState>(NfcState.Idle)
    val state: StateFlow<NfcState> = _state.asStateFlow()

    private var sessionEmployeeId: String = ""

    private var isReady = false

    init {
        viewModelScope.launch {
            sessionEmployeeId = sessionStorage.getEmployeeId() ?: ""
            isReady = true
            println("NfcViewModel: employeeId загружен = $sessionEmployeeId")
        }
    }



    fun startScan() {
        _state.update { NfcState.Scanning }
    }

    fun stopScan() {
        _state.update { NfcState.Idle }
    }

    fun onTagRead(employeeId: String) {
        viewModelScope.launch {
            if (!isReady) {
                sessionEmployeeId = sessionStorage.getEmployeeId() ?: ""
                isReady = true
            }

            if (sessionEmployeeId.isEmpty()) {
                _state.update { NfcState.Error("Ошибка сессии — войдите заново") }
                return@launch
            }
            try {
                println("NfcViewModel: считан тег employeeId=$employeeId")

                // Проверяем что тег соответствует авторизованному сотруднику
                if (sessionEmployeeId.isNotEmpty() && employeeId != sessionEmployeeId) {
                    _state.update {
                        NfcState.Error("Карточка не соответствует текущему сотруднику")
                    }
                    return@launch
                }

                val now = DateTimeProvider()
                val dateStr = now.currentDateString()
                val timeStr = now.currentTimeString()

                // Проверяем есть ли уже открытый интервал сегодня
                val intervals = apiClient.getTimeIntervals(
                    employeeId = sessionEmployeeId,
                    date = dateStr
                )
                val hasOpenInterval = intervals.any { it.endTime == null }

                if (hasOpenInterval) {
                    // Закрываем последний открытый интервал — уход
                    val lastOpen = intervals.lastOrNull { it.endTime == null }
                    if (lastOpen?.id != null) {
                        apiClient.updateTimeInterval(
                            id = lastOpen.id,
                            endTime = "${dateStr}T${timeStr}:00"
                        )
                        println("NfcViewModel: зафиксирован уход $timeStr")
                    }

                    val employee = apiClient.getEmployee(sessionEmployeeId)
                    _state.update {
                        NfcState.Success(
                            NfcResult(
                                employeeName = employee.name,
                                tabelNumber = employee.tabelNumber ?: "",
                                time = timeStr,
                                status = "Отсутствие",
                                isCheckIn = false
                            )
                        )
                    }
                } else {
                    // Открываем новый интервал — приход
                    apiClient.recordTimeInterval(
                        TimeIntervalDto(
                            employeeId = sessionEmployeeId,
                            date = dateStr,
                            recordSource = "NFC",
                            startTime = "${dateStr}T${timeStr}:00",
                            endTime = null,
                            updateTime = "${dateStr}T${timeStr}:00"
                        )
                    )
                    println("NfcViewModel: зафиксирован приход $timeStr")

                    val employee = apiClient.getEmployee(sessionEmployeeId)
                    _state.update {
                        NfcState.Success(
                            NfcResult(
                                employeeName = employee.name,
                                tabelNumber = employee.tabelNumber ?: "",
                                time = timeStr,
                                status = "Приход",
                                isCheckIn = true
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                println("NfcViewModel: ошибка ${e.message}")
                _state.update { NfcState.Error(e.message ?: "Ошибка чтения тега") }
            }
        }
    }

    fun reset() {
        _state.update { NfcState.Idle }
    }
}