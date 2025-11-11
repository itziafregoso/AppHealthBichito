package com.example.healthbichito.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper
import com.example.healthbichito.ui.componentes.MedicacionItem
import com.example.healthbichito.ui.componentes.ModernSnackbar
import com.example.healthbichito.ui.componentes.SnackbarType
import com.example.healthbichito.ui.theme.AccentOrange
import kotlinx.coroutines.launch

@Composable
fun MedicacionSection(
    navController: NavController
) {

    // ✅ Escucha la LISTA de medicamentos (para saber si se añaden o eliminan)
    val listaMedicacion by FirebaseMedicacionHelper.obtenerMedicamentosFlow()
        .collectAsState(initial = emptyList())

    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarType by remember { mutableStateOf(SnackbarType.Info) }
    var snackbarTrigger by remember { mutableLongStateOf(0L) }

    val scope = rememberCoroutineScope()

    // Region de gestión de Snackbars (mensajes informativos)
    navController.currentBackStackEntry?.savedStateHandle?.let {
        val medAdded by it.getStateFlow("med_added", false).collectAsState()
        val alarmSnackbar by it.getStateFlow("snackbar_message", "").collectAsState()

        LaunchedEffect(medAdded) {
            if (medAdded) {
                snackbarMessage = "✅ Medicación agregada"
                snackbarType = SnackbarType.Success
                snackbarTrigger = System.currentTimeMillis()
                it.set("med_added", false)
            }
        }

        LaunchedEffect(alarmSnackbar) {
            if (alarmSnackbar.isNotEmpty()) {
                snackbarMessage = alarmSnackbar
                snackbarType = SnackbarType.Info
                snackbarTrigger = System.currentTimeMillis()
                it.set("snackbar_message", "")
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Horarios", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                Spacer(Modifier.height(16.dp))

                if (listaMedicacion.isEmpty()) {
                    Text("No tienes medicaciones registradas 💊", color = Color.Gray, fontSize = 14.sp)
                } else {
                    listaMedicacion.forEach { med ->
                        MedicacionItem(
                            medicacion = med,
                            // ✅ onEstadoCambiado eliminado. Cada item gestiona su propio estado.
                            onEliminar = {
                                scope.launch {
                                    FirebaseMedicacionHelper.eliminarMedicamento(med.id)
                                    snackbarMessage = "🗑️ Eliminado correctamente"
                                    snackbarType = SnackbarType.Success
                                    snackbarTrigger = System.currentTimeMillis()
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(60.dp))
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("agregar_medicamento") },
            containerColor = AccentOrange,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .size(48.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
        }

        if (snackbarMessage.isNotEmpty()) {
            ModernSnackbar(
                message = snackbarMessage,
                type = snackbarType,
                trigger = snackbarTrigger,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp)
            )
        }
    }
}