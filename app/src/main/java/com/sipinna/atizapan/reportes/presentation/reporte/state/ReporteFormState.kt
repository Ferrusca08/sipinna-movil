package com.sipinna.atizapan.reportes.presentation.reporte.state

import com.sipinna.atizapan.reportes.domain.model.CategoriaTrabajoInfantil
import com.sipinna.atizapan.reportes.domain.model.ContactoReportante
import com.sipinna.atizapan.reportes.domain.model.EdadEstimada
import com.sipinna.atizapan.reportes.domain.model.LugarEspecifico
import com.sipinna.atizapan.reportes.domain.model.Ubicacion

/**
 * Estado completo del formulario de 4 pasos.
 * Cada paso expone su propio [pasoNCompleto] para que el botón "Siguiente"
 * sepa habilitarse sin duplicar reglas en la UI.
 */
data class ReporteFormState(
    // Paso 1
    val categoria: CategoriaTrabajoInfantil? = null,

    // Paso 2
    val descripcion: String = "",
    val edadEstimada: EdadEstimada = EdadEstimada.NO_ESPECIFICADA,
    val cantidadNNA: Int = 1,

    // Paso 3
    val ubicacion: Ubicacion? = null,
    val lugarEspecifico: LugarEspecifico? = null,
    val permisoUbicacionDenegadoPermanente: Boolean = false,
    val obteniendoUbicacion: Boolean = false,
    val errorUbicacion: String? = null,

    // Paso 4
    val esAnonimo: Boolean = true,
    val contacto: ContactoReportante = ContactoReportante(),

    // Transversales
    val pasoActual: Int = 0,
    val enviando: Boolean = false,
    val error: String? = null,
    val erroresPorCampo: Map<String, String> = emptyMap(),
    val reporteIdCreado: String? = null,
) {
    val paso1Completo: Boolean
        get() = categoria != null

    val paso2Completo: Boolean
        get() = descripcion.trim().length in 10..2000 && cantidadNNA in 1..50

    val paso3Completo: Boolean
        get() = ubicacion?.dentroDelValleDeMexico == true && lugarEspecifico != null

    val paso4Completo: Boolean
        get() = esAnonimo || (contacto.tieneAlgo && contacto.telefonoValido && contacto.emailValido)

    fun pasoCompleto(indice: Int): Boolean = when (indice) {
        0 -> paso1Completo
        1 -> paso2Completo
        2 -> paso3Completo
        3 -> paso4Completo
        else -> false
    }

    val caracteresRestantes: Int
        get() = 2000 - descripcion.length

    val listoParaEnviar: Boolean
        get() = paso1Completo && paso2Completo && paso3Completo && paso4Completo
}

const val TOTAL_PASOS = 4
