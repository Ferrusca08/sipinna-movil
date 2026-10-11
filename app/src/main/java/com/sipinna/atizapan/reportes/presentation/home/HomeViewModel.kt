package com.sipinna.atizapan.reportes.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipinna.atizapan.reportes.data.local.PreferenciasDataStore
import com.sipinna.atizapan.reportes.data.local.TokenGuardado
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * La pantalla de inicio no consulta el backend. Solo necesita saber si esta
 * persona ya reportó antes, para poder sugerirle la consulta de sus casos.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    preferencias: PreferenciasDataStore,
) : ViewModel() {

    val misReportes: StateFlow<List<TokenGuardado>> = preferencias.tokens.stateIn(
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
}
