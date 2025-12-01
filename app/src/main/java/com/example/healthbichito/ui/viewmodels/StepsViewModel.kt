package com.example.healthbichito.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbichito.data.repositories.StepsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StepsViewModel(
    private val repository: StepsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StepsUiState())
    val uiState: StateFlow<StepsUiState> = _uiState

    /**
     * Llamado cada vez que llegan nuevos pasos desde el Wear OS.
     * Se actualiza Firebase SOLO si los pasos aumentan.
     */
    fun actualizarPasos(pasosActuales: Int) {
        viewModelScope.launch {
            repository.actualizarPasosDelDia(pasosActuales)

            _uiState.update { state ->
                val metaAlcanzada = pasosActuales >= state.metaPasos

                state.copy(
                    pasosTotalesHoy = pasosActuales,
                    metaAlcanzada = metaAlcanzada
                )
            }
        }
    }

    /**
     * 🔹 Cargar pasos guardados en Firebase cuando se abre el Dashboard.
     */
    fun cargarPasosDelDia() {
        viewModelScope.launch {
            val pasos = repository.obtenerPasosDelDia()

            _uiState.update { state ->
                val metaAlcanzada = pasos >= state.metaPasos

                state.copy(
                    pasosTotalesHoy = pasos,
                    metaAlcanzada = metaAlcanzada
                )
            }
        }
    }

    /**
     * 🔹 Permite actualizar la meta desde el Perfil (Firebase ya la debe guardar).
     */
    fun actualizarMetaPasos(nuevaMeta: Int) {
        _uiState.update { state ->
            state.copy(
                metaPasos = nuevaMeta,
                metaAlcanzada = state.pasosTotalesHoy >= nuevaMeta
            )
        }
    }
}

data class StepsUiState(
    val pasosTotalesHoy: Int = 0,
    val metaPasos: Int = 6000,   // 🔹 Meta editable desde Perfil
    val metaAlcanzada: Boolean = false
)
