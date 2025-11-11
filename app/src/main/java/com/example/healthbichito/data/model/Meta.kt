package com.example.healthbichito.data.model

data class Meta(
    // Metas por defecto para un nuevo usuario
    val metaPasos: Int = 8000,
    val metaCalorias: Int = 2000,
    val metaAgua: Int = 2000, // en ml

    // La meta de peso no tiene un valor por defecto, ya que es muy personal
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