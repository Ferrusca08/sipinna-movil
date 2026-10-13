package com.sipinna.atizapan.reportes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipinna.atizapan.reportes.model.repository.PreferenciasRepository
import com.sipinna.atizapan.reportes.model.TokenSeguimiento
import com.sipinna.atizapan.reportes.model.EstatusConsultado
import com.sipinna.atizapan.reportes.model.usecase.ConsultarEstatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConsultaUiState(
    val token: String = "",
    val consultando: Boolean = false,
    val resultado: EstatusConsultado? = null,
    val error: String? = null,
)

@HiltViewModel
class ConsultarEstatusViewModel @Inject constructor(
    private val consultarEstatus: ConsultarEstatusUseCase,
    preferencias: PreferenciasRepository,
) : ViewModel() {

    private val _estado = MutableStateFlow(ConsultaUiState())
    val estado: StateFlow<ConsultaUiState> = _estado.asStateFlow()

    /** Reportes que se enviaron desde este teléfono. */
    val misReportes: StateFlow<List<TokenSeguimiento>> = preferencias.tokens.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun cambiarToken(valor: String) {
        _estado.update {
            it.copy(token = valor.uppercase().filter { c -> c.isLetterOrDigit() }.take(26), error = null)
        }
    }

    fun consultar(token: String = _estado.value.token) {
        if (_estado.value.consultando) return

        _estado.update { it.copy(token = token, consultando = true, error = null, resultado = null) }

        viewModelScope.launch {
            consultarEstatus(token)
                .onSuccess { resultado ->
                    _estado.update { it.copy(consultando = false, resultado = resultado) }
                }
                .onFailure { fallo ->
                    _estado.update {
                        it.copy(
                            consultando = false,
                            error = fallo.message ?: "No se pudo consultar el reporte.",
                        )
                    }
                }
        }
    }

    fun limpiar() {
        _estado.update { ConsultaUiState() }
    }
}
