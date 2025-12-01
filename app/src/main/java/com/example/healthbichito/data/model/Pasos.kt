package com.example.healthbichito.data.model


data class Pasos(
    val pasosTotales: Int = 0,
    val ultimaActualizacion: Long = System.currentTimeMillis()
)
