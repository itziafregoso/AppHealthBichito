package com.example.healthbichito.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbichito.R
import com.example.healthbichito.data.firebase.FirebaseAuthHelper
import com.example.healthbichito.ui.componentes.ModernTextField
import com.example.healthbichito.ui.componentes.ModernSnackbar
import com.example.healthbichito.ui.componentes.SnackbarType
import com.example.healthbichito.ui.componentes.ModernDropdown
import com.example.healthbichito.ui.theme.PrimaryGreen
import com.example.healthbichito.ui.theme.AccentOrange
import com.example.healthbichito.util.BirthdateTransformation
import com.example.healthbichito.util.DateValidator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import com.example.healthbichito.ui.theme.TextDark

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repetirPassword by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }


    var mensaje by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(SnackbarType.Error) }
    var trigger by remember { mutableStateOf(0L) }

    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(trigger) {
        if (mensaje.isNotEmpty()) {
            delay(150)
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

        Spacer(Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.solobichito),
            contentDescription = "Logo",
            modifier = Modifier.size(110.dp)
        )

        Text(
            text = "Crear Cuenta",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Regístrate para continuar",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryGreen
        )

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ModernTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = "Nombre",
                    icon = Icons.Default.Person,
                    tint = PrimaryGreen
                )

                ModernTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = "Apellido",
                    icon = Icons.Default.Person,
                    tint = PrimaryGreen
                )

                ModernDropdown(
                    value = genero,
                    options = listOf("Femenino","Masculino","Otro","Prefiero no decirlo"),
                    label = "Género",
                    icon = Icons.Default.Person,
                    onSelect = { genero = it },
                    modifier = Modifier.fillMaxWidth(),

                )


                ModernTextField(
                    value = fechaNacimiento,
                    onValueChange = { fechaNacimiento = it },
                    label = "Fecha de nacimiento (AAAA-MM-DD)",
                    icon = Icons.Default.Person,
                    tint = PrimaryGreen,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = BirthdateTransformation()
                )

                ModernTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    icon = Icons.Default.Email,
                    tint = PrimaryGreen
                )

                ModernTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    icon = Icons.Default.Lock,
                    tint = PrimaryGreen,
                    password = true
                )

                ModernTextField(
                    value = repetirPassword,
                    onValueChange = { repetirPassword = it },
                    label = "Repetir contraseña",
                    icon = Icons.Default.Lock,
                    tint = PrimaryGreen,
                    password = true
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {

                        if (
                            nombre.isBlank() ||
                            apellido.isBlank() ||
                            fechaNacimiento.isBlank() ||
                            email.isBlank() ||
                            genero.isBlank() ||
                            password.isBlank() ||
                            repetirPassword.isBlank()
                        ) {
                            mensaje = "Completa todos los campos"
                            tipo = SnackbarType.Error
                            trigger = System.currentTimeMillis()
                            return@Button
                        }

                        if (password != repetirPassword) {
                            mensaje = "Las contraseñas no coinciden"
                            tipo = SnackbarType.Error
                            trigger = System.currentTimeMillis()
                            return@Button
                        }

                        val fechaFormateada =
                            if (fechaNacimiento.length == 10) fechaNacimiento
                            else BirthdateTransformation.formatInput(fechaNacimiento)

                        val resultado = DateValidator.validateBirthdate(fechaFormateada)

                        if (!resultado.valid) {
                            mensaje = resultado.error
                            tipo = SnackbarType.Error
                            trigger = System.currentTimeMillis()
                            return@Button
                        }

                        val edad = resultado.age

                        FirebaseAuthHelper.registrarUsuario(
                            email = email,
                            password = password,
                            datosExtra = mapOf(
                                "nombre" to nombre,
                                "apellido" to apellido,
                                "genero" to genero,
                                "fechaNacimiento" to fechaFormateada,
                                "edad" to edad,
                                "email" to email
                            ),
                            onSuccess = {
                                mensaje = "Usuario registrado ✅"
                                tipo = SnackbarType.Success
                                trigger = System.currentTimeMillis()

                                coroutine.launch {
                                    delay(1200)
                                    onRegisterSuccess()
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
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Crear cuenta",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = onCancel) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        color = AccentOrange
                    )
                }

                Spacer(Modifier.height(12.dp))

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




