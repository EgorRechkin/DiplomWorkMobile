package com.example.diplomwork.platform

import platform.Foundation.NSDate
import platform.Foundation.NSCalendar


// iosMain
actual class DateTimeProvider actual constructor() {
    actual fun currentTimeString(): String {
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(
            platform.Foundation.NSCalendarUnitHour or platform.Foundation.NSCalendarUnitMinute,
            NSDate()
        )
        val hour = components.hour.toString().padStart(2, '0')
        val minute = components.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }
    actual fun currentDateString(): String {
        // TODO: реализовать через NSCalendar
        return ""
    }
}
