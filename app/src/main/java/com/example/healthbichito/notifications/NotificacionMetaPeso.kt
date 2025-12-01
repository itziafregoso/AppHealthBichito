package com.example.healthbichito.notifications.pesoNotificacion

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.healthbichito.R

object NotificacionMetaPeso {

    fun enviar(context: Context, pesoActual: Double, pesoMeta: Double) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "META_PESO_CHANNEL")
            .setSmallIcon(R.drawable.ic_corazon)
            .setContentTitle("¡Objetivo de peso alcanzado!")
            .setContentText("Tu peso actual es $pesoActual kg. Meta lograda: $pesoMeta kg.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(3001, notification)
    }
}
