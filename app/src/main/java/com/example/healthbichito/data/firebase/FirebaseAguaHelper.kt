package com.example.healthbichito.data.firebase

import com.example.healthbichito.data.model.Agua
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

object FirebaseAguaHelper {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ✅ Fecha del día en formato YYYY-MM-DD
    private fun fechaHoy(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // ✅ Obtener o crear registro de HOY (documento nombrado por fecha)
    suspend fun obtenerRegistroHoy(): Agua {
        val uid = auth.currentUser?.uid ?: return Agua()

        val hoy = fechaHoy()

        // ✅ Documento con nombre = fecha
        val docRef = db.collection("usuarios")
            .document(uid)
            .collection("agua")
            .document(hoy)

        val doc = docRef.get().await()

        if (doc.exists()) {
            return Agua(
                id_agua = docRef.id,
                id_usuario = uid,
                cantidad_ml = doc.getLong("cantidad_ml")?.toInt() ?: 0,
                fecha_hora = doc.getTimestamp("fecha_hora") ?: Timestamp.now()
            )
        }

        // ✅ Si NO existe, crear el registro del día
        val nuevo = mapOf(
            "cantidad_ml" to 0,
            "fecha" to hoy,
            "fecha_hora" to Timestamp.now()
        )

        docRef.set(nuevo).await()

        return Agua(
            id_agua = hoy,
            id_usuario = uid,
            cantidad_ml = 0,
            fecha_hora = Timestamp.now()
        )
    }

    // ✅ Actualizar SIEMPRE el documento del día
    suspend fun actualizarRegistro(idAgua: String, cantidad: Int) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("usuarios")
            .document(uid)
            .collection("agua")
            .document(idAgua)
            .update("cantidad_ml", cantidad)
            .await()
    }
}



