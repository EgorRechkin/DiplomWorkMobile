package com.example.diplomwork.domain

import kotlinx.datetime.LocalDate

data class Holiday(
    val id: String,
    val nameId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val isDayBeforeShort: Boolean  // предпраздничный день, −1ч по ТК РФ
)