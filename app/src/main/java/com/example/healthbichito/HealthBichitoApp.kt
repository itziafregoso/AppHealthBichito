package com.example.healthbichito

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.healthbichito.scheduling.ReinicioDiarioWorker
import java.util.concurrent.TimeUnit

class HealthBichitoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        programarReinicioDiario()
    }

    private fun programarReinicioDiario() {
        val workManager = WorkManager.getInstance(this)

        // Crear la tarea para que se ejecute cada 24 horas
        val workRequest = PeriodicWorkRequestBuilder<ReinicioDiarioWorker>(
            24, TimeUnit.HOURS
        ).build()

        // Programar la tarea de forma única
        workManager.enqueueUniquePeriodicWork(
            "reinicio_diario",
            ExistingPeriodicWorkPolicy.KEEP, // Mantener el trabajo existente si ya está programado
            workRequest
        )
    }
}