package com.healthmate.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MintPrimary,
    onPrimary = Color.White,
    primaryContainer = MintDark,
    onPrimaryContainer = MintLight,
    
    secondary = CoralSecondary,
    onSecondary = Color.White,
    secondaryContainer = CoralDark,
    onSecondaryContainer = CoralLight,
    
    tertiary = PurpleTertiary,
    onTertiary = Color.White,
    tertiaryContainer = PurpleDark,
    onTertiaryContainer = PurpleLight,
    
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextTertiary,
    
    error = ErrorColor,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MintPrimary,
    onPrimary = Color.White,
    primaryContainer = MintLight,
    onPrimaryContainer = MintDark,
    
    secondary = CoralSecondary,
    onSecondary = Color.White,
    secondaryContainer = CoralLight,
    onSecondaryContainer = CoralDark,
    
    tertiary = PurpleTertiary,
    onTertiary = Color.White,
    tertiaryContainer = PurpleLight,
    onTertiaryContainer = PurpleDark,
    
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = CardLight,
    onSurfaceVariant = TextSecondary,
    
    error = ErrorColor,
    onError = Color.White
)

@Composable
fun HealthMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled by default to use our custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    // Apply status bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}