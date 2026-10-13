package com.sipinna.atizapan.reportes.view.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.sipinna.atizapan.reportes.model.LugarEspecifico
import com.sipinna.atizapan.reportes.model.Ubicacion
import com.sipinna.atizapan.reportes.view.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.view.theme.RojoAlerta
import com.sipinna.atizapan.reportes.view.theme.TextoSecundario

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PasoUbicacion(
    ubicacion: Ubicacion?,
    lugarSeleccionado: LugarEspecifico?,
    obteniendo: Boolean,
    errorUbicacion: String?,
    alPedirUbicacion: () -> Unit,
    alCancelarBusqueda: () -> Unit,
    alMoverMarcador: (Double, Double) -> Unit,
    alSeleccionarLugar: (LugarEspecifico) -> Unit,
    alCapturarDireccionManual: (String, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contexto = LocalContext.current
    var mostrarDialogoAjustes by remember { mutableStateOf(false) }
    var mostrarCapturaManual by remember { mutableStateOf(false) }

    val permisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { concedidos ->
        val hayPermiso = concedidos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            concedidos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        // Segunda negativa: Android ya no vuelve a mostrar el diálogo del
        // sistema, así que se manda a la persona a Ajustes.
        if (hayPermiso) alPedirUbicacion() else mostrarDialogoAjustes = true
    }

    // Al entrar al paso se pide el permiso una sola vez.
    LaunchedEffect(Unit) {
        if (ubicacion == null) {
            permisos.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    val puntoInicial = LatLng(
        ubicacion?.lat ?: Ubicacion.CENTRO_ATIZAPAN.lat,
        ubicacion?.lng ?: Ubicacion.CENTRO_ATIZAPAN.lng,
    )

    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(puntoInicial, 16f)
    }
    val marcador = rememberMarkerState(position = puntoInicial)

    // Cuando llega una ubicación nueva del GPS se recentra el mapa y el marcador.
    LaunchedEffect(ubicacion?.lat, ubicacion?.lng) {
        val u = ubicacion ?: return@LaunchedEffect
        val destino = LatLng(u.lat, u.lng)
        if (marcador.position != destino) marcador.position = destino
        camara.position = CameraPosition.fromLatLngZoom(destino, 16f)
    }

    // El colector vive mientras viva el marcador, así que no puede cerrar sobre
    // el parámetro: se quedaría con el valor de la primera composición (null) y
    // avisaría de un movimiento en cada recentrado automático, encadenando una
    // geocodificación por cada lectura del GPS.
    val ubicacionVigente by rememberUpdatedState(ubicacion)

    // El usuario arrastró el marcador: se avisa al ViewModel para regeocodificar.
    LaunchedEffect(marcador) {
        snapshotFlow { marcador.position }.collect { posicion ->
            val actual = ubicacionVigente
            if (posicion.latitude != actual?.lat || posicion.longitude != actual.lng) {
                alMoverMarcador(posicion.latitude, posicion.longitude)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "¿Dónde ocurrió?",
            style = MaterialTheme.typography.titleLarge,
            color = AzulInstitucional,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Mueve el marcador para ajustar el punto exacto.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
        )

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp)),
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = camara,
                properties = MapProperties(isMyLocationEnabled = false),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false,
                ),
            ) {
                Marker(
                    state = marcador,
                    draggable = true,
                    title = "Lugar del reporte",
                    snippet = "Mantén presionado y arrastra para ajustar",
                )
            }

            if (obteniendo) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AzulInstitucional)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Buscando tu ubicación…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AzulInstitucional,
                        )
                        // Salida siempre disponible: el mapa ya es utilizable a
                        // mano y nadie debería quedarse atrapado esperando al GPS.
                        TextButton(onClick = alCancelarBusqueda) {
                            Text("Marcar el punto a mano")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                permisos.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            enabled = !obteniendo,
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(8.dp))
            Text("Usar mi ubicación actual")
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(Modifier.padding(14.dp)) {
                // Las coordenadas crudas no le dicen nada a quien reporta y
                // ocupaban el lugar de la referencia, que si es legible. El punto
                // exacto se ve en el mapa, que es donde se ajusta.
                Text(
                    text = "Lugar marcado en el mapa",
                    style = MaterialTheme.typography.titleSmall,
                    color = AzulInstitucional,
                )

                val referencia = listOfNotNull(
                    ubicacion?.direccion?.takeIf { it.isNotBlank() },
                    ubicacion?.colonia?.takeIf { it.isNotBlank() },
                    ubicacion?.cp?.takeIf { it.isNotBlank() }?.let { "C.P. $it" },
                ).joinToString(", ")

                Spacer(Modifier.height(4.dp))
                Text(
                    text = referencia.ifBlank {
                        "Sin dirección automática. Puedes escribirla abajo."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                )
            }
        }

        if (errorUbicacion != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = errorUbicacion,
                color = RojoAlerta,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        // La dirección textual es opcional para el backend, pero escribirla es la
        // vía de escape cuando el geocodificador no responde: siempre a la mano.
        TextButton(onClick = { mostrarCapturaManual = true }) {
            Text("Escribir la dirección a mano")
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Tipo de lugar",
            style = MaterialTheme.typography.titleMedium,
            color = AzulInstitucional,
        )
        Spacer(Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LugarEspecifico.entries.forEach { lugar ->
                FilterChip(
                    selected = lugar == lugarSeleccionado,
                    onClick = { alSeleccionarLugar(lugar) },
                    label = { Text(lugar.etiqueta) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AzulInstitucional,
                        selectedLabelColor = Color.White,
                    ),
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    if (mostrarDialogoAjustes) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAjustes = false },
            title = { Text("Permiso de ubicación") },
            text = {
                Text(
                    "Sin permiso de ubicación no podemos marcar el punto automáticamente. " +
                        "Puedes activarlo en Ajustes o mover el marcador a mano en el mapa.",
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoAjustes = false
                    contexto.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", contexto.packageName, null),
                        ),
                    )
                }) { Text("Abrir Ajustes") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoAjustes = false }) {
                    Text("Lo hago a mano")
                }
            },
        )
    }

    if (mostrarCapturaManual) {
        CapturaManualDireccion(
            inicial = ubicacion,
            alCerrar = { mostrarCapturaManual = false },
            alGuardar = { direccion, colonia, cp ->
                alCapturarDireccionManual(direccion, colonia, cp)
                mostrarCapturaManual = false
            },
        )
    }
}

@Composable
private fun CapturaManualDireccion(
    inicial: Ubicacion?,
    alCerrar: () -> Unit,
    alGuardar: (String, String, String) -> Unit,
) {
    var direccion by remember { mutableStateOf(inicial?.direccion.orEmpty()) }
    var colonia by remember { mutableStateOf(inicial?.colonia.orEmpty()) }
    var cp by remember { mutableStateOf(inicial?.cp.orEmpty()) }

    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text("Dirección del lugar") },
        text = {
            Column {
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Calle y referencia") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                    ),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = colonia,
                    onValueChange = { colonia = it },
                    label = { Text("Colonia") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = cp,
                    onValueChange = { nuevo -> cp = nuevo.filter(Char::isDigit).take(5) },
                    label = { Text("Código postal") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { alGuardar(direccion, colonia, cp) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = alCerrar) { Text("Cancelar") }
        },
    )
}
