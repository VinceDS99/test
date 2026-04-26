package com.openclassrooms.eventorias.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.eventorias.ui.events.CreateEventScreen
import com.openclassrooms.eventorias.ui.events.EventDetailScreen
import com.openclassrooms.eventorias.ui.events.EventListScreen
import com.openclassrooms.eventorias.ui.events.EventListUiState
import com.openclassrooms.eventorias.ui.events.EventListViewModel
import com.openclassrooms.eventorias.ui.profile.ProfileScreen
import com.openclassrooms.eventorias.ui.theme.DarkBackground
import com.openclassrooms.eventorias.ui.theme.DarkSurface
import com.openclassrooms.eventorias.ui.theme.RedPrimary
import com.openclassrooms.eventorias.ui.theme.White

sealed class Screen(val route: String, val label: String) {
    object Events : Screen("events", "Events")
    object Profile : Screen("profile", "Profile")
    object EventDetail : Screen("event_detail/{eventId}", "Detail")
    object CreateEvent : Screen("create_event", "Create")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            if (currentRoute == Screen.Events.route || currentRoute == Screen.Profile.route) {
                NavigationBar(containerColor = DarkSurface) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Events.route,
                        onClick = { navController.navigate(Screen.Events.route) },
                        icon = {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Events"
                            )
                        },
                        label = { Text(Screen.Events.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = White,
                            selectedTextColor = White,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = RedPrimary.copy(alpha = 0.3f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Profile.route,
                        onClick = { navController.navigate(Screen.Profile.route) },
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text(Screen.Profile.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = White,
                            selectedTextColor = White,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = RedPrimary.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Events.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Events.route) {
                EventListScreen(
                    onEventClick = { event ->
                        navController.navigate("event_detail/${event.id}")
                    },
                    onCreateEvent = {
                        navController.navigate(Screen.CreateEvent.route)
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            composable("event_detail/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                val eventsViewModel: EventListViewModel = hiltViewModel()
                val uiState by eventsViewModel.uiState.collectAsState()
                val event = (uiState as? EventListUiState.Success)
                    ?.events?.find { it.id == eventId }

                if (event != null) {
                    EventDetailScreen(
                        event = event,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.CreateEvent.route) {
                CreateEventScreen(
                    onBack = { navController.popBackStack() },
                    onEventCreated = { navController.popBackStack() }
                )
            }

        }
    }
}