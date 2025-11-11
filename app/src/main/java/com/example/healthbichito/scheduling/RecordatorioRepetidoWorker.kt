package com.example.healthbichito.scheduling

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.healthbichito.MainActivity
import com.example.healthbichito.R
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper
import com.example.healthbichito.notifications.medicamentonotificacion.MedicamentoReceiver
import java.util.concurrent.TimeUnit

class RecordatorioRepetidoWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val medicacionId = inputData.getString("medicacion_id") ?: return Result.failure()
        val medicacionNombre = inputData.getString("medicacion_nombre") ?: "Medicamento"

        // Comprobar si ya se ha tomado
        val estadoHoy = FirebaseMedicacionHelper.getEstadoHoy(medicacionId)
        if (estadoHoy.tomado) {
            // Si se ha tomado, simplemente cancelamos el recordatorio y terminamos.
            cancelarRecordatorio(context, medicacionId)
            return Result.success()
        }

        // Si no se ha tomado, enviar notificación y reprogramar.
        enviarNotificacionRecordatorio(context, medicacionId, medicacionNombre)
        reprogramarSiguienteRecordatorio(context, medicacionId, medicacionNombre)

        return Result.success()
    }

    private fun enviarNotificacionRecordatorio(context: Context, medicacionId: String, medicacionNombre: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medicacion_recordatorio_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios Repetidos de Medicación"
            val descriptionText = "Notificaciones de recordatorio para tomar medicamentos."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply { description = descriptionText }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app (sin cambios)
        val intentApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentApp: PendingIntent = PendingIntent.getActivity(context, 0, intentApp, PendingIntent.FLAG_IMMUTABLE)

        // ✅ INTENT PARA MARCAR TOMADO (CON URI ÚNICA)
        val intentMarcarTomado = Intent(context, MedicamentoReceiver::class.java).apply {
            action = "com.example.healthbichito.MARCAR_TOMADO"
            data = Uri.parse("marcar_tomado_recordatorio://${medicacionId}") // URI Única
            putExtra("medicacion_id", medicacionId)
            putExtra("notification_id", medicacionId.hashCode() + 1)
        }
        val pendingIntentMarcarTomado = PendingIntent.getBroadcast(
            context, 0, intentMarcarTomado, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_pastilla)
            .setContentTitle("¡Recordatorio!")
            .setContentText("No olvides tomar tu $medicacionNombre.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntentApp)
            .addAction(R.drawable.ic_check, "Marcar como Tomado", pendingIntentMarcarTomado)
            .setAutoCancel(true)

        notificationManager.notify(medicacionId.hashCode() + 1, builder.build())
    }

    private fun reprogramarSiguienteRecordatorio(context: Context, medicacionId: String, medicacionNombre: String) {
        val workManager = WorkManager.getInstance(context)
        val data = Data.Builder()
            .putString("medicacion_id", medicacionId)
            .putString("medicacion_nombre", medicacionNombre)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<RecordatorioRepetidoWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES)
            .setInputData(data)
            .build()

        // ✅ USAR EL ID DEL MEDICAMENTO COMO NOMBRE ÚNICO DEL TRABAJO
        workManager.enqueueUniqueWork(
            medicacionId, // Clave: Usar el ID del medicamento
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    companion object {
        fun cancelarRecordatorio(context: Context, medicacionId: String) {
            // ✅ USAR EL ID DEL MEDICAMENTO PARA CANCELAR EL TRABAJO CORRECTO
            WorkManager.getInstance(context).cancelUniqueWork(medicacionId)
        }
    }
}