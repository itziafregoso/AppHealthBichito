package com.example.healthbichito.notifications.medicamentonotificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.healthbichito.MainActivity
import com.example.healthbichito.R
import com.example.healthbichito.scheduling.MedicacionReceiver
import com.example.healthbichito.scheduling.RecordatorioRepetidoWorker
import java.util.concurrent.TimeUnit

object NotificacionMedicamento {

    private const val CHANNEL_ID = "medicamento_channel"

    fun crearCanalNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de Medicamentos"
            val descriptionText = "Canal para notificaciones de recordatorios de medicamentos."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun enviarNotificacion(context: Context, medicacionId: String, nombreMedicamento: String, dosis: String, hora: String) {
        // Intent para abrir la app (sin cambios)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // ✅ INTENT PARA MARCAR TOMADO (CON URI ÚNICA)
        val marcarTomadoIntent = Intent(context, MedicamentoReceiver::class.java).apply {
            action = "com.example.healthbichito.MARCAR_TOMADO"
            data = Uri.parse("marcar_tomado://${medicacionId}") // URI Única
            putExtra("medicacion_id", medicacionId)
            putExtra("notification_id", medicacionId.hashCode()) // El ID de la notif sí puede ser el hash
        }
        val marcarTomadoPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, marcarTomadoIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pastilla)
            .setContentTitle("Hora de tu Medicamento!")
            .setContentText("Recuerda tomar $nombreMedicamento ($dosis) a las $hora.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_check, "Marcar como Tomado", marcarTomadoPendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(medicacionId.hashCode(), builder.build())

        // Programar el recordatorio (sin cambios aquí)
        programarRecordatorioRepetido(context, medicacionId, nombreMedicamento)
    }

    fun programarRecordatorioRepetido(context: Context, medicacionId: String, medicacionNombre: String) {
        val workManager = WorkManager.getInstance(context)
        val data = Data.Builder()
            .putString("medicacion_id", medicacionId)
            .putString("medicacion_nombre", medicacionNombre)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<RecordatorioRepetidoWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES) // Lo dejamos en 1 min para pruebas
            .setInputData(data)
            .build()

        // ✅ USAR EL ID DEL MEDICAMENTO COMO NOMBRE ÚNICO DEL TRABAJO
        workManager.enqueueUniqueWork(
            medicacionId, // Usamos el ID del medicamento como nombre único
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}