package com.sipinna.atizapan.reportes.model.repository

import com.sipinna.atizapan.reportes.model.TokenSeguimiento
import kotlinx.coroutines.flow.Flow

/**
 * Memoria local del teléfono: los folios de los reportes enviados y el borrador
 * del formulario en curso.
 *
 * El dominio declara qué necesita; que hoy se resuelva con DataStore y mañana
 * con otra cosa no debería alcanzar a los ViewModels.
 */
interface PreferenciasRepository {

    /** Folios enviados desde este teléfono, del más reciente al más antiguo. */
    val tokens: Flow<List<TokenSeguimiento>>

    suspend fun guardarToken(reporteId: String, fecha: String, categoria: String)

    suspend fun eliminarToken(reporteId: String)

    /** Borrador del formulario, para no perder la captura si la app muere. */
    val borrador: Flow<String?>

    suspend fun guardarBorrador(json: String)

    suspend fun limpiarBorrador()
}
