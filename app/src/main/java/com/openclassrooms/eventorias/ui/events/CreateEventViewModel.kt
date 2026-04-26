package com.openclassrooms.eventorias.ui.events

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.eventorias.data.model.Event
import com.openclassrooms.eventorias.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateEventUiState {
    object Idle : CreateEventUiState()
    object Loading : CreateEventUiState()
    object Success : CreateEventUiState()
    data class Error(val message: String) : CreateEventUiState()
}

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateEventUiState>(CreateEventUiState.Idle)
    val uiState: StateFlow<CreateEventUiState> = _uiState

    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    val date = MutableStateFlow("")
    val time = MutableStateFlow("")
    val address = MutableStateFlow("")
    val selectedImageUri = MutableStateFlow<Uri?>(null)

    fun onImageSelected(uri: Uri) {
        selectedImageUri.value = uri
    }

    fun createEvent(context: Context) {
        val imageUri = selectedImageUri.value

        if (imageUri == null) {
            _uiState.value = CreateEventUiState.Error("Veuillez sélectionner une image")
            return
        }
        if (title.value.isBlank() || address.value.isBlank() ||
            date.value.isBlank() || time.value.isBlank()
        ) {
            _uiState.value = CreateEventUiState.Error("Tous les champs sont obligatoires")
            return
        }

        viewModelScope.launch {
            _uiState.value = CreateEventUiState.Loading
            val event = Event(
                title = title.value,
                description = description.value,
                date = date.value,
                time = time.value,
                location = address.value
            )
            val result = repository.createEvent(event, imageUri, context)
            _uiState.value = if (result.isSuccess) {
                CreateEventUiState.Success
            } else {
                CreateEventUiState.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }
}