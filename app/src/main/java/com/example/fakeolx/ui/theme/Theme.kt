package com.example.fakeolx.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonLime,
    onPrimary = Color(0xFF0C1307),
    primaryContainer = Color(0xFF1C2B14),
    onPrimaryContainer = Color(0xFFD8FFC0),
    secondary = SoftWhite,
    onSecondary = Color(0xFF1A1F18),
    secondaryContainer = Color(0xFF20261F),
    onSecondaryContainer = Color(0xFFDDE4D9),
    tertiary = NeonCyan,
    onTertiary = Color(0xFF061F1A),
    tertiaryContainer = Color(0xFF12312A),
    onTertiaryContainer = Color(0xFFB1FFF3),
    background = Night,
    onBackground = SoftWhite,
    surface = NightSurface,
    onSurface = SoftWhite,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = MutedSilver,
    outline = NightOutline
)

private val LightColorScheme = lightColorScheme(
    primary = NeonLimeDeep,
    onPrimary = Color(0xFF0E1607),
    primaryContainer = Color(0xFFE0FFC8),
    onPrimaryContainer = Color(0xFF18230E),
    secondary = Color(0xFF2C332B),
    onSecondary = Color(0xFFF5F7F2),
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = Color(0xFF202620),
    tertiary = NeonAmber,
    onTertiary = Color(0xFF332200),
    tertiaryContainer = Color(0xFFFFE2A1),
    onTertiaryContainer = Color(0xFF332200),
    background = LightBackground,
    onBackground = Color(0xFF151A14),
    surface = LightSurface,
    onSurface = Color(0xFF151A14),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF4B564B),
    outline = LightOutline
)

@Composable
fun FakeOLXTheme(
    darkTheme: Boolean = true,
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
