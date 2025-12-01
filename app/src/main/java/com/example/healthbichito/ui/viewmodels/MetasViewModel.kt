package com.example.healthbichito.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbichito.data.firebase.FirebaseMetaHelper
import com.example.healthbichito.data.model.Meta
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MetasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(Meta())
    val uiState: StateFlow<Meta> = _uiState

    fun cargarMetas() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val metas = FirebaseMetaHelper.getMetas(uid)
            _uiState.value = metas ?: Meta()
        }
    }

    fun actualizarMetas(metas: Meta) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            FirebaseMetaHelper.updateMetas(uid, metas)
            _uiState.value = metas
        }
    }

    fun recargarMetasDesdeFirebase() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val metas = FirebaseMetaHelper.getMetas(uid)
            if (metas != null) {
                _uiState.value = _uiState.value.copy(
                    metaPasos = metas.metaPasos,
                    metaCalorias = metas.metaCalorias,
                    metaAgua = metas.metaAgua,
                    metaPeso = metas.metaPeso
                )
            }
        }
    }

}
