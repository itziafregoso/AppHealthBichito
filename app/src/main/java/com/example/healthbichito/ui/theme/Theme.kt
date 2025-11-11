package com.example.healthbichito.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ===========================================================
// LIGHT COLOR SCHEME
// ===========================================================
private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,

    secondary = SecondaryGreen,
    onSecondary = Color.White,

    tertiary = AccentOrange,

    background = BackgroundGray,
    onBackground = TextDark,

    surface = CardWhite,
    onSurface = TextDark,

    surfaceVariant = SecondaryGreenLight,
    onSurfaceVariant = TextDark,

    outline = Color(0xFFBDBDBD),

    error = Color(0xFFB00020)
)


// ===========================================================
// DARK COLOR SCHEME
// ===========================================================
private val DarkColors = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.Black,

    secondary = SecondaryGreen,
    onSecondary = Color.Black,

    tertiary = AccentOrange,

    background = DarkForest,
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,

    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,

    outline = DarkOutline,

    error = DarkError
)


// ===========================================================
// ✅ THEME FINAL: Detecta modo oscuro automáticamente
// ===========================================================
@Composable
fun HealthBichitoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
