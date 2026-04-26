package com.openclassrooms.eventorias.ui.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.eventorias.notification.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // Expose si l'utilisateur est déjà connecté (true/false)
    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun onSignInSuccess() {
        _isLoggedIn.value = true
        // Sauvegarde le token FCM après connexion
        TokenRepository.refreshAndSaveToken()
    }

    fun onSignOut() {
        auth.signOut()
        _isLoggedIn.value = false
    }
}