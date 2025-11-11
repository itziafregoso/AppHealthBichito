package com.example.healthbichito.ui.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper
import com.example.healthbichito.data.model.Medicacion
import com.example.healthbichito.ui.theme.PrimaryGreen
import kotlinx.coroutines.launch

@Composable
fun MedicacionItem(
    medicacion: Medicacion,
    onEliminar: () -> Unit // PARÁMETRO onEstadoCambiado ELIMINADO
) {
    val scope = rememberCoroutineScope()

    // Escuchar el estado de ESTE medicamento en tiempo real
    val estadoHoy by FirebaseMedicacionHelper.getEstadoHoyFlow(medicacion.id)
        .collectAsState(initial = null)

    // El estado `tomado` ahora viene del `Flow` en tiempo real
    val tomado = estadoHoy?.tomado ?: false

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tomado) Color(0xFFEAEAEA) else Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(Modifier.weight(1f)) {
                Text(
                    text = medicacion.nombre_medicamento,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = "Hora: ${medicacion.hora}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(
                    onClick = {
                        scope.launch {
                            // El item gestiona su propio estado. Llama directamente a Firebase.
                            FirebaseMedicacionHelper.setTomadoHoy(medicacion.id, !tomado)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (tomado) Icons.Default.Check else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = "Marcar tomado",
                        tint = if (tomado) PrimaryGreen else Color.Gray
                    )
                }

                IconButton(onClick = { onEliminar() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        tint = Color.Red,
                        contentDescription = "Eliminar"
                    )
                }
            }
        }
    }
}
