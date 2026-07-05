package com.example.diplomwork.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diplomwork.data.WorkDayDto
import com.example.diplomwork.platform.DateTimeProvider
import com.example.diplomwork.viewmodel.ScheduleViewModel
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<WorkDayDto?>(null) }
    var showSearchResult by remember { mutableStateOf(false) }

    LaunchedEffect(state.workDays) {
        val pending = viewModel.consumePendingSearch()
        if (pending != null) {
            val result = state.workDays.firstOrNull { it.date == pending }
            if (result != null) {
                searchResult = result
                showSearchResult = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            readOnly = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("дд.мм.гггг") },
            leadingIcon = {
                IconButton(onClick = {
                    // Сброс поиска
                    searchText = ""
                    showSearchResult = false
                    searchResult = null
                }) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Сброс"
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    val dateStr = parseDate(searchText)
                    if (dateStr != null) {
                        // Ищем в текущей неделе
                        val result = state.workDays.firstOrNull { it.date == dateStr }
                        if (result != null) {
                            searchResult = result
                            showSearchResult = true
                        } else {
                            // Вычисляем смещение недели и переходим
                            val offset = calculateWeekOffset(dateStr)
                            if (offset != null) {
                                viewModel.loadSchedule(offset)
                                // Запоминаем дату для поиска после загрузки
                                viewModel.setPendingSearch(dateStr)
                                showSearchResult = false
                            }
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Поиск"
                    )
                }
            },
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        // Навигация по неделям — скрываем при активном поиске
        if (!showSearchResult) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { viewModel.previousWeek() }) {
                    Text("< Пред. неделя")
                }
                TextButton(onClick = { viewModel.currentWeek() }) {
                    Text(state.scheduleName.ifEmpty { "Текущая" })
                }
                TextButton(onClick = { viewModel.nextWeek() }) {
                    Text("След. неделя >")
                }
            }
        } else {
            // Кнопка возврата к расписанию
            TextButton(
                onClick = {
                    showSearchResult = false
                    searchResult = null
                    searchText = ""
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("← Вернуться к расписанию")
            }
        }

        // Состояния загрузки / ошибки / контент
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            showSearchResult -> {
                // Показываем результат поиска
                if (searchResult != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        WorkDayItem(day = searchResult!!)
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "День не найден в текущей неделе",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.workDays) { day ->
                        WorkDayItem(day = day)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkDayItem(day: WorkDayDto) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = formatDate(day.date),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        when {
            day.isHoliday -> {
                Text(
                    text = "Праздничный день",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            day.gapType != null -> {
                Text(
                    text = when (day.gapType) {
                        "vacation" -> "Отпуск"
                        "absent" -> "Отсутствие"
                        "weekend" -> "Выходной день"
                        "holiday" -> "Праздничный день"
                        else -> day.gapType
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            !day.isWorkDay -> {
                Text(
                    text = "Выходной день",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                if (day.isShortDay) {
                    Text(
                        text = "Сокращённый день (предпраздничный)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Text(
                    text = "Начало рабочей смены: ${day.startTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Окончание рабочей смены: ${day.endTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun parseAndSearch(input: String, days: List<WorkDayDto>): WorkDayDto? {
    val parts = input.trim().split(".")
    if (parts.size != 3) return null
    val day = parts[0].padStart(2, '0')
    val month = parts[1].padStart(2, '0')
    val year = parts[2]
    if (year.length != 4) return null
    val dateStr = "$year-$month-$day"
    return days.firstOrNull { it.date == dateStr }
}

// "2026-05-04" -> "04.05.2026"
private fun formatDate(date: String): String {
    val parts = date.split("-")
    return if (parts.size == 3) "${parts[2]}.${parts[1]}.${parts[0]}" else date
}

// Парсим дд.мм.гггг → гггг-мм-дд
private fun parseDate(input: String): String? {
    val parts = input.trim().split(".")
    if (parts.size != 3) return null
    val day = parts[0].padStart(2, '0')
    val month = parts[1].padStart(2, '0')
    val year = parts[2]
    if (year.length != 4) return null
    return "$year-$month-$day"
}

private fun calculateWeekOffset(dateStr: String): Int? {
    return try {
        val parts = dateStr.split("-")
        if (parts.size != 3) return null
        val targetDate = kotlinx.datetime.LocalDate(
            parts[0].toInt(),
            parts[1].toInt(),
            parts[2].toInt()
        )
        val todayStr = DateTimeProvider().currentDateString()
        val todayParts = todayStr.split("-")
        val today = kotlinx.datetime.LocalDate(
            todayParts[0].toInt(),
            todayParts[1].toInt(),
            todayParts[2].toInt()
        )

        // Понедельник текущей недели
        val todayMonday = today.minus(
            kotlinx.datetime.DatePeriod(days = today.dayOfWeek.ordinal)
        )

        // Понедельник целевой недели
        val targetMonday = targetDate.minus(
            kotlinx.datetime.DatePeriod(days = targetDate.dayOfWeek.ordinal)
        )


        // Считаем разницу в днях через периоды
        var diff = 0
        var current = todayMonday
        while (current != targetMonday) {
            if (current < targetMonday) {
                current = current.plus(7, kotlinx.datetime.DateTimeUnit.DAY)
                diff++
            } else {
                current = current.minus(7, kotlinx.datetime.DateTimeUnit.DAY)
                diff--
            }
        }
        diff
    } catch (e: Exception) {
        null
    }
}