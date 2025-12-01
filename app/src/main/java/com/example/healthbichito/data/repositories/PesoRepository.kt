package com.example.healthbichito.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PesoRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private fun getUid(): String? = auth.currentUser?.uid

    // 🔹 Actualizar peso actual en Firebase
    suspend fun actualizarPeso(peso: Double) {
        val uid = getUid() ?: return
        firestore.collection("usuarios")
            .document(uid)
            .update("perfil.peso", peso)
            .await()
    }

    // 🔹 Obtener el peso actual desde Firebase
    suspend fun obtenerPesoActual(): Double {
        val uid = getUid() ?: return 0.0
        val snapshot = firestore.collection("usuarios")
            .document(uid)
            .get()
            .await()
        return snapshot.getDouble("perfil.peso") ?: 0.0
    }

    // 🔹 Obtener meta de peso desde Firebase
    suspend fun obtenerMetaPeso(): Double {
        val uid = getUid() ?: return 0.0
        val snapshot = firestore.collection("usuarios")
            .document(uid)
            .get()
            .await()
        return snapshot.getDouble("metas.metaPeso") ?: 0.0
    }

    // 🔹 Actualizar meta de peso en Firebase
    suspend fun actualizarMetaPeso(metaPeso: Double) {
        val uid = getUid() ?: return
        firestore.collection("usuarios")
            .document(uid)
            .update("metas.metaPeso", metaPeso)
            .await()
    }
}

