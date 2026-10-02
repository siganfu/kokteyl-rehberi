package com.kokteyl.rehberi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kokteyl.rehberi.data.settings.ThemeMode

private val Gold = Color(0xFFE8B04B)

private val DarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF1A1200),
    primaryContainer = Color(0xFF3A2B08),
    onPrimaryContainer = Color(0xFFFFDEA0),
    secondary = Color(0xFFE07A5F),
    onSecondary = Color.White,
    tertiary = Color(0xFF7FD48A),
    background = Color(0xFF0E0E10),
    onBackground = Color(0xFFF2EFE9),
    surface = Color(0xFF17171B),
    onSurface = Color(0xFFF2EFE9),
    surfaceVariant = Color(0xFF232329),
    onSurfaceVariant = Color(0xFFB9B4AA),
    outline = Color(0xFF4A4A52),
    error = Color(0xFFFF6B6B),
    errorContainer = Color(0xFF3B1A1A),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF9A6A00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDEA0),
    onPrimaryContainer = Color(0xFF3A2B08),
    secondary = Color(0xFFB5482E),
    onSecondary = Color.White,
    tertiary = Color(0xFF2E7D32),
    background = Color(0xFFFAF7F2),
    onBackground = Color(0xFF1B1B1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1E),
    surfaceVariant = Color(0xFFEFE9DF),
    onSurfaceVariant = Color(0xFF5A554B),
    outline = Color(0xFFB8B0A2),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun KokteylTheme(mode: ThemeMode, content: @Composable () -> Unit) {
    val dark = when (mode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content
    )
}
