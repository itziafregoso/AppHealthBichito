package com.example.healthbichito.scheduling

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper

class ReinicioDiarioWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Resetear el estado "tomado" de todos los medicamentos
            FirebaseMedicacionHelper.resetearEstadoTomadoDiario()

            // 2. Reprogramar todas las alarmas para el nuevo día
            val medicaciones = FirebaseMedicacionHelper.obtenerMedicamentos() // ✅ CORREGIDO
            val scheduler = MedicacionScheduler(context)

            medicaciones.forEach { medicacion ->
                if (medicacion.activo == 1) { // Solo reprogramar si está activo
                    scheduler.programarAlarma(medicacion)
                }
            }

            Result.success()
        } catch (e: Exception) {
            // Si algo falla, reintentar más tarde
            Result.retry()
        }
    }
}