package com.example.diplomwork.domain

import kotlinx.datetime.LocalDate

data class Employee(
    val id: String,
    val name: String,
    val divisionId: String?,
    val roleId: String?,
    val scheduleTypeId: String?,
    val tabelNumber: String?
)