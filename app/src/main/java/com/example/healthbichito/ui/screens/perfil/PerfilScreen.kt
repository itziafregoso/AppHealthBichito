package com.example.healthbichito.ui.screens.perfil

import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
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
import com.example.healthbichito.ui.componentes.LogoutConfirmationDialog
import com.example.healthbichito.ui.componentes.ModernSnackbar
import com.example.healthbichito.ui.componentes.ModernTextField
import com.example.healthbichito.ui.componentes.SnackbarType
import com.example.healthbichito.ui.theme.AzulFuerte
import com.example.healthbichito.ui.theme.AzulSuave
import com.example.healthbichito.ui.theme.PrimaryGreen
import com.example.healthbichito.ui.theme.VerdeFuerte
import com.example.healthbichito.ui.theme.VerdeSuave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilLoader = viewModel()
) {
    val uiState = viewModel.uiState
    val usuario = uiState.usuario

    var datosSnackbarMessage by remember { mutableStateOf<String?>(null) }
    var datosSnackbarTrigger by remember { mutableStateOf(0L) }
    var metasSnackbarMessage by remember { mutableStateOf<String?>(null) }
    var metasSnackbarTrigger by remember { mutableStateOf(0L) }
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else if (usuario != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // DATOS PERSONALES
                item {
                    ProfileSectionCard(
                        title = "Datos Personales",
                        headerColor = AzulFuerte,
                        cardColor = AzulSuave
                    ) {
                        ModernTextField(
                            value = usuario.nombre,
                            onValueChange = {},
                            label = "Nombre",
                            icon = Icons.Default.Person,
                            enabled = false,
                            tint = AzulFuerte
                        )
                        Spacer(Modifier.height(8.dp))
                        ModernTextField(
                            value = usuario.email,
                            onValueChange = {},
                            label = "Email",
                            icon = Icons.Default.Email,
                            enabled = false,
                            tint = AzulFuerte
                        )
                        Spacer(Modifier.height(8.dp))

                        ModernTextField(
                            value = uiState.editable.altura,
                            onValueChange = { viewModel.onCampoEditado("altura", it) },
                            label = "Altura (m)",
                            icon = Icons.Default.Height,
                            tint = AzulFuerte,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        Spacer(Modifier.height(8.dp))

                        ModernTextField(
                            value = uiState.editable.peso,
                            onValueChange = { viewModel.onCampoEditado("peso", it) },
                            label = "Peso (kg)",
                            icon = Icons.Default.MonitorWeight,
                            tint = AzulFuerte,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.savePerfil { msg ->
                                    datosSnackbarMessage = msg
                                    datosSnackbarTrigger = System.currentTimeMillis()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AzulFuerte)
                        ) {
                            Text("Guardar Datos", color = Color.White)
                        }

                        datosSnackbarMessage?.let {
                            Spacer(Modifier.height(16.dp))
                            ModernSnackbar(message = it, type = SnackbarType.Success, trigger = datosSnackbarTrigger)
                        }
                    }
                }

                // MIS METAS
                item {
                    ProfileSectionCard(
                        title = "Mis Metas",
                        headerColor = VerdeFuerte,
                        cardColor = VerdeSuave
                    ) {
                        ModernTextField(
                            value = uiState.editable.metaPasos,
                            onValueChange = { viewModel.onCampoEditado("metaPasos", it) },
                            label = "Meta de Pasos Diarios",
                            icon = Icons.Default.DirectionsWalk,
                            tint = VerdeFuerte,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(Modifier.height(8.dp))

                        ModernTextField(
                            value = uiState.editable.metaCalorias,
                            onValueChange = { viewModel.onCampoEditado("metaCalorias", it) },
                            label = "Meta de Calorías",
                            icon = Icons.Default.LocalFireDepartment,
                            tint = VerdeFuerte,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(Modifier.height(8.dp))

                        ModernTextField(
                            value = uiState.editable.metaAgua,
                            onValueChange = { viewModel.onCampoEditado("metaAgua", it) },
                            label = "Meta de Agua (ml)",
                            icon = Icons.Default.WaterDrop,
                            tint = VerdeFuerte,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(Modifier.height(8.dp))

                        ModernTextField(
                            value = uiState.editable.metaPeso,
                            onValueChange = { viewModel.onCampoEditado("metaPeso", it) },
                            label = "Meta de Peso (kg)",
                            icon = Icons.Default.FitnessCenter,
                            tint = VerdeFuerte,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.saveMetas { msg ->
                                    metasSnackbarMessage = msg
                                    metasSnackbarTrigger = System.currentTimeMillis()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeFuerte)
                        ) {
                            Text("Guardar Metas", color = Color.White)
                        }

                        metasSnackbarMessage?.let {
                            Spacer(Modifier.height(16.dp))
                            ModernSnackbar(message = it, type = SnackbarType.Success, trigger = metasSnackbarTrigger)
                        }
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                viewModel.signOut()
                navController.navigate("login") { popUpTo(0) }
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}


@Composable
private fun ProfileSectionCard(
    title: String,
    headerColor: Color,
    cardColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor)
                    .padding(vertical = 14.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}

