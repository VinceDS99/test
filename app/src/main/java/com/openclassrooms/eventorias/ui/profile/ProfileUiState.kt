package com.openclassrooms.eventorias.ui.profile

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val notificationsEnabled: Boolean = true
)