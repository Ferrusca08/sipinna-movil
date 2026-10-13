package com.sipinna.atizapan.reportes.model.repository

import com.sipinna.atizapan.reportes.model.remote.ApiService
import com.sipinna.atizapan.reportes.model.remote.dto.ContactoDto
import com.sipinna.atizapan.reportes.model.remote.dto.CrearReporteRequest
import com.sipinna.atizapan.reportes.model.remote.dto.UbicacionDto
import com.sipinna.atizapan.reportes.model.remote.interceptors.ApiException
import com.sipinna.atizapan.reportes.model.ErrorDeValidacion
import com.sipinna.atizapan.reportes.model.AccionPublica
import com.sipinna.atizapan.reportes.model.CategoriaTrabajoInfantil
import com.sipinna.atizapan.reportes.model.EstatusConsultado
import com.sipinna.atizapan.reportes.model.EstatusReporte
import com.sipinna.atizapan.reportes.model.EtapaLineaTiempo
import com.sipinna.atizapan.reportes.model.Reporte
import com.sipinna.atizapan.reportes.model.ReporteCreado
import com.sipinna.atizapan.reportes.model.UrlEvidencia
import com.sipinna.atizapan.reportes.model.repository.ReporteRepository
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
        // La frontera de la capa de datos es el ultimo lugar donde se conoce el
        // transporte. Lo que sale de aqui ya es vocabulario del dominio, para que
        // el ViewModel no tenga que saber que detras hay HTTP.
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
        }.recoverCatching { fallo ->
            // Un rechazo por campos invalidos se vuelve un error del dominio; el
            // resto de fallos viaja tal cual porque su mensaje ya es mostrable.
            throw if (fallo is ApiException && fallo.erroresDeCampo.isNotEmpty()) {
                ErrorDeValidacion(fallo.message, fallo.erroresDeCampo)
            } else {
                fallo
            }
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
