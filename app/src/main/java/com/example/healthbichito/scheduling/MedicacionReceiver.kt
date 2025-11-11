package com.example.healthbichito.scheduling

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.healthbichito.MainActivity
import com.example.healthbichito.R
import java.util.concurrent.TimeUnit

class MedicacionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicacionId = intent.getStringExtra("medicacion_id") ?: return
        val medicacionNombre = intent.getStringExtra("medicacion_nombre") ?: "Medicamento"

        // 1. Mostrar la notificación inicial
        enviarNotificacionInicial(context, medicacionId, medicacionNombre)


    }

    private fun enviarNotificacionInicial(context: Context, medicacionId: String, medicacionNombre: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medicacion_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de Medicación"
            val descriptionText = "Notificaciones para tomar medicamentos."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }

        // ✅ Intent para abrir la app
        val intentApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentApp: PendingIntent = PendingIntent.getActivity(context, medicacionId.hashCode(), intentApp, PendingIntent.FLAG_IMMUTABLE)

        // Intent para la acción "Marcar como Tomado"
        val intentMarcarTomado = Intent(context, MarcarTomadoReceiver::class.java).apply {
            putExtra("medicacion_id", medicacionId)
        }
        val pendingIntentMarcarTomado = PendingIntent.getBroadcast(
            context,
            medicacionId.hashCode() + 1, // Request code único
            intentMarcarTomado,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_pastilla) // TODO: Cambiar ícono
            .setContentTitle("Hora de tu medicamento")
            .setContentText("Es hora de tomar tu $medicacionNombre.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntentApp) // ✅ Acción al hacer clic
            .addAction(R.drawable.ic_check, "Marcar como Tomado", pendingIntentMarcarTomado)
            .setAutoCancel(true)

        notificationManager.notify(medicacionId.hashCode(), builder.build())
    }


}