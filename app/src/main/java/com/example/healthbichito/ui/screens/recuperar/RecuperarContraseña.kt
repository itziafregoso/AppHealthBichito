package com.example.healthbichito.ui.screens.recuperar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbichito.R
import com.example.healthbichito.ui.componentes.ModernSnackbar
import com.example.healthbichito.ui.componentes.ModernTextField
import com.example.healthbichito.ui.componentes.SnackbarType
import com.example.healthbichito.ui.theme.PrimaryGreen
import com.example.healthbichito.ui.theme.AccentOrange
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun RecuperarContraseña(
    onBackToLogin: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var enviado by remember { mutableStateOf(false) }

    var mensaje by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(SnackbarType.Error) }
    var trigger by remember { mutableStateOf(0L) }

    val scroll = rememberScrollState()

    LaunchedEffect(trigger) {
        if (mensaje.isNotEmpty()) {
            delay(150)
            scroll.animateScrollTo(scroll.maxValue)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scroll)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            if (!enviado) {

                Image(
                    painter = painterResource(id = R.drawable.solobichito),
                    contentDescription = null,
                    modifier = Modifier.size(130.dp)
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Recuperar Contraseña",
                    fontSize = 28.sp,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Ingresa tu correo y te enviaremos un enlace\npara restablecer tu contraseña.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(30.dp))

                ModernTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    icon = Icons.Default.Email,
                    tint = PrimaryGreen,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(Modifier.height(30.dp))

                Button(
                    onClick = {
                        if (!email.contains("@")) {
                            mensaje = "Ingresa un correo válido"
                            tipo = SnackbarType.Error
                            trigger = System.currentTimeMillis()
                            return@Button
                        }

                        isLoading = true

                        FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(email.trim())
                            .addOnSuccessListener {
                                isLoading = false
                                enviado = true
                                mensaje = "Correo enviado ✅"
                                tipo = SnackbarType.Success
                                trigger = System.currentTimeMillis()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                mensaje = e.message ?: "Error inesperado"
                                tipo = SnackbarType.Error
                                trigger = System.currentTimeMillis()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    ),
                    enabled = !isLoading
                ) {

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Enviar enlace",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(Modifier.height(25.dp))

                TextButton(onClick = onBackToLogin) {
                    Text(
                        text = "Volver al inicio de sesión",
                        color = AccentOrange,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(80.dp))
            }

            // ✅ PANTALLA DE ÉXITO
            if (enviado) {

                Spacer(Modifier.height(40.dp))

                Icon(
                    imageVector = Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "¡Revisa tu correo!",
                    fontSize = 28.sp,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Te enviamos un enlace para restablecer tu contraseña.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(40.dp))

                Button(
                    onClick = onBackToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Volver al inicio",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(Modifier.height(80.dp))
            }
        }

        // ✅ SNACKBAR
        if (mensaje.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                ModernSnackbar(
                    message = mensaje,
                    type = tipo,
                    trigger = trigger,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


