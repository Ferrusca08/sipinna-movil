package com.sipinna.atizapan.reportes.presentation.reporte.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sipinna.atizapan.reportes.presentation.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.presentation.theme.BordeGris
import com.sipinna.atizapan.reportes.presentation.theme.TextoSecundario

private val NOMBRES_PASOS = listOf("Tipo", "Detalles", "Ubicación", "Contacto")

/** Barra de 4 segmentos que se llenan conforme avanza el formulario. */
@Composable
fun IndicadorProgreso(
    pasoActual: Int,
    total: Int = 4,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(total) { indice ->
                val color by animateColorAsState(
                    targetValue = if (indice <= pasoActual) AzulInstitucional else BordeGris,
                    label = "colorSegmento$indice",
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(color, RoundedCornerShape(3.dp)),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Paso ${pasoActual + 1} de $total · ${NOMBRES_PASOS.getOrElse(pasoActual) { "" }}",
            style = MaterialTheme.typography.labelMedium,
            color = TextoSecundario,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
    }
}
