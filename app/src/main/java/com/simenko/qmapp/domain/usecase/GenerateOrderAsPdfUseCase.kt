package com.simenko.qmapp.domain.usecase

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.util.TypedValue
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class GenerateOrderAsPdfUseCase @Inject constructor() {
    suspend fun execute(context: Context, directory: File) {
        val pageHeight = 72 * 5
        val pageWidth = 72 * 4
        val pdfDocument = PdfDocument()

        val myPageInfo = PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val myPage = pdfDocument.startPage(myPageInfo)

        val canvas: Canvas = myPage.canvas

        drawOrder(canvas, context)

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

fun drawOrder(canvas: Canvas? = null, context: Context): Bitmap {

    val finalValue: (Float) -> Float = {
        if (canvas != null) it else it.dpToPx(context)
    }

    val bitmap = Bitmap.createBitmap(finalValue(72 * 4f).toInt(), finalValue(72 * 5f).toInt(), Bitmap.Config.ARGB_8888)

    val finalCanvas = canvas ?: Canvas(bitmap)

    val styleTitle = Paint()
    styleTitle.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    styleTitle.color = Color.BLACK
    styleTitle.textSize = finalValue(10f)

    val styleOrderNumber = Paint()
    styleOrderNumber.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    styleOrderNumber.color = Color.BLACK
    styleOrderNumber.textSize = finalValue(26f)

    val styleNormalRowText = Paint()
    styleNormalRowText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    styleNormalRowText.color = Color.BLACK
    styleNormalRowText.textSize = finalValue(13f)


    val styleWhite = Paint()
    styleWhite.color = Color.WHITE


    val space = finalValue(6f)
    var topOffset = 0f

    topOffset += space
    topOffset += styleOrderNumber.textSize
    finalCanvas.drawText("Замовлення №:", finalValue(8f), topOffset, styleTitle)
    finalCanvas.drawText("366932", finalValue(8f + 78f), topOffset, styleOrderNumber)

    topOffset += space
    topOffset += styleNormalRowText.textSize
    finalCanvas.drawText("Підрозділ:", finalValue(8f), topOffset, styleTitle)
    finalCanvas.drawText("ГШСК №1/ШК", finalValue(8f + 78f), topOffset, styleNormalRowText)

    topOffset += space
    topOffset += styleNormalRowText.textSize
    finalCanvas.drawText("Канал:", finalValue(8f), topOffset, styleTitle)
    finalCanvas.drawText("Канал 8", finalValue(8f + 78f), topOffset, styleNormalRowText)

    topOffset += space
    topOffset += styleNormalRowText.textSize
    finalCanvas.drawText("Лінія:", finalValue(8f), topOffset, styleTitle)
    finalCanvas.drawText("IR", finalValue(8f + 78f), topOffset, styleNormalRowText)

    topOffset += space
    topOffset += styleNormalRowText.textSize
    finalCanvas.drawText("Операція:", finalValue(8f), topOffset, styleTitle)
    finalCanvas.drawText("035 Пара 1 (FBM65)", finalValue(8f + 78f), topOffset, styleNormalRowText)

    topOffset += space
    topOffset += styleNormalRowText.textSize
    finalCanvas.drawText("Замовлення розмістив:", finalValue(8f), topOffset, styleTitle)
    finalCanvas.drawText("Михайловський Роман", finalValue(8f + 115f), topOffset, styleNormalRowText)

    topOffset += space
    topOffset += styleNormalRowText.textSize
    finalCanvas.drawText("Дата/час розміщення:", finalValue(8f), topOffset, styleTitle)
    finalCanvas.drawText("30.10.2024 Ср 19:42", finalValue(8f + 115f), topOffset, styleNormalRowText)

    topOffset += space
    finalCanvas.drawLine(0f, topOffset, bitmap.width.toFloat(), topOffset, styleNormalRowText)

    topOffset += space
    topOffset += styleNormalRowText.textSize
    finalCanvas.drawText("Позначення деталі:", finalValue(8f), topOffset, styleTitle)
    finalCanvas.drawText("IR-32208/VU1006 (V.11)", finalValue(8f + 115f), topOffset, styleNormalRowText)

    topOffset += space
    topOffset += styleNormalRowText.textSize
    finalCanvas.drawText("Кількість:", finalValue(8f), topOffset, styleTitle)
    finalCanvas.drawText("2 шт.", finalValue(8f + 78f), topOffset, styleNormalRowText)

    topOffset += space
    topOffset += styleNormalRowText.textSize
    finalCanvas.drawText("Замовлення на замір параметрів:", finalValue(8f), topOffset, styleTitle)

    val characteristics = listOf(
        "Профіль опорного борта",
        "Шорсткість отвору",
        "Шорсткість доріжки кочення IR",
        "Хвилястість доріжки кочення IR",
        "Шорсткість опорного борта",
        "Профіль доріжки кочення (випуклий)",
        "Хвилястість опорного борта",
    )

    topOffset += space
    topOffset += styleNormalRowText.textSize
    characteristics.forEachIndexed { index, s ->
        finalCanvas.drawRect(
            /* left = */ finalValue(8f),
            /* top = */ topOffset - styleNormalRowText.textSize + finalValue(2f),
            /* right = */ finalValue(8f) + styleNormalRowText.textSize,
            /* bottom = */ topOffset + finalValue(2f),
            /* paint = */ styleNormalRowText
        )

        finalCanvas.drawRect(
            /* left = */ finalValue(9f),
            /* top = */ topOffset - styleNormalRowText.textSize + finalValue(3f),
            /* right = */ finalValue(7f) + styleNormalRowText.textSize,
            /* bottom = */ topOffset + finalValue(1f),
            /* paint = */ styleWhite
        )
        finalCanvas.drawText("${index + 1}. $s", finalValue(8f + 24f), topOffset, styleNormalRowText)
        topOffset += space
        topOffset += styleNormalRowText.textSize
    }
    return bitmap
}

fun Float.dpToPx(context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
fun Int.spToPx(context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toFloat(), context.resources.displayMetrics)
fun Int.pxToDp(context: Context) = this / (context.resources.displayMetrics.density)

@Composable
fun SubOrderTicket(modifier: Modifier = Modifier) {
    val localContext = LocalContext.current
    Column {
        Image(bitmap = drawOrder(context = localContext).asImageBitmap(), contentDescription = "JustToTest")
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun SubOrderTicketPreview() {
    SubOrderTicket(Modifier)
}