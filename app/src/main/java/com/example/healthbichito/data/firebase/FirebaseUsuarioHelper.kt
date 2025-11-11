package com.example.healthbichito.data.firebase

import com.example.healthbichito.data.model.Meta
import com.example.healthbichito.data.model.Perfil
import com.example.healthbichito.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

object FirebaseUsuarioHelper {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun getUsuario(uid: String): Usuario? {
        if (uid.isEmpty()) return null
        return try {
            val doc = db.collection("usuarios").document(uid).get().await()
            // Convertir el documento de Firestore a nuestro objeto Usuario
            // Firestore se encarga de los objetos anidados automáticamente
            doc.toObject<Usuario>()?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // Nueva función para actualizar solo el perfil
    suspend fun updatePerfil(uid: String, perfil: Perfil) {
        if (uid.isEmpty()) return
        // Usamos "perfil" para indicar el campo del objeto anidado que queremos actualizar
        db.collection("usuarios").document(uid).update("perfil", perfil.toMap()).await()
    }
}
