package com.sipinna.atizapan.reportes.view

import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sipinna.atizapan.reportes.view.components.IndicadorProgreso
import com.sipinna.atizapan.reportes.view.components.PasoCategoria
import com.sipinna.atizapan.reportes.view.components.PasoContacto
import com.sipinna.atizapan.reportes.view.components.PasoDescripcion
import com.sipinna.atizapan.reportes.view.components.PasoUbicacion
import com.sipinna.atizapan.reportes.viewmodel.TOTAL_PASOS
import com.sipinna.atizapan.reportes.view.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.view.theme.FondoGris
import com.sipinna.atizapan.reportes.view.theme.RojoAlerta
import com.sipinna.atizapan.reportes.model.Reporte
import com.sipinna.atizapan.reportes.viewmodel.NuevoReporteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoReporteScreen(
    alRegresar: () -> Unit,
    alEnviar: (String) -> Unit,
    viewModel: NuevoReporteViewModel = hiltViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val pager = rememberPagerState(pageCount = { TOTAL_PASOS })

    // El pager y el estado del ViewModel se mantienen sincronizados en ambos
    // sentidos: el usuario puede deslizar o usar los botones.
    LaunchedEffect(estado.pasoActual) {
        if (pager.currentPage != estado.pasoActual) pager.animateScrollToPage(estado.pasoActual)
    }
    LaunchedEffect(pager.currentPage) {
        if (pager.currentPage != estado.pasoActual) viewModel.irAPaso(pager.currentPage)
    }

    LaunchedEffect(estado.reporteIdCreado) {
        estado.reporteIdCreado?.let(alEnviar)
    }

    Scaffold(
        containerColor = FondoGris,
        topBar = {
            TopAppBar(
                title = { Text("Nuevo reporte") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (estado.pasoActual == 0) alRegresar() else viewModel.pasoAnterior()
                    }) {
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
                .background(FondoGris),
        ) {
            IndicadorProgreso(
                pasoActual = estado.pasoActual,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            )

            HorizontalPager(
                state = pager,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                // Se avanza con los botones para poder validar cada paso.
                userScrollEnabled = false,
            ) { pagina ->
                Box(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                    when (pagina) {
                        0 -> PasoCategoria(
                            seleccionada = estado.categoria,
                            alSeleccionar = viewModel::seleccionarCategoria,
                        )

                        1 -> PasoDescripcion(
                            descripcion = estado.descripcion,
                            edadSeleccionada = estado.edadEstimada,
                            cantidadNNA = estado.cantidadNNA,
                            alCambiarDescripcion = viewModel::cambiarDescripcion,
                            alSeleccionarEdad = viewModel::seleccionarEdad,
                            alCambiarCantidad = viewModel::cambiarCantidadNNA,
                        )

                        2 -> PasoUbicacion(
                            ubicacion = estado.ubicacion,
                            lugarSeleccionado = estado.lugarEspecifico,
                            obteniendo = estado.obteniendoUbicacion,
                            errorUbicacion = estado.errorUbicacion,
                            alPedirUbicacion = viewModel::solicitarUbicacionActual,
                            alCancelarBusqueda = viewModel::cancelarBusquedaUbicacion,
                            alMoverMarcador = viewModel::moverMarcador,
                            alSeleccionarLugar = viewModel::seleccionarLugar,
                            alCapturarDireccionManual = viewModel::capturarDireccionManual,
                        )

                        3 -> PasoContacto(
                            esAnonimo = estado.esAnonimo,
                            contacto = estado.contacto,
                            alCambiarAnonimo = viewModel::cambiarAnonimo,
                            alCambiarContacto = viewModel::cambiarContacto,
                        )
                    }
                }
            }

            // --- Barra inferior de navegación ---------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (estado.pasoActual > 0) {
                    OutlinedButton(
                        onClick = viewModel::pasoAnterior,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !estado.enviando,
                    ) {
                        Text("Atrás")
                    }
                }

                val esUltimo = estado.pasoActual == TOTAL_PASOS - 1

                Button(
                    onClick = {
                        if (esUltimo) viewModel.enviar() else viewModel.siguientePaso()
                    },
                    modifier = Modifier.weight(if (estado.pasoActual > 0) 1.4f else 1f).height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    enabled = estado.pasoCompleto(estado.pasoActual) && !estado.enviando,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (esUltimo) RojoAlerta else AzulInstitucional,
                    ),
                ) {
                    if (estado.enviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.size(10.dp))
                        Text("Enviando…")
                    } else {
                        Text(if (esUltimo) "Enviar Reporte" else "Siguiente")
                    }
                }
            }
        }
    }

    estado.error?.let { mensaje ->
        AlertDialog(
            onDismissRequest = viewModel::descartarError,
            title = { Text("No se pudo enviar") },
            text = {
                Column {
                    Text(mensaje)
                    estado.erroresPorCampo.forEach { (campo, detalle) ->
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "• $campo: $detalle",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::descartarError) { Text("Entendido") }
            },
        )
    }
}
