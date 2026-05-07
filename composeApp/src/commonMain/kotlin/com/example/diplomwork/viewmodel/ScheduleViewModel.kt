package com.example.diplomwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplomwork.data.ApiClient
import com.example.diplomwork.data.WorkDayDto
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
    private val apiClient: ApiClient
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleState())
    val state: StateFlow<ScheduleState> = _state.asStateFlow()

    // Пока хардкодим ID сотрудника
    private val employeeId = "139ee633-5543-40e5-a4f7-4d845efb5443"

    init {
        loadSchedule()
    }

    fun loadSchedule(weekOffset: Int = 0) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val schedule = apiClient.getSchedule(employeeId, weekOffset)
                _state.update {
                    it.copy(
                        scheduleName = schedule.scheduleName,
                        workDays = schedule.weekDays,
                        isLoading = false,
                        weekOffset = weekOffset
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось получить информацию о графике: ${e.message}"
                    )
                }
            }
        }
    }

    fun previousWeek() = loadSchedule(_state.value.weekOffset - 1)
    fun nextWeek() = loadSchedule(_state.value.weekOffset + 1)
    fun currentWeek() = loadSchedule(0)
}