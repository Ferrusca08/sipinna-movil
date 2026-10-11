package com.sipinna.atizapan.reportes.presentation.reporte.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.sipinna.atizapan.reportes.domain.model.ContactoReportante
import com.sipinna.atizapan.reportes.presentation.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.presentation.theme.AzulSuave
import com.sipinna.atizapan.reportes.presentation.theme.TextoSecundario

/** Lo que gana quien reporta al tener cuenta. Nada de esto altera el reporte. */
private val BENEFICIOS_CUENTA = listOf(
    "Ves todos tus reportes y su avance en un solo lugar.",
    "No necesitas guardar el folio de 26 caracteres para dar seguimiento.",
    "Puedes agregar fotos a un reporte que ya enviaste.",
    "Recuperas tu historial aunque cambies de teléfono.",
)

@Composable
fun PasoContacto(
    esAnonimo: Boolean,
    contacto: ContactoReportante,
    alCambiarAnonimo: (Boolean) -> Unit,
    alCambiarContacto: (ContactoReportante) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "¿Quieres dejar tus datos?",
            style = MaterialTheme.typography.titleLarge,
            color = AzulInstitucional,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "No es obligatorio. Tu reporte se atiende igual si decides " +
                "mantenerte en el anonimato.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
        )

        Spacer(Modifier.height(20.dp))

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
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = if (esAnonimo) "Reporte anónimo" else "Con datos de contacto",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulInstitucional,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (esAnonimo) {
                            "No guardamos ningún dato tuyo."
                        } else {
                            "SIPINNA podrá contactarte si necesita más información."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                    )
                }

                Switch(
                    checked = !esAnonimo,
                    onCheckedChange = { conDatos -> alCambiarAnonimo(!conDatos) },
                    colors = SwitchDefaults.colors(checkedTrackColor = AzulInstitucional),
                )
            }
        }

        // La cuenta no cambia como se atiende el reporte; solo le da a quien
        // reporta una forma de seguirlo sin cargar con el folio. Se explica aqui
        // porque es el momento en que la persona decide cuanto quiere exponerse.
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AzulSuave),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Si inicias sesión",
                    style = MaterialTheme.typography.titleSmall,
                    color = AzulInstitucional,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))

                BENEFICIOS_CUENTA.forEach { beneficio ->
                    Row(Modifier.padding(bottom = 6.dp)) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AzulInstitucional,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = beneficio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario,
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tu cuenta nunca se le muestra a la persona reportada, y " +
                        "puedes seguir enviando reportes anónimos cuando quieras.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario,
                )
            }
        }

        if (!esAnonimo) {
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = contacto.nombre,
                onValueChange = { alCambiarContacto(contacto.copy(nombre = it)) },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = contacto.telefono,
                onValueChange = { nuevo ->
                    alCambiarContacto(contacto.copy(telefono = nuevo.filter(Char::isDigit).take(10)))
                },
                label = { Text("Teléfono (10 dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = contacto.telefono.isNotEmpty() && !contacto.telefonoValido,
                supportingText = {
                    Text("${contacto.telefono.length} / 10")
                },
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = contacto.email,
                onValueChange = { alCambiarContacto(contacto.copy(email = it.trim())) },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = contacto.email.isNotEmpty() && !contacto.emailValido,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Al enviar aceptas que SIPINNA Atizapán use esta información " +
                "exclusivamente para la atención del caso.",
            style = MaterialTheme.typography.labelMedium,
            color = TextoSecundario,
        )

        Spacer(Modifier.height(24.dp))
    }
}
