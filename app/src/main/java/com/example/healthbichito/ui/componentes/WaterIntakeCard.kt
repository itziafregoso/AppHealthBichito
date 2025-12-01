package com.example.healthbichito.ui.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WaterIntakeCardPro(
    totalMl: Int,
    metaMl: Int,
    onAdd: () -> Unit,
    onSubtract: () -> Unit,
    isDisabled: Boolean
) {
    val progreso = (totalMl.toFloat() / metaMl.toFloat()).coerceIn(0f, 1f)
    val porcentaje = (progreso * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF9F1)),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header con icono + texto
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalDrink,
                    contentDescription = "Agua",
                    tint = Color(0xFF33691E),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ingesta de Agua",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "$totalMl ml",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF33691E)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = progreso,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = Color(0xFF2D91EA),
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$porcentaje%  (Meta: $metaMl ml)",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onSubtract,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D)),
                    shape = CircleShape,
                    enabled = totalMl > 0
                ) {
                    Text("-", fontSize = 26.sp, color = Color.White)
                }

                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = CircleShape,
                    enabled = !isDisabled
                ) {
                    Text("+", fontSize = 26.sp, color = Color.White)
                }
            }
        }
    }
}







