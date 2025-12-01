package com.example.healthbichito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.healthbichito.notifications.aguanotificacion.NotificacionAgua
import com.example.healthbichito.permissions.PermissionUtils
import com.example.healthbichito.ui.navigation.AppNavHost
import com.example.healthbichito.ui.theme.HealthBichitoTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Permiso de notificaciones (Android 13+)
        PermissionUtils.solicitarPermisoNotificaciones(this)

        // 2) Canal de notificaciones de agua
        NotificacionAgua.crearCanal(this)

        // 3) Verificar si hay sesión iniciada
        val currentUser = FirebaseAuth.getInstance().currentUser
        val startDestination = if (currentUser != null) "dashboard" else "splash"

        setContent {
            HealthBichitoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination
                    )

                    // 🔹 Detectar si la app fue abierta desde una notificación
                    LaunchedEffect(Unit) {
                        val startDestination =
                            if (currentUser != null && intent?.getBooleanExtra("open_dashboard", false) == true)
                                "dashboard"
                            else if (currentUser != null)
                                "dashboard"
                            else
                                "splash"

                    }
                }
            }
        }
    }
}

