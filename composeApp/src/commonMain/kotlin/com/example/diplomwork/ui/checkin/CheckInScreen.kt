package com.example.diplomwork.ui.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diplomwork.ui.schedule.ScheduleScreen
import com.example.diplomwork.viewmodel.CheckInViewModel
import com.example.diplomwork.viewmodel.WifiViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CheckInScreen(
    viewModel: CheckInViewModel,
    onNfcClick: () -> Unit,
    onWifiClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Главная", "Расписание")

    Column(modifier = Modifier.fillMaxSize()) {
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
            0 -> CheckInContent(viewModel, onNfcClick, onWifiClick)
            1 -> ScheduleScreen()
        }
    }
}

@Composable
private fun CheckInContent(
    viewModel: CheckInViewModel,
    onNfcClick: () -> Unit,
    onWifiClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val wifiViewModel = koinViewModel<WifiViewModel>()
    val wifiState by wifiViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Сегодня",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "WiFi статус",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Текущая сеть:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = wifiState.currentSsid ?: "Не подключено",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Рабочая сеть:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (wifiState.isWorkNetwork) "Да ✓" else "Нет ✗",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (wifiState.isWorkNetwork)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Мониторинг:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (wifiState.isMonitoring) "Активен" else "Остановлен",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (wifiState.todayEvents.isNotEmpty()) {
            Text(
                text = "События сегодня",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            wifiState.todayEvents.forEach { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = event.time, style = MaterialTheme.typography.bodySmall)
                        Text(text = event.ssid, style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onWifiClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Открыть WiFi экран")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNfcClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Открыть NFC экран")
        }
    }
}