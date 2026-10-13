package com.sipinna.atizapan.reportes.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipinna.atizapan.reportes.model.usecase.ProgresoSubida
import com.sipinna.atizapan.reportes.model.usecase.SubirEvidenciaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MAX_FOTOS = 5

data class EvidenciaUiState(
    val fotos: List<Uri> = emptyList(),
    val subiendo: Boolean = false,
    val progreso: ProgresoSubida? = null,
    val subidasConfirmadas: Int = 0,
    val terminado: Boolean = false,
    val error: String? = null,
) {
    val puedeAgregarMas: Boolean
        get() = fotos.size < MAX_FOTOS && !subiendo

    val espaciosLibres: Int
        get() = MAX_FOTOS - fotos.size
}

@HiltViewModel
class EvidenciaViewModel @Inject constructor(
    private val subirEvidencia: SubirEvidenciaUseCase,
    estadoGuardado: SavedStateHandle,
) : ViewModel() {

    val reporteId: String = estadoGuardado.get<String>("reporteId").orEmpty()

    private val _estado = MutableStateFlow(EvidenciaUiState())
    val estado: StateFlow<EvidenciaUiState> = _estado.asStateFlow()

    fun agregarFotos(nuevas: List<Uri>) {
        _estado.update { actual ->
            // Se recortan las que excedan el máximo en lugar de rechazar todo el lote.
            val combinadas = (actual.fotos + nuevas).distinct().take(MAX_FOTOS)
            actual.copy(fotos = combinadas, error = null)
        }
    }

    fun agregarFoto(uri: Uri) = agregarFotos(listOf(uri))

    fun quitarFoto(uri: Uri) {
        _estado.update { it.copy(fotos = it.fotos - uri) }
    }

    fun descartarError() {
        _estado.update { it.copy(error = null) }
    }

    fun subir() {
        val actual = _estado.value
        if (actual.subiendo || actual.fotos.isEmpty() || reporteId.isBlank()) return

        _estado.update {
            it.copy(subiendo = true, error = null, progreso = ProgresoSubida(0, it.fotos.size))
        }

        viewModelScope.launch {
            subirEvidencia(
                reporteId = reporteId,
                fotos = actual.fotos,
                alProgresar = { progreso -> _estado.update { it.copy(progreso = progreso) } },
            )
                .onSuccess { total ->
                    _estado.update {
                        it.copy(subiendo = false, subidasConfirmadas = total, terminado = true)
                    }
                }
                .onFailure { fallo ->
                    _estado.update {
                        it.copy(
                            subiendo = false,
                            error = fallo.message ?: "No se pudieron subir las fotos.",
                        )
                    }
                }
        }
    }
}
