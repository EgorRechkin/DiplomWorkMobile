package com.example.diplomwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplomwork.data.WorkTimeCalculator
import com.example.diplomwork.domain.AttendanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckInViewModel(
    private val attendanceUseCase: AttendanceUseCase,
    private val workTimeCalculator: WorkTimeCalculator
) : ViewModel() {

    private val _state = MutableStateFlow(CheckInState())
    val state: StateFlow<CheckInState> = _state.asStateFlow()

    fun selectMethod(method: CheckInMethod) {
        _state.update { it.copy(selectedMethod = method) }
    }

    fun onNfcTagRead(employeeId: String) {
        viewModelScope.launch {
            //val result = attendanceUseCase.recordEvent(
            //    employeeId = employeeId,
            //    source = RecordSource.NFC
            //)
            //_state.update { it.copy(lastResult = result) }
        }
    }

    fun onWifiConnected(ssid: String) {
        viewModelScope.launch {
            /*if (attendanceUseCase.isWorkNetwork(ssid)) {
                attendanceUseCase.recordEvent(
                    employeeId = _state.value.employeeId,
                    source = RecordSource.WIFI
                )
            }*/
        }
    }
}

data class CheckInState(
    val employeeName: String = "",
    val employeeId: String = "",
    val checkInTime: String? = null,
    val plannedEndTime: String? = null,
    val plannedHours: Int = 8,
    val overtime: String = "0:00",
    val isOverworking: Boolean = false,
    val isCheckedIn: Boolean = false,
    val selectedMethod: CheckInMethod = CheckInMethod.NFC,
    val currentStatus: String? = null,
    //val lastResult: AttendanceResult? = null
)

enum class CheckInMethod { NFC, WIFI, MANUAL }