package com.openclassrooms.eventorias

import com.openclassrooms.eventorias.ui.auth.LoginScreen
import com.openclassrooms.eventorias.ui.theme.EventoriasTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.eventorias.ui.auth.AuthViewModel
import com.openclassrooms.eventorias.ui.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Eventorias)
        enableEdgeToEdge()
        setContent {
            EventoriasTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

                if (isLoggedIn) {
                    AppNavigation()  // ← écran principal avec bottom nav
                } else {
                    LoginScreen(
                        onLoginSuccess = { authViewModel.onSignInSuccess() }
                    )
                }
            }
        }
    }
}