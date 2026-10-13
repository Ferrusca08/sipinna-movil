package com.sipinna.atizapan.reportes.model.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.sipinna.atizapan.reportes.model.remote.ApiService
import com.sipinna.atizapan.reportes.model.remote.dto.ConfirmarEvidenciaRequest
import com.sipinna.atizapan.reportes.model.remote.dto.SolicitarEvidenciaRequest
import com.sipinna.atizapan.reportes.model.UrlEvidencia
import com.sipinna.atizapan.reportes.model.repository.EvidenciaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EvidenciaRepositoryImpl @Inject constructor(
    @ApplicationContext private val contexto: Context,
    private val api: ApiService,
    private val io: CoroutineDispatcher,
) : EvidenciaRepository {

    override suspend fun solicitarUrls(reporteId: String, cantidad: Int): Result<List<UrlEvidencia>> =
        withContext(io) {
            runCatching {
                api.solicitarUrlsEvidencia(
                    reporteId,
                    SolicitarEvidenciaRequest(
                        cantidad = cantidad,
                        tiposContenido = List(cantidad) { "image/jpeg" },
                    ),
                ).urlsEvidencia.map {
                    UrlEvidencia(clave = it.clave, url = it.url, tipoContenido = it.tipoContenido)
                }
            }
        }

    override suspend fun subir(uri: Uri, destino: UrlEvidencia): Result<String> =
        withContext(io) {
            runCatching {
                val bytes = comprimir(uri)

                // Sin encabezados de cifrado: el bucket cifra por omisión y la
                // firma no los incluye. Agregar un encabezado x-amz- que la firma
                // no contemple hace que S3 rechace la subida.
                val respuesta = api.subirEvidenciaAS3(
                    url = destino.url,
                    tipoContenido = "image/jpeg",
                    archivo = bytes.toRequestBody("image/jpeg".toMediaType()),
                )

                if (!respuesta.isSuccessful) {
                    error("S3 respondió ${respuesta.code()} al subir la evidencia.")
                }

                destino.clave
            }
        }

    override suspend fun confirmar(reporteId: String, claves: List<String>): Result<Int> =
        withContext(io) {
            runCatching {
                api.confirmarEvidencia(reporteId, ConfirmarEvidenciaRequest(claves)).total
            }
        }

    /**
     * Reduce la foto antes de subirla: en zonas con red débil una imagen de 8 MP
     * hace fallar la subida, y para identificar una situación basta 1600 px.
     * De paso se descartan los metadatos EXIF, que suelen traer la geolocalización
     * exacta del dispositivo de quien reporta.
     */
    private fun comprimir(uri: Uri, ladoMaximo: Int = 1600, calidad: Int = 80): ByteArray {
        val original = contexto.contentResolver.openInputStream(uri).use { entrada ->
            BitmapFactory.decodeStream(entrada)
        } ?: error("No se pudo leer la imagen seleccionada.")

        val escala = minOf(
            1f,
            ladoMaximo.toFloat() / maxOf(original.width, original.height).toFloat(),
        )

        val ajustado = if (escala < 1f) {
            Bitmap.createScaledBitmap(
                original,
                (original.width * escala).toInt(),
                (original.height * escala).toInt(),
                true,
            )
        } else {
            original
        }

        return ByteArrayOutputStream().use { salida ->
            ajustado.compress(Bitmap.CompressFormat.JPEG, calidad, salida)
            if (ajustado !== original) ajustado.recycle()
            original.recycle()
            salida.toByteArray()
        }
    }
}
