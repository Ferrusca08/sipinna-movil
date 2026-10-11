package com.sipinna.atizapan.reportes.presentation.reporte

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipinna.atizapan.reportes.data.local.PreferenciasDataStore
import com.sipinna.atizapan.reportes.data.remote.interceptors.ApiException
import com.sipinna.atizapan.reportes.domain.model.CategoriaTrabajoInfantil
import com.sipinna.atizapan.reportes.domain.model.ContactoReportante
import com.sipinna.atizapan.reportes.domain.model.EdadEstimada
import com.sipinna.atizapan.reportes.domain.model.LugarEspecifico
import com.sipinna.atizapan.reportes.domain.model.Reporte
import com.sipinna.atizapan.reportes.domain.model.Ubicacion
import com.sipinna.atizapan.reportes.domain.model.UrlEvidencia
import com.sipinna.atizapan.reportes.domain.repository.TimeoutError
import com.sipinna.atizapan.reportes.domain.usecase.CrearReporteUseCase
import com.sipinna.atizapan.reportes.domain.usecase.ObtenerUbicacionUseCase
import com.sipinna.atizapan.reportes.presentation.reporte.state.ReporteFormState
import com.sipinna.atizapan.reportes.presentation.reporte.state.TOTAL_PASOS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class NuevoReporteViewModel @Inject constructor(
    private val crearReporte: CrearReporteUseCase,
    private val obtenerUbicacion: ObtenerUbicacionUseCase,
    private val preferencias: PreferenciasDataStore,
) : ViewModel() {

    private val _estado = MutableStateFlow(ReporteFormState())
    val estado: StateFlow<ReporteFormState> = _estado.asStateFlow()

    /** URLs que devolvió el backend al crear: las reusa la pantalla de evidencia. */
    var urlsEvidencia: List<UrlEvidencia> = emptyList()
        private set

    /** Búsqueda de GPS en curso, para no encimar dos y poder cancelarla. */
    private var trabajoUbicacion: Job? = null

    init {
        recuperarBorrador()
    }

    // --- Paso 1 -------------------------------------------------------------
    fun seleccionarCategoria(categoria: CategoriaTrabajoInfantil) {
        _estado.update { it.copy(categoria = categoria, error = null) }
        guardarBorrador()
    }

    // --- Paso 2 -------------------------------------------------------------
    fun cambiarDescripcion(texto: String) {
        if (texto.length > 2000) return
        _estado.update { it.copy(descripcion = texto, error = null) }
        guardarBorrador()
    }

    fun seleccionarEdad(edad: EdadEstimada) {
        _estado.update { it.copy(edadEstimada = edad) }
        guardarBorrador()
    }

    fun cambiarCantidadNNA(cantidad: Int) {
        _estado.update { it.copy(cantidadNNA = cantidad.coerceIn(1, 50)) }
        guardarBorrador()
    }

    // --- Paso 3 -------------------------------------------------------------
    fun solicitarUbicacionActual() {
        // El candado va sobre el trabajo en curso y no sobre la bandera: si la
        // bandera se quedara encendida, el boton dejaria de responder para siempre.
        if (trabajoUbicacion?.isActive == true) return

        _estado.update { it.copy(obteniendoUbicacion = true, errorUbicacion = null) }

        trabajoUbicacion = viewModelScope.launch {
            // Tope de seguridad sobre los plazos del repositorio: pase lo que pase
            // con el GPS o con el geocodificador, el paso nunca se queda cargando.
            val resultado = withTimeoutOrNull(PLAZO_TOTAL_UBICACION_MS) { obtenerUbicacion() }
                ?: Result.failure(
                    TimeoutError("No se pudo obtener tu ubicación. Ajusta el punto en el mapa."),
                )

            resultado
                .onSuccess { ubicacion ->
                    _estado.update {
                        it.copy(ubicacion = ubicacion, obteniendoUbicacion = false, errorUbicacion = null)
                    }
                    guardarBorrador()
                }
                .onFailure { fallo ->
                    _estado.update {
                        it.copy(
                            obteniendoUbicacion = false,
                            errorUbicacion = fallo.message
                                ?: "No se pudo obtener tu ubicación. Ajusta el punto en el mapa.",
                            // Se deja el mapa en el centro del municipio para que
                            // la persona pueda mover el marcador a mano.
                            ubicacion = it.ubicacion ?: Ubicacion.CENTRO_ATIZAPAN,
                        )
                    }
                }
        }
    }

    /** El usuario arrastró el marcador: se reintenta describir la dirección. */
    fun moverMarcador(lat: Double, lng: Double) {
        _estado.update {
            it.copy(ubicacion = (it.ubicacion ?: Ubicacion.CENTRO_ATIZAPAN).copy(lat = lat, lng = lng))
        }

        viewModelScope.launch {
            val descrita = obtenerUbicacion.describir(lat, lng)
            _estado.update { actual ->
                // Solo se sobrescribe si el marcador no volvió a moverse.
                if (actual.ubicacion?.lat == lat && actual.ubicacion.lng == lng) {
                    actual.copy(ubicacion = descrita)
                } else {
                    actual
                }
            }
            guardarBorrador()
        }
    }

    /** Salida manual: la persona no quiere seguir esperando al GPS. */
    fun cancelarBusquedaUbicacion() {
        trabajoUbicacion?.cancel()
        trabajoUbicacion = null
        _estado.update {
            it.copy(
                obteniendoUbicacion = false,
                ubicacion = it.ubicacion ?: Ubicacion.CENTRO_ATIZAPAN,
                errorUbicacion = "Búsqueda cancelada. Mueve el marcador al lugar exacto.",
            )
        }
    }

    fun seleccionarLugar(lugar: LugarEspecifico) {
        _estado.update { it.copy(lugarEspecifico = lugar) }
        guardarBorrador()
    }

    fun marcarPermisoDenegadoPermanente(denegado: Boolean) {
        _estado.update { it.copy(permisoUbicacionDenegadoPermanente = denegado) }
    }

    fun capturarDireccionManual(direccion: String, colonia: String, cp: String) {
        _estado.update {
            it.copy(
                ubicacion = (it.ubicacion ?: Ubicacion.CENTRO_ATIZAPAN)
                    .copy(direccion = direccion, colonia = colonia, cp = cp),
                errorUbicacion = null,
            )
        }
        guardarBorrador()
    }

    // --- Paso 4 -------------------------------------------------------------
    fun cambiarAnonimo(anonimo: Boolean) {
        _estado.update {
            it.copy(esAnonimo = anonimo, contacto = if (anonimo) ContactoReportante() else it.contacto)
        }
    }

    fun cambiarContacto(contacto: ContactoReportante) {
        _estado.update { it.copy(contacto = contacto) }
    }

    // --- Navegación entre pasos --------------------------------------------
    fun irAPaso(indice: Int) {
        _estado.update { it.copy(pasoActual = indice.coerceIn(0, TOTAL_PASOS - 1)) }
    }

    fun siguientePaso() = irAPaso(_estado.value.pasoActual + 1)

    fun pasoAnterior() = irAPaso(_estado.value.pasoActual - 1)

    fun descartarError() {
        _estado.update { it.copy(error = null, erroresPorCampo = emptyMap()) }
    }

    // --- Envío --------------------------------------------------------------
    fun enviar(cantidadFotosPlaneadas: Int = 0) {
        val actual = _estado.value
        if (actual.enviando || !actual.listoParaEnviar) return

        _estado.update { it.copy(enviando = true, error = null, erroresPorCampo = emptyMap()) }

        viewModelScope.launch {
            val reporte = Reporte(
                descripcion = actual.descripcion.trim(),
                categoriaTrabajo = actual.categoria!!,
                edadEstimada = actual.edadEstimada,
                cantidadNNA = actual.cantidadNNA,
                ubicacion = actual.ubicacion!!,
                lugarEspecifico = actual.lugarEspecifico!!,
                contactoReportante = if (actual.esAnonimo) null else actual.contacto,
                cantidadEvidencia = cantidadFotosPlaneadas,
            )

            crearReporte(reporte)
                .onSuccess { creado ->
                    urlsEvidencia = creado.urlsEvidencia
                    _estado.update { it.copy(enviando = false, reporteIdCreado = creado.reporteId) }
                }
                .onFailure { fallo ->
                    _estado.update {
                        it.copy(
                            enviando = false,
                            error = fallo.message ?: "No se pudo enviar el reporte.",
                            erroresPorCampo = (fallo as? ApiException)?.erroresDeCampo.orEmpty(),
                        )
                    }
                }
        }
    }

    // --- Borrador -----------------------------------------------------------
    // Se guarda como JSON plano: el borrador es efímero y no justifica una
    // tabla ni un serializador generado.
    private fun guardarBorrador() {
        val e = _estado.value
        viewModelScope.launch {
            val json = JSONObject().apply {
                put("categoria", e.categoria?.valor ?: "")
                put("descripcion", e.descripcion)
                put("edad", e.edadEstimada.valor)
                put("cantidad", e.cantidadNNA)
                put("lat", e.ubicacion?.lat ?: 0.0)
                put("lng", e.ubicacion?.lng ?: 0.0)
                put("direccion", e.ubicacion?.direccion ?: "")
                put("colonia", e.ubicacion?.colonia ?: "")
                put("cp", e.ubicacion?.cp ?: "")
                put("lugar", e.lugarEspecifico?.valor ?: "")
            }
            preferencias.guardarBorrador(json.toString())
        }
    }

    private fun recuperarBorrador() {
        viewModelScope.launch {
            val json = runCatching { preferencias.borrador.first() }.getOrNull() ?: return@launch

            runCatching {
                val objeto = JSONObject(json)
                val lat = objeto.optDouble("lat", 0.0)
                val lng = objeto.optDouble("lng", 0.0)

                _estado.update {
                    it.copy(
                        categoria = CategoriaTrabajoInfantil.desdeValor(objeto.optString("categoria")),
                        descripcion = objeto.optString("descripcion"),
                        edadEstimada = EdadEstimada.desdeValor(objeto.optString("edad")),
                        cantidadNNA = objeto.optInt("cantidad", 1).coerceIn(1, 50),
                        ubicacion = if (lat != 0.0 && lng != 0.0) {
                            Ubicacion(
                                lat = lat,
                                lng = lng,
                                direccion = objeto.optString("direccion"),
                                colonia = objeto.optString("colonia"),
                                cp = objeto.optString("cp"),
                            )
                        } else {
                            null
                        },
                        lugarEspecifico = objeto.optString("lugar")
                            .takeIf { valor -> valor.isNotBlank() }
                            ?.let(LugarEspecifico::desdeValor),
                    )
                }
            }
        }
    }

    private companion object {
        /**
         * Techo de toda la operacion de ubicacion, por encima de los plazos que
         * ya aplica el repositorio. Existe para que un fallo no previsto no deje
         * el paso 3 cargando de forma indefinida.
         */
        const val PLAZO_TOTAL_UBICACION_MS = 25_000L
    }
}
