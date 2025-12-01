package com.example.healthbichito.scheduling

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.healthbichito.R
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class RecordatorioRepetidoWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val id = inputData.getString("medicacion_id") ?: return@withContext Result.failure()
        val nombre = inputData.getString("medicacion_nombre") ?: "Medicamento"

        if (FirebaseMedicacionHelper.getEstadoHoy(id).tomado) {
            WorkManager.getInstance(applicationContext).cancelUniqueWork(id)
            return@withContext Result.success()
        }

        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val noti = NotificationCompat.Builder(applicationContext, "medicacion_channel")
            .setSmallIcon(R.drawable.ic_pastilla)
            .setContentTitle("Recordatorio")
            .setContentText("Aún no tomaste tu $nombre.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(id.hashCode() + 1, noti)

        reprogramar(id, nombre)
        Result.success()
    }

    private fun reprogramar(id: String, nombre: String) {
        val data = Data.Builder()
            .putString("medicacion_id", id)
            .putString("medicacion_nombre", nombre)
            .build()

        val work = OneTimeWorkRequestBuilder<RecordatorioRepetidoWorker>()
            .setInitialDelay(3, TimeUnit.MINUTES)
            .setInputData(data)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(id, ExistingWorkPolicy.REPLACE, work)
    }
}


