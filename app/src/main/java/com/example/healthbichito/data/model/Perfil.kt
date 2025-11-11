package com.example.healthbichito.data.model

data class Perfil(
    val altura: Double = 0.0,
    val peso: Double = 0.0,
    val contactoEmergencia: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "altura" to altura,
            "peso" to peso,
            "contactoEmergencia" to contactoEmergencia
        )
    }
}