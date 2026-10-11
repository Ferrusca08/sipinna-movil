package com.sipinna.atizapan.reportes.data.repository

import com.sipinna.atizapan.reportes.data.remote.ApiService
import com.sipinna.atizapan.reportes.data.remote.dto.ContactoDto
import com.sipinna.atizapan.reportes.data.remote.dto.CrearReporteRequest
import com.sipinna.atizapan.reportes.data.remote.dto.UbicacionDto
import com.sipinna.atizapan.reportes.domain.model.AccionPublica
import com.sipinna.atizapan.reportes.domain.model.CategoriaTrabajoInfantil
import com.sipinna.atizapan.reportes.domain.model.EstatusConsultado
import com.sipinna.atizapan.reportes.domain.model.EstatusReporte
import com.sipinna.atizapan.reportes.domain.model.EtapaLineaTiempo
import com.sipinna.atizapan.reportes.domain.model.Reporte
import com.sipinna.atizapan.reportes.domain.model.ReporteCreado
import com.sipinna.atizapan.reportes.domain.model.UrlEvidencia
import com.sipinna.atizapan.reportes.domain.repository.ReporteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReporteRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val io: CoroutineDispatcher,
) : ReporteRepository {

    override suspend fun crear(reporte: Reporte): Result<ReporteCreado> = withContext(io) {
        runCatching {
            val respuesta = api.crearReporte(
                CrearReporteRequest(
                    descripcion = reporte.descripcion.trim(),
                    categoriaTrabajo = reporte.categoriaTrabajo.valor,
                    edadEstimada = reporte.edadEstimada.valor,
                    cantidadNNA = reporte.cantidadNNA,
                    ubicacion = UbicacionDto(
                        lat = reporte.ubicacion.lat,
                        lng = reporte.ubicacion.lng,
                        direccion = reporte.ubicacion.direccion,
                        cp = reporte.ubicacion.cp,
                        colonia = reporte.ubicacion.colonia,
                    ),
                    lugarEspecifico = reporte.lugarEspecifico.valor,
                    contactoReportante = reporte.contactoReportante
                        ?.takeIf { it.tieneAlgo }
                        ?.let {
                            ContactoDto(
                                nombre = it.nombre.trim(),
                                telefono = it.telefono.filter(Char::isDigit),
                                email = it.email.trim(),
                            )
                        },
                    cantidadEvidencia = reporte.cantidadEvidencia,
                ),
            )

            ReporteCreado(
                reporteId = respuesta.reporteId,
                fechaCreacion = respuesta.fechaCreacion,
                estatus = respuesta.estatus,
                prioridad = respuesta.prioridad,
                urlsEvidencia = respuesta.urlsEvidencia.map {
                    UrlEvidencia(clave = it.clave, url = it.url, tipoContenido = it.tipoContenido)
                },
            )
        }
    }

    override suspend fun consultarEstatus(reporteId: String): Result<EstatusConsultado> =
        withContext(io) {
            runCatching {
                val respuesta = api.consultarEstatus(reporteId.trim().uppercase())

                EstatusConsultado(
                    reporteId = respuesta.reporteId,
                    estatus = EstatusReporte.desdeValor(respuesta.estatus),
                    descripcionEstatus = respuesta.descripcionEstatus,
                    fechaCreacion = respuesta.fechaCreacion,
                    actualizadoEn = respuesta.actualizadoEn,
                    categoria = CategoriaTrabajoInfantil.desdeValor(respuesta.categoriaTrabajo),
                    descartado = respuesta.descartado,
                    lineaTiempo = respuesta.lineaTiempo.map {
                        EtapaLineaTiempo(
                            etapa = EstatusReporte.desdeValor(it.etapa),
                            alcanzada = it.alcanzada,
                            actual = it.actual,
                        )
                    },
                    acciones = respuesta.acciones.map {
                        AccionPublica(fecha = it.fecha, descripcion = it.descripcion)
                    },
                )
            }
        }
}
