package com.example.healthbichito.data.firebase

import com.example.healthbichito.data.model.EstadoMedicacion
import com.example.healthbichito.data.model.Medicacion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

object FirebaseMedicacionHelper {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun hoy(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun horaAhora(): String =
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    // ===============================================
    // ESCRITURA
    // ===============================================

    suspend fun agregarMedicamento(medicacion: Medicacion): String? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection("usuarios")
            .document(uid)
            .collection("medicamentos")
            .add(medicacion.toMap())
            .await()
            .id
    }

    /**
     * 🔴 Elimina el medicamento Y TODOS SUS ESTADOS
     */
    suspend fun eliminarMedicamento(id: String) {
        val uid = auth.currentUser?.uid ?: return
        val medRef = db.collection("usuarios")
            .document(uid)
            .collection("medicamentos")
            .document(id)

        // 1) Borrar subcolección "estados"
        val estadosSnap = medRef.collection("estados").get().await()
        estadosSnap.documents.forEach { estadoDoc ->
            estadoDoc.reference.delete().await()
        }

        // 2) Borrar el documento del medicamento
        medRef.delete().await()
    }

    suspend fun setTomadoHoy(medId: String, tomado: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val fecha = hoy()
        val ref = db.collection("usuarios")
            .document(uid)
            .collection("medicamentos")
            .document(medId)
            .collection("estados")
            .document(fecha)

        val data = if (tomado) {
            mapOf(
                "tomado" to true,
                "tomadoHora" to horaAhora()
            )
        } else {
            mapOf(
                "tomado" to false,
                "tomadoHora" to null
            )
        }

        ref.set(data, SetOptions.merge()).await()
    }

    suspend fun resetearEstadoTomadoDiario() {
        val medicamentos = obtenerMedicamentos()
        medicamentos.forEach { med ->
            if (med.activo == 1) {
                setTomadoHoy(med.id, false)
            }
        }
    }

    // ===============================================
    // LECTURA UNA VEZ
    // ===============================================

    suspend fun obtenerMedicamentos(): List<Medicacion> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val snap = db.collection("usuarios")
            .document(uid)
            .collection("medicamentos")
            .get()
            .await()

        return snap.documents.mapNotNull { doc ->
            doc.toObject(Medicacion::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun getEstadoHoy(medId: String): EstadoMedicacion {
        val uid = auth.currentUser?.uid ?: return EstadoMedicacion()
        val fecha = hoy()
        val ref = db.collection("usuarios")
            .document(uid)
            .collection("medicamentos")
            .document(medId)
            .collection("estados")
            .document(fecha)

        val snap = ref.get().await()

        return if (snap.exists()) {
            EstadoMedicacion(
                fecha = fecha,
                tomado = snap.getBoolean("tomado") ?: false,
                tomadoHora = snap.getString("tomadoHora")
            )
        } else {
            // Si no existe el estado para hoy, lo crea como 'no tomado'
            ref.set(mapOf("tomado" to false)).await()
            EstadoMedicacion(fecha = fecha, tomado = false)
        }
    }

    // ===============================================
    // LECTURA EN TIEMPO REAL (Flow)
    // ===============================================

    fun obtenerMedicamentosFlow(): Flow<List<Medicacion>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            awaitClose()
            return@callbackFlow
        }

        val listener = db.collection("usuarios")
            .document(uid)
            .collection("medicamentos")
            .addSnapshotListener { snapshot, _ ->
                val medicaciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Medicacion::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(medicaciones)
            }

        awaitClose { listener.remove() }
    }

    fun getEstadoHoyFlow(medId: String): Flow<EstadoMedicacion> = callbackFlow {
        val uid = auth.currentUser?.uid
        val fecha = hoy()

        if (uid == null) {
            trySend(EstadoMedicacion(fecha = fecha, tomado = false))
            awaitClose()
            return@callbackFlow
        }

        val ref = db.collection("usuarios")
            .document(uid)
            .collection("medicamentos")
            .document(medId)
            .collection("estados")
            .document(fecha)

        val listener = ref.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                trySend(
                    EstadoMedicacion(
                        fecha = fecha,
                        tomado = snapshot.getBoolean("tomado") ?: false,
                        tomadoHora = snapshot.getString("tomadoHora")
                    )
                )
            } else {
                ref.set(mapOf("tomado" to false))
                trySend(EstadoMedicacion(fecha = fecha, tomado = false))
            }
        }

        awaitClose { listener.remove() }
    }
}
