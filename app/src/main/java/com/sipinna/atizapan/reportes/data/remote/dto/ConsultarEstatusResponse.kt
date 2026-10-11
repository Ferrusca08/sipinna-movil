package com.sipinna.atizapan.reportes.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EtapaDto(
    val etapa: String,
    val alcanzada: Boolean = false,
    val actual: Boolean = false,
)

@JsonClass(generateAdapter = true)
data class AccionDto(
    val fecha: String = "",
    val descripcion: String = "",
)

@JsonClass(generateAdapter = true)
data class ConsultarEstatusResponse(
    val reporteId: String,
    val estatus: String,
    val descripcionEstatus: String = "",
    val fechaCreacion: String = "",
    val actualizadoEn: String = "",
    val categoriaTrabajo: String? = null,
    val zonaGeografica: String = "",
    val descartado: Boolean = false,
    val lineaTiempo: List<EtapaDto> = emptyList(),
    val acciones: List<AccionDto> = emptyList(),
)
