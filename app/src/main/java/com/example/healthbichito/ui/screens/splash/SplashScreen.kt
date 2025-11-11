package com.example.healthbichito.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthbichito.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinish: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000) // ⏳ Duración del splash
        onFinish()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🐞 Logo de Bichito
        Image(
            painter = painterResource(id = R.drawable.solobichito), // cambia al tuyo
            contentDescription = "Logo Health Bichito",
            modifier = Modifier.size(200.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Health Bichito+",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Tu pequeño aliado para una vida más sana",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(30.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            strokeWidth = 4.dp,
            color = MaterialTheme.colorScheme.tertiary // naranja
        )
    }
}


