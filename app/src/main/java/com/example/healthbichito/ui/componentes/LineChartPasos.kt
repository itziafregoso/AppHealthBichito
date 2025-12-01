package com.example.healthbichito.ui.componentes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun LineChartPasos(
    historialPasos: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    if (historialPasos.isEmpty()) return

    // 🔹 Separar fechas y valores para graficar
    val fechas = historialPasos.keys.toList()
    val valores = historialPasos.values.toList()

    val maxValor = valores.maxOrNull() ?: 1
    val animationProgress by animateFloatAsState(targetValue = 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Evolución de Pasos",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF01579B),
                fontSize = 18.sp
            )
            Spacer(Modifier.height(8.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                scale(scaleX = animationProgress, scaleY = 1f) {

                    // 🔹 Dibujar la línea
                    for (i in 1 until valores.size) {
                        val x1 = (i - 1) * size.width / (valores.size - 1)
                        val y1 = size.height - (valores[i - 1] * size.height / maxValor)
                        val x2 = i * size.width / (valores.size - 1)
                        val y2 = size.height - (valores[i] * size.height / maxValor)

                        drawLine(
                            color = Color(0xFF01579B),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 6f
                        )
                    }

                    // 🔹 Dibujar puntos
                    valores.forEachIndexed { index, valor ->
                        val x = index * size.width / (valores.size - 1)
                        val y = size.height - (valor * size.height / maxValor)

                        drawCircle(
                            color = Color(0xFF0277BD),
                            center = Offset(x, y),
                            radius = 8f
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // 🔹 Etiquetas de fecha (primer día, medio y último)
            if (fechas.size >= 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(fechas.first(), fontSize = 12.sp, color = Color.Gray)
                    Text("...", fontSize = 12.sp, color = Color.Gray)
                    Text(fechas.last(), fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
