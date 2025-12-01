package com.example.healthbichito.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthbichito.data.repositories.RitmoCardiacoRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RitmoCardiacoViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RitmoCardiacoViewModel::class.java)) {
            return RitmoCardiacoViewModel(
                repository = RitmoCardiacoRepository(
                    auth = FirebaseAuth.getInstance(),
                    firestore = FirebaseFirestore.getInstance()
                )
            ) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}
