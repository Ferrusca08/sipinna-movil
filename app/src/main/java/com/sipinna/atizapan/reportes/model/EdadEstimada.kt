package com.sipinna.atizapan.reportes.model

enum class EdadEstimada(val valor: String, val etiqueta: String) {
    DE_0_A_5("0-5", "0 a 5 años"),
    DE_6_A_9("6-9", "6 a 9 años"),
    DE_10_A_13("10-13", "10 a 13 años"),
    DE_14_A_17("14-17", "14 a 17 años"),
    NO_ESPECIFICADA("no_especificada", "No sé");

    companion object {
        fun desdeValor(valor: String?): EdadEstimada =
            entries.firstOrNull { it.valor == valor } ?: NO_ESPECIFICADA
    }
}
