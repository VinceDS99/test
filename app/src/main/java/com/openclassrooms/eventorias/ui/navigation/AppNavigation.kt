package com.openclassrooms.eventorias.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.eventorias.ui.events.EventListScreen
import com.openclassrooms.eventorias.ui.theme.DarkBackground
import com.openclassrooms.eventorias.ui.theme.DarkSurface
import com.openclassrooms.eventorias.ui.theme.RedPrimary
import com.openclassrooms.eventorias.ui.theme.White
import com.openclassrooms.eventorias.ui.profile.ProfileScreen
import com.openclassrooms.eventorias.data.model.Event


// Destinations de navigation
sealed class Screen(val route: String, val label: String) {
    object Events : Screen("events", "Events")
    object Profile : Screen("profile", "Profile")
    object EventDetail : Screen("event_detail/{eventId}", "Detail")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
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
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Events.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Events.route) {
                EventListScreen(        onEventClick = { event ->
                    // Stocke l'événement sélectionné dans le ViewModel partagé
                    navController.navigate("event_detail/${event.id}")
                }
                )
            }
            composable(Screen.Profile.route) {
                // Écran profil — sera implémenté à l'étape suivante
                ProfileScreen()
            }
        }
    }
}