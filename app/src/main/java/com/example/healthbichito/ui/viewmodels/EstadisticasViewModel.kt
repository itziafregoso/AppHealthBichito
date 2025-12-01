package com.example.healthbichito.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbichito.data.repositories.EstadisticasRepository
import com.example.healthbichito.util.BirthdateTransformation
import com.example.healthbichito.util.PDFHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class EstadisticasUiState(
    val resumenDia: ResumenDiario = ResumenDiario(),
    val resumenRango: ResumenRangoEstadisticas = ResumenRangoEstadisticas(),
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val mensajePDF: String = "",
    val cargando: Boolean = false,
    val pdfBotonHabilitado: Boolean = false // 👈 AÑADIDO
)

class EstadisticasViewModel : ViewModel() {

    var uiState by mutableStateOf(EstadisticasUiState())
        private set

    private val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        cargarResumenDia()
    }

    fun actualizarFechaInicio(value: String) { uiState = uiState.copy(fechaInicio = value, pdfBotonHabilitado = false) }
    fun actualizarFechaFin(value: String) { uiState = uiState.copy(fechaFin = value, pdfBotonHabilitado = false) }

    fun cargarResumenDia() {
        viewModelScope.launch {
            val hoy = formatoFecha.format(Date())
            val resumen = EstadisticasRepository.obtenerResumenDiario(hoy)
            uiState = uiState.copy(resumenDia = resumen)
        }
    }

    fun calcularResumenRango(
        context: Context,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(cargando = true, pdfBotonHabilitado = false)

                val fechaInicioFormateada = BirthdateTransformation.formatInput(uiState.fechaInicio)
                val fechaFinFormateada = BirthdateTransformation.formatInput(uiState.fechaFin)

                val resumen = EstadisticasRepository.obtenerResumenRango(
                    fechaInicioFormateada,
                    fechaFinFormateada
                )

                uiState = uiState.copy(
                    resumenRango = resumen,
                    cargando = false,
                    pdfBotonHabilitado = resumen.totalDias > 0 // Habilita si hay datos
                )

                onResult("Resumen generado exitosamente")

            } catch (e: Exception) {
                uiState = uiState.copy(cargando = false)
                onResult("Error: ${e.message}")
            }
        }
    }

    fun generarPDF(context: Context, onResult: (String) -> Unit) {
        if (!uiState.pdfBotonHabilitado) { // 👈 AÑADIDO
            onResult("Analiza un rango de fechas primero")
            return
        }

        viewModelScope.launch {
            val file = PDFHelper.crearPDFCompleto(context, uiState.resumenDia, uiState.resumenRango)

            if (file != null) {
                val openResult = PDFHelper.abrirPDF(context, file)
                onResult(openResult)
            } else {
                onResult("Error al generar PDF")
            }
        }
    }

    fun limpiarMensajePDF() {
        uiState = uiState.copy(mensajePDF = "")
    }
}

// 🔹 DATA CLASSES para los datos del usuario
data class ResumenDiario(
    val pasos: Int = 0,
    val calorias: Double = 0.0,
    val agua: Int = 0,
    val medicacion: Boolean = false
)

data class ResumenRangoEstadisticas(
    // Pasos
    val maxPasos: Int = 0,
    val promedioPasos: Double = 0.0,
    val diaMaxPasos: String = "",
    val historialPasos: Map<String, Int> = emptyMap(),
    // Calorías
    val promedioCalorias: Double = 0.0,
    val maxCalorias: Double = 0.0,
    val minCalorias: Double = 0.0,
    // Agua
    val diasMetaAguaCumplida: Int = 0,
    val promedioAgua: Double = 0.0,
    val maxAgua: Int = 0,
    val minAgua: Int = 0,
    // Ritmo cardiaco
    val promedioRitmo: Double = 0.0,
    val maxRitmo: Int = 0,
    val minRitmo: Int = 0,
    // General
    val totalDias: Int = 0
)
