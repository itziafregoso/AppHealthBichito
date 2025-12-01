package com.example.healthbichito.domain

import com.example.healthbichito.data.model.IntervaloRitmoCardiaco
import com.example.healthbichito.data.repositories.RitmoCardiacoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RitmoCardiacoAggregator(
    private val repository: RitmoCardiacoRepository,
    private val intervaloMs: Long = 60 * 1000 // 1 minuto (para pruebas)
) {

    private val lecturas = mutableListOf<Int>()
    private var tiempoInicio = System.currentTimeMillis()

    fun agregarLectura(valor: Int) {
        lecturas.add(valor)
    }

    suspend fun procesarSiEsTiempo() = withContext(Dispatchers.IO) {
        val ahora = System.currentTimeMillis()

        if (ahora - tiempoInicio >= intervaloMs && lecturas.isNotEmpty()) {

            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

            val intervalo = IntervaloRitmoCardiaco(
                timestampInicio = tiempoInicio,
                timestampFin = ahora,
                fechaHoraInicio = formatter.format(Date(tiempoInicio)),
                fechaHoraFin = formatter.format(Date(ahora)),
                promedio = lecturas.average().toInt(),
                minimo = lecturas.minOrNull() ?: 0,
                maximo = lecturas.maxOrNull() ?: 0,
                cantidadMuestras = lecturas.size
            )


            repository.guardarEstadistica(intervalo)

            lecturas.clear()
            tiempoInicio = ahora
        }
    }

    fun obtenerPromedioActual(): Float =
        if (lecturas.isNotEmpty()) lecturas.average().toFloat() else 0f
}