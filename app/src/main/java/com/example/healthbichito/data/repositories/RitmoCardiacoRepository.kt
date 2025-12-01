package com.example.healthbichito.data.repositories

import com.example.healthbichito.data.model.IntervaloRitmoCardiaco
import com.example.healthbichito.data.firebase.FirebaseRitmoCardiaco
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RitmoCardiacoRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private fun getUid(): String? = auth.currentUser?.uid

    suspend fun guardarEstadistica(intervalo: IntervaloRitmoCardiaco) {
        FirebaseRitmoCardiaco.guardarIntervalo(intervalo)
    }

    fun obtenerIntervalosPorFecha(fecha: String): Flow<List<IntervaloRitmoCardiaco>> = callbackFlow {
        val uid = getUid() ?: return@callbackFlow

        val listener = firestore
            .collection("usuarios").document(uid)
            .collection("ritmoCardiaco").document(fecha)
            .collection("intervalos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.mapNotNull {
                    it.toObject(IntervaloRitmoCardiaco::class.java)
                } ?: emptyList()

                trySend(lista)
            }

        awaitClose { listener.remove() }
    }
}
