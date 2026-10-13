package com.sipinna.atizapan.reportes.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sipinna.atizapan.reportes.model.EdadEstimada
import com.sipinna.atizapan.reportes.view.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.view.theme.RojoAlerta
import com.sipinna.atizapan.reportes.view.theme.TextoSecundario

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PasoDescripcion(
    descripcion: String,
    edadSeleccionada: EdadEstimada,
    cantidadNNA: Int,
    alCambiarDescripcion: (String) -> Unit,
    alSeleccionarEdad: (EdadEstimada) -> Unit,
    alCambiarCantidad: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val longitud = descripcion.trim().length
    val faltanCaracteres = longitud in 1..9

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Cuéntanos qué viste",
            style = MaterialTheme.typography.titleLarge,
            color = AzulInstitucional,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Entre más detalle, más rápido se puede atender el caso.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = alCambiarDescripcion,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            label = { Text("Descripción de la situación") },
            placeholder = {
                Text("Ejemplo: dos niños venden dulces en el crucero desde temprano, sin ningún adulto cerca.")
            },
            isError = faltanCaracteres,
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = if (faltanCaracteres) {
                            "Faltan ${10 - longitud} caracteres"
                        } else {
                            "Mínimo 10 caracteres"
                        },
                        color = if (faltanCaracteres) RojoAlerta else TextoSecundario,
                    )
                    Text("${descripcion.length} / 2000", color = TextoSecundario)
                }
            },
            shape = RoundedCornerShape(12.dp),
        )

        Spacer(Modifier.height(24.dp))

        // --- Edad estimada ---------------------------------------------------
        Text(
            text = "Edad aproximada",
            style = MaterialTheme.typography.titleMedium,
            color = AzulInstitucional,
        )
        Spacer(Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EdadEstimada.entries.forEach { edad ->
                FilterChip(
                    selected = edad == edadSeleccionada,
                    onClick = { alSeleccionarEdad(edad) },
                    label = { Text(edad.etiqueta) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AzulInstitucional,
                        selectedLabelColor = Color.White,
                    ),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- Cantidad de niñas, niños y adolescentes ------------------------
        Text(
            text = "¿Cuántas niñas, niños y adolescentes?",
            style = MaterialTheme.typography.titleMedium,
            color = AzulInstitucional,
        )
        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                FilledIconButton(
                    onClick = { alCambiarCantidad(cantidadNNA - 1) },
                    enabled = cantidadNNA > 1,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = AzulInstitucional,
                    ),
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Restar uno")
                }

                // La etiqueta ya no cabe en una línea: el peso le da el ancho
                // sobrante de la fila para que envuelva sin empujar los botones.
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = cantidadNNA.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        color = AzulInstitucional,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        // En singular la frase no concuerda en plural: una sola
                        // persona es "una niña, niño o adolescente".
                        text = if (cantidadNNA == 1) {
                            "niña, niño o adolescente"
                        } else {
                            "niñas, niños y adolescentes"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoSecundario,
                        textAlign = TextAlign.Center,
                    )
                }

                FilledIconButton(
                    onClick = { alCambiarCantidad(cantidadNNA + 1) },
                    enabled = cantidadNNA < 50,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = AzulInstitucional,
                    ),
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Sumar uno",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
