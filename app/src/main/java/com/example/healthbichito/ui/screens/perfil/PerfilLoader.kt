package com.example.healthbichito.ui.screens.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbichito.data.firebase.FirebaseAuthHelper
import com.example.healthbichito.data.firebase.FirebaseMetaHelper
import com.example.healthbichito.data.firebase.FirebaseUsuarioHelper
import com.example.healthbichito.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

data class ProfileUiState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = true
)

class PerfilLoader : ViewModel() {

    var uiState by mutableStateOf(_root_ide_package_.com.example.healthbichito.ui.screens.perfil.ProfileUiState())
        private set

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val uid = FirebaseAuthHelper.getUid()
            if (uid != null) {
                val user = FirebaseUsuarioHelper.getUsuario(uid)
                uiState = uiState.copy(usuario = user, isLoading = false)
            } else {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    // --- MANEJO DE DATOS DE PERFIL ---
    fun onAlturaChanged(altura: String) {
        uiState.usuario?.let {
            val newPerfil = it.perfil.copy(altura = altura.toDoubleOrNull() ?: 0.0)
            uiState = uiState.copy(usuario = it.copy(perfil = newPerfil))
        }
    }

    fun onPesoChanged(peso: String) {
        uiState.usuario?.let {
            val newPerfil = it.perfil.copy(peso = peso.toDoubleOrNull() ?: 0.0)
            uiState = uiState.copy(usuario = it.copy(perfil = newPerfil))
        }
    }

    fun onContactoEmergenciaChanged(contacto: String) {
        uiState.usuario?.let {
            val newPerfil = it.perfil.copy(contactoEmergencia = contacto)
            uiState = uiState.copy(usuario = it.copy(perfil = newPerfil))
        }
    }

    // ✅ MODIFICADO: AHORA NOTIFICA EL RESULTADO
    fun savePerfil(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                uiState.usuario?.let { user ->
                    FirebaseUsuarioHelper.updatePerfil(user.id, user.perfil)
                    onResult("Tus datos se han guardado con éxito.")
                }
            } catch (e: Exception) {
                onResult("Error al guardar los datos: ${e.message}")
            }
        }
    }

    // --- MANEJO DE METAS ---
    fun onMetaPasosChanged(pasos: String) {
        uiState.usuario?.let {
            val newMetas = it.metas.copy(metaPasos = pasos.toIntOrNull() ?: 0)
            uiState = uiState.copy(usuario = it.copy(metas = newMetas))
        }
    }

    fun onMetaCaloriasChanged(calorias: String) {
        uiState.usuario?.let {
            val newMetas = it.metas.copy(metaCalorias = calorias.toIntOrNull() ?: 0)
            uiState = uiState.copy(usuario = it.copy(metas = newMetas))
        }
    }

    fun onMetaAguaChanged(agua: String) {
        uiState.usuario?.let {
            val newMetas = it.metas.copy(metaAgua = agua.toIntOrNull() ?: 0)
            uiState = uiState.copy(usuario = it.copy(metas = newMetas))
        }
    }

    fun onMetaPesoChanged(peso: String) {
        uiState.usuario?.let {
            val newMetas = it.metas.copy(metaPeso = peso.toDoubleOrNull() ?: 0.0)
            uiState = uiState.copy(usuario = it.copy(metas = newMetas))
        }
    }

    // ✅ MODIFICADO: AHORA NOTIFICA EL RESULTADO
    fun saveMetas(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                uiState.usuario?.let { user ->
                    FirebaseMetaHelper.updateMetas(user.id, user.metas)
                    onResult("Tus metas se han guardado con éxito.")
                }
            } catch (e: Exception) {
                onResult("Error al guardar las metas: ${e.message}")
            }
        }
    }

    // ✅ NUEVA FUNCIÓN PARA CERRAR SESIÓN
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}
