package com.example.healthbichito.ui.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeightMonitorCard(
    pesoActual: Double,
    metaPeso: Double,
    metaAlcanzada: Boolean,
    onPesoIngresado: (Double) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DialogoIngresarPeso(
            onDismiss = { showDialog = false },
            onConfirm = { nuevoPeso ->
                onPesoIngresado(nuevoPeso)
                showDialog = false
            }
        )
    }

    // ✔ Color dinámico de fondo (verde solo si YA alcanzó la meta)
    val fondoColor = if (metaPeso > 0 && pesoActual <= metaPeso) {
        Color(0xFFDFF6DD) // Verde suave
    } else {
        Color(0xFFCBE8FF) // Azul suave
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(200.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = fondoColor),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // 🔹 TÍTULO CENTRADO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MonitorWeight,
                    contentDescription = "Icono de monitoreo de peso",
                    modifier = Modifier.size(24.dp),
                    tint = if (metaAlcanzada) Color(0xFF2E7D32) else Color(0xFF01579B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Monitoreo de Peso",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF003C67)
                )
            }

            // 🔹 VALOR DE PESO
            Text(
                text = String.format("%.1f kg", pesoActual),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (metaAlcanzada) Color(0xFF2E7D32) else Color(0xFF01579B)
            )

            // 🔹 CÁLCULO CORRECTO DEL PROGRESO EN META DE REDUCCIÓN
            val progreso = if (metaPeso > 0) {
                ((metaPeso - pesoActual) / metaPeso)
                    .toFloat()
                    .coerceIn(0f, 1f)
            } else 0f

            LinearProgressIndicator(
                progress = progreso,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (metaAlcanzada) Color(0xFF2E7D32) else Color(0xFF01579B),
                trackColor = Color.LightGray.copy(alpha = 0.25f)
            )

            // 🔹 TEXTO DE META
            Text(
                text = "Meta: ${String.format("%.1f kg", metaPeso)}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            // 🔹 BOTÓN DE INGRESO DE PESO
            Button(
                onClick = { showDialog = true },
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF01579B)
                )
            ) {
                Text("Ingresar Peso")
            }
        }
    }
}

@Composable
fun DialogoIngresarPeso(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ingresar Peso",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF01579B)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                ModernTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = "Peso actual (kg)",
                    icon = Icons.Default.MonitorWeight,
                    tint = Color(0xFF01579B),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Text(
                    text = "Ejemplo: 64.5",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val peso = inputText.toDoubleOrNull()
                    if (peso != null) onConfirm(peso)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01579B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF01579B)
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}



