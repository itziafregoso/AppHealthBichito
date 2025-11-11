package com.example.healthbichito.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbichito.ui.theme.AccentBlue
import com.example.healthbichito.ui.theme.PrimaryGreen

@Composable
fun WaterIntakeCard(
    totalMl: Int,
    metaMl: Int = 2000,               // ✅ Meta diaria por defecto
    onAdd: () -> Unit,
    onSubtract: () -> Unit,
    isDisabled: Boolean = false
) {
    // ✅ Cálculo del progreso normalizado (0 a 8)
    val pasos = (totalMl.toFloat() / metaMl.toFloat()) * 8
    val porcentaje = pasos.toInt().coerceIn(0, 8)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------- Texto ml ----------
            Text(
                text = "$totalMl ml",
                color = PrimaryGreen,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(25.dp))

            // ---------- Bolitas ----------
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 25.dp)
            ) {
                repeat(8) { index ->
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < porcentaje)
                                    AccentBlue
                                else
                                    Color(0xFFBDBDBD).copy(alpha = 0.35f)
                            )
                    )
                }
            }

            // ---------- Botones ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ---- (-) restar ----
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB74D))
                        .clickable { onSubtract() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(25.dp))

                // ---- (+) sumar ----
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDisabled) Color(0xFFA5D6A7) // verde clarito desactivado
                            else PrimaryGreen
                        )
                        .clickable(enabled = !isDisabled) { onAdd() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}




