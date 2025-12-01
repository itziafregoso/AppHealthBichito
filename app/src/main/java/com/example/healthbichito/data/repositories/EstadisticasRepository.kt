package com.example.healthbichito.data.repositories

import com.example.healthbichito.data.firebase.FirebaseMetaHelper
import com.example.healthbichito.data.model.IntervaloRitmoCardiaco
import com.example.healthbichito.ui.viewmodels.ResumenDiario
import com.example.healthbichito.ui.viewmodels.ResumenRangoEstadisticas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object EstadisticasRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun getUid(): String? = auth.currentUser?.uid

    suspend fun obtenerResumenDiario(fecha: String): ResumenDiario {
        val uid = getUid() ?: return ResumenDiario()

        // Lee los pasos
        val actividadRef = db.collection("usuarios").document(uid)
            .collection("actividadDiaria").document(fecha)
            .get().await()
        val pasos = actividadRef.getLong("pasosTotales")?.toInt() ?: 0

        // Lee las calorías (desde su propia ruta)
        val caloriasRef = db.collection("usuarios").document(uid)
            .collection("actividadDiaria").document(fecha)
            .collection("calorias").document("calorias_totales")
            .get().await()
        val calorias = caloriasRef.getDouble("caloriasTotales") ?: 0.0

        // Lee el agua
        val aguaRef = db.collection("usuarios").document(uid)
            .collection("agua").document(fecha)
            .get().await()
        val agua = aguaRef.getLong("cantidad_ml")?.toInt() ?: 0

        // Lee los medicamentos
        val medicamentosDoc = db.collection("usuarios").document(uid)
            .collection("medicamentos").get().await()

        var medicacionTomada = false
        for (med in medicamentosDoc.documents) {
            val estadoRef = med.reference.collection("estados").document(fecha)
                .get().await()
            if (estadoRef.exists() && estadoRef.getBoolean("tomado") == true) {
                medicacionTomada = true
                break
            }
        }

        return ResumenDiario(pasos, calorias, agua, medicacionTomada)
    }

    suspend fun obtenerResumenRango(fechaInicio: String, fechaFin: String): ResumenRangoEstadisticas {
        val uid = getUid() ?: return ResumenRangoEstadisticas()

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            return ResumenRangoEstadisticas()
        }

        var fInicio = fechaInicio
        var fFin = fechaFin
        if (fInicio > fFin) { 
            val temp = fInicio
            fInicio = fFin
            fFin = temp
        }

        val fechas = generarListaFechas(fInicio, fFin)
        val metas = FirebaseMetaHelper.getMetas(uid)
        val metaAgua = metas?.metaAgua ?: 2000

        val historialPasos = mutableMapOf<String, Int>()
        var totalPasos = 0
        var maxPasos = 0
        var diaMaxPasos = ""

        var totalCalorias = 0.0
        var diasConCalorias = 0
        var maxCalorias = 0.0
        var minCalorias = -1.0

        var totalAgua = 0
        var diasConAgua = 0
        var maxAgua = 0
        var minAgua = -1
        var diasMetaAguaCumplida = 0

        var totalRitmo = 0
        var diasConRitmo = 0
        var maxRitmo = 0
        var minRitmo = -1

        for (fecha in fechas) {
            // Lee los pasos
            val docPasos = db.collection("usuarios").document(uid)
                .collection("actividadDiaria").document(fecha)
                .get().await()

            if (docPasos.exists()) {
                val pasos = docPasos.getLong("pasosTotales")?.toInt() ?: 0
                historialPasos[fecha] = pasos
                totalPasos += pasos
                if (pasos > maxPasos) {
                    maxPasos = pasos
                    diaMaxPasos = fecha
                }
            }

            // Lee las calorías (desde su propia ruta)
            val docCalorias = db.collection("usuarios").document(uid)
                .collection("actividadDiaria").document(fecha)
                .collection("calorias").document("calorias_totales")
                .get().await()
            
            if (docCalorias.exists()) {
                val calorias = docCalorias.getDouble("caloriasTotales") ?: 0.0
                if (calorias > 0) {
                    totalCalorias += calorias
                    diasConCalorias++
                    if (calorias > maxCalorias) maxCalorias = calorias
                    if (minCalorias == -1.0 || calorias < minCalorias) minCalorias = calorias
                }
            }

            // Lee el agua
            val docAgua = db.collection("usuarios").document(uid)
                .collection("agua").document(fecha)
                .get().await()

            if (docAgua.exists()) {
                val aguaConsumida = docAgua.getLong("cantidad_ml")?.toInt() ?: 0
                totalAgua += aguaConsumida
                diasConAgua++
                if (aguaConsumida > maxAgua) maxAgua = aguaConsumida
                if (minAgua == -1 || aguaConsumida < minAgua) minAgua = aguaConsumida
                if (aguaConsumida >= metaAgua) diasMetaAguaCumplida++
            }

            // Lee el ritmo cardíaco
            val docsRitmo = db.collection("usuarios").document(uid)
                .collection("ritmoCardiaco").document(fecha)
                .collection("intervalos")
                .get().await()

            if (!docsRitmo.isEmpty) {
                var totalRitmoDia = 0
                var muestrasRitmoDia = 0
                var minRitmoDia = -1
                var maxRitmoDia = 0

                for (docRitmo in docsRitmo.documents) {
                    val intervalo = docRitmo.toObject(IntervaloRitmoCardiaco::class.java)
                    if (intervalo != null && intervalo.cantidadMuestras > 0) {
                        totalRitmoDia += (intervalo.promedio * intervalo.cantidadMuestras)
                        muestrasRitmoDia += intervalo.cantidadMuestras
                        if (intervalo.maximo > maxRitmoDia) maxRitmoDia = intervalo.maximo
                        if (minRitmoDia == -1 || intervalo.minimo < minRitmoDia) minRitmoDia = intervalo.minimo
                    }
                }

                if (muestrasRitmoDia > 0) {
                    totalRitmo += (totalRitmoDia / muestrasRitmoDia)
                    diasConRitmo++
                    if (maxRitmoDia > maxRitmo) maxRitmo = maxRitmoDia
                    if (minRitmo == -1 || minRitmoDia < minRitmo) minRitmo = minRitmoDia
                }
            }
        }

        val promedioPasos = if (fechas.isNotEmpty()) totalPasos.toDouble() / fechas.size else 0.0
        val promedioCalorias = if (diasConCalorias > 0) totalCalorias / diasConCalorias else 0.0
        val promedioAgua = if (diasConAgua > 0) totalAgua.toDouble() / diasConAgua else 0.0
        val promedioRitmo = if (diasConRitmo > 0) totalRitmo.toDouble() / diasConRitmo else 0.0

        return ResumenRangoEstadisticas(
            maxPasos = maxPasos,
            promedioPasos = promedioPasos,
            diaMaxPasos = diaMaxPasos,
            historialPasos = historialPasos,
            promedioCalorias = promedioCalorias,
            maxCalorias = maxCalorias,
            minCalorias = if (minCalorias == -1.0) 0.0 else minCalorias,
            diasMetaAguaCumplida = diasMetaAguaCumplida,
            promedioAgua = promedioAgua,
            maxAgua = maxAgua,
            minAgua = if (minAgua == -1) 0 else minAgua,
            promedioRitmo = promedioRitmo,
            maxRitmo = maxRitmo,
            minRitmo = if (minRitmo == -1) 0 else minRitmo,
            totalDias = fechas.size
        )
    }

    private fun generarListaFechas(inicio: String, fin: String): List<String> {
        val lista = mutableListOf<String>()
        var fecha = inicio
        while (fecha <= fin) {
            lista.add(fecha)
            fecha = sumarDia(fecha)
        }
        return lista
    }

    private fun sumarDia(fecha: String): String {
        val partes = fecha.split("-").map { it.toInt() }
        val calendar = java.util.Calendar.getInstance()
        calendar.set(partes[0], partes[1] - 1, partes[2])
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val newFecha = calendar.time
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(newFecha)
    }
}
