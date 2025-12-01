package com.example.healthbichito.data.model

data class Meta(
    // Metas por defecto para un nuevo usuario
    val metaPasos: Int = 6000,
    val metaCalorias: Int = 200,
    val metaAgua: Int = 2000, // en ml
    val metaPeso: Double = 0.0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "metaPasos" to metaPasos,
            "metaCalorias" to metaCalorias,
            "metaAgua" to metaAgua,
            "metaPeso" to metaPeso
        )
    }
}