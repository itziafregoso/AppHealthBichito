package com.example.healthbichito.util

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.healthbichito.ui.viewmodels.ResumenDiario
import com.example.healthbichito.ui.viewmodels.ResumenRangoEstadisticas
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ReportePdfHelper {

    fun crearReportePDF(
        context: Context,
        resumenDia: ResumenDiario,
        resumenRango: ResumenRangoEstadisticas
    ) {
        try {
            val pdfDocument = android.graphics.pdf.PdfDocument()
            val paint = Paint()
            val titlePaint = Paint()

            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val verdeFuerte = Color.parseColor("#4CAF50")
            val negro = Color.BLACK
            val gris = Color.DKGRAY

            // 🔹 Encabezado
            paint.color = verdeFuerte
            canvas.drawRect(0f, 0f, 595f, 100f, paint)

            titlePaint.color = Color.WHITE
            titlePaint.textSize = 28f
            titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Reporte de Estadísticas", 180f, 60f, titlePaint)

            // 🔹 Fecha de reporte
            paint.color = Color.WHITE
            paint.textSize = 14f
            canvas.drawText("Generado: ${obtenerFechaActual()}", 420f, 85f, paint)

            // ======== SECCIÓN 1: RESUMEN DEL DÍA ========
            titlePaint.color = negro
            titlePaint.textSize = 20f
            canvas.drawText("Resumen del Día:", 40f, 140f, titlePaint)

            paint.color = gris
            paint.textSize = 16f
            canvas.drawText("Pasos hoy: ${resumenDia.pasos}", 40f, 170f, paint)
            canvas.drawText("Calorías quemadas hoy: ${resumenDia.calorias} kcal", 40f, 200f, paint)
            canvas.drawText("Agua tomada hoy: ${resumenDia.agua} ml", 40f, 230f, paint)
            canvas.drawText(
                "¿Medicaciones tomadas?: ${if (resumenDia.medicacion) "Sí" else "No"}",
                40f,
                260f,
                paint
            )

            // ======== SECCIÓN 2: RESUMEN DEL RANGO ========
            titlePaint.textSize = 20f
            canvas.drawText("Resumen del Rango:", 40f, 310f, titlePaint)

            paint.textSize = 16f
            canvas.drawText("Día con más pasos: ${resumenRango.diaMaxPasos}", 40f, 340f, paint)
            canvas.drawText("Máximo de pasos: ${resumenRango.maxPasos}", 40f, 370f, paint)
            canvas.drawText("Promedio de pasos: ${resumenRango.promedioPasos}", 40f, 400f, paint)
            canvas.drawText(
                "Días que cumplió meta de agua: ${resumenRango.diasMetaAguaCumplida}/${resumenRango.totalDias}",
                40f,
                430f,
                paint
            )

            // 🔹 Historial de pasos (tabla simplificada)
            canvas.drawText("Historial de pasos:", 40f, 470f, titlePaint)
            var yPos = 500f
            resumenRango.historialPasos.forEach { (fecha, pasos) ->
                canvas.drawText("$fecha: $pasos pasos", 60f, yPos, paint)
                yPos += 25f
            }

            pdfDocument.finishPage(page)

            // ======== GUARDAR ARCHIVO ========
            val carpeta = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "HealthBichito"
            )
            if (!carpeta.exists()) carpeta.mkdirs()

            val nombreArchivo = "Reporte_${obtenerFechaActual().replace(":", "-")}.pdf"
            val file = File(carpeta, nombreArchivo)

            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            Toast.makeText(context, "PDF generado: ${file.path}", Toast.LENGTH_LONG).show()

            // 📂 Abrir PDF automáticamente
            PDFHelper.abrirPDF(context, file)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun obtenerFechaActual(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    }
}
