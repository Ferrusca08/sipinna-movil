package com.sipinna.atizapan.reportes.model.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UrlEvidenciaDto(
    val clave: String,
    val url: String,
    val tipoContenido: String = "image/jpeg",
    val expiraEnSegundos: Int = 900,
)

@JsonClass(generateAdapter = true)
data class CrearReporteResponse(
    val reporteId: String,
    val fechaCreacion: String,
    val estatus: String,
    val prioridad: String,
    val turnoReporte: String = "",
    val zonaGeografica: String = "",
    val urlsEvidencia: List<UrlEvidenciaDto> = emptyList(),
    val mensaje: String = "",
)

@JsonClass(generateAdapter = true)
data class PresignedUrlResponse(
    val reporteId: String,
    val urlsEvidencia: List<UrlEvidenciaDto> = emptyList(),
    val yaSubidas: Int = 0,
    val maximo: Int = 5,
)

@JsonClass(generateAdapter = true)
data class ConfirmarEvidenciaResponse(
    val reporteId: String,
    val evidenciaUrls: List<String> = emptyList(),
    val total: Int = 0,
)

/** Forma del cuerpo de error que devuelven todas las Lambda. */
@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: Boolean = true,
    val mensaje: String = "",
    val detalles: List<DetalleErrorDto>? = null,
)

@JsonClass(generateAdapter = true)
data class DetalleErrorDto(
    val campo: String = "",
    val mensaje: String = "",
)
