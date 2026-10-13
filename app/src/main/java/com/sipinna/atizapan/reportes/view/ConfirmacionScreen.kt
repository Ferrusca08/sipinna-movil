package com.sipinna.atizapan.reportes.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sipinna.atizapan.reportes.view.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.view.theme.FondoGris
import com.sipinna.atizapan.reportes.view.theme.TextoSecundario
import com.sipinna.atizapan.reportes.view.theme.VerdeExito
import kotlinx.coroutines.launch
import com.sipinna.atizapan.reportes.model.Reporte

@Composable
fun ConfirmacionScreen(
    reporteId: String,
    alAgregarFotos: () -> Unit,
    alVolverAlInicio: () -> Unit,
) {
    val contexto = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()

    Scaffold(
        containerColor = FondoGris,
        snackbarHost = { SnackbarHost(snackbar) },
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .size(96.dp)
                    .background(VerdeExito, CircleShape),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "¡Reporte enviado con éxito!",
                style = MaterialTheme.typography.headlineSmall,
                color = AzulInstitucional,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Gracias por reportar. SIPINNA Atizapán dará seguimiento al caso.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(28.dp))

            // --- Token de seguimiento ------------------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "TU CÓDIGO DE SEGUIMIENTO",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoSecundario,
                    )

                    Spacer(Modifier.height(12.dp))

                    // Se parte en bloques de 6 para poder leerlo o dictarlo.
                    Text(
                        text = reporteId.chunked(6).joinToString(" "),
                        style = MaterialTheme.typography.titleLarge,
                        color = AzulInstitucional,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                copiarAlPortapapeles(contexto, reporteId)
                                alcance.launch { snackbar.showSnackbar("Código copiado") }
                            },
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Icon(Icons.Filled.ContentCopy, null, Modifier.size(18.dp))
                            Spacer(Modifier.size(8.dp))
                            Text("Copiar")
                        }

                        OutlinedButton(
                            onClick = { compartir(contexto, reporteId) },
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Icon(Icons.Filled.Share, null, Modifier.size(18.dp))
                            Spacer(Modifier.size(8.dp))
                            Text("Compartir")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Guarda este código para consultar el estatus de tu reporte. " +
                    "También quedó guardado en esta app.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = alAgregarFotos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulInstitucional),
            ) {
                Icon(Icons.Filled.PhotoCamera, null, Modifier.size(20.dp))
                Spacer(Modifier.size(10.dp))
                Text("Agregar fotos (opcional)")
            }

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = alVolverAlInicio, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al inicio", color = AzulInstitucional)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun copiarAlPortapapeles(contexto: Context, texto: String) {
    val portapapeles = contexto.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    portapapeles?.setPrimaryClip(ClipData.newPlainText("Código SIPINNA", texto))
}

private fun compartir(contexto: Context, reporteId: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Código de seguimiento de mi reporte a SIPINNA Atizapán: $reporteId",
        )
    }
    runCatching {
        contexto.startActivity(Intent.createChooser(intent, "Compartir código"))
    }
}
