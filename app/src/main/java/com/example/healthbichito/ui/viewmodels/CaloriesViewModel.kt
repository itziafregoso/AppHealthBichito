package com.example.healthbichito.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbichito.data.repositories.CaloriesRepository
import com.example.healthbichito.notifications.caloriasNotificacion.NotificacionMetaCalorias
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CaloriesViewModel(
    app: Application,
    private val repository: CaloriesRepository
) : AndroidViewModel(app) {

    private val context = app.applicationContext

    private val _uiState = MutableStateFlow(CaloriesUiState())
    val uiState: StateFlow<CaloriesUiState> = _uiState.asStateFlow()

    /**
     * 🔹 Se llama cuando se reciben nuevas calorías desde WearOS o sensores.
     */
    fun onCaloriesReceived(nuevasCalorias: Float) {
        viewModelScope.launch {

            repository.actualizarCalorias(nuevasCalorias)

            val meta = repository.obtenerMetaCalorias()

            _uiState.update { state ->
                val metaCumplida = nuevasCalorias >= meta

                state.copy(
                    caloriasTotales = nuevasCalorias,
                    metaCalorias = meta,
                    metaAlcanzada = metaCumplida
                )
            }

            // 🔔 Enviar notificación si se cumple la meta
            if (_uiState.value.metaAlcanzada && !_uiState.value.notificacionEnviada) {
                NotificacionMetaCalorias.enviar(context, nuevasCalorias)
                _uiState.update { state -> state.copy(notificacionEnviada = true) }
            }
        }
    }

    /**
     * 🔹 Cargar calorías y meta al abrir Dashboard.
     */
    fun cargarCaloriasDelDia() {
        viewModelScope.launch {
            val valor = repository.obtenerCaloriasDelDia()
            val meta = repository.obtenerMetaCalorias()

            _uiState.update {
                it.copy(
                    caloriasTotales = valor,
                    metaCalorias = meta,
                    metaAlcanzada = valor >= meta
                )
            }
        }
    }

    /**
     * 🔹 Recargar solo la meta (cuando se cambia desde Perfil).
     */
    fun actualizarMetaCalorias(nuevaMeta: Int) {
        _uiState.update { state ->
            state.copy(
                metaCalorias = nuevaMeta,
                metaAlcanzada = state.caloriasTotales >= nuevaMeta,
                notificacionEnviada = false // 🔄 Reiniciar notificación
            )
        }
    }

    /**
     * 🔹 Cargar meta directamente desde Firebase cuando la app inicia.
     */
    fun cargarMetaCalorias() {
        viewModelScope.launch {
            val meta = repository.obtenerMetaCalorias()
            _uiState.update { state ->
                state.copy(
                    metaCalorias = meta,
                    metaAlcanzada = state.caloriasTotales >= meta
                )
            }
        }
    }
}

data class CaloriesUiState(
    val caloriasTotales: Float = 0f,
    val metaCalorias: Int = 200,
    val metaAlcanzada: Boolean = false,
    val notificacionEnviada: Boolean = false
)

