package com.example.healthbichito.scheduling

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper
import androidx.work.WorkManager
import kotlinx.coroutines.*

class MarcarTomadoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra("medicacion_id") ?: return
        val pending = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            FirebaseMedicacionHelper.setTomadoHoy(id, true)

            WorkManager.getInstance(context).cancelUniqueWork(id)

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(id.hashCode())
            nm.cancel(id.hashCode() + 1)

            pending.finish()
        }
    }
}


