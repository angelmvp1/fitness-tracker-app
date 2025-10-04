package com.example.fitnesstracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitnesstracker.MainViewModel
import com.example.fitnesstracker.ui.screens.cycle.CycleScreen
import com.example.fitnesstracker.ui.screens.dashboard.DashboardScreen
import com.example.fitnesstracker.ui.screens.metrics.MetricsScreen
import com.example.fitnesstracker.ui.screens.reminders.ReminderScreen

private enum class Destination(val route: String) {
    Dashboard("dashboard"),
    Metrics("metrics"),
    Cycle("cycle"),
    Reminders("reminders")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    val destinations = listOf(
        Destination.Dashboard to Icons.Default.BarChart,
        Destination.Metrics to Icons.Default.FitnessCenter,
        Destination.Cycle to Icons.Default.Settings,
        Destination.Reminders to Icons.Default.Notifications
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                destinations.forEach { (destination, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = icon, contentDescription = destination.name) },
                        label = { Text(destination.name) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Dashboard.route) {
                DashboardScreen(viewModel)
            }
            composable(Destination.Metrics.route) {
                MetricsScreen(viewModel)
            }
            composable(Destination.Cycle.route) {
                CycleScreen(viewModel)
            }
            composable(Destination.Reminders.route) {
                ReminderScreen(viewModel)
            }
        }
    }
}
