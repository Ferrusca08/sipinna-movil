package com.sipinna.atizapan.reportes.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sipinna.atizapan.reportes.view.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.view.theme.AzulSuave
import com.sipinna.atizapan.reportes.view.theme.FondoGris
import com.sipinna.atizapan.reportes.view.theme.RojoAlerta
import com.sipinna.atizapan.reportes.view.theme.TextoSecundario
import com.sipinna.atizapan.reportes.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    alReportar: () -> Unit,
    alConsultar: () -> Unit,
    alVerInfo: () -> Unit,
    alIrAMisReportes: () -> Unit,
    alIrALogin: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val contexto = LocalContext.current
    val usuario by viewModel.usuarioActual.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoGris)
            .verticalScroll(rememberScrollState()),
    ) {
        // --- Encabezado institucional ---------------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulInstitucional)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Fila superior con estado de cuenta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (usuario != null) {
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .clickable(onClick = alIrAMisReportes)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = usuario?.nombre?.ifBlank { "Mi cuenta" } ?: "Mi cuenta",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .clickable(onClick = alIrALogin)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Iniciar Sesión",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "SIPINNA",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Atizapán de Zaragoza",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Reporte de Trabajo Infantil",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Tu reporte puede cambiar la vida de una niña, niño o adolescente. " +
                    "Puedes reportar con tu cuenta o de forma anónima.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
            )
        }

        // --- Banner de cuenta ciudadana ------------------------------------
        if (usuario != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .clickable(onClick = alIrAMisReportes),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AzulSuave),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cuenta activa: ${usuario?.email}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextoSecundario,
                        )
                        Text(
                            text = "Consulta los reportes que has creado",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulInstitucional,
                        )
                    }
                    Button(
                        onClick = alIrAMisReportes,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulInstitucional),
                    ) {
                        Text("Mis Reportes", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // --- Acciones principales -------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Button(
                onClick = alReportar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoAlerta),
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("Hacer un Reporte", style = MaterialTheme.typography.labelLarge)
            }

            Button(
                onClick = alIrAMisReportes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulInstitucional),
            ) {
                Icon(Icons.Filled.ListAlt, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("Mis Reportes (Solo los míos)", style = MaterialTheme.typography.labelLarge)
            }

            OutlinedButton(
                onClick = alConsultar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Filled.Search, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("Consultar por Folio Anónimo")
            }

            TextButton(
                onClick = alVerInfo,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = AzulInstitucional)
                Spacer(Modifier.width(8.dp))
                Text("¿Qué es trabajo infantil?", color = AzulInstitucional)
            }
        }

        Spacer(Modifier.height(8.dp))

        // --- Líneas de emergencia ------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = "¿Es una emergencia?",
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulInstitucional,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Si hay riesgo inmediato para la niña, niño o adolescente, llama de inmediato.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                )

                Spacer(Modifier.height(16.dp))

                BotonTelefono(
                    numero = "911",
                    etiqueta = "Emergencias",
                    alLlamar = { marcar(contexto, "911") },
                )

                Spacer(Modifier.height(10.dp))

                BotonTelefono(
                    numero = "800 911 2511",
                    etiqueta = "Línea de la Infancia",
                    alLlamar = { marcar(contexto, "8009112511") },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- Redes Sociales ------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = null,
                        tint = AzulInstitucional,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Redes Sociales",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulInstitucional,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Sigue las actividades, campañas de prevención y avisos oficiales de SIPINNA Atizapán de Zaragoza.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                )

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = {
                        abrirUrl(
                            contexto,
                            "https://www.instagram.com/sipinnaatizapandezaragoza/",
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE1306C),
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Instagram @sipinnaatizapandezaragoza",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun BotonTelefono(numero: String, etiqueta: String, alLlamar: () -> Unit) {
    OutlinedButton(
        onClick = alLlamar,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
    ) {
        Icon(
            Icons.Filled.Call,
            contentDescription = null,
            tint = RojoAlerta,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(numero, fontWeight = FontWeight.SemiBold, color = AzulInstitucional)
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
        }
    }
}

private fun marcar(contexto: android.content.Context, numero: String) {
    runCatching {
        contexto.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero")))
    }
}

private fun abrirUrl(contexto: android.content.Context, url: String) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        contexto.startActivity(intent)
    }
}
