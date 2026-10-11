package com.sipinna.atizapan.reportes.data.remote.interceptors

import com.sipinna.atizapan.reportes.data.remote.dto.ErrorResponse
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/** Error de negocio ya traducido a un mensaje que se puede mostrar tal cual. */
class ApiException(
    val codigo: Int,
    override val message: String,
    val erroresDeCampo: Map<String, String> = emptyMap(),
) : IOException(message)

class SinConexionException : IOException("Sin conexión. Verifica tu red.")

class TiempoAgotadoException : IOException("El servidor tardó demasiado en responder.")

/**
 * Convierte las respuestas de error del backend en excepciones con el mensaje
 * en español que ya manda la Lambda, en vez de dejar que Retrofit lance un
 * HttpException genérico que la UI no puede mostrar.
 */
class ErrorInterceptor(private val moshi: Moshi) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val respuesta = try {
            chain.proceed(chain.request())
        } catch (e: UnknownHostException) {
            throw SinConexionException()
        } catch (e: SocketTimeoutException) {
            throw TiempoAgotadoException()
        }

        if (respuesta.isSuccessful) return respuesta

        // La subida a S3 no responde JSON: se deja pasar con su código.
        val esS3 = respuesta.request.url.host.contains("s3")
        if (esS3) {
            respuesta.close()
            throw ApiException(respuesta.code, "No se pudo subir la foto. Intenta de nuevo.")
        }

        val cuerpo = respuesta.body?.string().orEmpty()
        respuesta.close()

        val parseado = runCatching {
            moshi.adapter(ErrorResponse::class.java).fromJson(cuerpo)
        }.getOrNull()

        val mensaje = parseado?.mensaje?.takeIf { it.isNotBlank() }
            ?: when (respuesta.code) {
                400 -> "Los datos del reporte no son válidos."
                404 -> "No encontramos el reporte con ese código."
                429 -> "Demasiadas solicitudes. Espera un momento."
                in 500..599 -> "El servidor no está disponible. Intenta más tarde."
                else -> "Ocurrió un error inesperado (${respuesta.code})."
            }

        val porCampo = parseado?.detalles.orEmpty().associate { it.campo to it.mensaje }

        throw ApiException(respuesta.code, mensaje, porCampo)
    }
}
