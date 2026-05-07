package com.example.diplomwork.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class TimeInterval(
    val id: String,
    val date: LocalDate,
    val employeeId: String,
    val recordSource: RecordSource,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val updateTime: LocalDateTime?
)

enum class RecordSource {
    NFC, WIFI, MANUAL;

    companion object {
        fun fromString(value: String?): RecordSource = when (value?.uppercase()) {
            "NFC"    -> NFC
            "WIFI"   -> WIFI
            else     -> MANUAL
        }
    }
}