package com.example.diplomwork.ui.checkin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diplomwork.data.WorkTimeCalculator
import com.example.diplomwork.platform.DateTimeProvider
import com.example.diplomwork.ui.schedule.ScheduleScreen
import com.example.diplomwork.viewmodel.CheckInViewModel
import com.example.diplomwork.viewmodel.ScheduleViewModel
import com.example.diplomwork.viewmodel.WifiEvent
import com.example.diplomwork.viewmodel.WifiViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CheckInScreen(
    viewModel: CheckInViewModel,
    onNfcClick: () -> Unit,
    onWifiClick: () -> Unit,
    onRequestClick: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Главная", "Расписание")

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }

        }

        when (selectedTab) {
            0 -> CheckInContent(viewModel, onNfcClick, onWifiClick, onRequestClick, onLogout)
            1 -> ScheduleScreen()
        }
    }
}

@Composable
private fun CheckInContent(
    viewModel: CheckInViewModel,
    onNfcClick: () -> Unit,
    onWifiClick: () -> Unit,
    onRequestClick: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val wifiViewModel = koinViewModel<WifiViewModel>()
    val wifiState by wifiViewModel.state.collectAsState()
    val scheduleViewModel = koinViewModel<ScheduleViewModel>()
    val scheduleState by scheduleViewModel.state.collectAsState()

    // Берём сегодняшний день из расписания
    val today = scheduleState.workDays.firstOrNull {
        it.date == "2026.05.07"//getCurrentDateString()
    }
    val workedMinutes = calculateWorkedMinutes(wifiState.todayEvents)
    val plannedMinutes = parsePlannedMinutes(today?.startTime, today?.endTime)
    val overtimeMinutes = maxOf(0L, workedMinutes - plannedMinutes)
    val calculator = remember { WorkTimeCalculator() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onLogout) {
                Text(
                    text = "Выйти",
                    color = Color(0xFF666666),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Text(
            text = "Статус",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Статус с индикатором
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (wifiState.isWorkNetwork) "На работе" else "Не на работе",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (wifiState.isWorkNetwork) Color(0xFF4CAF50)
                        else Color(0xFFE53935)
                    )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Карточка с информацией
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoRow(
                    label = "Начало рабочего дня:",
                    value = today?.startTime ?: "—"
                )

                HorizontalDivider()

                InfoRow(
                    label = "Окончание рабочего дня:",
                    value = today?.endTime ?: "—"
                )

                HorizontalDivider()

                InfoRow(
                    label = "Последний вход:",
                    value = wifiState.todayEvents
                        .lastOrNull { it.description.contains("приход", ignoreCase = true) }
                        ?.time ?: "—"
                )

                HorizontalDivider()

                InfoRow(
                    label = "Отработано сегодня:",
                    value = calculator.formatDuration(workedMinutes)
                )

                HorizontalDivider()

                InfoRow(
                    label = "Переработка:",
                    value = if (overtimeMinutes > 0)
                        calculator.formatDuration(overtimeMinutes)
                    else "—",
                    valueColor = if (overtimeMinutes > 0)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                InfoRow(
                    label = "Осталось до конца дня:",
                    value = calculateRemainingTime(today?.endTime)
                )
            }
        }


        Spacer(modifier = Modifier.weight(1f))

        /*// Временная кнопка для тестирования — УДАЛИТЬ ПОСЛЕ ТЕСТОВ
        //if (BuildConfig.DEBUG) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    if (wifiState.isWorkNetwork) {
                        wifiViewModel.simulateCheckOut()
                    } else {
                        wifiViewModel.simulateCheckIn()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF666666))
            ) {
                Text(
                    text = if (wifiState.isWorkNetwork) "🔴 Симулировать уход" else "🟢 Симулировать приход",
                    color = Color(0xFF666666),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        //}

        // Кнопка согласования
        Button(
            onClick = { onRequestClick()  },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A1A)
            )
        ) {
            Text(
                text = "Согласовать изменение расписания",
                style = MaterialTheme.typography.bodyMedium
            )
        }*/

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color =
        MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor
        )
    }
}

// Считаем отработанные минуты из событий WiFi
private fun calculateWorkedMinutes(events: List<WifiEvent>): Long {
    if (events.isEmpty()) return 0L

    var totalMinutes = 0L
    var lastConnectTime: String? = null

    events.forEach { event ->
        if (event.description.contains("приход", ignoreCase = true)) {
            lastConnectTime = event.time
        } else if (event.description.contains("уход", ignoreCase = true)) {
            lastConnectTime?.let { connectTime ->
                totalMinutes += minutesBetween(connectTime, event.time)
                lastConnectTime = null
            }
        }
    }

    // Если сейчас на работе — добавляем время с последнего входа
    if (lastConnectTime != null) {
        val now = DateTimeProvider().currentTimeString()
        totalMinutes += minutesBetween(lastConnectTime!!, now)
    }

    return totalMinutes
}

// Считаем плановые минуты из времени начала и конца смены
private fun parsePlannedMinutes(startTime: String?, endTime: String?): Long {
    if (startTime == null || endTime == null) return 480L // 8 часов по умолчанию
    return minutesBetween(startTime, endTime)
}

private fun minutesBetween(start: String, end: String): Long {
    val startParts = start.split(":")
    val endParts = end.split(":")
    if (startParts.size < 2 || endParts.size < 2) return 0L
    val startMin = (startParts[0].toIntOrNull() ?: 0) * 60L + (startParts[1].toIntOrNull() ?: 0)
    val endMin = (endParts[0].toIntOrNull() ?: 0) * 60L + (endParts[1].toIntOrNull() ?: 0)
    return maxOf(0L, endMin - startMin)
}

private fun calculateRemainingTime(endTime: String?): String {
    if (endTime == null) return "—"
    val now = DateTimeProvider().currentTimeString()
    val remaining = minutesBetween(now, endTime)
    if (remaining <= 0) return "Рабочий день окончен"
    val hours = remaining / 60
    val minutes = remaining % 60
    return "${hours}ч ${minutes}мин"
}

private fun getCurrentDateString(): String {
    return DateTimeProvider().currentDateString()
}