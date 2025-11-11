package com.example.healthbichito.ui.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MonitorCard(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = modifier
            .width(115.dp)
            .height(130.dp)
            .padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Icon(
                icono,
                contentDescription = titulo,
                tint = color,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = titulo,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = valor,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}
