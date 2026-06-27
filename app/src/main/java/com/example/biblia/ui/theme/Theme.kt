package com.example.biblia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    background = DarkBg,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = DarkTextSecondary,
    error = DarkError,
    outline = DarkOutline,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    background = LightBg,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightText,
    onSurface = LightText,
    onSurfaceVariant = LightTextSecondary,
    error = LightError,
    outline = LightOutline,
)

private val SepiaColorScheme = lightColorScheme(
    primary = SepiaPrimary,
    onPrimary = SepiaOnPrimary,
    secondary = SepiaSecondary,
    background = SepiaBg,
    surface = SepiaSurface,
    surfaceVariant = SepiaSurfaceVariant,
    onBackground = SepiaText,
    onSurface = SepiaText,
    onSurfaceVariant = SepiaTextSecondary,
    error = SepiaError,
    outline = SepiaOutline,
)

@Immutable
data class BibliaColors(
    val strongColor: Color,
    val footnoteBackground: Color,
    val verseNumberColor: Color,
    val bookmarkTint: Color,
)

val BibliaThemeColors: Map<String, BibliaColors> = mapOf(
    "dark" to BibliaColors(
        strongColor = DarkSecondary,
        footnoteBackground = DarkSurfaceVariant,
        verseNumberColor = DarkPrimary,
        bookmarkTint = DarkSecondary,
    ),
    "sepia" to BibliaColors(
        strongColor = SepiaSecondary,
        footnoteBackground = SepiaSurfaceVariant,
        verseNumberColor = SepiaPrimary,
        bookmarkTint = SepiaSecondary,
    ),
    "light" to BibliaColors(
        strongColor = LightSecondary,
        footnoteBackground = LightSurfaceVariant,
        verseNumberColor = LightPrimary,
        bookmarkTint = LightSecondary,
    ),
)

@Composable
fun BibliaTheme(
    themeMode: String = "dark",
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        "dark" -> true
        "light" -> false
        "sepia" -> false
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        themeMode == "sepia" -> SepiaColorScheme
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BibliaTypography,
        content = content
    )
}
