package com.example.healthbichito.ui.screens.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbichito.data.firebase.FirebaseAuthHelper
import com.example.healthbichito.data.firebase.FirebaseMetaHelper
import com.example.healthbichito.data.firebase.FirebaseUsuarioHelper
import com.example.healthbichito.data.model.Perfil
import com.example.healthbichito.data.model.Meta
import com.example.healthbichito.data.model.Usuario
import kotlinx.coroutines.launch

data class ProfileEditableState(
    val altura: String = "",
    val peso: String = "",
    val contactoEmergencia: String = "",
    val metaPasos: String = "",
    val metaCalorias: String = "",
    val metaAgua: String = "",
    val metaPeso: String = ""
)

data class ProfileUiState(
    val usuario: Usuario? = null,
    val editable: ProfileEditableState = ProfileEditableState(),
    val isLoading: Boolean = true
)

class PerfilLoader : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
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

                uiState = uiState.copy(
                    usuario = user,
                    editable = ProfileEditableState(
                        altura = user?.perfil?.altura?.toString().orEmpty(),
                        peso = user?.perfil?.peso?.toString().orEmpty(),
                        contactoEmergencia = user?.perfil?.contactoEmergencia.orEmpty(),
                        metaPasos = user?.metas?.metaPasos?.toString().orEmpty(),
                        metaCalorias = user?.metas?.metaCalorias?.toString().orEmpty(),
                        metaAgua = user?.metas?.metaAgua?.toString().orEmpty(),
                        metaPeso = user?.metas?.metaPeso?.toString().orEmpty(),
                    ),
                    isLoading = false
                )
            } else {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    // 🔹 Actualizar campos mientras el usuario escribe (tipo String)
    fun onCampoEditado(campo: String, valor: String) {
        // Limpia y valida el número
        val limpio = valor
            .replace(Regex("[^0-9.]"), "")     // Quita letras y símbolos
            .replace(Regex("\\.(?=.*\\.)"), "") // Evita múltiples puntos
            .trimStart('0')                     // Elimina ceros al inicio
            .ifEmpty { "0" }                    // Evita quedar vacío

        uiState = uiState.copy(
            editable = when (campo) {
                "altura" -> uiState.editable.copy(altura = limpio)
                "peso" -> uiState.editable.copy(peso = limpio)
                "metaPasos" -> uiState.editable.copy(metaPasos = limpio)
                "metaCalorias" -> uiState.editable.copy(metaCalorias = limpio)
                "metaAgua" -> uiState.editable.copy(metaAgua = limpio)
                "metaPeso" -> uiState.editable.copy(metaPeso = limpio)
                "contacto" -> uiState.editable.copy(contactoEmergencia = valor) // texto normal
                else -> uiState.editable
            }
        )
    }


    // 🔹 Guardar perfil (convertir solo al final)
    fun savePerfil(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                uiState.usuario?.let { user ->
                    val perfil = Perfil(
                        altura = uiState.editable.altura.toDoubleOrNull() ?: 0.0,
                        peso = uiState.editable.peso.toDoubleOrNull() ?: 0.0,
                        contactoEmergencia = uiState.editable.contactoEmergencia
                    )
                    FirebaseUsuarioHelper.updatePerfil(user.id, perfil)
                    onResult("Datos guardados correctamente.")
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }

    // 🔹 Guardar metas (convertir solo al final)
    fun saveMetas(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                uiState.usuario?.let { user ->
                    val metas = Meta(
                        metaPasos = uiState.editable.metaPasos.toIntOrNull() ?: 0,
                        metaCalorias = uiState.editable.metaCalorias.toIntOrNull() ?: 0,
                        metaAgua = uiState.editable.metaAgua.toIntOrNull() ?: 0,
                        metaPeso = uiState.editable.metaPeso.toDoubleOrNull() ?: 0.0
                    )
                    FirebaseMetaHelper.updateMetas(user.id, metas)
                    onResult("Metas guardadas correctamente.")
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }

    // 🔹 Cerrar sesión del usuario
    fun signOut() {
        FirebaseAuthHelper.signOut()
    }

}

