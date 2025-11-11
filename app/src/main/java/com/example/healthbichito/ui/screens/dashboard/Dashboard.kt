package com.example.healthbichito.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthbichito.data.firebase.FirebaseAguaHelper
import com.example.healthbichito.data.firebase.FirebaseMetaHelper
import com.example.healthbichito.data.firebase.FirebaseUsuarioHelper
import com.example.healthbichito.data.model.Medicacion
import com.example.healthbichito.notifications.aguanotificacion.NotificacionMetaAgua
import com.example.healthbichito.permissions.PermissionUtils
import com.example.healthbichito.ui.componentes.MonitorCard
import com.example.healthbichito.ui.componentes.WaterIntakeCard
import com.example.healthbichito.ui.theme.PrimaryGreen
import com.example.healthbichito.data.wear.WearListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun DashboardScreen(
    nombreUsuario: String,
    medicamentos: List<Medicacion>,
    onAgregarMedicamento: () -> Unit,
    onActualizarMedicamento: (Medicacion) -> Unit,
    onEliminarMedicamento: (Medicacion) -> Unit,
    navController: NavController
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // -----------------------------
    // AGUA STATES
    // -----------------------------
    var metaAgua by remember { mutableStateOf(2000) }
    var registroId by remember { mutableStateOf("") }
    var aguaActual by remember { mutableStateOf(0) }
    var metaAlcanzada by remember { mutableStateOf(false) }
    var genero by remember { mutableStateOf("") }

    // -----------------------------
    // CARGA DE INICIAL (CORREGIDA)
    // -----------------------------
    LaunchedEffect(true) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            // Cargar metas y datos de usuario con los nuevos helpers
            val metas = FirebaseMetaHelper.getMetas(uid)
            val usuario = FirebaseUsuarioHelper.getUsuario(uid)

            // Asignar valores de forma segura
            metaAgua = metas?.metaAgua ?: 2000
            genero = usuario?.genero ?: ""
        }

        // El registro de agua parece ser independiente, lo dejamos como estaba
        val registro = FirebaseAguaHelper.obtenerRegistroHoy()
        registroId = registro.id_agua
        aguaActual = registro.cantidad_ml
        metaAlcanzada = aguaActual >= metaAgua
    }

    val heartRate by WearListener.ritmoCardiaco.collectAsState()
    val pasos by WearListener.pasos.collectAsState()
    val calorias by WearListener.calorias.collectAsState()

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
    ) {

        // ----------------------------------------------------
        // ✅ HEADER
        // ----------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RectangleShape, clip = false)
                .background(PrimaryGreen)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Panel de Salud",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    saludoSegunGenero(nombreUsuario, genero),
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.95f)
                )
            }

            // Ícono de perfil (CORREGIDO: Navega a la pantalla de perfil)
            IconButton(
                onClick = { navController.navigate("profile") },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 25.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Ir al perfil",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }


        Spacer(Modifier.height(25.dp))

        // ----------------------------------------------------
        // ✅ MONITOREO DE SALUD
        // ----------------------------------------------------
        Text(
            "Monitoreo de Salud",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            MonitorCard(
                titulo = "Ritmo cardiaco",
                valor = "$heartRate bpm",
                icono = Icons.Default.Favorite,
                color = Color(0xFFF44336)
            )

            MonitorCard(
                titulo = "Pasos",
                valor = pasos.toString(),
                icono = Icons.Default.DirectionsRun,
                color = Color(0xFFFF9800)
            )

            MonitorCard(
                titulo = "Calorías",
                valor = "${String.format("%.1f", calorias)} kcal",
                icono = Icons.Default.LocalFireDepartment,
                color = Color(0xFF29B6F6)
            )

        }

        Spacer(Modifier.height(25.dp))

        // ----------------------------------------------------
        // ✅ INGESTA DE AGUA
        // ----------------------------------------------------
        Text(
            "Ingesta de Agua",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp)
        )

        Spacer(Modifier.height(20.dp))

        suspend fun agregarAgua() {
            if (metaAlcanzada) return
            val nuevo = (aguaActual + 250).coerceAtMost(metaAgua)
            aguaActual = nuevo

            FirebaseAguaHelper.actualizarRegistro(registroId, nuevo)

            if (nuevo >= metaAgua) {
                PermissionUtils.vibrar(context)
                NotificacionMetaAgua.enviar(context)
                metaAlcanzada = true
            }
        }

        suspend fun restarAgua() {
            val nuevo = (aguaActual - 250).coerceAtLeast(0)
            aguaActual = nuevo

            FirebaseAguaHelper.actualizarRegistro(registroId, nuevo)

            if (nuevo < metaAgua) metaAlcanzada = false
        }

        WaterIntakeCard(
            totalMl = aguaActual,
            metaMl = metaAgua,
            onAdd = { scope.launch { agregarAgua() } },
            onSubtract = { scope.launch { restarAgua() } },
            isDisabled = metaAlcanzada
        )

        Spacer(Modifier.height(35.dp))

        // ----------------------------------------------------
        // ✅ MEDICACIÓN — DISEÑO LOCAL ADAPTADO
        // ----------------------------------------------------

        Text(
            "Medicacion",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp)
        )

        Spacer(Modifier.height(20.dp))

        MedicacionSection(navController = navController)

        Spacer(Modifier.height(20.dp))

    }
}


fun saludoSegunGenero(nombre: String, genero: String): String {
    return when (genero.lowercase()) {
        "femenino" -> "¡Bienvenida, $nombre!"
        "masculino" -> "¡Bienvenido, $nombre!"
        "otro" -> "¡Bienvenide, $nombre!"
        else -> "¡Hola, $nombre!"
    }
}
