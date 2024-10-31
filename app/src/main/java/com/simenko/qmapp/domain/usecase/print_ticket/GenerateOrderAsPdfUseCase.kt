package com.simenko.qmapp.domain.usecase.print_ticket

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.util.TypedValue
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.simenko.qmapp.R
import com.simenko.qmapp.data.repository.InvestigationsRepository
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.DomainResultComplete
import com.simenko.qmapp.domain.entities.DomainSampleComplete
import com.simenko.qmapp.domain.entities.DomainSubOrderComplete
import com.simenko.qmapp.domain.entities.DomainSubOrderTaskComplete
import com.simenko.qmapp.utils.Rounder
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.StringUtils.getStringDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

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

fun Float.dpToPx(context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
fun Float.finalValue(canvas: Canvas?, context: Context, useAsItIs: Boolean = false): Float {
    return if ((canvas != null) || useAsItIs) this else this.dpToPx(context)
}

fun getTitleStyle(canvas: Canvas? = null, context: Context, useAsItIs: Boolean = false): Paint {
    val styleTitle = Paint()
    styleTitle.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    styleTitle.color = Color.BLACK
    styleTitle.textSize = 10f.finalValue(canvas, context, useAsItIs)
    return styleTitle
}

fun getOrderNumberStyle(canvas: Canvas? = null, context: Context, useAsItIs: Boolean = false): Paint {
    val styleOrderNumber = Paint()
    styleOrderNumber.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    styleOrderNumber.color = Color.BLACK
    styleOrderNumber.textSize = (26f).finalValue(canvas, context, useAsItIs)
    return styleOrderNumber
}

fun getNormalRowStyle(canvas: Canvas? = null, context: Context, useAsItIs: Boolean = false): Paint {
    val styleNormalRowText = Paint()
    styleNormalRowText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    styleNormalRowText.color = Color.BLACK
    styleNormalRowText.textSize = (10f).finalValue(canvas, context, useAsItIs)
    return styleNormalRowText
}

const val PAGE_WIDTH = 72 * 4.0f
const val PAGE_HEIGHT = 72 * 9.2f

class GenerateOrderAsPdfUseCase @Inject constructor(private val repository: InvestigationsRepository) {
    suspend fun execute(context: Context, directory: File, subOrderId: ID) {
        withContext(Dispatchers.IO) {
            val subOrderComplete = repository.subOrderCompleteById(subOrderId)
            val subOrderTasks = repository.tasksCompleteBySubOrderId(subOrderId)
            val samples = repository.samplesCompleteBySubOrderId(subOrderId)
            val results = repository.resultsCompleteBySubOrderId(subOrderId).filter { it.result.isOk == false }

            val styleTitle = getTitleStyle(null, context, true)
            val styleOrderNumber = getOrderNumberStyle(null, context, true)
            val styleNormalRow = getNormalRowStyle(null, context, true)

            val finalHeight =
                (styleOrderNumber.textSize + 6f) + // title with number
                        (11 * (styleNormalRow.textSize + 6f)) + // default rows
                        (if (subOrderComplete.subOrder.statusId == 3L || subOrderComplete.subOrder.statusId == 4L) (2 * (styleNormalRow.textSize + 6f)) else 0f) +
                                (subOrderTasks.size * (styleNormalRow.textSize + 6f)) + // tasks
                                (samples.filter { it.sampleResult.isOk == false }.size * (styleNormalRow.textSize + 6f)) + // samples
                                (results.filter { it.result.isOk == false }.size * ((styleNormalRow.textSize + 6f) * 4 + 8f)) // results

            val pdfDocument = PdfDocument()
            val myPageInfo = PageInfo.Builder(PAGE_WIDTH.toInt(), finalHeight.toInt(), 1).create()
            val myPage = pdfDocument.startPage(myPageInfo)
            drawOrder(
                canvas = myPage.canvas,
                context = context,
                styleTitle = styleTitle,
                styleOrderNumber = styleOrderNumber,
                styleNormalRow = styleNormalRow,
                height = PAGE_HEIGHT,
                width = PAGE_WIDTH,

                subOrder = subOrderComplete,
                tasks = subOrderTasks,
                samples = samples,
                results = results
            )
            pdfDocument.finishPage(myPage)

            val file = File(directory, "result.pdf")
            try {
                pdfDocument.writeTo(FileOutputStream(file))
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            pdfDocument.close()
        }
    }
}

fun drawOrder(
    canvas: Canvas? = null,
    context: Context,
    styleTitle: Paint,
    styleOrderNumber: Paint,
    styleNormalRow: Paint,
    height: Float,
    width: Float,

    subOrder: DomainSubOrderComplete,
    tasks: List<DomainSubOrderTaskComplete>,
    samples: List<DomainSampleComplete>,
    results: List<DomainResultComplete>
): Bitmap {

    val bitmap = Bitmap.createBitmap(width.finalValue(canvas, context).toInt(), height.finalValue(canvas, context).toInt(), Bitmap.Config.ARGB_8888)
    val finalCanvas = canvas ?: Canvas(bitmap)

    val space = (6f).finalValue(canvas, context)
    var topOffset = 0f

    topOffset += space
    topOffset += styleOrderNumber.textSize
    finalCanvas.drawText("Замовлення №:", (8f).finalValue(canvas, context), topOffset, styleTitle)
    finalCanvas.drawText(
        /* text = */ StringUtils.concatTwoStrings(subOrder.orderShort.order.orderNumber?.toString(), subOrder.subOrder.subOrderNumber.toString()),
        /* x = */ (8f + 78f).finalValue(canvas, context),
        /* y = */ topOffset,
        /* paint = */ styleOrderNumber
    )

    topOffset += space
    topOffset += styleNormalRow.textSize
    val resultValueDbl = subOrder.subOrderResult.good?.let { good ->
        subOrder.subOrderResult.total?.let { total ->
            if (total == ZeroValue.num.toInt()) null else (good.toDouble() / total.toDouble()) * 100.0
        }
    }
    val resultValue = resultValueDbl?.let {
        if (it == 100.0) {
            AppCompatResources.getDrawable(context, R.drawable.outline_check_box_24)?.let { drawable ->
                drawableToBitmap(drawable)?.let { bitmap ->
                    val scale = 24f.finalValue(canvas, context) / bitmap.height * 0.5f * 100f
                    finalCanvas.drawBitmap(
                        /* bitmap = */ Bitmap.createScaledBitmap(bitmap, scale.toInt(), scale.toInt(), false),
                        /* left = */ (249f).finalValue(canvas, context),
                        /* top = */ topOffset - styleNormalRow.textSize - (2f).finalValue(canvas, context),
                        /* paint = */ styleNormalRow
                    )
                }
            }
        } else {
            AppCompatResources.getDrawable(context, R.drawable.outline_disabled_by_default_24)?.let { drawable ->
                drawableToBitmap(drawable)?.let { bitmap ->
                    val scale = 24f.finalValue(canvas, context) / bitmap.height * 0.5f * 100f
                    finalCanvas.drawBitmap(
                        /* bitmap = */ Bitmap.createScaledBitmap(bitmap, scale.toInt(), scale.toInt(), false),
                        /* left = */ (249f).finalValue(canvas, context),
                        /* top = */ topOffset - styleNormalRow.textSize - (2f).finalValue(canvas, context),
                        /* paint = */ styleNormalRow
                    )
                }
            }
        }

        " (відповідність: ${Rounder.withToleranceStrCustom(it, 2)}%)"
    } ?: EmptyString.str

    finalCanvas.drawText("Статус замовлення:", (8f).finalValue(canvas, context), topOffset, styleTitle)
    finalCanvas.drawText((subOrder.status.statusDescription ?: NoString.str) + resultValue, (8f + 100f).finalValue(canvas, context), topOffset, styleNormalRow)


    topOffset += space
    topOffset += styleNormalRow.textSize
    finalCanvas.drawText("Підрозділ:", (8f).finalValue(canvas, context), topOffset, styleTitle)
    finalCanvas.drawText(StringUtils.concatTwoStrings(subOrder.department.depAbbr, subOrder.subDepartment.subDepAbbr), (8f + 78f).finalValue(canvas, context), topOffset, styleNormalRow)

    topOffset += space
    topOffset += styleNormalRow.textSize
    finalCanvas.drawText("Канал:", (8f).finalValue(canvas, context), topOffset, styleTitle)
    finalCanvas.drawText(subOrder.channel.channelAbbr ?: NoString.str, (8f + 78f).finalValue(canvas, context), topOffset, styleNormalRow)

    topOffset += space
    topOffset += styleNormalRow.textSize
    finalCanvas.drawText("Лінія:", (8f).finalValue(canvas, context), topOffset, styleTitle)
    finalCanvas.drawText(subOrder.line.lineAbbr, (8f + 78f).finalValue(canvas, context), topOffset, styleNormalRow)

    topOffset += space
    topOffset += styleNormalRow.textSize
    finalCanvas.drawText("Операція:", (8f).finalValue(canvas, context), topOffset, styleTitle)
    finalCanvas.drawText(StringUtils.concatTwoStrings1(subOrder.operation.operationAbbr, subOrder.operation.equipment), (8f + 78f).finalValue(canvas, context), topOffset, styleNormalRow)

    topOffset += space
    topOffset += styleNormalRow.textSize
    finalCanvas.drawText("Замовлення розмістив:", (8f).finalValue(canvas, context), topOffset, styleTitle)
    finalCanvas.drawText(subOrder.orderedBy.fullName, (8f + 115f).finalValue(canvas, context), topOffset, styleNormalRow)

    topOffset += space
    topOffset += styleNormalRow.textSize
    finalCanvas.drawText("Дата/час розміщення:", (8f).finalValue(canvas, context), topOffset, styleTitle)
    finalCanvas.drawText(getStringDate(subOrder.subOrder.createdDate), (8f + 115f).finalValue(canvas, context), topOffset, styleNormalRow)

    if (subOrder.subOrder.statusId == 3L || subOrder.subOrder.statusId == 4L) {
        topOffset += space
        topOffset += styleNormalRow.textSize
        finalCanvas.drawText("Замовлення виконав:", (8f).finalValue(canvas, context), topOffset, styleTitle)
        finalCanvas.drawText(subOrder.completedBy?.fullName ?: NoString.str, (8f + 115f).finalValue(canvas, context), topOffset, styleNormalRow)

        topOffset += space
        topOffset += styleNormalRow.textSize
        finalCanvas.drawText("Дата/час виконання:", (8f).finalValue(canvas, context), topOffset, styleTitle)
        finalCanvas.drawText(getStringDate(subOrder.subOrder.completedDate), (8f + 115f).finalValue(canvas, context), topOffset, styleNormalRow)
    }

    topOffset += space
    finalCanvas.drawLine(0f, topOffset, bitmap.width.toFloat(), topOffset, styleNormalRow)

    topOffset += space
    topOffset += styleNormalRow.textSize
    finalCanvas.drawText("Позначення деталі:", (8f).finalValue(canvas, context), topOffset, styleTitle)
    finalCanvas.drawText(
        /* text = */ StringUtils.concatThreeStrings2(
            subOrder.itemVersionComplete.itemComplete.key.componentKey,
            subOrder.itemVersionComplete.itemComplete.item.itemDesignation,
            subOrder.itemVersionComplete.itemVersion.versionDescription
        ),
        /* x = */ (8f + 115f).finalValue(canvas, context),
        /* y = */ topOffset,
        /* paint = */ styleNormalRow
    )

    topOffset += space
    topOffset += styleNormalRow.textSize
    finalCanvas.drawText("Кількість:", (8f).finalValue(canvas, context), topOffset, styleTitle)
    finalCanvas.drawText("${subOrder.subOrder.samplesCount?.toString() ?: NoString.str} шт.", (8f + 78f).finalValue(canvas, context), topOffset, styleNormalRow)

    topOffset += space
    topOffset += styleNormalRow.textSize
    finalCanvas.drawText("Замовлення на замір параметрів:", (8f).finalValue(canvas, context), topOffset, styleTitle)

    topOffset += space
    topOffset += styleNormalRow.textSize
    tasks.forEachIndexed { index, s ->
        when (s.taskResult.isOk) {
            true -> {
                AppCompatResources.getDrawable(context, R.drawable.outline_check_box_24)?.let { drawable ->
                    drawableToBitmap(drawable)?.let { bitmap ->
                        val scale = 24f.finalValue(canvas, context) / bitmap.height * 0.5f * 100f
                        finalCanvas.drawBitmap(
                            /* bitmap = */ Bitmap.createScaledBitmap(bitmap, scale.toInt(), scale.toInt(), false),
                            /* left = */ (4f).finalValue(canvas, context),
                            /* top = */ topOffset - styleNormalRow.textSize - (2f).finalValue(canvas, context),
                            /* paint = */ styleNormalRow
                        )
                    }
                }
            }

            false -> {
                AppCompatResources.getDrawable(context, R.drawable.outline_disabled_by_default_24)?.let { drawable ->
                    drawableToBitmap(drawable)?.let { bitmap ->
                        val scale = 24f.finalValue(canvas, context) / bitmap.height * 0.5f * 100f
                        finalCanvas.drawBitmap(
                            /* bitmap = */ Bitmap.createScaledBitmap(bitmap, scale.toInt(), scale.toInt(), false),
                            /* left = */ (4f).finalValue(canvas, context),
                            /* top = */ topOffset - styleNormalRow.textSize - (2.0f).finalValue(canvas, context),
                            /* paint = */ styleNormalRow
                        )
                    }
                }
            }

            else -> {
                AppCompatResources.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24)?.let { drawable ->
                    drawableToBitmap(drawable)?.let { bitmap ->
                        val scale = 24f.finalValue(canvas, context) / bitmap.height * 0.5f * 100f
                        finalCanvas.drawBitmap(
                            /* bitmap = */ Bitmap.createScaledBitmap(bitmap, scale.toInt(), scale.toInt(), false),
                            /* left = */ (4f).finalValue(canvas, context),
                            /* top = */ topOffset - styleNormalRow.textSize - (2.0f).finalValue(canvas, context),
                            /* paint = */ styleNormalRow
                        )
                    }
                }
            }
        }

        finalCanvas.drawText("${index + 1}. ${s.characteristic.characteristic.charDescription}", (8f + 16f).finalValue(canvas, context), topOffset, styleNormalRow)

        samples.filter { it.sampleResult.taskId == s.subOrderTask.id && it.sampleResult.isOk == false }.let { wrongSamples ->
            if (wrongSamples.isNotEmpty()) {
                wrongSamples.forEach { wrongSample ->
                    topOffset += space
                    topOffset += styleNormalRow.textSize
                    finalCanvas.drawText("Зразок № ${wrongSample.sample.sampleNumber}", (8f + 24f).finalValue(canvas, context), topOffset, styleNormalRow)

                    results.filter { it.result.sampleId == wrongSample.sample.id && it.result.taskId == s.subOrderTask.id && it.result.isOk == false }.let { wrongResults ->
                        if (wrongResults.isNotEmpty()) {
                            wrongResults.forEach { wrongResult ->
                                topOffset += space
                                topOffset += styleNormalRow.textSize

                                AppCompatResources.getDrawable(context, R.drawable.outline_disabled_by_default_24)?.let { drawable ->
                                    drawableToBitmap(drawable)?.let { bitmap ->
                                        val scale = 24f.finalValue(canvas, context) / bitmap.height * 0.5f * 100f
                                        finalCanvas.drawBitmap(
                                            /* bitmap = */ Bitmap.createScaledBitmap(bitmap, scale.toInt(), scale.toInt(), false),
                                            /* left = */ (36f).finalValue(canvas, context),
                                            /* top = */ topOffset - styleNormalRow.textSize - (2.0f).finalValue(canvas, context),
                                            /* paint = */ styleNormalRow
                                        )
                                    }
                                }

                                finalCanvas.drawText("Позначення:", (8f + 48f).finalValue(canvas, context), topOffset, styleTitle)
                                finalCanvas.drawText(wrongResult.metrix.metrixDesignation ?: NoString.str, (8f + 112f).finalValue(canvas, context), topOffset, styleNormalRow)
                                finalCanvas.drawText("Результат заміру:", (8f + 186f).finalValue(canvas, context), topOffset, styleTitle)

                                topOffset += space
                                topOffset += styleNormalRow.textSize
                                finalCanvas.drawText("Одиниці:", (8f + 48f).finalValue(canvas, context), topOffset, styleTitle)
                                finalCanvas.drawText(wrongResult.metrix.units ?: NoString.str, (8f + 112f).finalValue(canvas, context), topOffset, styleNormalRow)
                                finalCanvas.drawText(
                                    wrongResult.result.result?.toString() ?: NoString.str,
                                    (8f + 186f).finalValue(canvas, context),
                                    topOffset + (11f).finalValue(canvas, context),
                                    styleOrderNumber
                                )

                                topOffset += space
                                topOffset += styleNormalRow.textSize
                                finalCanvas.drawText("Номінал:", (8f + 48f).finalValue(canvas, context), topOffset, styleTitle)
                                finalCanvas.drawText(wrongResult.resultTolerance.nominal?.toString() ?: NoString.str, (8f + 112f).finalValue(canvas, context), topOffset, styleNormalRow)

                                topOffset += space
                                topOffset += styleNormalRow.textSize
                                finalCanvas.drawText("LSL/USL:", (8f + 48f).finalValue(canvas, context), topOffset, styleTitle)
                                finalCanvas.drawText(
                                    wrongResult.resultTolerance.run { StringUtils.concatTwoStrings(lsl?.toString(), usl?.toString()) },
                                    (8f + 112f).finalValue(canvas, context),
                                    topOffset,
                                    styleNormalRow
                                )

                                topOffset += (8f).finalValue(canvas, context)
                            }
                        }
                    }
                }
            }
        }

        topOffset += space
        topOffset += styleNormalRow.textSize
    }
    return bitmap
}

@Composable
fun SubOrderTicket(modifier: Modifier = Modifier) {
    val localContext = LocalContext.current

    val styleTitle = getTitleStyle(null, localContext)
    val styleOrderNumber = getOrderNumberStyle(null, localContext)
    val styleNormalRow = getNormalRowStyle(null, localContext)

    Column {
        Image(
            bitmap = drawOrder(
                canvas = null,
                context = localContext,
                styleTitle = styleTitle,
                styleOrderNumber = styleOrderNumber,
                styleNormalRow = styleNormalRow,
                height = PAGE_HEIGHT,
                width = PAGE_WIDTH,

                subOrder = subOrderComplete,
                tasks = subOrderTasks,
                samples = samples,
                results = results
            ).asImageBitmap(),
            contentDescription = "JustToTest"
        )
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
