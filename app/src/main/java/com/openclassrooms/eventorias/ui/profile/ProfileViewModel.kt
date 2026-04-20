package com.openclassrooms.eventorias.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        // Récupère les infos de l'utilisateur connecté depuis Firebase Auth
        val user = auth.currentUser
        _uiState.value = ProfileUiState(
            displayName = user?.displayName ?: "Nom inconnu",
            email = user?.email ?: "Email inconnu",
            photoUrl = user?.photoUrl?.toString() ?: ""
        )
    }

    fun onNotificationsToggle(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        // La persistance des notifs sera gérée à l'étape notifications
    }
}