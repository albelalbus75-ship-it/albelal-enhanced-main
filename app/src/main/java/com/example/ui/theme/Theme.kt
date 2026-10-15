package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = LuxuryGold,
    onPrimary = TextDark,
    primaryContainer = LuxuryGoldSurface,
    onPrimaryContainer = LuxuryGoldLight,
    secondary = LuxuryGoldDark,
    onSecondary = TextDark,
    secondaryContainer = DarkElevated,
    onSecondaryContainer = TextWhite,
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkCardBackground,
    onSurface = TextWhite,
    surfaceVariant = DarkElevated,
    onSurfaceVariant = TextMuted,
    outline = LuxuryGoldBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBackground.toArgb()
            window.navigationBarColor = DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = LuxuryDarkColorScheme,
        typography = Typography,
        content = content
    )
}
