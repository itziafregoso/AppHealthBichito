package com.example.healthbichito.notifications.medicamentonotificacion

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicamentoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.healthbichito.MARCAR_TOMADO") {
            val medicacionId = intent.getStringExtra("medicacion_id")
            val notificationId = intent.getIntExtra("notification_id", 0)

            if (medicacionId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    FirebaseMedicacionHelper.setTomadoHoy(medicacionId, true)
                }

                // Cancelar la notificación
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)

                // Cancelar el recordatorio repetido
                WorkManager.getInstance(context).cancelUniqueWork(medicacionId)
            }
        }
    }
}
