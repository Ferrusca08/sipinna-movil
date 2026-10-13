package com.sipinna.atizapan.reportes.model

/** Punto geográfico del reporte, con la referencia postal que capture el usuario. */
data class Ubicacion(
    val lat: Double,
    val lng: Double,
    val direccion: String = "",
    val cp: String = "",
    val colonia: String = "",
) {
    /**
     * El backend rechaza coordenadas fuera del Valle de México; se valida antes
     * de enviar para no gastar un viaje de red.
     */
    val dentroDelValleDeMexico: Boolean
        get() = lat in 19.0..20.0 && lng in -99.8..-98.5

    companion object {
        /** Plaza principal de Atizapán de Zaragoza: centro por defecto del mapa. */
        val CENTRO_ATIZAPAN = Ubicacion(lat = 19.5588, lng = -99.2536)
    }
}
