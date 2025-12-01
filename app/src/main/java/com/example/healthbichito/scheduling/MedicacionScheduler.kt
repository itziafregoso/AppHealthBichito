package com.example.healthbichito.scheduling

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.example.healthbichito.data.model.Medicacion
import java.util.*

class MedicacionScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun programarAlarma(medicacion: Medicacion) {
        val intent = Intent(context, MedicacionReceiver::class.java).apply {
            action = "ALARM_MEDICACION"
            data = Uri.parse("medicacion://${medicacion.id}")
            putExtra("medicacion_id", medicacion.id)
            putExtra("medicacion_nombre", medicacion.nombre_medicamento)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicacion.id.hashCode(), // 👉 UN REQUEST CODE ÚNICO
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            val (h, m) = medicacion.hora.split(":").map { it.toInt() }
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}


