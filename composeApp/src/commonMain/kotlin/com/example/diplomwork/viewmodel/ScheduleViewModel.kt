package com.example.diplomwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplomwork.data.ApiClient
import com.example.diplomwork.data.WorkDayDto
import com.example.diplomwork.platform.SessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class WorkDay(
    val date: LocalDate,
    val startTime: String,
    val endTime: String,
    val isWorkDay: Boolean = true
)

data class ScheduleState(
    val scheduleName: String = "",
    val workDays: List<WorkDayDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val weekOffset: Int = 0
)

class ScheduleViewModel(
    private val apiClient: ApiClient,
    private val sessionStorage: SessionStorage  // ← добавьте
) : ViewModel() {

    private var employeeId: String = ""

    init {
        viewModelScope.launch {
            employeeId = sessionStorage.getEmployeeId() ?: ""
            if (employeeId.isNotEmpty()) {
                loadSchedule(0)
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось получить информацию о графике"
                    )
                }
            }
        }
    }
    private val _state = MutableStateFlow(ScheduleState())
    val state: StateFlow<ScheduleState> = _state.asStateFlow()

    private var pendingSearchDate: String? = null

    fun setPendingSearch(date: String) {
        pendingSearchDate = date
    }

    fun consumePendingSearch(): String? {
        val date = pendingSearchDate
        pendingSearchDate = null
        return date
    }

    fun loadSchedule(weekOffset: Int = 0) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                println("ScheduleViewModel: загружаем расписание employeeId=$employeeId weekOffset=$weekOffset")
                val schedule = apiClient.getSchedule(employeeId, weekOffset)
                println("ScheduleViewModel: получено дней=${schedule.weekDays.size}")

                _state.update {
                    it.copy(
                        scheduleName = schedule.scheduleName,
                        workDays = schedule.weekDays,
                        isLoading = false,
                        weekOffset = weekOffset
                    )
                }
            } catch (e: Exception) {
                println("ScheduleViewModel: ОШИБКА ${e::class.simpleName}: ${e.message}")
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось получить информацию о графике"
                    )
                }
            }
        }
    }

    fun previousWeek() = loadSchedule(_state.value.weekOffset - 1)
    fun nextWeek() = loadSchedule(_state.value.weekOffset + 1)
    fun currentWeek() = loadSchedule(0)

    private fun shortenByOneHour(endTime: String?): String? {
        if (endTime == null) return null
        val parts = endTime.split(":")
        if (parts.size < 2) return endTime
        val hour = (parts[0].toIntOrNull() ?: return endTime) - 1
        val hourStr = if (hour < 10) "0$hour" else "$hour"
        return "$hourStr:${parts[1]}"
    }
}