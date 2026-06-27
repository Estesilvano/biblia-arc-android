package com.example.biblia.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeMode { LIGHT, DARK, SEPIA, SYSTEM }

private val DarkColorScheme = darkColorScheme(
    primary = BibleColors.DarkPrimary,
    onPrimary = BibleColors.DarkOnPrimary,
    primaryContainer = BibleColors.DarkSurfaceVariant,
    secondary = BibleColors.DarkAccent,
    background = BibleColors.DarkBackground,
    surface = BibleColors.DarkSurface,
    surfaceVariant = BibleColors.DarkSurfaceVariant,
    onBackground = BibleColors.DarkText,
    onSurface = BibleColors.DarkText,
    onSurfaceVariant = BibleColors.DarkTextSecondary,
    outline = BibleColors.DarkTextSecondary.copy(alpha = 0.3f)
)

private val SepiaColorScheme = lightColorScheme(
    primary = BibleColors.SepiaPrimary,
    onPrimary = BibleColors.SepiaOnPrimary,
    primaryContainer = BibleColors.SepiaBackground,
    secondary = BibleColors.SepiaAccent,
    background = BibleColors.SepiaBackground,
    surface = BibleColors.SepiaSurface,
    surfaceVariant = BibleColors.SepiaBackground,
    onBackground = BibleColors.SepiaText,
    onSurface = BibleColors.SepiaText,
    onSurfaceVariant = BibleColors.SepiaTextSecondary,
    outline = BibleColors.SepiaTextSecondary.copy(alpha = 0.3f)
)

private val LightColorScheme = lightColorScheme(
    primary = BibleColors.LightPrimary,
    onPrimary = BibleColors.LightOnPrimary,
    primaryContainer = BibleColors.LightSurface,
    secondary = BibleColors.LightAccent,
    background = BibleColors.LightBackground,
    surface = BibleColors.LightSurface,
    surfaceVariant = Color(0xFFF5F0FA),
    onBackground = BibleColors.LightText,
    onSurface = BibleColors.LightText,
    onSurfaceVariant = BibleColors.LightTextSecondary,
    outline = BibleColors.LightTextSecondary.copy(alpha = 0.3f)
)

@Composable
fun BibliaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SEPIA -> false
        ThemeMode.SYSTEM -> systemDark
    }
    val isSepia = themeMode == ThemeMode.SEPIA

    val colorScheme = when {
        isSepia -> SepiaColorScheme
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BibleTypography,
        content = content
    )
}
