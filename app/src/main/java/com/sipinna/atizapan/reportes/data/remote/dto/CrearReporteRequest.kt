package com.sipinna.atizapan.reportes.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UbicacionDto(
    val lat: Double,
    val lng: Double,
    val direccion: String = "",
    val cp: String = "",
    val colonia: String = "",
)

@JsonClass(generateAdapter = true)
data class ContactoDto(
    val nombre: String = "",
    val telefono: String = "",
    val email: String = "",
)

@JsonClass(generateAdapter = true)
data class CrearReporteRequest(
    val descripcion: String,
    val categoriaTrabajo: String,
    val edadEstimada: String,
    val cantidadNNA: Int,
    val ubicacion: UbicacionDto,
    val lugarEspecifico: String,
    val contactoReportante: ContactoDto? = null,
    val cantidadEvidencia: Int = 0,
)

@JsonClass(generateAdapter = true)
data class SolicitarEvidenciaRequest(
    val cantidad: Int,
    val tiposContenido: List<String> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class ConfirmarEvidenciaRequest(
    val clavesConfirmadas: List<String>,
)
