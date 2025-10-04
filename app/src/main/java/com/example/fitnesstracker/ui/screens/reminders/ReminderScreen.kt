package com.example.fitnesstracker.ui.screens.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitnesstracker.MainViewModel
import com.example.fitnesstracker.data.entities.Reminder
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun ReminderScreen(viewModel: MainViewModel) {
    val reminders by viewModel.reminders.collectAsState()
    val scope = rememberCoroutineScope()

    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val date = remember { mutableStateOf(LocalDate.now()) }
    val hour = remember { mutableStateOf(LocalTime.now().hour) }
    val minute = remember { mutableStateOf(LocalTime.now().minute) }
    val daysOfWeek = remember { mutableStateListOf<Int>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Recordatorios", style = MaterialTheme.typography.displayLarge)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title.value,
                    onValueChange = { title.value = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description.value,
                    onValueChange = { description.value = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = date.value.toString(),
                        onValueChange = {
                            runCatching { LocalDate.parse(it) }.onSuccess { parsed -> date.value = parsed }
                        },
                        label = { Text("Fecha (AAAA-MM-DD)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = "%02d".format(hour.value),
                        onValueChange = { hour.value = it.toIntOrNull()?.coerceIn(0, 23) ?: hour.value },
                        label = { Text("Hora") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.5f)
                    )
                    OutlinedTextField(
                        value = "%02d".format(minute.value),
                        onValueChange = { minute.value = it.toIntOrNull()?.coerceIn(0, 59) ?: minute.value },
                        label = { Text("Min") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.5f)
                    )
                }

                DaysOfWeekSelector(selectedDays = daysOfWeek)

                Button(onClick = {
                    if (title.value.isNotBlank()) {
                        val trigger = LocalDateTime.of(date.value, LocalTime.of(hour.value, minute.value))
                            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        scope.launch {
                            viewModel.scheduleReminder(
                                Reminder(
                                    title = title.value,
                                    description = description.value,
                                    triggerAt = trigger,
                                    repeatingDays = daysOfWeek.toList()
                                )
                            )
                            title.value = ""
                            description.value = ""
                        }
                    }
                }) {
                    Text("Guardar recordatorio")
                }
            }
        }

        if (reminders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            ) {
                Text(
                    text = "Aún no tienes recordatorios programados.",
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f, fill = true)) {
                items(reminders) { reminder ->
                    ReminderItem(reminder = reminder, onDelete = {
                        scope.launch { viewModel.deleteReminder(reminder) }
                    })
                }
            }
        }
    }
}

@Composable
private fun DaysOfWeekSelector(selectedDays: MutableList<Int>) {
    val days = listOf(
        "Dom" to Calendar.SUNDAY,
        "Lun" to Calendar.MONDAY,
        "Mar" to Calendar.TUESDAY,
        "Mié" to Calendar.WEDNESDAY,
        "Jue" to Calendar.THURSDAY,
        "Vie" to Calendar.FRIDAY,
        "Sáb" to Calendar.SATURDAY
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Repetir en", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            days.forEach { (label, value) ->
                val checked = selectedDays.contains(value)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Checkbox(checked = checked, onCheckedChange = { isChecked ->
                        if (isChecked) selectedDays.add(value) else selectedDays.remove(value)
                    })
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
private fun ReminderItem(reminder: Reminder, onDelete: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM · HH:mm")
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(reminder.triggerAt),
        ZoneId.systemDefault()
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = reminder.title, style = MaterialTheme.typography.titleLarge)
            Text(text = reminder.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = formatter.format(dateTime), style = MaterialTheme.typography.labelSmall)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (reminder.repeatingDays.isEmpty()) "Único" else "Semanal")
                Button(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}
