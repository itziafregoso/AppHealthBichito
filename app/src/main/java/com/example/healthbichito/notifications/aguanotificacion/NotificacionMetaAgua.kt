package com.example.healthbichito.notifications.aguanotificacion


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.healthbichito.R

object NotificacionMetaAgua {

    private const val CHANNEL_ID = "agua_meta_channel"

    fun enviar(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Meta de ingesta alcanzada",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notificacion_agua)
            .setContentTitle("¡Meta alcanzada! 🎉")
            .setContentText("Has alcanzado tu meta de agua del día.")
            .setAutoCancel(true)
            .build()

        manager.notify(3003, notification)
    }
}
