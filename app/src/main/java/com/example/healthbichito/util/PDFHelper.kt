package com.example.healthbichito.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.healthbichito.ui.viewmodels.ResumenDiario
import com.example.healthbichito.ui.viewmodels.ResumenRangoEstadisticas
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PDFHelper {

    fun crearPDFCompleto(
        context: Context,
        resumenDia: ResumenDiario,
        resumenRango: ResumenRangoEstadisticas
    ): File? {
        return try {
            val pdfPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toString()

            val file = File(pdfPath, "Reporte_HealthBichito_${System.currentTimeMillis()}.pdf")

            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 16f
            }
            val titlePaint = Paint().apply {
                color = Color.rgb(35, 118, 52) // Verde HealthBichito
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            // 🔹 Encabezado
            canvas.drawText("HealthBichito+", 30f, 40f, titlePaint)
            paint.textSize = 14f
            canvas.drawText("Reporte Estadístico del Usuario", 30f, 65f, paint)

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            paint.textSize = 10f
            paint.color = Color.GRAY
            canvas.drawText("Generado el: ${sdf.format(Date())}", 30f, 85f, paint)

            var offsetY = 120f

            // 🔹 Resumen del día
            drawSectionTitle(canvas, "Resumen de Hoy", offsetY)
            offsetY += 25
            drawData(canvas, listOf(
                "Pasos dados: ${resumenDia.pasos}",
                "Calorías quemadas: ${String.format("%.2f", resumenDia.calorias)} kcal",
                "Agua ingerida: ${resumenDia.agua} ml",
                "Medicamentos tomados: ${if (resumenDia.medicacion) "Sí" else "No"}"
            ), offsetY)
            offsetY += 4 * 22 + 15
            drawSeparator(canvas, offsetY)
            offsetY += 25

            // 🔹 Observaciones Inteligentes (REORDENADO)
            drawSectionTitle(canvas, "Observaciones Inteligentes", offsetY)
            offsetY += 25
            val insightsPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                color = Color.rgb(80, 80, 80)
            }
            val text = generarInsights(resumenRango)
            val textWidth = pageInfo.pageWidth - 60
            val staticLayout = android.text.StaticLayout.Builder.obtain(text, 0, text.length, android.text.TextPaint(insightsPaint), textWidth).build()
            canvas.save()
            canvas.translate(30f, offsetY)
            staticLayout.draw(canvas)
            canvas.restore()
            offsetY += staticLayout.height + 15
            drawSeparator(canvas, offsetY)
            offsetY += 25

            // 🔹 Resumen del rango (Pasos)
            drawSectionTitle(canvas, "Estadísticas de Pasos (Rango)", offsetY)
            offsetY += 25
            drawData(canvas, listOf(
                "Día con más pasos: ${resumenRango.diaMaxPasos}",
                "Máximo de pasos: ${resumenRango.maxPasos}",
                "Promedio de pasos: ${String.format("%.1f", resumenRango.promedioPasos)}",
                "Días analizados: ${resumenRango.totalDias}"
            ), offsetY)
            offsetY += 4 * 22 + 15
            drawSeparator(canvas, offsetY)
            offsetY += 25
            
            // 🔹 Resumen del rango (Calorías)
            if (resumenRango.maxCalorias > 0) {
                drawSectionTitle(canvas, "Estadísticas de Calorías (Rango)", offsetY)
                offsetY += 25
                drawData(canvas, listOf(
                    "Promedio de quemadas: ${String.format("%.2f", resumenRango.promedioCalorias)} kcal",
                    "Máximo de quemadas: ${String.format("%.2f", resumenRango.maxCalorias)} kcal",
                    "Mínimo de quemadas: ${String.format("%.2f", resumenRango.minCalorias)} kcal"
                ), offsetY)
                offsetY += 3 * 22 + 15
                drawSeparator(canvas, offsetY)
                offsetY += 25
            }

            // 🔹 Resumen del rango (Agua)
            drawSectionTitle(canvas, "Estadísticas de Agua (Rango)", offsetY)
            offsetY += 25
            drawData(canvas, listOf(
                "Promedio de consumo: ${String.format("%.1f", resumenRango.promedioAgua)} ml",
                "Consumo máximo: ${resumenRango.maxAgua} ml",
                "Consumo mínimo: ${resumenRango.minAgua} ml",
                "Cumplimiento meta: ${resumenRango.diasMetaAguaCumplida}/${resumenRango.totalDias} días"
            ), offsetY)
            offsetY += 4 * 22 + 15
            drawSeparator(canvas, offsetY)
            offsetY += 25

            // 🔹 Resumen del rango (Ritmo Cardíaco)
            if (resumenRango.maxRitmo > 0) {
                drawSectionTitle(canvas, "Estadísticas de Ritmo Cardíaco (Rango)", offsetY)
                offsetY += 25
                drawData(canvas, listOf(
                    "Promedio: ${String.format("%.1f", resumenRango.promedioRitmo)} bpm",
                    "Máximo: ${resumenRango.maxRitmo} bpm",
                    "Mínimo: ${resumenRango.minRitmo} bpm"
                ), offsetY)
                offsetY += 3 * 22 + 15
                drawSeparator(canvas, offsetY)
                offsetY += 25
            }

            // 🔹 Historial de pasos (REORDENADO)
            if (resumenRango.historialPasos.isNotEmpty()) {
                drawSectionTitle(canvas, "Historial de Pasos", offsetY)
                offsetY += 30
                paint.textSize = 12f
                resumenRango.historialPasos.forEach { (fecha, pasos) ->
                    if (offsetY > 800) { // Lógica simple para evitar desbordamiento
                        return@forEach
                    }
                    canvas.drawText("· $fecha → $pasos pasos", 30f, offsetY, paint)
                    offsetY += 20
                }
            }

            document.finishPage(page)
            document.writeTo(FileOutputStream(file))
            document.close()

            file

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun abrirPDF(context: Context, file: File): String {
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }

            context.startActivity(Intent.createChooser(intent, "Abrir reporte PDF"))
            "PDF abierto correctamente"

        } catch (e: Exception) {
            e.printStackTrace()
            "No se pudo abrir el PDF"
        }
    }

    private fun drawSectionTitle(canvas: Canvas, title: String, y: Float) {
        val paint = Paint().apply {
            color = Color.rgb(18, 110, 130) // Azul Fuerte
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(title, 30f, y, paint)
    }

    private fun drawData(canvas: Canvas, lines: List<String>, startY: Float) {
        val paint = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
        }
        var y = startY
        lines.forEach {
            canvas.drawText(it, 30f, y, paint)
            y += 22
        }
    }
    
    private fun drawSeparator(canvas: Canvas, y: Float) {
        val paint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        canvas.drawLine(30f, y, 565f, y, paint)
    }

    private fun generarInsights(rango: ResumenRangoEstadisticas): String {
        val insights = mutableListOf<String>()

        if (rango.promedioPasos > 0) {
            when {
                rango.promedioPasos > 10000 -> insights.add("• Pasos: ¡Excelente! Superas la meta recomendada de 10.000 pasos diarios.")
                rango.promedioPasos > 7000 -> insights.add("• Pasos: Muy bien. Mantienes un nivel de actividad física saludable y constante.")
                rango.promedioPasos > 4000 -> insights.add("• Pasos: Buen comienzo. Intenta añadir pequeñas caminatas para aumentar tu promedio.")
                else -> insights.add("• Pasos: Tu nivel de actividad es bajo. Busca oportunidades para moverte más durante el día.")
            }
        }
        
        if (rango.promedioCalorias > 0) {
             when {
                rango.promedioCalorias > 2500 -> insights.add("• Calorías: Tu gasto calórico promedio es alto, lo que refleja un buen nivel de actividad.")
                rango.promedioCalorias > 1800 -> insights.add("• Calorías: Mantienes un gasto calórico moderado. ¡Sigue así!")
                else -> insights.add("• Calorías: Tu gasto calórico es bajo. Incrementar tu actividad física ayudará a elevarlo.")
            }
        }
        
        if (rango.promedioRitmo > 0) {
            when {
                rango.promedioRitmo > 100 -> insights.add("• Ritmo Cardíaco: Tu ritmo cardíaco promedio en reposo parece elevado. Considera consultar a un médico.")
                rango.promedioRitmo < 60 -> insights.add("• Ritmo Cardíaco: Tu ritmo cardíaco en reposo es bajo. Esto es común en personas muy activas, pero consúltalo si tienes dudas.")
                else -> insights.add("• Ritmo Cardíaco: Tu ritmo cardíaco promedio en reposo se encuentra en un rango saludable.")
            }
        }

        return if (insights.isEmpty()) "No hay suficientes datos para generar observaciones." else insights.joinToString("\n\n")
    }
}
