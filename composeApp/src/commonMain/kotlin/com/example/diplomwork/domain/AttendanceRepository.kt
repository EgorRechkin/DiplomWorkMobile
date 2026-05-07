package com.example.diplomwork.domain

import kotlinx.datetime.LocalDate

interface AttendanceRepository {
    suspend fun getEmployeeById(id: String): Employee?
    suspend fun getTimeIntervals(employeeId: String, date: LocalDate): List<TimeInterval>
    suspend fun recordTimeInterval(interval: TimeInterval): Result<String>
}