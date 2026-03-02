package com.globalnotes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.globalnotes.android.ui.theme.GlobalNotesTheme
import com.globalnotes.android.ui.screens.MainScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var appState by remember { mutableStateOf<AppState>(AppState.Loading) }

            // Automatically navigate to Auth from any screen when Firebase signs out
            DisposableEffect(Unit) {
                val listener = FirebaseAuth.AuthStateListener { fa ->
                    if (fa.currentUser == null && appState == AppState.Main) {
                        appState = AppState.Auth
                    }
                }
                FirebaseAuth.getInstance().addAuthStateListener(listener)
                onDispose { FirebaseAuth.getInstance().removeAuthStateListener(listener) }
            }

            GlobalNotesTheme {
                AnimatedContent(
                    targetState = appState,
                    label = "appTransition",
                    transitionSpec = {
                        // Instant transition on sign-out so the sidebar "User" flash never shows
                        if (initialState == AppState.Main && targetState == AppState.Auth) {
                            EnterTransition.None togetherWith ExitTransition.None
                        } else {
                            fadeIn() togetherWith fadeOut()
                        }
                    }
                ) { state ->
                    when (state) {
                        AppState.Loading -> {
                            com.globalnotes.android.ui.screens.LoadingScreen(onFinish = { 
                                appState = AppState.Onboarding 
                            })
                        }
                        AppState.Onboarding -> {
                            com.globalnotes.android.ui.screens.OnboardingScreen(
                                onBack = { appState = AppState.Loading },
                                onSkip = { appState = AppState.Main },
                                onFinish = { appState = AppState.Auth }
                            )
                        }
                        AppState.Auth -> {
                            com.globalnotes.android.ui.screens.AuthScreen(
                                onBack = { appState = AppState.Onboarding },
                                onAuthSuccess = { appState = AppState.Main }
                            )
                        }
                        AppState.Main -> {
                            MainScreen(onSignOut = { appState = AppState.Auth })
                        }
                    }
                }
            }
        }
    }
}

sealed class AppState {
    data object Loading : AppState()
    data object Onboarding : AppState()
    data object Auth : AppState()
    data object Main : AppState()
}
