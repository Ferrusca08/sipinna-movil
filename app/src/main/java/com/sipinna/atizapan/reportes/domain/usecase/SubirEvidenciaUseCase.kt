package com.sipinna.atizapan.reportes.domain.usecase

import android.net.Uri
import com.sipinna.atizapan.reportes.domain.repository.EvidenciaRepository
import javax.inject.Inject

/** Progreso de la subida, para pintar la barra de la pantalla de evidencia. */
data class ProgresoSubida(
    val subidas: Int,
    val total: Int,
    val fallidas: Int = 0,
) {
    val porcentaje: Float
        get() = if (total == 0) 0f else (subidas + fallidas).toFloat() / total.toFloat()

    val terminado: Boolean
        get() = subidas + fallidas >= total
}

class SubirEvidenciaUseCase @Inject constructor(
    private val repositorio: EvidenciaRepository,
) {
    /**
     * Pide las URLs, sube las fotos una por una y confirma al final.
     * Sube en serie a propósito: en red móvil débil varias subidas en paralelo
     * se estorban entre sí y terminan fallando todas.
     */
    suspend operator fun invoke(
        reporteId: String,
        fotos: List<Uri>,
        urlsPrevias: List<com.sipinna.atizapan.reportes.domain.model.UrlEvidencia> = emptyList(),
        alProgresar: (ProgresoSubida) -> Unit = {},
    ): Result<Int> {
        if (fotos.isEmpty()) return Result.success(0)
        if (fotos.size > 5) {
            return Result.failure(IllegalArgumentException("Máximo 5 fotos por reporte."))
        }

        // Se reutilizan las URLs que vinieron al crear el reporte; solo se piden
        // más si hacen falta.
        val destinos = if (urlsPrevias.size >= fotos.size) {
            urlsPrevias.take(fotos.size)
        } else {
            repositorio.solicitarUrls(reporteId, fotos.size).getOrElse {
                return Result.failure(it)
            }
        }

        val exitosas = mutableListOf<String>()
        var fallidas = 0

        fotos.forEachIndexed { indice, uri ->
            val destino = destinos.getOrNull(indice)
            if (destino == null) {
                fallidas++
            } else {
                repositorio.subir(uri, destino)
                    .onSuccess { exitosas += it }
                    .onFailure { fallidas++ }
            }
            alProgresar(ProgresoSubida(exitosas.size, fotos.size, fallidas))
        }

        if (exitosas.isEmpty()) {
            return Result.failure(IllegalStateException("No se pudo subir ninguna foto."))
        }

        return repositorio.confirmar(reporteId, exitosas)
    }
}
