package com.example.healthbichito.ui.screens.dashboard

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.healthbichito.ui.componentes.ModernSnackbar
import com.example.healthbichito.ui.componentes.ModernTextField
import com.example.healthbichito.ui.componentes.SnackbarType
import com.example.healthbichito.ui.theme.AzulFuerte
import com.example.healthbichito.ui.theme.PrimaryGreen
import com.example.healthbichito.ui.theme.VerdeSuave
import com.example.healthbichito.ui.viewmodels.EstadisticasViewModel
import com.example.healthbichito.util.BirthdateTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    navController: NavController,
    application: Application,
    viewModel: EstadisticasViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarType by remember { mutableStateOf(SnackbarType.Info) }
    var snackbarTrigger by remember { mutableStateOf(0L) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.generarPDF(context) { mensaje ->
                                snackbarMessage = mensaje
                                snackbarType = if (mensaje.contains("Error", true) || mensaje.contains("Analiza", true)) SnackbarType.Error else SnackbarType.Success
                                snackbarTrigger = System.currentTimeMillis()
                            }
                        },
                        enabled = uiState.pdfBotonHabilitado // 👈 AÑADIDO
                    ) {
                        Icon(
                            Icons.Default.PictureAsPdf, 
                            contentDescription = "Generar PDF",
                            tint = if (uiState.pdfBotonHabilitado) Color.White else Color.Gray // 👈 AÑADIDO
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 👤 RESUMEN DEL DÍA
            item {
                EstadisticaCard(title = "Resumen del día") {
                    Text("Pasos: ${uiState.resumenDia.pasos}")
                    Text("Calorías: ${String.format("%.2f", uiState.resumenDia.calorias)}")
                    Text("Agua ingerida: ${uiState.resumenDia.agua} ml")
                    Text("Medicamentos tomados: ${if (uiState.resumenDia.medicacion) "Sí" else "No"}")
                }
            }

            // 📅 SELECCIÓN DE FECHAS
            item {
                Text("Estadísticas por rango de fechas", fontWeight = FontWeight.Bold)

                ModernTextField(
                    value = uiState.fechaInicio,
                    onValueChange = viewModel::actualizarFechaInicio,
                    label = "Fecha Inicio (AAAA-MM-DD)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = BirthdateTransformation()
                )

                Spacer(Modifier.height(8.dp))

                ModernTextField(
                    value = uiState.fechaFin,
                    onValueChange = viewModel::actualizarFechaFin,
                    label = "Fecha Fin (AAAA-MM-DD)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = BirthdateTransformation()
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.calcularResumenRango(context) { mensaje ->
                            snackbarMessage = mensaje
                            snackbarType = if (mensaje.contains("Error", true)) SnackbarType.Error else SnackbarType.Success
                            snackbarTrigger = System.currentTimeMillis()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulFuerte
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Analizar rango de fechas", color = Color.White)
                }
            }

            // 📊 RESUMEN DEL RANGO
            if (uiState.resumenRango.totalDias > 0) {
                item {
                    EstadisticaCard(title = "Resumen del rango analizado") {
                        Text("Día con más pasos: ${uiState.resumenRango.diaMaxPasos}")
                        Text("Máximo de pasos: ${uiState.resumenRango.maxPasos}")
                        Text("Promedio de pasos: ${String.format("%.1f", uiState.resumenRango.promedioPasos)}")
                        Text("Días con meta de agua cumplida: ${uiState.resumenRango.diasMetaAguaCumplida}")
                        Text("Días analizados: ${uiState.resumenRango.totalDias}")
                    }
                }
            }
        }

        // 🔔 SNACKBAR
        if (snackbarMessage.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ModernSnackbar(
                    message = snackbarMessage,
                    type = snackbarType,
                    trigger = snackbarTrigger,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun EstadisticaCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VerdeSuave),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = AzulFuerte)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}


