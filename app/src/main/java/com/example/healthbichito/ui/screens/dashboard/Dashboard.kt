package com.example.healthbichito.ui.screens.dashboard

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.healthbichito.data.firebase.FirebaseAguaHelper
import com.example.healthbichito.data.firebase.FirebaseMetaHelper
import com.example.healthbichito.data.firebase.FirebaseUsuarioHelper
import com.example.healthbichito.data.model.Medicacion
import com.example.healthbichito.data.repositories.CaloriesRepository
import com.example.healthbichito.data.repositories.PesoRepository
import com.example.healthbichito.data.wear.WearListener
import com.example.healthbichito.notifications.aguanotificacion.NotificacionAgua
import com.example.healthbichito.permissions.PermissionUtils
import com.example.healthbichito.ui.componentes.HealthMonitorItem
import com.example.healthbichito.ui.componentes.WaterIntakeCardPro
import com.example.healthbichito.ui.componentes.WeightMonitorCard
import com.example.healthbichito.ui.theme.PrimaryGreen
import com.example.healthbichito.ui.viewmodels.CaloriesViewModel
import com.example.healthbichito.ui.viewmodels.CaloriesViewModelFactory
import com.example.healthbichito.ui.viewmodels.MetasViewModel
import com.example.healthbichito.ui.viewmodels.PesoViewModel
import com.example.healthbichito.ui.viewmodels.PesoViewModelFactory
import com.example.healthbichito.ui.viewmodels.RitmoCardiacoViewModel
import com.example.healthbichito.ui.viewmodels.RitmoCardiacoViewModelFactory
import com.example.healthbichito.ui.viewmodels.StepsViewModel
import com.example.healthbichito.ui.viewmodels.StepsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    // ===== VIEWMODELS =====
    val heartRateViewModel: RitmoCardiacoViewModel = viewModel(factory = RitmoCardiacoViewModelFactory())
    val uiHeartState by heartRateViewModel.uiState.collectAsState()

    val stepsViewModel: StepsViewModel = viewModel(factory = StepsViewModelFactory())
    val uiStepsState by stepsViewModel.uiState.collectAsState()

    val metasViewModel: MetasViewModel = viewModel()
    val metasState by metasViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {

        metasViewModel.cargarMetas()
    }

    val caloriesRepository = remember {
        CaloriesRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    }

    val caloriesViewModel: CaloriesViewModel = viewModel(
        factory = CaloriesViewModelFactory(
            context.applicationContext as Application,
            caloriesRepository
        )
    )

    val uiCaloriesState by caloriesViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        caloriesViewModel.cargarCaloriasDelDia()
    }

    // ===== ESCUCHA SENSORES WEAR =====
    LaunchedEffect(Unit) {
        WearListener.ritmoCardiaco.collect { heartRateViewModel.onHeartRateReceived(it) }
    }

    LaunchedEffect(Unit) {
        WearListener.pasos.collect { stepsViewModel.actualizarPasos(it) }
    }

    LaunchedEffect(Unit) {
        WearListener.calorias.collect { caloriesViewModel.onCaloriesReceived(it) }
    }

    LaunchedEffect(metasState.metaPasos, metasState.metaCalorias) {

        stepsViewModel.actualizarMetaPasos(metasState.metaPasos)
        caloriesViewModel.actualizarMetaCalorias(metasState.metaCalorias)
    }
    val pesoRepository = remember { PesoRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()) }

    val pesoViewModel: PesoViewModel = viewModel(
        factory = PesoViewModelFactory(
            context.applicationContext as Application,
            pesoRepository
        )
    )

    val uiPesoState by pesoViewModel.uiState.collectAsState()


    // ===== ESTADO AGUA =====
    var metaAgua by remember { mutableStateOf(2000) }
    var registroId by remember { mutableStateOf("") }
    var aguaActual by remember { mutableStateOf(0) }
    var metaAlcanzada by remember { mutableStateOf(false) }
    var genero by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val metas = FirebaseMetaHelper.getMetas(uid)
            val usuario = FirebaseUsuarioHelper.getUsuario(uid)
            metaAgua = metas?.metaAgua ?: 2000
            genero = usuario?.genero ?: ""
        }

        val registro = FirebaseAguaHelper.obtenerRegistroHoy()
        registroId = registro.id_agua
        aguaActual = registro.cantidad_ml
        metaAlcanzada = aguaActual >= metaAgua
    }

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
    ) {

        // ===== ENCABEZADO =====
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

        // ===== MONITOREO =====
        Text(
            "Monitoreo de Salud",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp)
        )
        Spacer(Modifier.height(25.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            HealthMonitorItem(
                titulo = "Ritmo cardíaco",
                valor = uiHeartState.ultimoRitmo
            )

            HealthMonitorItem(
                titulo = "Pasos",
                valor = uiStepsState.pasosTotalesHoy,
                meta = uiStepsState.metaPasos
            )

            HealthMonitorItem(
                titulo = "Calorías",
                valor = uiCaloriesState.caloriasTotales.toInt(),
                meta = uiCaloriesState.metaCalorias
            )
        }

        Spacer(Modifier.height(25.dp))

        // ===== AGUA =====
        Text(
            "Ingesta de Agua",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp)
        )

        Spacer(Modifier.height(25.dp))

        suspend fun agregarAgua() {
            if (metaAlcanzada) return

            val anterior = aguaActual
            val nuevo = (aguaActual + 250).coerceAtMost(metaAgua) // paso de 250 ml
            aguaActual = nuevo

            FirebaseAguaHelper.actualizarRegistro(registroId, nuevo)

            // Notificación de progreso en cada avance de 250 ml
            if ((nuevo / 250) > (anterior / 250) && nuevo < metaAgua) {
                NotificacionAgua.enviarProgreso(
                    context = context,
                    totalMl = nuevo,
                    metaMl = metaAgua
                )
            }

            // Meta alcanzada
            if (nuevo >= metaAgua && !metaAlcanzada) {
                PermissionUtils.vibrar(context)
                NotificacionAgua.enviarMetaAlcanzada(context, metaAgua)
                metaAlcanzada = true
            }
        }

        suspend fun restarAgua() {
            val nuevo = (aguaActual - 250).coerceAtLeast(0)
            aguaActual = nuevo
            FirebaseAguaHelper.actualizarRegistro(registroId, nuevo)

            if (nuevo < metaAgua) {
                metaAlcanzada = false
            }
        }

        WaterIntakeCardPro(
            totalMl = aguaActual,
            metaMl = metaAgua,
            onAdd = { scope.launch { agregarAgua() } },
            onSubtract = { scope.launch { restarAgua() } },
            isDisabled = metaAlcanzada
        )

        Spacer(Modifier.height(25.dp))

        Text(
            "Monitoreo de Peso",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 20.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(15.dp))

        WeightMonitorCard(
            pesoActual = uiPesoState.pesoActual,
            metaPeso = uiPesoState.metaPeso,
            metaAlcanzada = uiPesoState.metaAlcanzada,
            onPesoIngresado = { nuevoPeso ->
                pesoViewModel.actualizarPesoActual(nuevoPeso)
            }
        )


        Spacer(Modifier.height(25.dp))

        // ===== MEDICACIÓN =====
        Text(
            "Medicacion",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp)
        )

        Spacer(Modifier.height(25.dp))

        MedicacionSection(navController = navController)

        Spacer(Modifier.height(25.dp))



        Button(
            onClick = {
                navController.navigate("estadisticas")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Estadísticas", color = Color.White)
        }


        Spacer(Modifier.height(50.dp))
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
