package com.example.healthbichito.data.repositories

import android.util.Log
import com.example.healthbichito.util.toFechaCorta
import com.example.healthbichito.util.toFechaLarga
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class StepsRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private fun getUid(): String? = auth.currentUser?.uid

    suspend fun actualizarPasosDelDia(nuevosPasos: Int) {
        val uid = getUid() ?: return
        val timestamp = System.currentTimeMillis()
        val fecha = timestamp.toFechaCorta()

        val docRef = firestore.collection("usuarios")
            .document(uid)
            .collection("actividadDiaria")
            .document(fecha)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            val pasosActuales = snapshot.getLong("pasosTotales")?.toInt() ?: 0
            val pasosActualizados = maxOf(pasosActuales, nuevosPasos)

            val data = mapOf(
                "pasosTotales" to pasosActualizados,
                "fecha_hora" to timestamp.toFechaLarga()
            )

            // Usa merge para no sobreescribir otros campos del documento (como las calorías)
            transaction.set(docRef, data, SetOptions.merge())
        }.await()

        Log.d("StepsRepository", "📤 Pasos actualizados: $nuevosPasos")
    }

    suspend fun obtenerPasosDelDia(): Int {
        val uid = getUid() ?: return 0
        val timestamp = System.currentTimeMillis()
        val fecha = timestamp.toFechaCorta()

        val snapshot = firestore.collection("usuarios")
            .document(uid)
            .collection("actividadDiaria")
            .document(fecha)
            .get()
            .await()

        return snapshot.getLong("pasosTotales")?.toInt() ?: 0
    }

    // 🔹 NUEVO — Obtener meta de pasos desde Firebase
    suspend fun obtenerMetaPasos(): Int {
        val uid = getUid() ?: return 6000  // Meta por defecto
        val snapshot = firestore.collection("usuarios")
            .document(uid)
            .get()
            .await()

        return snapshot.getLong("metas.metaPasos")?.toInt() ?: 6000
    }
}
