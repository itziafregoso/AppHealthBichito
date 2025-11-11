package com.example.healthbichito.data.model

import com.google.firebase.Timestamp

data class Medicacion(
    val id: String = "",
    val nombre_medicamento: String = "",
    val dosis: String = "",
    val unidad: String = "",
    val hora: String = "",
    val observaciones: String = "",
    val activo: Int = 1   // 1 = recordatorio activo, 0 = inactivo
) {

    fun toMap() = mapOf(
        "nombre_medicamento" to nombre_medicamento,
        "dosis" to dosis,
        "unidad" to unidad,
        "hora" to hora,
        "observaciones" to observaciones,
        "activo" to activo
    )
}