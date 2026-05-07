package com.example.diplomwork.data

import com.example.diplomwork.domain.AttendanceRepository
import com.example.diplomwork.domain.Employee
import com.example.diplomwork.domain.TimeInterval
import com.example.diplomwork.domain.RecordSource
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class AttendanceRepositoryImpl(private val apiClient: ApiClient) : AttendanceRepository {

    override suspend fun getEmployeeById(id: String): Employee? {
        return try {
            val dto = apiClient.getEmployee(id)
            dto.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getTimeIntervals(employeeId: String, date: LocalDate): List<TimeInterval> {
        return try {
            apiClient.getTimeIntervals(employeeId, date.toString())
                .map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun recordTimeInterval(interval: TimeInterval): Result<String> {
        return try {
            val dto = TimeIntervalDto(
                employeeId = interval.employeeId,
                date = interval.date.toString(),
                recordSource = interval.recordSource.name,
                startTime = interval.startTime.toString(),
                endTime = interval.endTime?.toString(),
                updateTime = interval.updateTime.toString()
            )
            val id = apiClient.recordTimeInterval(dto)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun EmployeeDto.toDomain() = Employee(
    id = id,
    name = name,
    divisionId = divisionId,
    roleId = roleId,
    scheduleTypeId = scheduleTypeId,
    tabelNumber = tabelNumber
)

private fun TimeIntervalDto.toDomain() = TimeInterval(
    id = id ?: "",
    date = LocalDate.parse(date),
    employeeId = employeeId,
    recordSource = RecordSource.fromString(recordSource),
    startTime = LocalDateTime.parse(startTime),
    endTime = endTime?.let { LocalDateTime.parse((it)) },
    updateTime = LocalDateTime.parse(updateTime)
)