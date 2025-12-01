package com.example.healthbichito.notifications.aguanotificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.healthbichito.MainActivity
import com.example.healthbichito.R

object NotificacionAgua {

    private const val CHANNEL_ID_AGUA = "CANAL_AGUA"
    private const val CHANNEL_NAME_AGUA = "Hidratación diaria"

    private const val ID_NOTI_PROGRESO = 3002
    private const val ID_NOTI_META = 3003

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return

            val channel = NotificationChannel(
                CHANNEL_ID_AGUA,
                CHANNEL_NAME_AGUA,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones relacionadas con tu meta diaria de agua"
            }

            manager.createNotificationChannel(channel)
        }
    }

    fun enviarProgreso(context: Context, totalMl: Int, metaMl: Int) {
        val restante = (metaMl - totalMl).coerceAtLeast(0)
        val progreso = (totalMl.toFloat() / metaMl.toFloat()).coerceIn(0f, 1f)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = crearPendingIntent(context)

        val noti = NotificationCompat.Builder(context, CHANNEL_ID_AGUA)
            .setSmallIcon(R.drawable.ic_notificacion_agua)
            .setContentTitle("Hidratación en progreso")
            .setContentText("Llevas $totalMl ml. Te faltan $restante ml para tu meta.")
            .setOnlyAlertOnce(true)
            .setProgress(100, (progreso * 100).toInt(), false)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(ID_NOTI_PROGRESO, noti)
    }


    fun enviarMetaAlcanzada(context: Context, metaMl: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = crearPendingIntent(context)

        val noti = NotificationCompat.Builder(context, CHANNEL_ID_AGUA)
            .setSmallIcon(R.drawable.ic_notificacion_agua)
            .setContentTitle("¡Meta de agua alcanzada!")
            .setContentText("Has llegado a tu meta diaria de $metaMl ml. ¡Buen trabajo!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(ID_NOTI_META, noti)
    }


    private fun crearPendingIntent(context: Context): PendingIntent {
        // Abrirá la app directo en la pantalla Dashboard
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("healthbichito://dashboard"),
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
