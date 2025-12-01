package com.example.healthbichito.data.repositories

import android.util.Log
import com.example.healthbichito.util.toFechaLarga
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class CaloriesRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private fun getUid(): String? = auth.currentUser?.uid

    private fun getCurrentDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    suspend fun actualizarCalorias(calorias: Float) {
        val uid = getUid() ?: return
        val fecha = getCurrentDate()
        val timestamp = System.currentTimeMillis()

        // 🔹 RUTA CORREGIDA: Apunta al documento de actividad diaria principal
        val docRef = firestore.collection("usuarios")
            .document(uid)
            .collection("actividadDiaria")
            .document(fecha)

        val data = mapOf(
            "caloriasTotales" to calorias,
            "fecha_hora_calorias" to timestamp.toFechaLarga() // Campo de fecha único
        )

        // 🔹 ESCRITURA CORREGIDA: Usa merge para no sobrescribir los pasos
        docRef.set(data, SetOptions.merge()).await()

        Log.d("CaloriesRepository", "🔥 Calorías actualizadas y fusionadas: $calorias")
    }

    suspend fun obtenerCaloriasDelDia(): Float {
        val uid = getUid() ?: return 0f
        val fecha = getCurrentDate()

        // 🔹 RUTA DE LECTURA CORREGIDA
        val snapshot = firestore.collection("usuarios")
            .document(uid)
            .collection("actividadDiaria")
            .document(fecha)
            .get()
            .await()

        return snapshot.getDouble("caloriasTotales")?.toFloat() ?: 0f
    }

    suspend fun obtenerMetaCalorias(): Int {
        val uid = getUid() ?: return 0

        val snapshot = firestore.collection("usuarios")
            .document(uid)
            .get()
            .await()

        return snapshot.getLong("metas.metaCalorias")?.toInt() ?: 0
    }

}
