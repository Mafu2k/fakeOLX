package com.example.fakeolx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF0E1D18),
    primaryContainer = Color(0xFF12362B),
    onPrimaryContainer = Color(0xFFBCEBDD),
    secondary = DarkSecondary,
    onSecondary = Color(0xFF0F1A16),
    secondaryContainer = Color(0xFF28332F),
    onSecondaryContainer = Color(0xFFD5E0DA),
    tertiary = DarkTertiary,
    onTertiary = Color(0xFF352300),
    tertiaryContainer = Color(0xFF4A3411),
    onTertiaryContainer = Color(0xFFFFE0B2),
    background = DarkBackground,
    onBackground = Color(0xFFE6ECE9),
    surface = DarkSurface,
    onSurface = Color(0xFFE6ECE9),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB8C3BC),
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color(0xFFF3FFF9),
    primaryContainer = BrandMint,
    onPrimaryContainer = Color(0xFF093126),
    secondary = BrandInk,
    onSecondary = Color(0xFFF5F7F7),
    secondaryContainer = Color(0xFFDDE7E3),
    onSecondaryContainer = Color(0xFF1B2A2A),
    tertiary = BrandAmber,
    onTertiary = Color(0xFF382500),
    tertiaryContainer = Color(0xFFFFE4B6),
    onTertiaryContainer = Color(0xFF382500),
    background = BrandBackground,
    onBackground = BrandInk,
    surface = BrandSurface,
    onSurface = BrandInk,
    surfaceVariant = BrandSurfaceVariant,
    onSurfaceVariant = BrandMuted,
    outline = BrandOutline
)

@Composable
fun FakeOLXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
