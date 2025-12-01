package com.example.healthbichito.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HealthMonitorItem(
    titulo: String,
    valor: Int,
    meta: Int? = null,
    modifier: Modifier = Modifier
) {
    val progreso = if (meta != null) (valor.toFloat() / meta.toFloat()).coerceIn(0f, 1f) else null

    // Ícono y color predeterminados según el tipo
    val (icono: ImageVector, colorBase: Color) = when (titulo) {
        "Ritmo cardíaco" -> Icons.Default.Favorite to Color(0xFFE53935)
        "Pasos" -> Icons.AutoMirrored.Filled.DirectionsRun to Color(0xFFFF9800)
        "Calorías" -> Icons.Default.LocalFireDepartment to Color(0xFF01579B)
        else -> Icons.Default.Favorite to Color.Gray
    }

    // Cambia color a verde si se cumple la meta
    val colorFinal = if (meta != null && valor >= meta) Color(0xFF4CAF50) else colorBase

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(50))
                    .background(colorFinal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = colorFinal,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = when (titulo) {
                        "Ritmo cardíaco" -> "$valor bpm"
                        "Calorías" -> "$valor kcal"
                        else -> "$valor"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorFinal
                )

                if (progreso != null && meta != null) {
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = progreso,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = colorFinal,
                        trackColor = Color.LightGray.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "Meta: $meta",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}





