package com.sipinna.atizapan.reportes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipinna.atizapan.reportes.model.TokenSeguimiento
import com.sipinna.atizapan.reportes.model.Usuario
import com.sipinna.atizapan.reportes.model.repository.AuthRepository
import com.sipinna.atizapan.reportes.model.repository.PreferenciasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * La pantalla de inicio gestiona los accesos rápidos a hacer reportes,
 * consultar reportes propios o anónimos y el estado de la cuenta ciudadana.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    preferencias: PreferenciasRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val usuarioActual: StateFlow<Usuario?> = authRepository.usuarioActual.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val misReportes: StateFlow<List<TokenSeguimiento>> = preferencias.tokens.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val tieneReportesPrevios: StateFlow<Boolean> = preferencias.tokens
        .map { it.isNotEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun cerrarSesion() {
        viewModelScope.launch {
            authRepository.cerrarSesion()
        }
    }
}
