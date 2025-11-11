package com.example.healthbichito.data.model
import com.google.firebase.Timestamp

data class Agua(
    val id_agua: String = "",
    val id_usuario: String = "",
    val cantidad_ml: Int = 0,
    val fecha_hora: Timestamp = Timestamp.now()
)
