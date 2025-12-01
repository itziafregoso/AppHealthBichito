package com.example.healthbichito.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbichito.data.repositories.PesoRepository
import com.example.healthbichito.notifications.pesoNotificacion.NotificacionMetaPeso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PesoViewModel(
    app: Application,
    private val repository: PesoRepository
) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(PesoUiState())
    val uiState: StateFlow<PesoUiState> = _uiState.asStateFlow()

    init {
        cargarDatosDesdeFirebase()
    }

    fun cargarDatosDesdeFirebase() {
        viewModelScope.launch {
            val pesoActual = repository.obtenerPesoActual()
            val metaPeso = repository.obtenerMetaPeso()

            _uiState.update {
                it.copy(
                    pesoActual = pesoActual,
                    metaPeso = metaPeso,
                    metaAlcanzada = pesoActual <= metaPeso && metaPeso > 0,
                    notificacionEnviada = false  // 🚀 Se reinicia para poder volver a notificar
                )
            }
        }
    }

    fun actualizarPesoActual(nuevoPeso: Double) {
        viewModelScope.launch {
            repository.actualizarPeso(nuevoPeso)

            _uiState.update { it.copy(pesoActual = nuevoPeso) }
            verificarMeta()
        }
    }

    fun actualizarMetaPeso(nuevaMeta: Double) {
        viewModelScope.launch {
            repository.actualizarMetaPeso(nuevaMeta)

            _uiState.update { it.copy(metaPeso = nuevaMeta) }
            verificarMeta()
        }
    }

    private fun verificarMeta() {
        val estado = _uiState.value

        val metaAlcanzada = estado.metaPeso > 0 && estado.pesoActual <= estado.metaPeso

        _uiState.update {
            it.copy(metaAlcanzada = metaAlcanzada)
        }

        if (metaAlcanzada && !_uiState.value.notificacionEnviada) {
            NotificacionMetaPeso.enviar(
                getApplication(),
                estado.pesoActual,
                estado.metaPeso
            )
            _uiState.update { it.copy(notificacionEnviada = true) }
        }

        // 🎯 Si ya excedió el peso meta, permitir reenviar notificación en otro momento
        if (!metaAlcanzada) {
            _uiState.update { it.copy(notificacionEnviada = false) }
        }
    }
}

data class PesoUiState(
    val pesoActual: Double = 0.0,
    val metaPeso: Double = 0.0,
    val metaAlcanzada: Boolean = false,
    val notificacionEnviada: Boolean = false
)
