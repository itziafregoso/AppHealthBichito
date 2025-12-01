package com.example.healthbichito.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.healthbichito.data.model.Medicacion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun DashboardLoader(
    userId: String?,
    navController: NavController
) {

    if (userId == null) {
        Text(
            text = "No se encontró el usuario",
            color = MaterialTheme.colorScheme.error
        )
        return
    }

    var nombre by remember { mutableStateOf<String?>(null) }
    var medicamentos by remember { mutableStateOf<List<Medicacion>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        val db = FirebaseFirestore.getInstance()

        try {
            // ✅ Datos usuario
            val userDoc = db.collection("usuarios")
                .document(userId)
                .get()
                .await()

            nombre = userDoc.getString("nombre") ?: "Usuario"

            // ✅ Medicamentos
            val medsSnap = db.collection("usuarios")
                .document(userId)
                .collection("medicamentos")
                .get()
                .await()

            medicamentos = medsSnap.documents.mapNotNull { doc ->

                Medicacion(
                    id = doc.id,
                    nombre_medicamento = doc.getString("nombre_medicamento") ?: "",
                    dosis = doc.getString("dosis") ?: "",
                    unidad = doc.getString("unidad") ?: "",
                    hora = doc.getString("hora") ?: "",
                    observaciones = doc.getString("observaciones") ?: "",
                    activo = doc.getLong("activo")?.toInt() ?: 1
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        cargando = false
    }

    if (cargando) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    } else {

        DashboardScreen(
            nombreUsuario = nombre ?: "Usuario",
            medicamentos = medicamentos,
            onAgregarMedicamento = { navController.navigate("agregar_medicamento") },

            onActualizarMedicamento = { medicacion ->
                // Aquí puedes actualizar firebase si quieres
            },

            onEliminarMedicamento = { medicacion ->
                // Aquí puedes eliminar firebase si quieres
            },

            navController = navController
        )
    }
}