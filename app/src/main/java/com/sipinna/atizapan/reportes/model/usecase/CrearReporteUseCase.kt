package com.sipinna.atizapan.reportes.model.usecase

import com.sipinna.atizapan.reportes.model.repository.PreferenciasRepository
import com.sipinna.atizapan.reportes.model.Reporte
import com.sipinna.atizapan.reportes.model.ReporteCreado
import com.sipinna.atizapan.reportes.model.repository.ReporteRepository
import javax.inject.Inject

/**
 * Envía el reporte y, si el backend lo acepta, guarda el token en el teléfono
 * y limpia el borrador. Las tres cosas van juntas para que la pantalla no tenga
 * que orquestarlas.
 */
class CrearReporteUseCase @Inject constructor(
    private val repositorio: ReporteRepository,
    private val preferencias: PreferenciasRepository,
) {
    suspend operator fun invoke(reporte: Reporte): Result<ReporteCreado> {
        val validacion = validar(reporte)
        if (validacion != null) return Result.failure(IllegalArgumentException(validacion))

        return repositorio.crear(reporte).onSuccess { creado ->
            preferencias.guardarToken(
                reporteId = creado.reporteId,
                fecha = creado.fechaCreacion,
                categoria = reporte.categoriaTrabajo.etiqueta,
            )
            preferencias.limpiarBorrador()
        }
    }

    /** Mismas reglas que el backend, para no gastar un viaje de red en vano. */
    private fun validar(reporte: Reporte): String? = when {
        reporte.descripcion.trim().length < 10 ->
            "La descripción debe tener al menos 10 caracteres."

        reporte.descripcion.trim().length > 2000 ->
            "La descripción no puede pasar de 2000 caracteres."

        reporte.cantidadNNA !in 1..50 ->
            "La cantidad de niñas, niños y adolescentes debe estar entre 1 y 50."

        !reporte.ubicacion.dentroDelValleDeMexico ->
            "La ubicación seleccionada está fuera de la zona de cobertura."

        reporte.contactoReportante?.telefonoValido == false ->
            "El teléfono debe tener 10 dígitos."

        reporte.contactoReportante?.emailValido == false ->
            "El correo no tiene un formato válido."

        reporte.cantidadEvidencia !in 0..5 ->
            "Se permiten máximo 5 fotos por reporte."

        else -> null
    }
}
