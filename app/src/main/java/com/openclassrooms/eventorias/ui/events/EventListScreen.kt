package com.openclassrooms.eventorias.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.openclassrooms.eventorias.data.model.Event
import com.openclassrooms.eventorias.ui.theme.DarkBackground
import com.openclassrooms.eventorias.ui.theme.DarkSurface
import com.openclassrooms.eventorias.ui.theme.RedPrimary
import com.openclassrooms.eventorias.ui.theme.White


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    viewModel: EventListViewModel = hiltViewModel(),
    onCreateEvent: () -> Unit = {},
    onEventClick: (Event) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var isSearchVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (isSearchVisible) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.onSearchQueryChange(it) },
                                placeholder = { Text("Rechercher...", color = Color.Gray) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = White,
                                    unfocusedTextColor = White,
                                    cursorColor = White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = "Event list",
                                color = White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    },
                    actions = {
                        // Icône recherche
                        IconButton(onClick = {
                            isSearchVisible = !isSearchVisible
                            if (!isSearchVisible) viewModel.onSearchQueryChange("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Rechercher",
                                tint = White
                            )
                        }
                        // Icône tri (fonctionnalité future)
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Trier",
                                tint = White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateEvent,
                containerColor = RedPrimary,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Créer un événement")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is EventListUiState.Loading -> {
                    CircularProgressIndicator(color = White)
                }
                is EventListUiState.Error -> {
                    ErrorState(onRetry = { viewModel.retry() })
                }
                is EventListUiState.Success -> {
                    if (state.events.isEmpty()) {
                        Text("Aucun événement", color = Color.Gray)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(state.events, key = { it.id }) { event ->
                                EventItem(event = event,
                                    onClick = { onEventClick(event) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(event: Event,
              onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar circulaire à gauche
        AsyncImage(
            model = event.authorPhotoUrl,
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )

        // Titre + date au centre
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = event.title,
                color = White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.date,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        // Image de l'événement à droite
        AsyncImage(
            model = event.imageUrl,
            contentDescription = "Image de l'événement",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 80.dp, height = 56.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Gray)
        )
    }
}

@Composable
fun ErrorState(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Cercle gris avec point d'exclamation
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Error", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "An error has occured,\nplease try again later",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.width(160.dp)
        ) {
            Text("Try again", color = White)
        }
    }
}