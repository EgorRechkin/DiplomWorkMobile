package com.example.diplomwork.platform

import java.time.LocalTime
import java.time.format.DateTimeFormatter

actual fun currentTimeString(): String {
    val now = LocalTime.now()
    return "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
}