package com.sipinna.atizapan.reportes.model

/**
 * Representa la sesión del usuario ciudadano autenticado en la app.
 */
data class Usuario(
    val id: String,
    val email: String,
    val nombre: String,
    val token: String,
)
