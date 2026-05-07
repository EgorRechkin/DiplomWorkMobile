package com.example.diplomwork.data

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.time.Duration

// commonMain/WorkTimeCalculator.kt
data class WorkDayResult(
    val plannedEnd: LocalTime,
    val factualEnd: LocalTime?,
    val overtime: Duration,        // переработка
    val isOverworking: Boolean     // tabel.is_overworking
)

class WorkTimeCalculator {
    /*fun calculateDay(
        scheduleType: ScheduleType,   // working_days, days_off
        startTime: LocalDateTime,
        gaps: List<Gap>,              // отсутствия
        holidays: List<Holiday>       // праздники с is_day_before_short
    ): WorkDayResult {
        // 1. Базовая длина смены из schedule_type
        // 2. Вычитаем gap (больничный, отпуск — по gap_type)
        // 3. Проверяем holidays.is_day_before_short (сокращённый день, −1ч по ТК РФ)
        // 4. Сравниваем hours_planned vs hours_factually
        // 5. Если factually > planned → is_overworking = true
    }*/
}