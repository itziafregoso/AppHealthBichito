package com.example.healthbichito.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbichito.R
import com.example.healthbichito.data.firebase.FirebaseAuthHelper
import com.example.healthbichito.ui.componentes.ModernSnackbar
import com.example.healthbichito.ui.componentes.ModernTextField
import com.example.healthbichito.ui.componentes.SnackbarType
import com.example.healthbichito.ui.theme.PrimaryGreen
import com.example.healthbichito.ui.theme.AccentOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import com.example.healthbichito.ui.theme.TextDark

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit,
    onGoToRecuperar: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(SnackbarType.Error) }
    var trigger by remember { mutableStateOf(0L) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // ✅ Auto-scroll cuando aparece snackbar
    LaunchedEffect(trigger) {
        if (mensaje.isNotEmpty()) {
            delay(120)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(40.dp))

        // ✅ Logo
        Image(
            painter = painterResource(id = R.drawable.solobichito),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "Health Bichito+",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp,
            color = TextDark
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Bienvenido",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = PrimaryGreen
        )

        Spacer(Modifier.height(25.dp))

        // ✅ CARD del formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Iniciar Sesión",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // ✅ EMAIL
                ModernTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo",
                    icon = Icons.Default.Email,
                    tint = PrimaryGreen
                )

                // ✅ CONTRASEÑA
                ModernTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    icon = Icons.Default.Lock,
                    tint = PrimaryGreen,
                    password = true
                )

                Spacer(Modifier.height(20.dp))

                // ✅ BOTÓN PRINCIPAL VERDE
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            mensaje = "Por favor llena todos los campos"
                            tipo = SnackbarType.Error
                            trigger = System.currentTimeMillis()
                            return@Button
                        }

                        FirebaseAuthHelper.loginUsuario(
                            email = email,
                            password = password,
                            onSuccess = {
                                mensaje = "Inicio de sesión exitoso ✅"
                                tipo = SnackbarType.Success
                                trigger = System.currentTimeMillis()

                                scope.launch {
                                    delay(1200)
                                    onLoginSuccess()
                                }
                            },
                            onError = { error ->
                                mensaje = error
                                tipo = SnackbarType.Error
                                trigger = System.currentTimeMillis()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Ingresar",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp))

                // ✅ OLVIDASTE TU CONTRASEÑA
                TextButton(onClick = onGoToRecuperar) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = AccentOrange,
                        fontSize = 14.sp
                    )
                }

                // ✅ CREAR CUENTA
                TextButton(onClick = onGoToRegister) {
                    Text(
                        text = "¿No tienes cuenta? Crea una",
                        color = AccentOrange,
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                // ✅ SNACKBAR dentro de la CARD
                if (mensaje.isNotEmpty()) {
                    ModernSnackbar(
                        message = mensaje,
                        type = tipo,
                        trigger = trigger,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}




