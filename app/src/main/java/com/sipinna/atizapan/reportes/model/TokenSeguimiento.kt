package com.sipinna.atizapan.reportes.model

/**
 * Folio de un reporte enviado desde este teléfono.
 *
 * Vive en el dominio y no en la capa de datos porque la pantalla de inicio y la
 * de consulta lo muestran: si fuera un modelo de almacenamiento, cambiar cómo se
 * guardan los folios obligaría a tocar la interfaz.
 */
data class TokenSeguimiento(
    val reporteId: String,
    val fecha: String,
    val categoria: String,
)
