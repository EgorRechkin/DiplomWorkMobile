package com.example.diplomwork.ui.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diplomwork.ui.components.TimelineItem
import com.example.diplomwork.viewmodel.WifiViewModel

@Composable
fun WifiStatusScreen(viewModel: WifiViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        //TopBar(title = "WiFi", onBack = onBack)

        Spacer(modifier = Modifier.height(12.dp))

        // Текущая сеть
        Column {
            //LabelValueRow(
            //    label = "Текущая сеть",
            //    value = state.currentSsid ?: "Не подключено"
            //)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Статус", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                //if (state.isWorkNetwork)
                //    StatusBadge(text = "Рабочая", color = StatusColor.SUCCESS)
                //else
                //    StatusBadge(text = "Не рабочая", color = StatusColor.NEUTRAL)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Разрешённые сети
        Column {
            Text("Разрешённые сети",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            state.allowedNetworks.forEach { network ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(network.ssid)
                    //StatusDot(active = network.ssid == state.currentSsid)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Таймлайн событий (из time_interval)
        Column {
            Text("История сегодня",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            state.todayEvents.forEachIndexed { i, event ->
                /*TimelineItem(
                    event = event,
                    isLast = i == state.todayEvents.lastIndex
                )*/
            }
        }
    }
}