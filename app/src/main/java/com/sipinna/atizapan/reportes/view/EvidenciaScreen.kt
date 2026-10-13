package com.sipinna.atizapan.reportes.view

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sipinna.atizapan.reportes.view.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.view.theme.FondoGris
import com.sipinna.atizapan.reportes.view.theme.RojoAlerta
import com.sipinna.atizapan.reportes.view.theme.TextoSecundario
import java.io.File
import com.sipinna.atizapan.reportes.viewmodel.EvidenciaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenciaScreen(
    alTerminar: () -> Unit,
    alRegresar: () -> Unit,
    viewModel: EvidenciaViewModel = hiltViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    // URI destino de la foto que está por tomarse.
    var uriCamara by remember { mutableStateOf<Uri?>(null) }

    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(5),
    ) { seleccionadas -> viewModel.agregarFotos(seleccionadas) }

    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { exitosa ->
        val uri = uriCamara
        if (exitosa && uri != null) viewModel.agregarFoto(uri)
        uriCamara = null
    }

    val permisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { concedido ->
        if (concedido) {
            uriCamara = crearArchivoTemporal(contexto)?.also { camara.launch(it) }
        } else {
            // Sin cámara la persona todavía puede adjuntar desde la galería.
            galeria.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        }
    }

    LaunchedEffect(estado.terminado) {
        if (estado.terminado) alTerminar()
    }

    Scaffold(
        containerColor = FondoGris,
        topBar = {
            TopAppBar(
                title = { Text("Agregar evidencia") },
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
                .padding(20.dp),
        ) {
            Text(
                text = "Fotos del lugar o la situación",
                style = MaterialTheme.typography.titleLarge,
                color = AzulInstitucional,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Puedes subir hasta 5 fotos. Evita tomar el rostro de la niña, niño o adolescente.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { permisoCamara.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.weight(1f),
                    enabled = estado.puedeAgregarMas,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Icon(Icons.Filled.PhotoCamera, null, Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Cámara")
                }

                OutlinedButton(
                    onClick = {
                        galeria.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = estado.puedeAgregarMas,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Icon(Icons.Filled.PhotoLibrary, null, Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Galería")
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(estado.fotos) { uri ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp)),
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Evidencia",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )

                        if (!estado.subiendo) {
                            FilledIconButton(
                                onClick = { viewModel.quitarFoto(uri) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(28.dp),
                                shape = CircleShape,
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = RojoAlerta,
                                ),
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Quitar foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                }
            }

            estado.progreso?.let { progreso ->
                if (estado.subiendo) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Subiendo ${progreso.subidas + progreso.fallidas} de ${progreso.total}…",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoSecundario,
                    )
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progreso.porcentaje },
                        modifier = Modifier.fillMaxWidth(),
                        color = AzulInstitucional,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { if (estado.fotos.isEmpty()) alTerminar() else viewModel.subir() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = !estado.subiendo,
                colors = ButtonDefaults.buttonColors(containerColor = AzulInstitucional),
            ) {
                Text(if (estado.fotos.isEmpty()) "Listo" else "Subir ${estado.fotos.size} foto(s)")
            }
        }
    }

    estado.error?.let { mensaje ->
        AlertDialog(
            onDismissRequest = viewModel::descartarError,
            title = { Text("No se pudo subir") },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = viewModel::descartarError) { Text("Entendido") }
            },
            dismissButton = {
                TextButton(onClick = alTerminar) { Text("Salir sin fotos") }
            },
        )
    }
}

/**
 * Archivo temporal en la caché para la foto de cámara. Se expone por
 * FileProvider porque desde Android 7 no se pueden compartir file:// URIs.
 */
private fun crearArchivoTemporal(contexto: android.content.Context): Uri? = runCatching {
    val carpeta = File(contexto.cacheDir, "evidencia").apply { mkdirs() }
    val archivo = File(carpeta, "foto_${System.currentTimeMillis()}.jpg")

    FileProvider.getUriForFile(contexto, "${contexto.packageName}.fileprovider", archivo)
}.getOrNull()
