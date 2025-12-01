package com.example.healthbichito.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper
import com.example.healthbichito.data.model.Medicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicacionViewModel : ViewModel() {

    private val _medicamentos = MutableStateFlow<List<Medicacion>>(emptyList())
    val medicamentos: StateFlow<List<Medicacion>> = _medicamentos.asStateFlow()

    init {
        observarMedicamentos()
    }

    private fun observarMedicamentos() {
        viewModelScope.launch {
            FirebaseMedicacionHelper
                .obtenerMedicamentosFlow()
                .collect { lista ->
                    _medicamentos.value = lista
                }
        }
    }

    fun eliminarMedicamento(medicacion: Medicacion) {
        viewModelScope.launch {
            FirebaseMedicacionHelper.eliminarMedicamento(medicacion.id)
        }
    }

    fun marcarTomadoHoy(medicacion: Medicacion, tomado: Boolean) {
        viewModelScope.launch {
            FirebaseMedicacionHelper.setTomadoHoy(medicacion.id, tomado)
        }
    }
}
