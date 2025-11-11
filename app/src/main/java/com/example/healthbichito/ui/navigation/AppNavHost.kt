package com.example.healthbichito.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthbichito.ui.screens.dashboard.DashboardLoader
import com.example.healthbichito.ui.screens.login.LoginScreen
import com.example.healthbichito.ui.screens.agregarmedicamento.AgregarMedicamentoScreen
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

        // --- Rutas existentes ---

        composable("splash") { SplashScreen(onFinish = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } }) }
        composable("login") { LoginScreen(onLoginSuccess = { navController.navigate("dashboard") { popUpTo("login") { inclusive = true } } }, onGoToRegister = { navController.navigate("register") }, onGoToRecuperar = { navController.navigate("recuperar") }) }
        composable("register") { RegisterScreen(onRegisterSuccess = { navController.navigate("login") { popUpTo("register") { inclusive = true } } }, onCancel = { navController.popBackStack() }) }
        composable("recuperar") { RecuperarContraseña(onBackToLogin = { navController.navigate("login") { popUpTo("recuperar") { inclusive = true } } }) }
        composable("dashboard") { val userId = FirebaseAuth.getInstance().currentUser?.uid; DashboardLoader(userId = userId, navController = navController) }
        composable("agregar_medicamento") { AgregarMedicamentoScreen(navController = navController) }
        composable("profile") { PerfilScreen(navController = navController) }
    }
}
