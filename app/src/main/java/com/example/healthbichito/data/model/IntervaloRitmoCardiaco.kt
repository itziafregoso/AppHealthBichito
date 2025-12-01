package com.example.healthbichito.data.model

data class IntervaloRitmoCardiaco(
    val timestampInicio: Long = 0,
    val timestampFin: Long = 0,
    val fechaHoraInicio: String = "",
    val fechaHoraFin: String = "",
    val promedio: Int = 0,
    val minimo: Int = 0,
    val maximo: Int = 0,
    val cantidadMuestras: Int = 0
) {
    fun toMap(): Map<String, Any> = mapOf(
        "timestampInicio" to timestampInicio,
        "timestampFin" to timestampFin,
        "fechaHoraInicio" to fechaHoraInicio,
        "fechaHoraFin" to fechaHoraFin,
        "promedio" to promedio,
        "minimo" to minimo,
        "maximo" to maximo,
        "cantidadMuestras" to cantidadMuestras)
}


