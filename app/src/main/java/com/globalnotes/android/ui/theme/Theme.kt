package com.globalnotes.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeType {
    AMOLED_DARK, NATURE_GREEN, CORPORATE_GRAY, MINIMAL_WHITE
}

@Composable
fun GlobalNotesTheme(
    themeType: AppThemeType = AppThemeType.AMOLED_DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        AppThemeType.AMOLED_DARK -> darkColorScheme(
            primary = AmoledAccent,
            background = AmoledBackground,
            surface = AmoledSurface,
            onBackground = androidx.compose.ui.graphics.Color.White,
            onSurface = androidx.compose.ui.graphics.Color.White
        )
        AppThemeType.NATURE_GREEN -> darkColorScheme(
            primary = NatureAccent,
            background = NatureSurface,
            surface = NatureSidebar,
            onBackground = androidx.compose.ui.graphics.Color.White,
            onSurface = androidx.compose.ui.graphics.Color.White
        )
        AppThemeType.CORPORATE_GRAY -> darkColorScheme(
            primary = CorporateAccent,
            background = CorporateSurface,
            surface = CorporateSidebar,
            onBackground = androidx.compose.ui.graphics.Color.White,
            onSurface = androidx.compose.ui.graphics.Color.White
        )
        AppThemeType.MINIMAL_WHITE -> lightColorScheme(
            primary = MinimalAccent,
            background = MinimalBackground,
            surface = MinimalSurface,
            onBackground = androidx.compose.ui.graphics.Color.Black,
            onSurface = androidx.compose.ui.graphics.Color.Black
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = 
                themeType == AppThemeType.MINIMAL_WHITE
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
