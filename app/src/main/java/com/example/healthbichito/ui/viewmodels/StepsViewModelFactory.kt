package com.example.healthbichito.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthbichito.data.repositories.StepsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StepsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepsViewModel::class.java)) {
            return StepsViewModel(
                StepsRepository(
                    FirebaseAuth.getInstance(),
                    FirebaseFirestore.getInstance()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
