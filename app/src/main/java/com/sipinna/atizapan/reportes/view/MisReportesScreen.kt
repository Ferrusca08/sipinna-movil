package com.sipinna.atizapan.reportes.view

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sipinna.atizapan.reportes.model.EstatusConsultado
import com.sipinna.atizapan.reportes.view.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.view.theme.AzulSuave
import com.sipinna.atizapan.reportes.view.theme.BordeGris
import com.sipinna.atizapan.reportes.view.theme.FondoGris
import com.sipinna.atizapan.reportes.view.theme.RojoAlerta
import com.sipinna.atizapan.reportes.view.theme.TextoSecundario
import com.sipinna.atizapan.reportes.view.theme.VerdeExito
import com.sipinna.atizapan.reportes.viewmodel.MisReportesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReportesScreen(
    alRegresar: () -> Unit,
    alIrALogin: () -> Unit,
    alIrARegistro: () -> Unit,
    alCrearReporte: () -> Unit,
    viewModel: MisReportesViewModel = hiltViewModel(),
) {
    val usuario by viewModel.usuarioActual.collectAsStateWithLifecycle()
    val reportes by viewModel.misReportes.collectAsStateWithLifecycle()
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = FondoGris,
        topBar = {
            TopAppBar(
                title = { Text("Mis Reportes") },
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (usuario != null) {
                        IconButton(onClick = { viewModel.cerrarSesion() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                tint = Color.White,
                            )
                        }
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
            val sesion = usuario
            if (sesion == null) {
                // --- Pantalla cuando NO hay sesión activa -------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = null,
                            tint = AzulInstitucional,
                            modifier = Modifier.size(54.dp),
                        )

                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = "Consulta personalizada de reportes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AzulInstitucional,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Inicia sesión o crea una cuenta ciudadana para consultar únicamente los reportes que tú has creado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = alIrALogin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AzulInstitucional),
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Iniciar Sesión", fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = alIrARegistro,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text("Crear Cuenta Ciudadana", color = AzulInstitucional)
                        }
                    }
                }
            } else {
                // --- Pantalla con SESIÓN ACTIVA -----------------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulSuave),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = null,
                                tint = AzulInstitucional,
                                modifier = Modifier.size(42.dp),
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = sesion.nombre.ifBlank { "Ciudadano" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulInstitucional,
                                )
                                Text(
                                    text = sesion.email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario,
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.cerrarSesion() },
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Salir", style = MaterialTheme.typography.labelSmall, color = RojoAlerta)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Detalle del reporte seleccionado si se abrió
                estado.reporteSeleccionado?.let { detalle ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Detalle del reporte",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AzulInstitucional,
                                    fontWeight = FontWeight.Bold,
                                )
                                IconButton(onClick = { viewModel.cerrarDetalle() }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            TarjetaDetalle(detalle)
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                }

                estado.error?.let { err ->
                    Text(err, color = RojoAlerta, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                }

                Text(
                    text = "Reportes creados por ti (${reportes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulInstitucional,
                )

                Spacer(Modifier.height(10.dp))

                if (reportes.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Aún no has enviado ningún reporte con esta cuenta.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = alCrearReporte,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AzulInstitucional),
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Hacer un reporte ahora")
                            }
                        }
                    }
                } else {
                    reportes.forEach { tokenSeg ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clickable { viewModel.consultarDetalle(tokenSeg.reporteId) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tokenSeg.categoria,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = AzulInstitucional,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Folio: ${tokenSeg.reporteId}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextoSecundario,
                                    )
                                    Text(
                                        text = "Fecha: ${tokenSeg.fecha.take(10)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextoSecundario,
                                    )
                                }

                                Button(
                                    onClick = { viewModel.consultarDetalle(tokenSeg.reporteId) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AzulInstitucional),
                                ) {
                                    Icon(Icons.Filled.Search, contentDescription = null, Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Ver avance", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TarjetaDetalle(resultado: EstatusConsultado) {
    Column {
        Text(
            text = "Estatus: ${resultado.estatus.etiqueta}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (resultado.descartado) TextoSecundario else AzulInstitucional,
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = resultado.descripcionEstatus,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
        )

        Spacer(Modifier.height(14.dp))

        // Línea de tiempo
        if (!resultado.descartado) {
            resultado.lineaTiempo.forEachIndexed { indice, etapa ->
                Row(verticalAlignment = Alignment.Top) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
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
                                    modifier = Modifier.size(12.dp),
                                )
                            }
                        }

                        if (indice < resultado.lineaTiempo.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(24.dp)
                                    .background(if (etapa.alcanzada) VerdeExito else BordeGris),
                            )
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = etapa.etapa.etiqueta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (etapa.actual) AzulInstitucional else TextoSecundario,
                        fontWeight = if (etapa.actual) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }

        if (resultado.acciones.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Acciones tomadas por SIPINNA:",
                style = MaterialTheme.typography.labelLarge,
                color = AzulInstitucional,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            resultado.acciones.forEach { accion ->
                Text(
                    text = "• ${accion.fecha.take(10)}: ${accion.descripcion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario,
                )
            }
        }
    }
}
