package com.sipinna.atizapan.reportes.model

/** Datos de contacto de quien reporta. Nulo cuando el reporte es anónimo. */
data class ContactoReportante(
    val nombre: String = "",
    val telefono: String = "",
    val email: String = "",
) {
    val telefonoValido: Boolean
        get() = telefono.isEmpty() || telefono.filter { it.isDigit() }.length == 10

    val emailValido: Boolean
        get() = email.isEmpty() || Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$").matches(email)

    val tieneAlgo: Boolean
        get() = nombre.isNotBlank() || telefono.isNotBlank() || email.isNotBlank()
}

/** Reporte listo para enviar al backend. */
data class Reporte(
    val descripcion: String,
    val categoriaTrabajo: CategoriaTrabajoInfantil,
    val edadEstimada: EdadEstimada,
    val cantidadNNA: Int,
    val ubicacion: Ubicacion,
    val lugarEspecifico: LugarEspecifico,
    val contactoReportante: ContactoReportante? = null,
    val cantidadEvidencia: Int = 0,
)

/** Respuesta al dar de alta un reporte. */
data class ReporteCreado(
    val reporteId: String,
    val fechaCreacion: String,
    val estatus: String,
    val prioridad: String,
    val urlsEvidencia: List<UrlEvidencia>,
)

data class UrlEvidencia(
    val clave: String,
    val url: String,
    val tipoContenido: String,
)

/** Una etapa de la línea de tiempo que ve el ciudadano. */
data class EtapaLineaTiempo(
    val etapa: EstatusReporte,
    val alcanzada: Boolean,
    val actual: Boolean,
)

data class AccionPublica(
    val fecha: String,
    val descripcion: String,
)

/** Estatus de un reporte tal como se muestra en la pantalla de consulta. */
data class EstatusConsultado(
    val reporteId: String,
    val estatus: EstatusReporte,
    val descripcionEstatus: String,
    val fechaCreacion: String,
    val actualizadoEn: String,
    val categoria: CategoriaTrabajoInfantil?,
    val descartado: Boolean,
    val lineaTiempo: List<EtapaLineaTiempo>,
    val acciones: List<AccionPublica>,
)
