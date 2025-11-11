package com.example.healthbichito.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Usuario(
    @get:Exclude var id: String = "",
    val nombre: String = "",
    val email: String = "",

    // Campos que ya tenías
    val apellido: String? = null,
    val fechaNacimiento: String? = null,
    val edad: Int? = null,
    val genero: String? = null,
    val fechaRegistro: Timestamp? = null,

    // Nuevos objetos anidados
    val perfil: Perfil = Perfil(),
    val metas: Meta = Meta()
) {
    // Método para convertir el objeto a un mapa para Firestore
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nombre" to nombre,
            "email" to email,
            "apellido" to apellido,
            "fechaNacimiento" to fechaNacimiento,
            "edad" to edad,
            "genero" to genero,
            "fechaRegistro" to fechaRegistro,
            // Guardar los objetos anidados como mapas
            "perfil" to perfil.toMap(),
            "metas" to metas.toMap()
        )
    }
}