package com.sipinna.atizapan.reportes.view

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sipinna.atizapan.reportes.view.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.view.theme.FondoGris
import com.sipinna.atizapan.reportes.view.theme.RojoAlerta
import com.sipinna.atizapan.reportes.view.theme.TextoSecundario
import com.sipinna.atizapan.reportes.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    alRegresar: () -> Unit,
    alIrALogin: () -> Unit,
    alRegistroExitoso: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = FondoGris,
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(10.dp))

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
                        Icons.Filled.PersonAdd,
                        contentDescription = null,
                        tint = AzulInstitucional,
                        modifier = Modifier.size(48.dp),
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Registro de Ciudadanía",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AzulInstitucional,
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Crea tu cuenta para que todos los reportes que envíes queden guardados y puedas consultar solo los tuyos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = estado.nombre,
                        onValueChange = viewModel::cambiarNombre,
                        label = { Text("Nombre completo") },
                        placeholder = { Text("Nombre y Apellidos") },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = AzulInstitucional)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = estado.email,
                        onValueChange = viewModel::cambiarEmail,
                        label = { Text("Correo electrónico") },
                        placeholder = { Text("ejemplo@correo.com") },
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = null, tint = AzulInstitucional)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = estado.contrasena,
                        onValueChange = viewModel::cambiarContrasena,
                        label = { Text("Contraseña (mínimo 6 caracteres)") },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = AzulInstitucional)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = estado.confirmarContrasena,
                        onValueChange = viewModel::cambiarConfirmarContrasena,
                        label = { Text("Confirmar contraseña") },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = AzulInstitucional)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                    )

                    estado.error?.let { mensaje ->
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = mensaje,
                            color = RojoAlerta,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.registrar(alRegistroExitoso) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !estado.cargando &&
                            estado.nombre.isNotBlank() &&
                            estado.email.isNotBlank() &&
                            estado.contrasena.isNotBlank() &&
                            estado.confirmarContrasena.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulInstitucional),
                    ) {
                        if (estado.cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text("Registrar mi cuenta", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("¿Ya tienes una cuenta?", color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(6.dp))
                TextButton(onClick = alIrALogin) {
                    Text("Iniciar sesión", color = AzulInstitucional, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
