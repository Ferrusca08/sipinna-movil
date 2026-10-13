package com.sipinna.atizapan.reportes.model.usecase

import com.sipinna.atizapan.reportes.model.EstatusConsultado
import com.sipinna.atizapan.reportes.model.repository.ReporteRepository
import javax.inject.Inject

class ConsultarEstatusUseCase @Inject constructor(
    private val repositorio: ReporteRepository,
) {
    suspend operator fun invoke(token: String): Result<EstatusConsultado> {
        val limpio = token.trim().uppercase().replace(" ", "")

        // El ULID trae 26 caracteres del alfabeto Crockford (sin I, L, O ni U).
        if (!Regex("^[0-9A-HJKMNP-TV-Z]{26}$").matches(limpio)) {
            return Result.failure(
                IllegalArgumentException("El código de seguimiento debe tener 26 caracteres."),
            )
        }

        return repositorio.consultarEstatus(limpio)
    }
}
