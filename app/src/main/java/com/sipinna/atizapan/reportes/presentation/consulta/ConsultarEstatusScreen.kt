package com.sipinna.atizapan.reportes.presentation.consulta

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sipinna.atizapan.reportes.domain.model.EstatusConsultado
import com.sipinna.atizapan.reportes.presentation.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.presentation.theme.BordeGris
import com.sipinna.atizapan.reportes.presentation.theme.FondoGris
import com.sipinna.atizapan.reportes.presentation.theme.RojoAlerta
import com.sipinna.atizapan.reportes.presentation.theme.TextoSecundario
import com.sipinna.atizapan.reportes.presentation.theme.VerdeExito

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultarEstatusScreen(
    alRegresar: () -> Unit,
    viewModel: ConsultarEstatusViewModel = hiltViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val misReportes by viewModel.misReportes.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = FondoGris,
        topBar = {
            TopAppBar(
                title = { Text("Consultar reporte") },
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
            OutlinedTextField(
                value = estado.token,
                onValueChange = viewModel::cambiarToken,
                label = { Text("Código de seguimiento") },
                placeholder = { Text("26 caracteres") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                supportingText = { Text("${estado.token.length} / 26") },
                isError = estado.error != null,
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { viewModel.consultar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = estado.token.length == 26 && !estado.consultando,
                colors = ButtonDefaults.buttonColors(containerColor = AzulInstitucional),
            ) {
                if (estado.consultando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(Icons.Filled.Search, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Consultar")
                }
            }

            estado.error?.let { mensaje ->
                Spacer(Modifier.height(12.dp))
                Text(mensaje, color = RojoAlerta, style = MaterialTheme.typography.bodyMedium)
            }

            estado.resultado?.let { resultado ->
                Spacer(Modifier.height(20.dp))
                TarjetaResultado(resultado)
            }

            if (misReportes.isNotEmpty() && estado.resultado == null) {
                Spacer(Modifier.height(28.dp))

                Text(
                    text = "Mis reportes anteriores",
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulInstitucional,
                )
                Spacer(Modifier.height(10.dp))

                misReportes.forEach { guardado ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { viewModel.consultar(guardado.reporteId) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                text = guardado.categoria,
                                style = MaterialTheme.typography.titleMedium,
                                color = AzulInstitucional,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = guardado.reporteId,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextoSecundario,
                            )
                            Text(
                                text = guardado.fecha.take(10),
                                style = MaterialTheme.typography.labelMedium,
                                color = TextoSecundario,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TarjetaResultado(resultado: EstatusConsultado) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = resultado.estatus.etiqueta,
                style = MaterialTheme.typography.headlineSmall,
                color = if (resultado.descartado) TextoSecundario else AzulInstitucional,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = resultado.descripcionEstatus,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Recibido: ${resultado.fechaCreacion.take(10)}",
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario,
            )
            Text(
                text = "Última actualización: ${resultado.actualizadoEn.take(10)}",
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario,
            )

            Spacer(Modifier.height(20.dp))

            // --- Línea de tiempo ------------------------------------------------
            if (!resultado.descartado) {
                resultado.lineaTiempo.forEachIndexed { indice, etapa ->
                    Row(verticalAlignment = Alignment.Top) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(
                                        if (etapa.alcanzada) VerdeExito else BordeGris,
                                        CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (etapa.alcanzada) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                            }

                            if (indice < resultado.lineaTiempo.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(28.dp)
                                        .background(if (etapa.alcanzada) VerdeExito else BordeGris),
                                )
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = etapa.etapa.etiqueta,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (etapa.actual) AzulInstitucional else TextoSecundario,
                            fontWeight = if (etapa.actual) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(top = 1.dp),
                        )
                    }
                }
            }

            if (resultado.acciones.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Acciones realizadas",
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulInstitucional,
                )
                Spacer(Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    resultado.acciones.forEach { accion ->
                        Text(
                            text = "• ${accion.fecha.take(10)} — ${accion.descripcion}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario,
                        )
                    }
                }
            }
        }
    }
}
