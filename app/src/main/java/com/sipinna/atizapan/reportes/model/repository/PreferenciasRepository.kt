package com.sipinna.atizapan.reportes.model.repository

import com.sipinna.atizapan.reportes.model.TokenSeguimiento
import com.sipinna.atizapan.reportes.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Memoria local del teléfono: los folios de los reportes enviados,
 * la sesión de usuario y el borrador del formulario en curso.
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

    // --- Sesión de usuario --------------------------------------------------
    val usuarioActual: Flow<Usuario?>

    suspend fun guardarUsuario(usuario: Usuario)

    suspend fun cerrarSesion()

    suspend fun obtenerTokenAuth(): String?

    // --- Reportes por cuenta de usuario -------------------------------------
    fun tokensDeUsuario(usuarioId: String): Flow<List<TokenSeguimiento>>

    suspend fun guardarTokenUsuario(
        usuarioId: String,
        reporteId: String,
        fecha: String,
        categoria: String,
    )

    // --- Registro y autenticación -------------------------------------------
    suspend fun registrarCuenta(nombre: String, email: String, hash: String): Result<Usuario>

    suspend fun autenticarCuenta(email: String, hash: String): Result<Usuario>
}
