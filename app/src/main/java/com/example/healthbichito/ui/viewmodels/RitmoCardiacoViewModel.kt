package com.example.healthbichito.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbichito.data.model.IntervaloRitmoCardiaco
import com.example.healthbichito.data.repositories.RitmoCardiacoRepository
import com.example.healthbichito.domain.RitmoCardiacoAggregator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RitmoCardiacoViewModel(
    private val repository: RitmoCardiacoRepository
) : ViewModel() {

    private val aggregator = RitmoCardiacoAggregator(repository)

    private val _uiState = MutableStateFlow(RitmoCardiacoUiState())
    val uiState: StateFlow<RitmoCardiacoUiState> = _uiState.asStateFlow()

    // 🚨 Se ejecuta cada vez que recibimos un nuevo dato desde el wearable
    fun onHeartRateReceived(valor: Int) {
        viewModelScope.launch {
            aggregator.agregarLectura(valor)
            aggregator.procesarSiEsTiempo()

            _uiState.update { estado ->
                estado.copy(
                    ultimoRitmo = valor,
                    promedioActual = aggregator.obtenerPromedioActual()
                )
            }
        }
    }

    // 📆 Cargar historial guardado según fecha
    fun cargarDatosPorFecha(fecha: String) {
        viewModelScope.launch {
            repository.obtenerIntervalosPorFecha(fecha).collect { lista ->
                val promedioFecha = if (lista.isNotEmpty()) {
                    lista.map { it.promedio }.average().toFloat()
                } else 0f

                _uiState.update { estado ->
                    estado.copy(
                        listaIntervalos = lista,
                        promedioActual = promedioFecha
                    )
                }
            }
        }
    }
}

// 🧠 Estado que la UI puede observar
data class RitmoCardiacoUiState(
    val ultimoRitmo: Int = 0,
    val promedioActual: Float = 0f,
    val listaIntervalos: List<IntervaloRitmoCardiaco> = emptyList()
)


