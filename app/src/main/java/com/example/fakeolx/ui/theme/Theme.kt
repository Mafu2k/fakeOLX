package com.example.fakeolx.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Caramel,
    onPrimary = CoffeeBlack,
    primaryContainer = Cocoa,
    onPrimaryContainer = Cream,
    secondary = Cream,
    onSecondary = CoffeeBlack,
    secondaryContainer = Mocha,
    onSecondaryContainer = Cream,
    tertiary = Copper,
    onTertiary = CoffeeBlack,
    tertiaryContainer = Espresso,
    onTertiaryContainer = Latte,
    background = CoffeeBlack,
    onBackground = Cream,
    surface = Espresso,
    onSurface = Cream,
    surfaceVariant = Mocha,
    onSurfaceVariant = CoffeeMuted,
    surfaceContainerLowest = CoffeeBlack,
    surfaceContainerLow = Espresso,
    surfaceContainer = Espresso,
    surfaceContainerHigh = Mocha,
    surfaceContainerHighest = Cocoa,
    outline = CoffeeOutline
)

private val LightColorScheme = lightColorScheme(
    primary = Espresso,
    onPrimary = Cream,
    primaryContainer = Latte,
    onPrimaryContainer = Espresso,
    secondary = Mocha,
    onSecondary = Cream,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = Espresso,
    tertiary = Copper,
    onTertiary = CoffeeBlack,
    tertiaryContainer = Color(0xFFF2D8B8),
    onTertiaryContainer = Espresso,
    background = LightBackground,
    onBackground = Espresso,
    surface = LightSurface,
    onSurface = Espresso,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF5A4A3C),
    outline = LightOutline
)

@Composable
fun FakeOLXTheme(
    darkTheme: Boolean = true,
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
