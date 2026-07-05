package com.example.diplomwork.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class RequestType(val label: String) {
    SCHEDULE("Изменение расписания"),
    STATUS("Изменение статуса")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    onBack: () -> Unit
) {
    var selectedType by remember { mutableStateOf(RequestType.SCHEDULE) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    // Варианты причин в зависимости от типа
    val reasonOptions = when (selectedType) {
        RequestType.SCHEDULE -> listOf(
            "Перенос выходного дня",
            "Изменение времени начала смены",
            "Изменение времени окончания смены",
            "Работа в выходной день",
            "Другое"
        )
        RequestType.STATUS -> listOf(
            "Командировка",
            "Больничный",
            "Отпуск",
            "Другое"
        )
    }

    if (showSuccess) {
        SuccessScreen(onBack = onBack)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Топбар
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад"
                )
            }
            Text(
                text = "Заявка руководителю",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        }

        HorizontalDivider()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Тип заявки
            Text(
                text = "Тип заявки",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF666666)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RequestType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            reason = ""
                        },
                        label = { Text(type.label) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1A1A1A),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            HorizontalDivider()

            // Причина
            Text(
                text = "Причина",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF666666)
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                reasonOptions.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = reason == option,
                            onClick = { reason = option },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF1A1A1A)
                            )
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            HorizontalDivider()

            // Даты
            Text(
                text = "Период",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF666666)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("С (дд.мм.гггг)") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Дата начала"
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("По (дд.мм.гггг)") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Дата окончания"
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }

            HorizontalDivider()

            // Комментарий
            Text(
                text = "Комментарий",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF666666)
            )

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Дополнительная информация...") },
                shape = RoundedCornerShape(8.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка отправки
            Button(
                onClick = {
                    // TODO: отправить на бэкенд
                    showSuccess = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A)
                ),
                enabled = reason.isNotEmpty() && startDate.isNotEmpty()
            ) {
                Text(
                    text = "Отправить руководителю",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun SuccessScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "✓",
            style = MaterialTheme.typography.displayLarge,
            color = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Заявка отправлена",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Руководитель получит уведомление\nи рассмотрит вашу заявку",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A1A)
            )
        ) {
            Text("Вернуться назад")
        }
    }
}