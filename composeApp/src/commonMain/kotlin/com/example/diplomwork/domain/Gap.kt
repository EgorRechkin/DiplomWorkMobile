package com.example.diplomwork.domain

import kotlinx.datetime.LocalDate

data class Gap(
    val id: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val employeeId: String,
    val gapTypeId: String?
)