package com.sipinna.atizapan.reportes.model.remote

import com.sipinna.atizapan.reportes.model.remote.dto.ConfirmarEvidenciaRequest
import com.sipinna.atizapan.reportes.model.remote.dto.ConfirmarEvidenciaResponse
import com.sipinna.atizapan.reportes.model.remote.dto.ConsultarEstatusResponse
import com.sipinna.atizapan.reportes.model.remote.dto.CrearReporteRequest
import com.sipinna.atizapan.reportes.model.remote.dto.CrearReporteResponse
import com.sipinna.atizapan.reportes.model.remote.dto.PresignedUrlResponse
import com.sipinna.atizapan.reportes.model.remote.dto.SolicitarEvidenciaRequest
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url
import retrofit2.http.GET

/**
 * Endpoints públicos del API. La app ciudadana nunca toca las rutas /admin,
 * por eso no maneja tokens de Cognito.
 */
interface ApiService {

    @POST("reportes")
    suspend fun crearReporte(@Body cuerpo: CrearReporteRequest): CrearReporteResponse

    @GET("reportes/{id}/estatus")
    suspend fun consultarEstatus(@Path("id") reporteId: String): ConsultarEstatusResponse

    @POST("reportes/{id}/evidencia")
    suspend fun solicitarUrlsEvidencia(
        @Path("id") reporteId: String,
        @Body cuerpo: SolicitarEvidenciaRequest,
    ): PresignedUrlResponse

    @POST("reportes/{id}/evidencia")
    suspend fun confirmarEvidencia(
        @Path("id") reporteId: String,
        @Body cuerpo: ConfirmarEvidenciaRequest,
    ): ConfirmarEvidenciaResponse

    /**
     * Sube el archivo directo a S3 con la presigned URL. Va por fuera del host
     * base del API, de ahí la @Url absoluta.
     */
    @PUT
    suspend fun subirEvidenciaAS3(
        @Url url: String,
        @Header("Content-Type") tipoContenido: String,
        @Body archivo: RequestBody,
    ): retrofit2.Response<Unit>
}
