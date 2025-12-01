package com.example.healthbichito.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.healthbichito.ui.screens.dashboard.DashboardLoader
import com.example.healthbichito.ui.screens.login.LoginScreen
import com.example.healthbichito.ui.screens.agregarmedicamento.AgregarMedicamentoScreen
import com.example.healthbichito.ui.screens.dashboard.EstadisticasScreen
import com.example.healthbichito.ui.screens.perfil.PerfilScreen
import com.example.healthbichito.ui.screens.recuperar.RecuperarContraseña
import com.example.healthbichito.ui.screens.register.RegisterScreen
import com.example.healthbichito.ui.screens.splash.SplashScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- Splash ---
        composable("splash") {
            SplashScreen(
                onFinish = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // --- Login ---
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate("register") },
                onGoToRecuperar = { navController.navigate("recuperar") }
            )
        }

        // --- Registro ---
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        // --- Recuperar contraseña ---
        composable("recuperar") {
            RecuperarContraseña(
                onBackToLogin = {
                    navController.navigate("login") {
                        popUpTo("recuperar") { inclusive = true }
                    }
                }
            )
        }

        // --- Dashboard con DeepLink para notificaciones ---
        composable(
            route = "dashboard",
            deepLinks = listOf(
                navDeepLink { uriPattern = "healthbichito://dashboard" }
            )
        ) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            DashboardLoader(
                userId = userId,
                navController = navController
            )
        }

        // --- Agregar medicamento ---
        composable("agregar_medicamento") {
            AgregarMedicamentoScreen(navController = navController)
        }

        // --- Perfil ---
        composable("profile") {
            PerfilScreen(navController = navController)
        }

        // --- Estadísticas ---
        composable("estadisticas") {
            val app = LocalContext.current.applicationContext as Application
            EstadisticasScreen(
                navController = navController,
                application = app
            )
        }

    }
}

