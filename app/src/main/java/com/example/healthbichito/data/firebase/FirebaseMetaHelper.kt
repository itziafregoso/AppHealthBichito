package com.example.healthbichito.data.firebase

import com.example.healthbichito.data.model.Meta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseMetaHelper {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun getMetas(uid: String): Meta? {
        if (uid.isEmpty()) return null
        return try {
            val doc = db.collection("usuarios").document(uid).get().await()
            doc.get("metas", Meta::class.java) ?: Meta() // Devuelve metas por defecto si no existe
        } catch (e: Exception) {
            Meta() // Devuelve metas por defecto en caso de error
        }
    }

    suspend fun updateMetas(uid: String, metas: Meta) {
        if (uid.isEmpty()) return
        db.collection("usuarios").document(uid).update("metas", metas.toMap()).await()
    }
}
