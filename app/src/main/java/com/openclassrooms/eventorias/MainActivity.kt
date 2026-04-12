package com.openclassrooms.eventorias

import com.openclassrooms.eventorias.ui.auth.LoginScreen
import com.openclassrooms.eventorias.ui.theme.EventoriasTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint  // obligatoire pour Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventoriasTheme {
                LoginScreen(
                    onLoginSuccess = {
                        // Navigation vers l'écran principal — sera implémentée à l'étape suivante
                    }
                )
            }
        }
    }
}