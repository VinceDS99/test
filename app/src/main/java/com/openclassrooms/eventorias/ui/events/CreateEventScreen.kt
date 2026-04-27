package com.openclassrooms.eventorias.ui.events

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.openclassrooms.eventorias.ui.theme.DarkBackground
import com.openclassrooms.eventorias.ui.theme.DarkSurface
import com.openclassrooms.eventorias.ui.theme.RedPrimary
import com.openclassrooms.eventorias.ui.theme.White
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    viewModel: CreateEventViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEventCreated: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val date by viewModel.date.collectAsState()
    val time by viewModel.time.collectAsState()
    val address by viewModel.address.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()

    // Navigation automatique après succès
    LaunchedEffect(uiState) {
        if (uiState is CreateEventUiState.Success) onEventCreated()
    }

    // --- DatePicker ---
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatted = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                            .format(Date(millis))
                        viewModel.date.value = formatted
                    }
                    showDatePicker = false
                }) { Text("OK", color = White) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annuler", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- TimePicker ---
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(is24Hour = false)

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = DarkSurface,
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    val amPm = if (hour < 12) "AM" else "PM"
                    val displayHour = when {
                        hour == 0 -> 12
                        hour > 12 -> hour - 12
                        else -> hour
                    }
                    viewModel.time.value = "%02d:%02d %s".format(displayHour, minute, amPm)
                    showTimePicker = false
                }) { Text("OK", color = White) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Annuler", color = Color.Gray)
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = DarkBackground,
                        clockDialSelectedContentColor = White,
                        clockDialUnselectedContentColor = Color.Gray,
                        selectorColor = RedPrimary,
                        containerColor = DarkSurface,
                        periodSelectorSelectedContainerColor = RedPrimary,
                        periodSelectorUnselectedContainerColor = DarkBackground,
                        periodSelectorSelectedContentColor = White,
                        periodSelectorUnselectedContentColor = Color.Gray,
                        timeSelectorSelectedContainerColor = RedPrimary,
                        timeSelectorUnselectedContainerColor = DarkBackground,
                        timeSelectorSelectedContentColor = White,
                        timeSelectorUnselectedContentColor = Color.Gray
                    )
                )
            }
        )
    }

    // --- Caméra ---
    val cameraImageUri = remember {
        val file = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.onImageSelected(it) } }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> if (success) viewModel.onImageSelected(cameraImageUri) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) cameraLauncher.launch(cameraImageUri) }

    // --- UI ---
    Scaffold(
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Creation of an event",
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
        },
        bottomBar = {
            Button(
                onClick = { viewModel.createEvent(context) },
                enabled = uiState !is CreateEventUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                if (uiState is CreateEventUiState.Loading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Validate", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Aperçu image sélectionnée
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Image sélectionnée",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            // Titre
            EventTextField(
                value = title,
                onValueChange = { viewModel.title.value = it },
                label = "Title",
                placeholder = "New event"
            )

            // Description
            EventTextField(
                value = description,
                onValueChange = { viewModel.description.value = it },
                label = "Description",
                placeholder = "Tap here to enter your description",
                minLines = 3
            )

            // Date + Time
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                // Date
                Box(modifier = Modifier.weight(1f)) {
                    EventTextField(
                        value = date,
                        onValueChange = {},
                        label = "Date",
                        placeholder = "MM/DD/YYYY",
                        readOnly = true
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }

                // Time
                Box(modifier = Modifier.weight(1f)) {
                    EventTextField(
                        value = time,
                        onValueChange = {},
                        label = "Time",
                        placeholder = "HH:MM AM",
                        readOnly = true
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showTimePicker = true }
                    )
                }
            }

            // Adresse
            EventTextField(
                value = address,
                onValueChange = { viewModel.address.value = it },
                label = "Address",
                placeholder = "Enter full address"
            )

            // Boutons caméra + galerie
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(
                    onClick = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    containerColor = DarkSurface,
                    contentColor = White
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Prendre une photo")
                }
                FloatingActionButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    containerColor = RedPrimary,
                    contentColor = White
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Choisir depuis la galerie")
                }
            }

            // Message d'erreur
            if (uiState is CreateEventUiState.Error) {
                Text(
                    text = (uiState as CreateEventUiState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun EventTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    minLines: Int = 1,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray, fontSize = 12.sp) },
        placeholder = { Text(placeholder, color = Color.Gray) },
        minLines = minLines,
        readOnly = readOnly,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = White,
            unfocusedTextColor = White,
            focusedContainerColor = DarkSurface,
            unfocusedContainerColor = DarkSurface,
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = White,
            disabledTextColor = White,
            disabledContainerColor = DarkSurface,
            disabledBorderColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp)
    )
}