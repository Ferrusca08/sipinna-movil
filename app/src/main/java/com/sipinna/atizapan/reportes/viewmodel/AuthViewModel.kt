package com.sipinna.atizapan.reportes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipinna.atizapan.reportes.model.Usuario
import com.sipinna.atizapan.reportes.model.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val nombre: String = "",
    val email: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _estado = MutableStateFlow(AuthUiState())
    val estado: StateFlow<AuthUiState> = _estado.asStateFlow()

    val usuarioActual: StateFlow<Usuario?> = authRepository.usuarioActual.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun cambiarNombre(valor: String) = _estado.update { it.copy(nombre = valor, error = null) }
    fun cambiarEmail(valor: String) = _estado.update { it.copy(email = valor, error = null) }
    fun cambiarContrasena(valor: String) = _estado.update { it.copy(contrasena = valor, error = null) }
    fun cambiarConfirmarContrasena(valor: String) =
        _estado.update { it.copy(confirmarContrasena = valor, error = null) }

    fun limpiarEstado() = _estado.update { AuthUiState() }

    fun iniciarSesion(alExito: () -> Unit) {
        val s = _estado.value
        if (s.cargando) return

        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            authRepository.iniciarSesion(s.email, s.contrasena)
                .onSuccess {
                    _estado.update { it.copy(cargando = false, exito = true) }
                    alExito()
                }
                .onFailure { error ->
                    _estado.update {
                        it.copy(
                            cargando = false,
                            error = error.message ?: "No se pudo iniciar sesión. Verifica tus datos.",
                        )
                    }
                }
        }
    }

    fun registrar(alExito: () -> Unit) {
        val s = _estado.value
        if (s.cargando) return

        if (s.contrasena != s.confirmarContrasena) {
            _estado.update { it.copy(error = "Las contraseñas no coinciden.") }
            return
        }

        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            authRepository.registrar(s.nombre, s.email, s.contrasena)
                .onSuccess {
                    _estado.update { it.copy(cargando = false, exito = true) }
                    alExito()
                }
                .onFailure { error ->
                    _estado.update {
                        it.copy(
                            cargando = false,
                            error = error.message ?: "No se pudo crear la cuenta.",
                        )
                    }
                }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            authRepository.cerrarSesion()
            limpiarEstado()
        }
    }
}
