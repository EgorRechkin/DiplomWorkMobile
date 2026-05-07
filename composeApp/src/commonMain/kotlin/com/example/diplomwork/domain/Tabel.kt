package com.example.diplomwork.domain

import kotlinx.datetime.LocalDate

data class Tabel(
    val id: String,
    val employeeId: String,
    val date: LocalDate,
    val hoursPlanned: Int,
    val hoursFactually: Int,
    val changedById: String?,
    val statusId: String?,
    val isOverworking: Boolean
)