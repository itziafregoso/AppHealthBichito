package com.example.healthbichito.scheduling

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.example.healthbichito.data.model.Medicacion
import java.util.Calendar

class MedicacionScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun programarAlarma(medicacion: Medicacion): Boolean {
        // ✅ INTENT CON URI ÚNICA
        val intent = Intent(context, MedicacionReceiver::class.java).apply {
            // La acción y la data única garantizan que el PendingIntent sea único.
            action = "com.example.healthbichito.MEDICATION_ALARM"
            data = Uri.parse("medicacion://${medicacion.id}") // URI única por medicamento

            putExtra("medicacion_id", medicacion.id)
            putExtra("medicacion_nombre", medicacion.nombre_medicamento)
            putExtra("dosis", medicacion.dosis)
            putExtra("hora", medicacion.hora)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0, // El requestCode puede ser 0 porque la URI ya es única
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            val horaMinutos = medicacion.hora.split(":")
            set(Calendar.HOUR_OF_DAY, horaMinutos[0].toInt())
            set(Calendar.MINUTE, horaMinutos[1].toInt())
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            return true
        } else {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            return false
        }
    }

    fun cancelarAlarma(medicacion: Medicacion) {
        // ✅ INTENT CON URI ÚNICA PARA ENCONTRAR LA ALARMA CORRECTA
        val intent = Intent(context, MedicacionReceiver::class.java).apply {
            action = "com.example.healthbichito.MEDICATION_ALARM"
            data = Uri.parse("medicacion://${medicacion.id}")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}