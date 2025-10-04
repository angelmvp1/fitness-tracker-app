package com.example.fitnesstracker.ui.screens.cycle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.fitnesstracker.data.entities.CompoundDose
import com.example.fitnesstracker.data.entities.CyclePhase
import com.example.fitnesstracker.data.entities.PerformanceNote
import com.example.fitnesstracker.data.entities.PhaseType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleScreen(viewModel: MainViewModel) {
    val currentPhase by viewModel.currentCycle.collectAsState()
    val dashboardState by viewModel.dashboardState.collectAsState()
    val scope = rememberCoroutineScope()

    val expanded = remember { mutableStateOf(false) }
    val selectedPhase = remember { mutableStateOf(currentPhase?.phase ?: PhaseType.BLAST) }
    val compounds = remember { mutableStateListOf<CompoundState>() }
    val noteText = remember { mutableStateOf("") }

    LaunchedEffect(currentPhase) {
        compounds.clear()
        currentPhase?.compounds?.forEach { compound ->
            compounds += CompoundState().apply {
                name.value = compound.name
                dosage.value = compound.dosageMg
                frequency.value = compound.frequencyPerWeek
            }
        }
        selectedPhase.value = currentPhase?.phase ?: PhaseType.BLAST
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Ciclo hormonal", style = MaterialTheme.typography.displayLarge)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = it }) {
                    TextField(
                        value = selectedPhase.value.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fase actual") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                        PhaseType.values().forEach { phase ->
                            DropdownMenuItem(
                                text = { Text(text = phase.name) },
                                onClick = {
                                    selectedPhase.value = phase
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }

                compounds.forEachIndexed { index, state ->
                    CompoundEditor(state = state, onRemove = { compounds.removeAt(index) })
                }
                Button(onClick = { compounds += CompoundState() }) {
                    Text("Agregar compuesto")
                }
                Button(onClick = {
                    scope.launch {
                        viewModel.updateCycle(
                            CyclePhase(
                                startDate = System.currentTimeMillis(),
                                phase = selectedPhase.value,
                                compounds = compounds.mapNotNull { it.toCompound() }
                            )
                        )
                    }
                }) {
                    Text("Guardar ciclo")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Notas diarias", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = noteText.value,
                    onValueChange = { noteText.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Efectos positivos/negativos del día") }
                )
                Button(onClick = {
                    if (noteText.value.isNotBlank()) {
                        scope.launch {
                            viewModel.addPerformanceNote(
                                PerformanceNote(
                                    date = System.currentTimeMillis(),
                                    mood = 5,
                                    notes = noteText.value
                                )
                            )
                            noteText.value = ""
                        }
                    }
                }) {
                    Text("Guardar nota")
                }

                val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
                dashboardState.notes.take(7).forEach { note ->
                    Text("${formatter.format(Date(note.date))}: ${note.notes}")
                }
            }
        }
    }
}

private class CompoundState {
    val name = mutableStateOf("")
    val dosage = mutableStateOf(0f)
    val frequency = mutableStateOf(0)

    fun toCompound(): CompoundDose? {
        if (name.value.isBlank()) return null
        return CompoundDose(
            name = name.value,
            dosageMg = dosage.value,
            frequencyPerWeek = frequency.value
        )
    }
}

@Composable
private fun CompoundEditor(state: CompoundState, onRemove: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = state.name.value,
            onValueChange = { state.name.value = it },
            label = { Text("Compuesto") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = if (state.dosage.value == 0f) "" else state.dosage.value.toString(),
                onValueChange = { state.dosage.value = it.toFloatOrNull() ?: 0f },
                label = { Text("Dosis (mg)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = if (state.frequency.value == 0) "" else state.frequency.value.toString(),
                onValueChange = { state.frequency.value = it.toIntOrNull() ?: 0 },
                label = { Text("Frecuencia/sem") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Button(onClick = onRemove, modifier = Modifier.align(Alignment.End)) {
            Text("Eliminar")
        }
    }
}
