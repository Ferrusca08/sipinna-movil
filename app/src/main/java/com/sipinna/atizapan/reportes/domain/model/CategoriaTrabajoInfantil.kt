package com.sipinna.atizapan.reportes.domain.model

/**
 * Tipos de trabajo infantil que el ciudadano puede reportar.
 * El [valor] es el que viaja al backend; la etiqueta es la que ve la persona.
 */
enum class CategoriaTrabajoInfantil(
    val valor: String,
    val etiqueta: String,
    val descripcion: String,
) {
    VENTA_AMBULANTE(
        "VENTA_AMBULANTE",
        "Venta ambulante",
        "Niñas, niños y adolescentes vendiendo dulces, flores u otros productos en la vía pública.",
    ),
    LIMPIA_PARABRISAS(
        "LIMPIA_PARABRISAS",
        "Limpiaparabrisas",
        "Niñas, niños y adolescentes limpiando parabrisas en cruceros y semáforos.",
    ),
    CARGA_MERCANCIA(
        "CARGA_MERCANCIA",
        "Carga de mercancía",
        "Niñas, niños y adolescentes cargando bultos o mercancía en mercados y centrales.",
    ),
    TRABAJO_DOMESTICO(
        "TRABAJO_DOMESTICO",
        "Trabajo doméstico",
        "Niñas, niños y adolescentes realizando labores del hogar en casa ajena.",
    ),
    MENDICIDAD(
        "MENDICIDAD",
        "Mendicidad",
        "Niñas, niños y adolescentes pidiendo dinero, solos o acompañados de un adulto.",
    ),
    RECOLECCION_BASURA(
        "RECOLECCION_BASURA",
        "Recolección de basura",
        "Niñas, niños y adolescentes separando o recolectando residuos.",
    ),
    TRABAJO_AGRICOLA(
        "TRABAJO_AGRICOLA",
        "Trabajo agrícola",
        "Niñas, niños y adolescentes en labores de siembra, cosecha o cuidado de animales.",
    ),
    COMERCIO_ESTABLECIDO(
        "COMERCIO_ESTABLECIDO",
        "Comercio establecido",
        "Niñas, niños y adolescentes atendiendo un local, taller o negocio.",
    ),
    ESPECTACULO_CALLEJERO(
        "ESPECTACULO_CALLEJERO",
        "Espectáculo callejero",
        "Niñas, niños y adolescentes haciendo actos o malabares para pedir dinero.",
    ),
    EXPLOTACION_SEXUAL(
        "EXPLOTACION_SEXUAL",
        "Explotación sexual",
        "Situación de máxima urgencia. Se atiende de forma inmediata.",
    ),
    ACTIVIDAD_ILICITA(
        "ACTIVIDAD_ILICITA",
        "Actividad ilícita",
        "Niñas, niños y adolescentes usados para vender o transportar sustancias u objetos ilegales.",
    ),
    OTRO(
        "OTRO",
        "Otro",
        "Cualquier otra situación de trabajo infantil.",
    );

    /** Categorías que el backend clasifica automáticamente como urgentes. */
    val esUrgente: Boolean
        get() = this == EXPLOTACION_SEXUAL || this == ACTIVIDAD_ILICITA

    companion object {
        fun desdeValor(valor: String?): CategoriaTrabajoInfantil? =
            entries.firstOrNull { it.valor == valor }
    }
}
