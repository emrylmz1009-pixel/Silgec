package com.example.swipeclean.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = ElectricBlue,
    tertiary = CyberPurple,
    background = DarkBackground,
    surface = DarkCardSurface,
    surfaceVariant = DarkCardElevated,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorderHighlight
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    secondary = NeonCyan,
    tertiary = CyberPurple,
    background = LightBackground,
    surface = LightCardSurface,
    surfaceVariant = Color(0xFFE2E8F0),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun SwipeCleanTheme(
    darkTheme: Boolean = true, // Default to stunning dark theme for cyberpunk look
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
