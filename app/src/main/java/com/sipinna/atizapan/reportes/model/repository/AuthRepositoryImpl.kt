package com.sipinna.atizapan.reportes.model.repository

import com.sipinna.atizapan.reportes.model.Usuario
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val preferencias: PreferenciasRepository,
    private val io: CoroutineDispatcher,
) : AuthRepository {

    override val usuarioActual: Flow<Usuario?> = preferencias.usuarioActual

    override suspend fun iniciarSesion(email: String, contrasena: String): Result<Usuario> =
        withContext(io) {
            val correoLimpio = email.trim().lowercase()
            if (!esCorreoValido(correoLimpio)) {
                return@withContext Result.failure(IllegalArgumentException("Ingresa un correo electrónico válido."))
            }
            if (contrasena.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Ingresa tu contraseña."))
            }

            val hash = hashear(contrasena)
            preferencias.autenticarCuenta(correoLimpio, hash)
        }

    override suspend fun registrar(
        nombre: String,
        email: String,
        contrasena: String,
    ): Result<Usuario> = withContext(io) {
        val nombreLimpio = nombre.trim()
        val correoLimpio = email.trim().lowercase()

        if (nombreLimpio.length < 2) {
            return@withContext Result.failure(IllegalArgumentException("Ingresa tu nombre completo."))
        }
        if (!esCorreoValido(correoLimpio)) {
            return@withContext Result.failure(IllegalArgumentException("Ingresa un correo electrónico válido."))
        }
        if (contrasena.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres."))
        }

        val hash = hashear(contrasena)
        preferencias.registrarCuenta(nombreLimpio, correoLimpio, hash)
    }

    override suspend fun cerrarSesion() = withContext(io) {
        preferencias.cerrarSesion()
    }

    override suspend fun obtenerToken(): String? = withContext(io) {
        preferencias.obtenerTokenAuth()
    }

    private fun hashear(texto: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(texto.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun esCorreoValido(correo: String): Boolean =
        correo.contains("@") && correo.contains(".") && correo.length >= 5
}
