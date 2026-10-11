package com.sipinna.atizapan.reportes.domain.usecase

import com.sipinna.atizapan.reportes.domain.model.Ubicacion
import com.sipinna.atizapan.reportes.domain.repository.UbicacionRepository
import javax.inject.Inject

class ObtenerUbicacionUseCase @Inject constructor(
    private val repositorio: UbicacionRepository,
) {
    suspend operator fun invoke(): Result<Ubicacion> {
        if (!repositorio.ubicacionHabilitada()) {
            return Result.failure(
                IllegalStateException("Activa la ubicación del teléfono para usar el GPS."),
            )
        }
        return repositorio.obtenerUbicacionActual()
    }

    /** Redescribe un punto que la persona movió a mano en el mapa. */
    suspend fun describir(lat: Double, lng: Double): Ubicacion =
        repositorio.describirUbicacion(lat, lng)
}
