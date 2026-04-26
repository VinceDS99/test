package com.openclassrooms.eventorias

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.openclassrooms.eventorias.ui.auth.LoginScreen
import com.openclassrooms.eventorias.ui.theme.EventoriasTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysGoogleButton() {
        composeTestRule.setContent {
            EventoriasTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }
        composeTestRule
            .onNodeWithText("Sign in with Google")
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysEmailButton() {
        composeTestRule.setContent {
            EventoriasTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }
        composeTestRule
            .onNodeWithText("Sign in with email")
            .assertIsDisplayed()
    }
}