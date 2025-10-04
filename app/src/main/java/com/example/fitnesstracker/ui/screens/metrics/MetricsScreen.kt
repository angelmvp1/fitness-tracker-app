package com.example.fitnesstracker.ui.screens.metrics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitnesstracker.MainViewModel
import com.example.fitnesstracker.data.entities.BodyMetric
import com.example.fitnesstracker.data.entities.ExerciseEntry
import com.example.fitnesstracker.data.entities.ExerciseSet
import com.example.fitnesstracker.data.entities.TrainingSession
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MetricsScreen(viewModel: MainViewModel) {
    val weight = remember { mutableStateOf(0f) }
    val bodyFat = remember { mutableStateOf(0f) }
    val chest = remember { mutableStateOf(0f) }
    val waist = remember { mutableStateOf(0f) }
    val arm = remember { mutableStateOf(0f) }
    val leg = remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    val exercises = remember { mutableStateListOf<ExerciseInputState>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Métricas corporales", style = MaterialTheme.typography.displayLarge)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricField(label = "Peso (kg)", state = weight.value) { weight.value = it }
                MetricField(label = "% Grasa", state = bodyFat.value) { bodyFat.value = it }
                MetricGrid(
                    values = listOf(
                        "Pecho" to chest,
                        "Cintura" to waist,
                        "Brazo" to arm,
                        "Pierna" to leg
                    )
                )
                Button(onClick = {
                    scope.launch {
                        viewModel.addBodyMetric(
                            BodyMetric(
                                date = System.currentTimeMillis(),
                                weightKg = weight.value,
                                bodyFatPercent = bodyFat.value,
                                chestCm = chest.value.takeIf { it > 0 },
                                waistCm = waist.value.takeIf { it > 0 },
                                armCm = arm.value.takeIf { it > 0 },
                                legCm = leg.value.takeIf { it > 0 }
                            )
                        )
                    }
                }) {
                    Text(text = "Guardar métrica")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Registro de entrenamiento", style = MaterialTheme.typography.titleLarge)
                exercises.forEachIndexed { index, exerciseState ->
                    ExerciseEditor(
                        state = exerciseState,
                        onRemove = { exercises.removeAt(index) }
                    )
                }
                Button(onClick = { exercises += ExerciseInputState() }) {
                    Text("Agregar ejercicio")
                }
                Button(onClick = {
                    if (exercises.isNotEmpty()) {
                        scope.launch {
                            viewModel.addTrainingSession(
                                TrainingSession(
                                    date = System.currentTimeMillis(),
                                    exercises = exercises.mapNotNull { it.toExerciseEntry() }
                                )
                            )
                            exercises.clear()
                        }
                    }
                }) {
                    Text("Guardar sesión")
                }
            }
        }
    }
}

@Composable
private fun MetricField(label: String, state: Float, onValueChange: (Float) -> Unit) {
    OutlinedTextField(
        value = if (state == 0f) "" else String.format(Locale.getDefault(), "%.1f", state),
        onValueChange = { input ->
            onValueChange(input.toFloatOrNull() ?: 0f)
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}

private class ExerciseInputState {
    val name = mutableStateOf("")
    val sets = mutableStateListOf<SetState>()

    fun toExerciseEntry(): ExerciseEntry? {
        if (name.value.isBlank() || sets.isEmpty()) return null
        return ExerciseEntry(name = name.value, sets = sets.mapNotNull { it.toExerciseSet() })
    }
}

private class SetState {
    val reps = mutableStateOf(0)
    val weight = mutableStateOf(0f)

    fun toExerciseSet(): ExerciseSet? {
        if (reps.value <= 0) return null
        return ExerciseSet(repetitions = reps.value, weight = weight.value)
    }
}

@Composable
private fun ExerciseEditor(state: ExerciseInputState, onRemove: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = state.name.value,
            onValueChange = { state.name.value = it },
            label = { Text("Nombre del ejercicio") },
            modifier = Modifier.fillMaxWidth()
        )
        state.sets.forEach { setState ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = if (setState.reps.value == 0) "" else setState.reps.value.toString(),
                    onValueChange = { setState.reps.value = it.toIntOrNull() ?: 0 },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = if (setState.weight.value == 0f) "" else String.format(Locale.getDefault(), "%.1f", setState.weight.value),
                    onValueChange = { setState.weight.value = it.toFloatOrNull() ?: 0f },
                    label = { Text("Peso (kg)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { state.sets += SetState() }) {
                Text("Agregar serie")
            }
            if (state.sets.isNotEmpty()) {
                Button(onClick = { state.sets.removeLast() }) {
                    Text("Quitar serie")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onRemove) {
                Text("Eliminar ejercicio")
            }
        }
    }
}

@Composable
private fun MetricGrid(values: List<Pair<String, androidx.compose.runtime.MutableState<Float>>>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        values.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                pair.forEach { (label, state) ->
                    OutlinedTextField(
                        value = if (state.value == 0f) "" else String.format(Locale.getDefault(), "%.1f", state.value),
                        onValueChange = { state.value = it.toFloatOrNull() ?: 0f },
                        label = { Text("$label (cm)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
