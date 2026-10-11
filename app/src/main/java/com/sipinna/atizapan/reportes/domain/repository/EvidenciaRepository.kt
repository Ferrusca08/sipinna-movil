package com.sipinna.atizapan.reportes.domain.repository

import android.net.Uri
import com.sipinna.atizapan.reportes.domain.model.UrlEvidencia

interface EvidenciaRepository {

    /** Pide al backend presigned URLs para subir [cantidad] fotos. */
    suspend fun solicitarUrls(reporteId: String, cantidad: Int): Result<List<UrlEvidencia>>

    /** Sube un archivo local a S3 usando su presigned URL. */
    suspend fun subir(uri: Uri, destino: UrlEvidencia): Result<String>

    /** Registra en el reporte las claves que ya quedaron en S3. */
    suspend fun confirmar(reporteId: String, claves: List<String>): Result<Int>
}
