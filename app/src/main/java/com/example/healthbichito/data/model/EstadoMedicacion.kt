package com.example.healthbichito.data.model

data class EstadoMedicacion(
    val fecha: String = "",
    val tomado: Boolean = false,
    val tomadoHora: String? = null
)
