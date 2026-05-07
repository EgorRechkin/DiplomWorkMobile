package com.example.diplomwork.platform

import platform.Foundation.NSDate
import platform.Foundation.NSCalendar

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