package com.example.healthbichito.scheduling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthbichito.data.firebase.FirebaseMedicacionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReinicioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val scheduler = MedicacionScheduler(context)
            CoroutineScope(Dispatchers.IO).launch {
                val medicamentos = FirebaseMedicacionHelper.obtenerMedicamentos()
                medicamentos.forEach {
                    if (it.activo == 1) {
                        scheduler.programarAlarma(it)
                    }
                }
            }
        }
    }
}
