package com.example.fitnesstracker.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnesstracker.MainViewModel
import com.example.fitnesstracker.data.entities.PerformanceNote
import com.example.fitnesstracker.data.entities.TrainingSession
import com.example.fitnesstracker.ui.theme.CyanAccent
import com.example.fitnesstracker.ui.theme.RoyalBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val state by viewModel.dashboardState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background)))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        GreetingHeader(state.latestMetrics?.weightKg)
        state.weeklyVolume.takeIf { it.isNotEmpty() }?.let { volume ->
            VolumeCard(volume)
        }
        RecentSessionsCard(state.lastSessions)
        NotesCard(state.notes)
    }
}

@Composable
private fun GreetingHeader(weight: Float?) {
    Column {
        Text(text = "Bienvenido de nuevo", style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.secondary))
        Text(
            text = "Prepárate para el próximo nivel",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )
        weight?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Peso actual: %.1f kg".format(it), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun VolumeCard(volume: Map<String, Float>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Volumen semanal", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            VolumeChart(volume)
        }
    }
}

@Composable
private fun VolumeChart(volume: Map<String, Float>) {
    if (volume.isEmpty()) return
    val maxVolume = (volume.values.maxOrNull() ?: 1f)
    val sortedVolume = volume.entries.toList()
    val labels = sortedVolume.map { it.key }
    val values = sortedVolume.map { (it.value / maxVolume).coerceIn(0f, 1f) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)) {
            val path = Path()
            val widthPerItem = size.width / values.size
            values.forEachIndexed { index, normalized ->
                val x = widthPerItem * index + widthPerItem / 2
                val y = size.height - (normalized * size.height)
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            drawPath(
                path = path,
                color = RoyalBlue,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f)
            )
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(
                        modifier = Modifier
                            .height(4.dp)
                            .background(CyanAccent)
                            .fillMaxWidth(0.6f)
                    )
                    Text(text = label.uppercase(Locale.getDefault()), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun RecentSessionsCard(sessions: List<TrainingSession>) {
    if (sessions.isEmpty()) return
    val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Sesiones recientes", style = MaterialTheme.typography.titleLarge)
            sessions.take(5).forEach { session ->
                Column {
                    Text(text = formatter.format(Date(session.date)), fontWeight = FontWeight.SemiBold)
                    session.exercises.forEach { entry ->
                        Text(
                            text = "${entry.name}: ${entry.sets.size} series",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesCard(notes: List<PerformanceNote>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Bitácora de sensaciones", style = MaterialTheme.typography.titleLarge)
            if (notes.isEmpty()) {
                Text(text = "Agrega notas para seguir tus sensaciones.")
            } else {
                val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
                notes.take(5).forEach { note ->
                    Text(
                        text = "${formatter.format(Date(note.date))} · ${note.notes}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
