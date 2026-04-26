package com.openclassrooms.eventorias.ui.profile

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data class Success(
        val displayName: String = "",
        val email: String = "",
        val photoUrl: String = "",
        val notificationsEnabled: Boolean = false
    ) : ProfileUiState()
}