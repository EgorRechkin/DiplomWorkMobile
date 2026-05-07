package com.example.diplomwork.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diplomwork.data.WorkDayDto
import com.example.diplomwork.viewmodel.ScheduleViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Поиск по дате
        OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Дата") },
            leadingIcon = {
                Button(onClick = {}) {
                    //Icon(
                    //    imageVector = Icons.Default.Menu,
                    //    contentDescription = "Фильтр"
                    //)
                }
            },
            trailingIcon = {
                Button(onClick = {}) {
                    //Icon(
                    //    imageVector = Icons.Default.Search,
                    //    contentDescription = "Поиск"
                    //)
                }
            },
            shape = MaterialTheme.shapes.medium
        )

        // Навигация по неделям
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

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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

        if (day.isWorkDay) {
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
        } else {
            Text(
                text = "Выходной день",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// "2026-05-04" -> "04.05.2026"
private fun formatDate(date: String): String {
    val parts = date.split("-")
    return if (parts.size == 3) "${parts[2]}.${parts[1]}.${parts[0]}" else date
}