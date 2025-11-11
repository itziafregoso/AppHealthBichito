package com.example.healthbichito.ui.screens.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.healthbichito.ui.componentes.ModernSnackbar
import com.example.healthbichito.ui.componentes.ModernTextField
import com.example.healthbichito.ui.componentes.SnackbarType
import com.example.healthbichito.ui.screens.perfil.PerfilLoader
import com.example.healthbichito.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilLoader = viewModel()
) {
    val uiState = viewModel.uiState
    val usuario = uiState.usuario

    // --- ESTADO PARA SNACKBAR DE DATOS PERSONALES ---
    var datosSnackbarMessage by remember { mutableStateOf<String?>(null) }
    var datosSnackbarType by remember { mutableStateOf(SnackbarType.Success) }
    var datosSnackbarTrigger by remember { mutableStateOf(0L) }

    // --- ESTADO PARA SNACKBAR DE METAS ---
    var metasSnackbarMessage by remember { mutableStateOf<String?>(null) }
    var metasSnackbarType by remember { mutableStateOf(SnackbarType.Success) }
    var metasSnackbarTrigger by remember { mutableStateOf(0L) }

    val personalDataCardColor = Color(0xFFE3F2FD)
    val goalsCardColor = Color(0xFFE8F5E9)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.signOut()
                        navController.navigate("login") { popUpTo(0) }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else if (usuario != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    ProfileSectionCard(
                        title = "Datos Personales",
                        cardColor = personalDataCardColor
                    ) {
                        ModernTextField(
                            value = usuario.nombre,
                            onValueChange = {},
                            label = "Nombre",
                            icon = Icons.Default.Person,
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ModernTextField(
                            value = usuario.email,
                            onValueChange = {},
                            label = "Email",
                            icon = Icons.Default.Email,
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ModernTextField(
                            value = usuario.perfil.altura.toString(),
                            onValueChange = viewModel::onAlturaChanged,
                            label = "Altura (m)",
                            icon = Icons.Default.Height,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ModernTextField(
                            value = usuario.perfil.peso.toString(),
                            onValueChange = viewModel::onPesoChanged,
                            label = "Peso (kg)",
                            icon = Icons.Default.MonitorWeight,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ModernTextField(
                            value = usuario.perfil.contactoEmergencia,
                            onValueChange = viewModel::onContactoEmergenciaChanged,
                            label = "Contacto de Emergencia",
                            icon = Icons.Default.ContactPhone,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.savePerfil { message ->
                                    datosSnackbarMessage = message
                                    datosSnackbarType = if (message.startsWith("Error")) SnackbarType.Error else SnackbarType.Success
                                    datosSnackbarTrigger = System.currentTimeMillis()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar Datos")
                        }

                        // --- SNACKBAR PARA DATOS PERSONALES ---
                        datosSnackbarMessage?.let {
                            Spacer(modifier = Modifier.height(16.dp))
                            ModernSnackbar(
                                message = it,
                                type = datosSnackbarType,
                                trigger = datosSnackbarTrigger,
                            )
                        }
                    }
                }
                item {
                    ProfileSectionCard(
                        title = "Mis Metas",
                        cardColor = goalsCardColor
                    ) {
                        ModernTextField(
                            value = usuario.metas.metaPasos.toString(),
                            onValueChange = viewModel::onMetaPasosChanged,
                            label = "Meta de Pasos Diarios",
                            icon = Icons.Default.DirectionsWalk,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ModernTextField(
                            value = usuario.metas.metaCalorias.toString(),
                            onValueChange = viewModel::onMetaCaloriasChanged,
                            label = "Meta de Calorías Diarias",
                            icon = Icons.Default.LocalFireDepartment,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ModernTextField(
                            value = usuario.metas.metaAgua.toString(),
                            onValueChange = viewModel::onMetaAguaChanged,
                            label = "Meta de Agua (ml)",
                            icon = Icons.Default.WaterDrop,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ModernTextField(
                            value = usuario.metas.metaPeso.toString(),
                            onValueChange = viewModel::onMetaPesoChanged,
                            label = "Meta de Peso (kg)",
                            icon = Icons.Default.FitnessCenter,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.saveMetas { message ->
                                    metasSnackbarMessage = message
                                    metasSnackbarType = if (message.startsWith("Error")) SnackbarType.Error else SnackbarType.Success
                                    metasSnackbarTrigger = System.currentTimeMillis()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar Metas")
                        }

                        // --- SNACKBAR PARA METAS ---
                        metasSnackbarMessage?.let {
                            Spacer(modifier = Modifier.height(16.dp))
                            ModernSnackbar(
                                message = it,
                                type = metasSnackbarType,
                                trigger = metasSnackbarTrigger,
                            )
                        }
                    }
                }
            }
        } else {
            // Puedes mostrar un mensaje si el usuario no se encuentra
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No se pudieron cargar los datos del usuario.")
            }
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    cardColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}