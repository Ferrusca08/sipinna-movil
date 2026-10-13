package com.sipinna.atizapan.reportes.model

enum class LugarEspecifico(val valor: String, val etiqueta: String) {
    CALLE("calle", "Calle"),
    MERCADO("mercado", "Mercado"),
    CRUCERO("crucero", "Crucero"),
    TRANSPORTE("transporte", "Transporte"),
    COMERCIO("comercio", "Comercio"),
    OTRO("otro", "Otro");

    companion object {
        fun desdeValor(valor: String?): LugarEspecifico =
            entries.firstOrNull { it.valor == valor } ?: OTRO
    }
}
