package com.example.healthbichito.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.healthbichito.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerRedondeado(
    horaActual: String,
    onHoraSeleccionada: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    // Caja verde donde se muestra la hora
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE8F5E9))
            .clickable { showDialog = true }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = if (horaActual.isBlank()) "Selecciona una hora" else horaActual,
                color = PrimaryGreen,
                fontSize = 16.sp
            )

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF9800)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Hora",
                    tint = Color.White
                )
            }
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 6.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    // TimePicker de Material3
                    val state = rememberTimePickerState()

                    TimePicker(state = state)

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar", color = Color.Gray)
                        }

                        Spacer(Modifier.width(10.dp))

                        Button(
                            onClick = {
                                val h = state.hour
                                val m = state.minute
                                onHoraSeleccionada(String.format("%02d:%02d", h, m))
                                showDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen
                            )
                        ) {
                            Text("Aceptar", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
