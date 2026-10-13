package com.sipinna.atizapan.reportes.model

/**
 * Estatus de un caso. El orden de la enum es el de la línea de tiempo que ve
 * el ciudadano; [DESCARTADO] queda fuera de ella por ser una salida lateral.
 */
enum class EstatusReporte(
    val valor: String,
    val etiqueta: String,
    val descripcionCiudadana: String,
) {
    NUEVO(
        "nuevo",
        "Recibido",
        "Recibimos tu reporte y está en espera de revisión.",
    ),
    EN_REVISION(
        "en_revision",
        "En revisión",
        "Personal de SIPINNA está revisando la información que enviaste.",
    ),
    ASIGNADO(
        "asignado",
        "Asignado",
        "Tu reporte fue asignado a un operador para su atención.",
    ),
    EN_SEGUIMIENTO(
        "en_seguimiento",
        "En seguimiento",
        "Se están realizando acciones de seguimiento en campo.",
    ),
    RESUELTO(
        "resuelto",
        "Resuelto",
        "El caso fue atendido y cerrado.",
    ),
    DESCARTADO(
        "descartado",
        "Descartado",
        "Tras la revisión, el caso no procedió como situación de trabajo infantil.",
    );

    companion object {
        /** Etapas que se dibujan en la línea de tiempo de la consulta. */
        val lineaTiempo: List<EstatusReporte> =
            listOf(NUEVO, EN_REVISION, ASIGNADO, EN_SEGUIMIENTO, RESUELTO)

        fun desdeValor(valor: String?): EstatusReporte =
            entries.firstOrNull { it.valor == valor } ?: NUEVO
    }
}
