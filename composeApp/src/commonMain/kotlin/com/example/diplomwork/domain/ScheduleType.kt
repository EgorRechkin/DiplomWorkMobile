package com.example.diplomwork.domain

data class ScheduleType(
    val id: String,
    val name: String,
    val workingDays: Int,   // schedule_type.working_days
    val daysOff: Int        // schedule_type.days_off
)