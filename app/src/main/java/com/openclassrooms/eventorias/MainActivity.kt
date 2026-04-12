package com.ton_nom.eventorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ton_nom.eventorias.ui.auth.LoginScreen
import com.ton_nom.eventorias.ui.theme.EventoriasTheme
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