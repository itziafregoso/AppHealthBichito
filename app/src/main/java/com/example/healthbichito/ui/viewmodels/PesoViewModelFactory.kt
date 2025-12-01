package com.example.healthbichito.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthbichito.data.repositories.PesoRepository

class PesoViewModelFactory(
    private val app: Application,
    private val repository: PesoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PesoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PesoViewModel(app, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

