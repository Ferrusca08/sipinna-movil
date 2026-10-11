package com.sipinna.atizapan.reportes.domain.repository

import com.sipinna.atizapan.reportes.domain.model.Ubicacion

interface UbicacionRepository {

    /**
     * Obtiene la posición actual con alta precisión.
     * Falla con [TimeoutError] si el GPS no fija en el tiempo dado, para que la
     * pantalla pueda ofrecer captura manual de la dirección.
     */
    suspend fun obtenerUbicacionActual(tiempoLimiteMs: Long = 15_000): Result<Ubicacion>

    /** Traduce coordenadas a calle, colonia y CP mediante el Geocoder del sistema. */
    suspend fun describirUbicacion(lat: Double, lng: Double): Ubicacion

    /** Indica si el sistema tiene el servicio de ubicación encendido. */
    fun ubicacionHabilitada(): Boolean
}

class TimeoutError(mensaje: String) : Exception(mensaje)

class PermisoUbicacionDenegado : Exception("Se requiere permiso de ubicación para continuar.")
