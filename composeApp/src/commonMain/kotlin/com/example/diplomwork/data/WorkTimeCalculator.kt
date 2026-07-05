package com.example.diplomwork.data

import com.example.diplomwork.domain.Gap
import com.example.diplomwork.domain.Holiday
import com.example.diplomwork.domain.TimeInterval
import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

data class WorkDayResult(
    val date: LocalDate,
    val plannedHours: Int,
    val factualMinutes: Long,
    val overtimeMinutes: Long,
    val isOverworking: Boolean,
    val isHoliday: Boolean,
    val isShortDay: Boolean,
    val gaps: List<String>
)

class WorkTimeCalculator {

    fun calculateDay(
        date: LocalDate,
        plannedHours: Int,
        timeIntervals: List<TimeInterval>,
        gaps: List<Gap>,
        holidays: List<Holiday>
    ): WorkDayResult {

        // 1. Проверяем праздники
        val holiday = holidays.firstOrNull { holiday ->
            date >= holiday.startDate && date <= holiday.endDate
        }
        val isHoliday = holidays.any { h ->
            date >= h.startDate && date <= h.endDate
        }
        val isShortDay = !isHoliday && holidays.any { h ->
            val dayBefore = h.startDate.minus(1, DateTimeUnit.DAY)
            date == dayBefore
        }
        // 2. Эффективные плановые часы с учётом сокращённого дня
        val effectivePlannedMinutes = when {
            isHoliday -> 0L
            isShortDay -> (plannedHours - 1) * 60L
            else -> plannedHours * 60L
        }

        // 3. Считаем фактически отработанное время из time_interval
        val factualMinutes = if (isHoliday) 0L else {
            timeIntervals
                .filter { it.date == date && it.endTime != null }
                .sumOf { interval ->
                    val start = interval.startTime
                    val end = interval.endTime!!
                    minutesBetween(start, end)
                }
        }

        // 4. Вычитаем gaps (отпуск и прогул вычитаются)
        val deductibleGapTypes = listOf("отпуск", "прогул")
        val activeGaps = gaps.filter { gap ->
            date >= gap.startDate && date <= gap.endDate &&
                    deductibleGapTypes.any { it.equals(gap.gapTypeName, ignoreCase = true) }
        }
        val gapMinutes = activeGaps.sumOf { gap ->
            // Вычитаем полный рабочий день за каждый день gap
            effectivePlannedMinutes
        }

        val adjustedFactual = maxOf(0L, factualMinutes - gapMinutes)

        // 5. Рассчитываем переработку по ТК РФ
        val overtimeMinutes = maxOf(0L, adjustedFactual - effectivePlannedMinutes)
        val isOverworking = overtimeMinutes > 0

        return WorkDayResult(
            date = date,
            plannedHours = plannedHours,
            factualMinutes = adjustedFactual,
            overtimeMinutes = overtimeMinutes,
            isOverworking = isOverworking,
            isHoliday = isHoliday,
            isShortDay = isShortDay,
            gaps = activeGaps.map { it.gapTypeName }
        )
    }

    // Расчёт переработки по ТК РФ
    // Первые 2 часа × 1.5, остальное × 2
    fun calculateOvertimeCoefficient(overtimeMinutes: Long): Double {
        if (overtimeMinutes <= 0) return 0.0
        val first2Hours = minOf(overtimeMinutes, 120L)
        val rest = maxOf(0L, overtimeMinutes - 120L)
        return (first2Hours * 1.5 + rest * 2.0) / 60.0
    }

    fun formatDuration(totalMinutes: Long): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "${hours}ч ${minutes}мин"
    }

    private fun minutesBetween(start: LocalDateTime, end: LocalDateTime): Long {
        val startInstant = start.toInstant(TimeZone.UTC)
        val endInstant = end.toInstant(TimeZone.UTC)
        return (endInstant - startInstant).inWholeMinutes
    }
}