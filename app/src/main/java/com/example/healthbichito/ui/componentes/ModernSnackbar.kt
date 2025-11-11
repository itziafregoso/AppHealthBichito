package com.example.healthbichito.ui.componentes

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModernSnackbar(
    message: String,
    type: SnackbarType = SnackbarType.Success,
    durationMillis: Long = 3000,
    modifier: Modifier = Modifier,
    trigger: Long = System.currentTimeMillis()
) {
    var visible by remember(trigger) { mutableStateOf(true) }

    LaunchedEffect(trigger) {
        visible = true
        kotlinx.coroutines.delay(durationMillis)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it / 2 } + fadeIn(tween(300)),
        exit = slideOutVertically { it / 2 } + fadeOut(tween(300))
    ) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(14.dp),
                color = when (type) {
                    SnackbarType.Success -> MaterialTheme.colorScheme.primary
                    SnackbarType.Error -> Color(0xFFD32F2F)
                    SnackbarType.Warning -> MaterialTheme.colorScheme.tertiary
                    SnackbarType.Info -> MaterialTheme.colorScheme.secondary
                }
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(vertical = 14.dp, horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 15.sp
                )
            }
        }
    }
}

enum class SnackbarType {
    Success, Error, Warning, Info
}