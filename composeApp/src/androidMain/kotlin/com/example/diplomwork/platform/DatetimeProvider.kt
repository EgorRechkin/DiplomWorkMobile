package com.example.diplomwork.platform

import java.time.LocalTime
import java.time.format.DateTimeFormatter

// androidMain
actual class DateTimeProvider actual constructor() {
    actual fun currentTimeString(): String {
        val time = java.time.LocalTime.now(java.time.ZoneId.of("Asia/Krasnoyarsk"))
        return "%02d:%02d".format(time.hour, time.minute)
    }
    actual fun currentDateString(): String {
        val date = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Krasnoyarsk"))
        return "%04d-%02d-%02d".format(date.year, date.monthValue, date.dayOfMonth)
    }
}