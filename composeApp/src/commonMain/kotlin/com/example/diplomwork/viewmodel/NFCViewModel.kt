package com.example.diplomwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

// --- ViewModel ---

class NfcViewModel : ViewModel() {

    private val _state = MutableStateFlow<NfcState>(NfcState.Idle)
    val state: StateFlow<NfcState> = _state.asStateFlow()

    fun startScan() {
        _state.update { NfcState.Scanning }
    }

    fun stopScan() {
        _state.update { NfcState.Idle }
    }

    // Вызывается когда платформенный NfcScanner прочитал тег
    fun onTagRead(employeeId: String) {
        viewModelScope.launch {
            try {
                // TODO: заменить на реальный запрос к репозиторию
                // val employee = employeeRepository.getById(employeeId)
                // val event = attendanceRepository.recordEvent(employeeId, RecordSource.NFC)

                //val now = Clock.System.now()
                //    .toLocalDateTime(TimeZone.currentSystemDefault())
                //val time = "${now.hour.toString().padStart(2, '0')}:" +
                //        "${now.minute.toString().padStart(2, '0')}"

                _state.update {
                    NfcState.Success(
                        NfcResult(
                            employeeName = "Иванов А.И.",   // employee.name
                            tabelNumber = "0042",            // employee.tabelNumber
                            time = "time",
                            status = "Я · 8ч",               // из tabel.status
                            isCheckIn = true                 // логика: есть ли уже start_time сегодня
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update { NfcState.Error(e.message ?: "Ошибка чтения тега") }
            }
        }
    }

    fun reset() {
        _state.update { NfcState.Idle }
    }
}