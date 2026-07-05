// commonMain/kotlin/com/example/diplomwork/data/ApiClient.kt
package com.example.diplomwork.data

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
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
                coerceInputValues = true
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

    suspend fun getEmployeeSsid(employeeId: String): String? {
        return try {
            val response: Map<String, String> = httpClient
                .get("$baseUrl/employees/$employeeId/ssid").body()
            response["ssid"]
        } catch (e: Exception) {
            null
        }
    }

    @kotlinx.serialization.Serializable
    data class RecordIntervalResponse(val id: String)

    suspend fun recordTimeInterval(interval: TimeIntervalDto): String {
        return try {
            val response: RecordIntervalResponse = httpClient.post("$baseUrl/time-intervals") {
                contentType(ContentType.Application.Json)
                setBody(interval)
            }.body()
            response.id
        } catch (e: Exception) {
            // Если бэкенд вернул просто строку — пробуем как текст
            httpClient.post("$baseUrl/time-intervals") {
                contentType(ContentType.Application.Json)
                setBody(interval)
            }.bodyAsText()
        }
    }
    suspend fun getSchedule(employeeId: String, weekOffset: Int = 0): ScheduleDto {
        return httpClient.get("$baseUrl/schedule/$employeeId") {
            parameter("weekOffset", weekOffset)
        }.body()
    }

    suspend fun updateTimeInterval(id: String, endTime: String) {
        httpClient.patch("$baseUrl/time-intervals/$id") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("endTime" to endTime))
        }
    }
    suspend fun login(tabelNumber: String): LoginResponse {
        return httpClient.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("tabelNumber" to tabelNumber))
        }.body()
    }

    suspend fun getHolidays(): List<HolidayDto> {
        return httpClient.get("$baseUrl/holidays").body()
    }

    suspend fun getGaps(employeeId: String): List<GapDto> {
        return httpClient.get("$baseUrl/gaps/$employeeId").body()
    }

    suspend fun calculateTabel(employeeId: String, date: String) {
        httpClient.post("$baseUrl/tabel/calculate/$employeeId/$date")
    }
    suspend fun checkAbsence(employeeId: String, date: String) {
        httpClient.post("$baseUrl/gaps/check-absence/$employeeId/$date")
    }
}

@kotlinx.serialization.Serializable
data class HolidayDto(
    val id: String,
    val name: String,
    val startDate: String,
    val endDate: String
)

@kotlinx.serialization.Serializable
data class GapDto(
    val id: String,
    val startDate: String,
    val endDate: String,
    val employeeId: String?,
    val gapTypeName: String
)

@kotlinx.serialization.Serializable
data class LoginResponse(
    val employeeId: String,
    val name: String,
    val tabelNumber: String,
    val divisionId: String?,
    val scheduleTypeId: String?
)

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
    val dayOfWeek: String,
    val isHoliday: Boolean = false,
    val isShortDay: Boolean = false,
    val holidayName: String? = null,
    val gapType: String? = null
)

@kotlinx.serialization.Serializable
data class ScheduleDto(
    val scheduleName: String,
    val workingDays: Int,
    val daysOff: Int,
    val weekDays: List<WorkDayDto>
)
