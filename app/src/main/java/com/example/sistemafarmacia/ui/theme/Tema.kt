package com.example.sistemafarmacia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColoresOscuros = darkColorScheme(
    primary = PrimarioOscuro,
    onPrimary = SobrePrimarioOscuro,
    primaryContainer = ContenedorPrimarioOscuro,
    onPrimaryContainer = SobreContenedorPrimarioOscuro,
    secondary = SecundarioOscuro,
    onSecondary = SobreSecundarioOscuro,
    secondaryContainer = ContenedorSecundarioOscuro,
    onSecondaryContainer = SobreContenedorSecundarioOscuro,
    tertiary = TerciarioOscuro,
    onTertiary = SobreTerciarioOscuro,
    tertiaryContainer = ContenedorTerciarioOscuro,
    onTertiaryContainer = SobreContenedorTerciarioOscuro,
    background = FondoOscuro,
    onBackground = SobreFondoOscuro,
    surface = SuperficieOscura,
    onSurface = SobreSuperficieOscura,
    surfaceVariant = VarianteSuperficieOscura,
    onSurfaceVariant = SobreVarianteSuperficieOscura
)

private val ColoresClaros = lightColorScheme(
    primary = Primario,
    onPrimary = SobrePrimario,
    primaryContainer = ContenedorPrimario,
    onPrimaryContainer = SobreContenedorPrimario,
    secondary = Secundario,
    onSecondary = SobreSecundario,
    secondaryContainer = ContenedorSecundario,
    onSecondaryContainer = SobreContenedorSecundario,
    tertiary = Terciario,
    onTertiary = SobreTerciario,
    tertiaryContainer = ContenedorTerciario,
    onTertiaryContainer = SobreContenedorTerciario,
    background = Fondo,
    onBackground = SobreFondo,
    surface = Superficie,
    onSurface = SobreSuperficie,
    surfaceVariant = VarianteSuperficie,
    onSurfaceVariant = SobreVarianteSuperficie
)

@Composable
fun SistemaFarmaciaTheme(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit
) {
    val paleta = if (temaOscuro) ColoresOscuros else ColoresClaros

    MaterialTheme(
        colorScheme = paleta,
        typography = Tipografia,
        content = contenido
    )
}
