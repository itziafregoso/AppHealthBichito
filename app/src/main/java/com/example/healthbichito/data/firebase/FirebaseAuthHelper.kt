package com.example.healthbichito.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseAuthHelper {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ✅ FUNCIÓN AÑADIDA
    fun getUid(): String? {
        return auth.currentUser?.uid
    }

    fun registrarUsuario(
        email: String,
        password: String,
        datosExtra: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val datos = datosExtra.toMutableMap()
                datos["fechaRegistro"] = FieldValue.serverTimestamp()

                db.collection("usuarios")
                    .document(uid)
                    .set(datos)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Error al guardar datos")
                    }
            }
            .addOnFailureListener { e ->
                onError(
                    when {
                        e.message?.contains("email address is already in use") == true ->
                            "Este correo ya está registrado"

                        e.message?.contains("Password should be at least") == true ->
                            "La contraseña es muy débil (mínimo 6 caracteres)"

                        else -> e.message ?: "Error desconocido"
                    }
                )
            }
    }


    fun loginUsuario(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(
                    when {
                        e.message?.contains("no user record") == true ->
                            "El usuario no existe"

                        e.message?.contains("password is invalid") == true ->
                            "Contraseña incorrecta"

                        else -> e.message ?: "Error desconocido"
                    }
                )
            }
    }
}