package com.example.healthbichito.scheduling

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarcarTomadoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicacionId = intent.getStringExtra("medicacion_id") ?: return

        // ✅ Pedirle al sistema tiempo extra para la operación asíncrona
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Marcar el medicamento como tomado en Firebase
                FirebaseMedicacionHelper.setTomadoHoy(medicacionId, true)

                // 2. Cancelar el recordatorio repetido futuro
                RecordatorioRepetidoWorker.cancelarRecordatorio(context, medicacionId)

                // 3. Cancelar la notificación de la barra de estado
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(medicacionId.hashCode()) // Notificación inicial
                notificationManager.cancel(medicacionId.hashCode() + 1) // Notificación de recordatorio

            } finally {
                // ✅ Avisarle al sistema que ya terminamos
                pendingResult.finish()
            }
        }
    }
}