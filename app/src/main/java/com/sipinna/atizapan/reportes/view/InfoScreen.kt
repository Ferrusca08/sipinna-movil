package com.sipinna.atizapan.reportes.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sipinna.atizapan.reportes.model.CategoriaTrabajoInfantil
import com.sipinna.atizapan.reportes.view.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.view.theme.FondoGris
import com.sipinna.atizapan.reportes.view.theme.RojoAlerta
import com.sipinna.atizapan.reportes.view.theme.TextoSecundario
import com.sipinna.atizapan.reportes.view.theme.VerdeExito

private val SENALES = listOf(
    "Niñas, niños y adolescentes trabajando en horario escolar.",
    "Niñas, niños y adolescentes solos en la vía pública durante la noche.",
    "Niñas, niños y adolescentes realizando esfuerzo físico o manejando herramienta peligrosa.",
    "Niñas, niños y adolescentes acompañados de un adulto que los vigila y recoge el dinero.",
    "Niñas, niños y adolescentes con signos de cansancio extremo, desnutrición o lesiones.",
    "Niñas, niños y adolescentes expuestos a sustancias químicas, humo o tráfico vehicular.",
)

private val SI_HACER = listOf(
    "Observa a distancia y toma nota de la hora y el lugar exacto.",
    "Reporta con el mayor detalle posible.",
    "Llama al 911 si hay riesgo inmediato para la niña, niño o adolescente.",
    "Si tomas foto, evita captar el rostro de la niña, niño o adolescente.",
)

private val NO_HACER = listOf(
    "No confrontes al adulto que acompaña a la niña, niño o adolescente.",
    "No ofrezcas dinero: refuerza la permanencia de la niña, niño o adolescente en la calle.",
    "No intentes llevarte ni retener a la niña, niño o adolescente.",
    "No difundas fotos de la niña, niño o adolescente en redes sociales.",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(alRegresar: () -> Unit) {
    Scaffold(
        containerColor = FondoGris,
        topBar = {
            TopAppBar(
                title = { Text("¿Qué es trabajo infantil?") },
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulInstitucional,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        },
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Bloque(titulo = "Definición") {
                Text(
                    text = "Es toda actividad económica realizada por personas menores de " +
                        "15 años, o por adolescentes de 15 a 17 años en trabajos peligrosos o " +
                        "que interfieren con su educación, salud o desarrollo. Está prohibido " +
                        "por el artículo 123 de la Constitución y por la Ley Federal del Trabajo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                )
            }

            Spacer(Modifier.height(16.dp))

            Bloque(titulo = "Tipos que puedes reportar") {
                CategoriaTrabajoInfantil.entries.forEachIndexed { indice, categoria ->
                    if (indice > 0) {
                        HorizontalDivider(Modifier.padding(vertical = 10.dp))
                    }
                    Text(
                        text = categoria.etiqueta,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (categoria.esUrgente) RojoAlerta else AzulInstitucional,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = categoria.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Bloque(titulo = "Cómo identificar una situación") {
                SENALES.forEach { senal ->
                    Text(
                        text = "• $senal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                        modifier = Modifier.padding(vertical = 3.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Bloque(titulo = "Qué SÍ hacer", colorTitulo = VerdeExito) {
                SI_HACER.forEach { accion ->
                    Text(
                        text = "• $accion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                        modifier = Modifier.padding(vertical = 3.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Bloque(titulo = "Qué NO hacer", colorTitulo = RojoAlerta) {
                NO_HACER.forEach { accion ->
                    Text(
                        text = "• $accion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                        modifier = Modifier.padding(vertical = 3.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Bloque(titulo = "Contacto") {
                Text(
                    text = "SIPINNA Atizapán de Zaragoza\n" +
                        "Sistema de Protección Integral de Niñas, Niños y Adolescentes\n\n" +
                        "Emergencias: 911\n" +
                        "Línea de la Infancia: 800 911 2511\n" +
                        "DIF Atizapán: acude a la oficina más cercana",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Bloque(
    titulo: String,
    colorTitulo: Color = AzulInstitucional,
    contenido: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                color = colorTitulo,
            )
            Spacer(Modifier.height(10.dp))
            contenido()
        }
    }
}
