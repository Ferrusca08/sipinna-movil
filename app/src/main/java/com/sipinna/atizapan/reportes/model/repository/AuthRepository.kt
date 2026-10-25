package com.sipinna.atizapan.reportes.model.repository

import com.sipinna.atizapan.reportes.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para autenticación y registro de cuentas ciudadanas.
 */
interface AuthRepository {
    /** Usuario actualmente autenticado, o null si la sesión no está activa. */
    val usuarioActual: Flow<Usuario?>

    suspend fun iniciarSesion(email: String, contrasena: String): Result<Usuario>

    suspend fun registrar(nombre: String, email: String, contrasena: String): Result<Usuario>

    suspend fun cerrarSesion()

    suspend fun obtenerToken(): String?
}
