package com.example.healthbichito.ui.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.healthbichito.ui.theme.PrimaryGreen
import com.example.healthbichito.ui.theme.TextDark

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    tint: Color = PrimaryGreen,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    password: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true // ✅ PARÁMETRO AÑADIDO
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) tint else TextDark.copy(alpha = 0.4f)
                )
            }
        },
        trailingIcon = {
            if (password) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector =
                            if (passwordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                        contentDescription = "Mostrar u ocultar contraseña",
                        tint = tint
                    )
                }
            }
        },
        visualTransformation =
            if (password) {
                if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation()
            } else {
                visualTransformation
            },

        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        enabled = enabled, // ✅ PASAR EL PARÁMETRO

        shape = RoundedCornerShape(16.dp),

        // ✅ COLORES MEJORADOS
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = tint,
            unfocusedBorderColor = TextDark.copy(alpha = 0.5f),
            focusedLabelColor = tint,
            cursorColor = tint,
            unfocusedLabelColor = TextDark.copy(alpha = 0.6f),
            // Colores para el estado deshabilitado
            disabledBorderColor = TextDark.copy(alpha = 0.2f),
            disabledLabelColor = TextDark.copy(alpha = 0.5f),
            disabledTextColor = TextDark.copy(alpha = 0.8f),
            disabledLeadingIconColor = TextDark.copy(alpha = 0.4f)
        ),

        modifier = modifier.fillMaxWidth()
    )
}