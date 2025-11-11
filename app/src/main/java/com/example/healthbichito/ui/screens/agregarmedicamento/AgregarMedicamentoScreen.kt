package com.example.healthbichito.ui.screens.agregarmedicamento

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper
import com.example.healthbichito.data.model.Medicacion
import com.example.healthbichito.scheduling.MedicacionScheduler
import com.example.healthbichito.ui.componentes.ModernDropdownSmall
import com.example.healthbichito.ui.componentes.ModernSnackbar
import com.example.healthbichito.ui.componentes.ModernTextField
import com.example.healthbichito.ui.componentes.SnackbarType
import com.example.healthbichito.ui.componentes.TimePickerRedondeado
import com.example.healthbichito.ui.theme.BackgroundGray
import com.example.healthbichito.ui.theme.PrimaryGreen
import kotlinx.coroutines.launch

@Composable
fun AgregarMedicamentoScreen(
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() }
) {

    var nombre by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("mg") }
    var hora by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scheduler = remember { MedicacionScheduler(context) }

    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarType by remember { mutableStateOf(SnackbarType.Error) }
    var snackbarTrigger by remember { mutableLongStateOf(0L) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .verticalScroll(scrollState)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryGreen)
                .padding(top = 35.dp, bottom = 25.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp).clickable { onBack() }
                )

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    "Agregar Medicamento",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            PrimaryGreen.copy(alpha = 0.28f),
                            PrimaryGreen.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    "Nuevo medicamento",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    "Ingresa los datos del medicamento para mantener un registro de tus dosis.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp)
                )

                ModernTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = "Nombre del medicamento",
                    icon = Icons.Default.Medication,
                    tint = PrimaryGreen
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(0.65f)) {
                        ModernTextField(
                            value = dosis,
                            onValueChange = { dosis = it },
                            label = "Dosis",
                            icon = Icons.Default.Edit,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            tint = PrimaryGreen
                        )
                    }
                    Box(modifier = Modifier.weight(0.35f)) {
                        ModernDropdownSmall(
                            value = unidad,
                            label = "Unidad",
                            options = listOf("mg", "g", "ml", "tableta"),
                            onSelect = { unidad = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TimePickerRedondeado(
                    horaActual = hora,
                    onHoraSeleccionada = { hora = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                ModernTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    label = "Observaciones (opcional)",
                    icon = Icons.Default.Edit,
                    tint = PrimaryGreen
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (nombre.isBlank() || dosis.isBlank() || hora.isBlank()) {
                            snackbarMessage = "Completa todos los campos obligatorios"
                            snackbarType = SnackbarType.Error
                            snackbarTrigger = System.currentTimeMillis()
                            return@Button
                        }

                        scope.launch {
                            var nuevaMedicacion = Medicacion(
                                nombre_medicamento = nombre,
                                dosis = dosis,
                                unidad = unidad,
                                hora = hora,
                                observaciones = observaciones,
                                activo = 1
                            )

                            val idGenerado = FirebaseMedicacionHelper.agregarMedicamento(nuevaMedicacion)

                            if (idGenerado != null) {
                                nuevaMedicacion = nuevaMedicacion.copy(id = idGenerado)

                                // ✅ Programar la alarma y comprobar el resultado
                                val alarmaExactaProgramada = scheduler.programarAlarma(nuevaMedicacion)

                                if (!alarmaExactaProgramada) {
                                    // ✅ Si no se pudo, mostrar aviso
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "snackbar_message",
                                        "¡Recuerda dar permiso de Alarmas para notificaciones exactas!"
                                    )
                                }

                                navController.previousBackStackEntry?.savedStateHandle?.set("med_added", true)
                                navController.popBackStack()

                            } else {
                                snackbarMessage = "Error al guardar en Firebase"
                                snackbarType = SnackbarType.Error
                                snackbarTrigger = System.currentTimeMillis()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(PrimaryGreen),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        "Guardar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        if (snackbarMessage.isNotEmpty()) {
            ModernSnackbar(
                message = snackbarMessage,
                type = snackbarType,
                trigger = snackbarTrigger
            )
        }
    }
}