package com.sipinna.atizapan.reportes.domain.repository

import com.sipinna.atizapan.reportes.domain.model.EstatusConsultado
import com.sipinna.atizapan.reportes.domain.model.Reporte
import com.sipinna.atizapan.reportes.domain.model.ReporteCreado

interface ReporteRepository {

    /** Envía el reporte al backend y devuelve el token de seguimiento. */
    suspend fun crear(reporte: Reporte): Result<ReporteCreado>

    /** Consulta pública del avance de un caso por su token. */
    suspend fun consultarEstatus(reporteId: String): Result<EstatusConsultado>
}
