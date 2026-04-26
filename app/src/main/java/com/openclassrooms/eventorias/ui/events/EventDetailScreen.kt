package com.openclassrooms.eventorias.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
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
import coil.compose.AsyncImage
import com.openclassrooms.eventorias.BuildConfig
import com.openclassrooms.eventorias.data.model.Event
import com.openclassrooms.eventorias.ui.theme.DarkBackground
import com.openclassrooms.eventorias.ui.theme.White
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = event.title,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Image principale pleine largeur
            AsyncImage(
                model = event.imageUrl,
                contentDescription = "Image de l'événement",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color.Gray)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Ligne date + avatar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.date,
                        color = White,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                    // Avatar auteur en haut à droite
                    AsyncImage(
                        model = event.authorPhotoUrl,
                        contentDescription = "Photo de l'auteur",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ligne heure
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.time,
                        color = White,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = event.description,
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))



                // Adresse à gauche + carte à droite
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.location,
                        color = White,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    val encodedLocation = URLEncoder.encode(event.location, "UTF-8")
                    val mapUrl = "https://maps.googleapis.com/maps/api/staticmap" +
                            "?center=$encodedLocation" +
                            "&zoom=15" +
                            "&size=300x200" +
                            "&maptype=roadmap" +
                            "&markers=color:red%7C$encodedLocation" +
                            "&key=${BuildConfig.MAPS_API_KEY}"

                    AsyncImage(
                        model = mapUrl,
                        contentDescription = "Carte",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 140.dp, height = 100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}