package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PowerRed,
    onPrimary = Color.White,
    primaryContainer = PowerRedPressed,
    onPrimaryContainer = Color.White,
    secondary = RemoteSurfaceElevation,
    onSecondary = TextPrimary,
    background = CharcoalBackground,
    onBackground = TextPrimary,
    surface = RemoteBodyDark,
    onSurface = TextPrimary,
    surfaceVariant = DialSurface,
    onSurfaceVariant = TextSecondary,
    outline = DialBorder
)

@Composable
fun AtombergTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
