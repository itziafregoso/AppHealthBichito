package com.example.healthbichito.data.firebase

import android.icu.text.SimpleDateFormat
import android.util.Log
import com.example.healthbichito.data.model.IntervaloRitmoCardiaco
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

object FirebaseRitmoCardiaco {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Obtiene el UID del usuario autenticado
    private fun getUid(): String? = auth.currentUser?.uid

    // Genera la fecha actual en formato "yyyy-MM-dd" (Ej: 2025-11-24)
    private fun getFechaActual(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    /**
     * Guarda un intervalo de ritmo cardíaco en Firebase según esta estructura:
     * usuarios/{uid}/ritmoCardiaco/{fecha}/intervalos/{rc_timestampInicio}
     *
     * El objeto se guarda como MAPA usando toMap(), lo cual permite filtrado
     * y consultas más adelante.
     */
    suspend fun guardarIntervalo(intervalo: IntervaloRitmoCardiaco) {
        val uid = getUid() ?: return

        val fecha = getFechaActual()
        val docId = "rc_${intervalo.timestampInicio}"  // Identificador único del intervalo

        try {
            firestore.collection("usuarios")
                .document(uid)
                .collection("ritmoCardiaco")
                .document(fecha)               // Documento del día
                .collection("intervalos")
                .document(docId)              // Intervalo dentro del día
                .set(intervalo.toMap())       // Guardado como Mapa
                .await()

            Log.d("FirebaseRitmoCardiaco", "Intervalo guardado correctamente: ${intervalo.toMap()}")

        } catch (e: Exception) {
            Log.e("FirebaseRitmoCardiaco", "Error guardando el intervalo: ${e.message}")
        }
    }
}
