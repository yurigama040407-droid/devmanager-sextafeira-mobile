package com.example.devmanagerweb.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.devmanagerweb.domain.model.Orcamento
import com.example.devmanagerweb.domain.model.Projeto
import java.io.File
import java.io.FileOutputStream

object BudgetPdfGenerator {
    
    fun generatePdf(context: Context, projeto: Projeto, orcamento: Orcamento): Uri? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDocument.startPage(pageInfo)
        
        val canvas: Canvas = page.canvas
        val titlePaint = Paint().apply {
            textSize = 24f
            color = Color.BLACK
            isFakeBoldText = true
        }
        val textPaint = Paint().apply {
            textSize = 16f
            color = Color.DKGRAY
        }
        
        var y = 50f
        
        canvas.drawText("Orçamento - DevManager Web", 50f, y, titlePaint)
        y += 40f
        
        canvas.drawText("Projeto: ${projeto.titulo}", 50f, y, textPaint)
        y += 30f
        
        canvas.drawText("Descrição: ${projeto.descricao}", 50f, y, textPaint)
        y += 30f
        
        canvas.drawText("Prazo: ${projeto.prazo}", 50f, y, textPaint)
        y += 30f
        
        canvas.drawText("Tecnologias: ${projeto.tecnologias}", 50f, y, textPaint)
        y += 50f
        
        canvas.drawText("Escopo Técnico:", 50f, y, titlePaint)
        y += 30f
        
        // Simples wrap text manual para o escopo
        val escopoLines = orcamento.escopo.chunked(60) 
        escopoLines.forEach { line ->
            canvas.drawText(line, 50f, y, textPaint)
            y += 25f
        }
        
        y += 30f
        canvas.drawText(String.format("Valor Total: R$ %.2f", orcamento.valor), 50f, y, titlePaint)
        
        pdfDocument.finishPage(page)
        
        val fileName = "orcamento_${projeto.id}.pdf"
        val file = File(context.cacheDir, fileName)
        
        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
