package com.example.sistemafarmacia.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PharmaDarkPrimary,
    onPrimary = PharmaDarkOnPrimary,
    primaryContainer = PharmaDarkPrimaryContainer,
    onPrimaryContainer = PharmaDarkOnPrimaryContainer,
    secondary = PharmaDarkSecondary,
    onSecondary = PharmaDarkOnSecondary,
    secondaryContainer = PharmaDarkSecondaryContainer,
    onSecondaryContainer = PharmaDarkOnSecondaryContainer,
    tertiary = PharmaDarkTertiary,
    onTertiary = PharmaDarkOnTertiary,
    tertiaryContainer = PharmaDarkTertiaryContainer,
    onTertiaryContainer = PharmaDarkOnTertiaryContainer,
    background = PharmaDarkBackground,
    onBackground = PharmaDarkOnBackground,
    surface = PharmaDarkSurface,
    onSurface = PharmaDarkOnSurface,
    surfaceVariant = PharmaDarkSurfaceVariant,
    onSurfaceVariant = PharmaDarkOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = PharmaPrimary,
    onPrimary = PharmaOnPrimary,
    primaryContainer = PharmaPrimaryContainer,
    onPrimaryContainer = PharmaOnPrimaryContainer,
    secondary = PharmaSecondary,
    onSecondary = PharmaOnSecondary,
    secondaryContainer = PharmaSecondaryContainer,
    onSecondaryContainer = PharmaOnSecondaryContainer,
    tertiary = PharmaTertiary,
    onTertiary = PharmaOnTertiary,
    tertiaryContainer = PharmaTertiaryContainer,
    onTertiaryContainer = PharmaOnTertiaryContainer,
    background = PharmaBackground,
    onBackground = PharmaOnBackground,
    surface = PharmaSurface,
    onSurface = PharmaOnSurface,
    surfaceVariant = PharmaSurfaceVariant,
    onSurfaceVariant = PharmaOnSurfaceVariant
)

@Composable
fun SistemaFarmaciaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Se establece en false por defecto para dar prioridad a la paleta épica personalizada
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
