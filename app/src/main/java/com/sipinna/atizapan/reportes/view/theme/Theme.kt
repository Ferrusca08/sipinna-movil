package com.sipinna.atizapan.reportes.view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Un solo esquema claro, sin variante oscura ni colores dinámicos: la
 * identidad institucional debe verse igual en cualquier teléfono y el contraste
 * está calibrado para uso en exteriores.
 */
private val EsquemaSipinna = lightColorScheme(
    primary = AzulInstitucional,
    onPrimary = Color.White,
    primaryContainer = AzulInstitucionalClaro,
    onPrimaryContainer = Color.White,

    secondary = AzulInstitucionalClaro,
    onSecondary = Color.White,

    error = RojoAlerta,
    onError = Color.White,
    errorContainer = RojoAlertaClaro,
    onErrorContainer = Color.White,

    background = FondoGris,
    onBackground = AzulInstitucionalOscuro,

    surface = SuperficieBlanca,
    onSurface = AzulInstitucionalOscuro,
    surfaceVariant = FondoGris,
    onSurfaceVariant = TextoSecundario,

    outline = BordeGris,
    outlineVariant = BordeGris,
)

@Composable
fun SipinnaTheme(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaSipinna,
        typography = SipinnaTypography,
        content = contenido,
    )
}
