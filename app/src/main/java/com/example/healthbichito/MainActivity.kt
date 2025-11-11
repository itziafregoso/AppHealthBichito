package com.example.healthbichito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.healthbichito.permissions.PermissionUtils
import com.example.healthbichito.ui.navigation.AppNavHost
import com.example.healthbichito.ui.theme.HealthBichitoTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        PermissionUtils.solicitarPermisoNotificaciones(this)

        // Verificar si el usuario ya ha iniciado sesión
        val currentUser = FirebaseAuth.getInstance().currentUser
        val startDestination = if (currentUser != null) "dashboard" else "splash"


        setContent {
            HealthBichitoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavHost(startDestination = startDestination)
                }
            }
        }
    }
}
