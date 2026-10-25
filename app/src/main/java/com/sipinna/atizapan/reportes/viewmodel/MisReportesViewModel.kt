package com.sipinna.atizapan.reportes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipinna.atizapan.reportes.model.EstatusConsultado
import com.sipinna.atizapan.reportes.model.TokenSeguimiento
import com.sipinna.atizapan.reportes.model.Usuario
import com.sipinna.atizapan.reportes.model.repository.AuthRepository
import com.sipinna.atizapan.reportes.model.repository.PreferenciasRepository
import com.sipinna.atizapan.reportes.model.repository.ReporteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemReporteUsuario(
    val token: TokenSeguimiento,
    val estatusDetalle: EstatusConsultado? = null,
    val cargandoDetalle: Boolean = false,
)

data class MisReportesUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val reporteSeleccionado: EstatusConsultado? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MisReportesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencias: PreferenciasRepository,
    private val reporteRepository: ReporteRepository,
) : ViewModel() {

    private val _estado = MutableStateFlow(MisReportesUiState())
    val estado: StateFlow<MisReportesUiState> = _estado.asStateFlow()

    val usuarioActual: StateFlow<Usuario?> = authRepository.usuarioActual.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    /** Reportes creados exclusivamente por este usuario. */
    val misReportes: StateFlow<List<TokenSeguimiento>> = usuarioActual
        .flatMapLatest { usuario ->
            if (usuario != null) {
                preferencias.tokensDeUsuario(usuario.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun consultarDetalle(reporteId: String) {
        if (_estado.value.cargando) return

        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            reporteRepository.consultarEstatus(reporteId)
                .onSuccess { detalle ->
                    _estado.update { it.copy(cargando = false, reporteSeleccionado = detalle) }
                }
                .onFailure { error ->
                    _estado.update {
                        it.copy(
                            cargando = false,
                            error = error.message ?: "No se pudo obtener el detalle del reporte.",
                        )
                    }
                }
        }
    }

    fun cerrarDetalle() {
        _estado.update { it.copy(reporteSeleccionado = null, error = null) }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            authRepository.cerrarSesion()
            _estado.update { MisReportesUiState() }
        }
    }
}
