package com.globalnotes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.globalnotes.android.ui.theme.GlobalNotesTheme
import com.globalnotes.android.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var appState by remember { mutableStateOf<AppState>(AppState.Loading) }
            
            GlobalNotesTheme {
                Crossfade(targetState = appState, label = "appTransition") { state ->
                    when (state) {
                        AppState.Loading -> {
                            com.globalnotes.android.ui.screens.LoadingScreen(onFinish = { 
                                appState = AppState.Onboarding 
                            })
                        }
                        AppState.Onboarding -> {
                            com.globalnotes.android.ui.screens.OnboardingScreen(
                                onBack = { appState = AppState.Loading },
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
                            MainScreen()
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
