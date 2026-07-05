package com.example.diplomwork.data


@kotlinx.serialization.Serializable
data class TimeIntervalDto(
    val id: String? = null,
    val employeeId: String,
    val date: String,
    val recordSource: String,
    val startTime: String,
    val endTime: String? = null,
    val updateTime: String? = null  // ← должно быть nullable
)