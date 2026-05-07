// commonMain/kotlin/com/example/diplomwork/data/ApiClient.kt
package com.example.diplomwork.data

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    // Базовый URL — для локального тестирования на Android
    // На реальном устройстве замените на IP вашего компьютера
    private val baseUrl = "http://10.0.2.2:8080"  // 10.0.2.2 — localhost для Android эмулятора
 //private val baseUrl = "http://192.168.x.x:8080"  // замените на ваш IP
    suspend fun getEmployees(): List<EmployeeDto> {
        return httpClient.get("$baseUrl/employees").body()
    }

    suspend fun getEmployee(id: String): EmployeeDto {
        return httpClient.get("$baseUrl/employees/$id").body()
    }

    suspend fun getTimeIntervals(employeeId: String, date: String): List<TimeIntervalDto> {
        return httpClient.get("$baseUrl/time-intervals") {
            parameter("employeeId", employeeId)
            parameter("date", date)
        }.body()
    }

    suspend fun recordTimeInterval(interval: TimeIntervalDto): String {
        val response: Map<String, String> = httpClient.post("$baseUrl/time-intervals") {
            contentType(ContentType.Application.Json)
            setBody(interval)
        }.body()
        return response["id"] ?: ""
    }
    suspend fun getSchedule(employeeId: String, weekOffset: Int = 0): ScheduleDto {
        return httpClient.get("$baseUrl/schedule/$employeeId") {
            parameter("weekOffset", weekOffset)
        }.body()
    }
}

// DTO для сериализации
@kotlinx.serialization.Serializable
data class EmployeeDto(
    val id: String,
    val name: String,
    val tabelNumber: String?,
    val divisionId: String?,
    val roleId: String?,
    val scheduleTypeId: String?
)

@kotlinx.serialization.Serializable
data class WorkDayDto(
    val date: String,
    val isWorkDay: Boolean,
    val startTime: String?,
    val endTime: String?,
    val dayOfWeek: String
)

@kotlinx.serialization.Serializable
data class ScheduleDto(
    val scheduleName: String,
    val workingDays: Int,
    val daysOff: Int,
    val weekDays: List<WorkDayDto>
)
