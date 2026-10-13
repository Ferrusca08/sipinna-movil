package com.sipinna.atizapan.reportes.model

/**
 * El servidor rechazó el reporte y señaló qué campos están mal.
 *
 * Es un tipo del dominio y no la excepción de red original: la pantalla necesita
 * saber qué campo resaltar, pero no tiene por qué enterarse de que detrás hay
 * HTTP, Retrofit ni códigos de estado.
 */
class ErrorDeValidacion(
    mensaje: String,
    val erroresPorCampo: Map<String, String> = emptyMap(),
) : Exception(mensaje)
