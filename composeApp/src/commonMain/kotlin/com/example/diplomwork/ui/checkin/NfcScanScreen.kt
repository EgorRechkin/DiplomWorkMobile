package com.example.diplomwork.ui.checkin

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.diplomwork.viewmodel.NfcResult
import com.example.diplomwork.viewmodel.NfcState
import com.example.diplomwork.viewmodel.NfcViewModel

@Composable
fun NfcScanScreen(
    viewModel: NfcViewModel,
    onBack: () -> Unit,
    onStartScan: ((String) -> Unit) -> Unit,
    onStopScan: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //TopBar(title = "NFC", onBack = onBack)

        Spacer(modifier = Modifier.height(24.dp))

        // Анимированная иконка NFC — пульсация через animate*AsState
        val scale by animateFloatAsState(
            targetValue = if (state is NfcState.Scanning) 1.1f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            /*Icon(
                painter = painterResource(Res.drawable),
                contentDescription = "NFC",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )*/
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val s = state) {
            is NfcState.Scanning -> {
                Text("Поднесите карточку", style = MaterialTheme.typography.titleSmall)
                Text(
                    "к задней панели телефона",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            is NfcState.Success -> NfcSuccessCard(result = s.result)
            is NfcState.Error -> {}//NfcErrorCard(message = s.message)
            is NfcState.Idle -> {}
        }
    }

    LaunchedEffect(Unit) {
        onStartScan { employeeId ->
            viewModel.onTagRead(employeeId)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onStopScan()
        }
    }
}

@Composable
fun NfcSuccessCard(result: NfcResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            //InfoRow(label = "Сотрудник", value = result.employeeName)
            //InfoRow(label = "Табельный №", value = result.tabelNumber)
            //InfoRow(label = "Время", value = result.time)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Статус", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
               // StatusBadge(status = result.status)
            }
        }
    }
}