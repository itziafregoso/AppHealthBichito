package com.example.healthbichito.notifications.caloriasNotificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.healthbichito.R

object NotificacionMetaCalorias {

    private const val CHANNEL_ID = "calorias_channel"

    fun enviar(context: Context, calorias: Float) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones de calorías",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_check) // Usa un ícono que ya tengas
            .setContentTitle("Meta de calorías alcanzada")
            .setContentText("Has quemado $calorias kcal. ¡Meta cumplida!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(1002, builder.build())
    }
}