package com.simenko.qmapp.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.simenko.qmapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class GenerateOrderAsPdfUseCase @Inject constructor() {
    suspend fun execute(context: Context, directory: File) {
        val pageHeight = 1120//72 * 4
        val pageWidth = 792//72 * 4
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val title = Paint()

        val myPageInfo = PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val myPage = pdfDocument.startPage(myPageInfo)

        val canvas: Canvas = myPage.canvas
        val bitmap: Bitmap? = AppCompatResources.getDrawable(context, R.drawable.qm_app_logo)?.let { drawableToBitmap(it) }
        val scaleBitMap: Bitmap? = bitmap?.let { Bitmap.createScaledBitmap(it, 120, 120, false) }
        scaleBitMap?.let { canvas.drawBitmap(scaleBitMap, 40f, 40f, paint) }

        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        title.textSize = 15f
        title.color = Color.BLUE
        canvas.drawText("My first pdf", 400f, 100f, title)
        canvas.drawText("Another line", 400f, 80f, title)

        title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        title.color = Color.BLACK
        title.textSize = 15f
        title.textAlign = Paint.Align.CENTER
        canvas.drawText("This is sample document which we have created", 396f, 560f, title)

        pdfDocument.finishPage(myPage)

        val file = File(directory, "sample.pdf")
        try {
            withContext(Dispatchers.IO) {
                pdfDocument.writeTo(FileOutputStream(file))
                Toast.makeText(context, "Pdf doc created!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        pdfDocument.close()
    }
}

private fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}