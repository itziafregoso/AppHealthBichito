package com.example.healthbichito.scheduling

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.healthbichito.MainActivity
import com.example.healthbichito.R

class MedicacionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MEDICACION_RECEIVER", "🔔 Alarma recibida para medicamento")
        val medicacionId = intent.getStringExtra("medicacion_id") ?: return
        val medicacionNombre = intent.getStringExtra("medicacion_nombre") ?: "Medicamento"

        enviarNotificacionInicial(context, medicacionId, medicacionNombre)
        iniciarRecordatorioCada15Min(context, medicacionId, medicacionNombre)

    }


    private fun enviarNotificacionInicial(context: Context, id: String, nombre: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medicacion_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Recordatorios", NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(channel)
        }

        val intentApp = Intent(context, MainActivity::class.java).apply {
            putExtra("open_dashboard", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentApp = PendingIntent.getActivity(
            context, id.hashCode(), intentApp, PendingIntent.FLAG_IMMUTABLE
        )

        val intentTomar = Intent(context, MarcarTomadoReceiver::class.java).apply {
            putExtra("medicacion_id", id)
            data = Uri.parse("marcar_tomado://${id}")
        }
        val pendingTomar = PendingIntent.getBroadcast(
            context,
            id.hashCode() + 1,
            intentTomar,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val noti = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_pastilla)
            .setContentTitle("Hora de tu medicamento")
            .setContentText("Toma tu $nombre ahora.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntentApp)
            .addAction(R.drawable.ic_check, "Marcar como tomado", pendingTomar)
            .setAutoCancel(true)
            .build()

        nm.notify(id.hashCode(), noti)
    }

    private fun iniciarRecordatorioCada15Min(context: Context, id: String, nombre: String) {
        val data = Data.Builder()
            .putString("medicacion_id", id)
            .putString("medicacion_nombre", nombre)
            .build()

        val work = OneTimeWorkRequestBuilder<RecordatorioRepetidoWorker>()
            .setInputData(data)
            .setInitialDelay(15, java.util.concurrent.TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(id, ExistingWorkPolicy.KEEP, work)
    }
}




