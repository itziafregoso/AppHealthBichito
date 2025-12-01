package com.example.healthbichito.notifications.aguanotificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.healthbichito.R

object NotificacionAguaProgreso {

    private const val CHANNEL_ID = "agua_progreso_channel"

    fun enviar(context: Context, cantidadActual: Int, restante: Int, progreso: Float) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Progreso de ingesta de agua",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notificacion_agua) // tu ícono en drawable
            .setContentTitle("Hidratación en progreso 💧")
            .setContentText("Llevas $cantidadActual ml. Te faltan $restante ml.")
            .setProgress(100, (progreso * 100).toInt(), false)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .build()

        manager.notify(3002, notification)
    }
}
