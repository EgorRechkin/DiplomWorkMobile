package com.example.diplomwork.domain

import com.example.diplomwork.data.ApiClient
import kotlinx.datetime.LocalDate

// commonMain/repository/TimeIntervalRepository.kt
interface TimeIntervalRepository {
    suspend fun save(interval: TimeInterval): Result<Unit>
    suspend fun getByEmployee(employeeId: String, date: LocalDate): List<TimeInterval>
    suspend fun syncPending(): Result<Unit>   // офлайн-очередь
}
