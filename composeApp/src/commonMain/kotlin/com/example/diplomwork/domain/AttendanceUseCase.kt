package com.example.diplomwork.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AttendanceUseCase(
    private val attendanceRepository: AttendanceRepository
) {

    suspend fun getEmployee(id: String): Employee? {
        return attendanceRepository.getEmployeeById(id)
    }

    suspend fun getTodayTimeIntervals(employeeId: String): List<TimeInterval> {
        return attendanceRepository.getTimeIntervals(
            employeeId,
            getCurrentDate()
        )
    }

    suspend fun recordEvent(employeeId: String, source: RecordSource): Result<String> {
        val now = getCurrentDateTime()
        val interval = TimeInterval(
            id = "",
            date = now.date,
            employeeId = employeeId,
            recordSource = source,
            startTime = now,
            endTime = null,
            updateTime = now
        )

        return attendanceRepository.recordTimeInterval(interval)
    }

    private fun getCurrentDateTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }

    private fun getCurrentDate(): LocalDate {
        return getCurrentDateTime().date
    }
}