package com.simenko.qmapp.ui.neworder.steps

import android.widget.NumberPicker
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.DomainSample
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.ui.theme.Accent200
import com.simenko.qmapp.utils.StringUtils
import java.util.*

private const val TAG = "InputInvestigationTypeComposition"

fun filterAllAfterQuantity(
    appModel: NewItemViewModel,
    samplesQuantity: Int,
    clear: Boolean = false
) {
//    ToDo is not optimized while quantity is not reset
    appModel.characteristicsMutable.performFiltration(
        s = appModel.characteristics,
        action = FilteringMode.ADD_BY_PARENT_ID_FROM_META_TABLE,
        trigger = appModel.pairedTrigger,
        p1Id = samplesQuantity,
        p2Id = appModel.currentSubOrder.value?.subOrder?.itemPreffix ?: NoString.str,
        p3Id = appModel.currentSubOrder.value?.subOrder?.operationId ?: NoRecord.num,
        pFlow = appModel.operationsFlows.value,
        m = appModel.inputForOrder,
        step = FilteringStep.CHARACTERISTICS
    )

    if (clear) {
        appModel.currentSubOrder.value?.subOrderTasks?.removeIf { it.isNewRecord }
        appModel.currentSubOrder.value?.subOrderTasks?.forEach { it.toBeDeleted = true }

        val subOrderId = appModel.currentSubOrder.value?.subOrder?.id
        var currentSize = ZeroValue.num
        appModel.currentSubOrder.value?.samples?.forEach {
            if (it.isNewRecord || !it.toBeDeleted)
                currentSize++
        }

        if (samplesQuantity > currentSize) {
            for (q in (currentSize + 1)..samplesQuantity)
                if ((appModel.currentSubOrder.value?.samples?.size ?: ZeroValue.num) >= q &&
                    appModel.currentSubOrder.value?.samples?.get(q - 1)?.isNewRecord == false
                )
                    appModel.currentSubOrder.value?.samples?.get(q - 1)?.toBeDeleted = false
                else
                    appModel.currentSubOrder.value?.samples?.add(
                        DomainSample().copy(subOrderId = subOrderId ?: NoRecord.num, sampleNumber = q, isNewRecord = true)
                    )
        } else if (samplesQuantity < currentSize) {
            for (q in currentSize downTo samplesQuantity + 1)
                if (appModel.currentSubOrder.value?.samples?.get(q - 1)?.isNewRecord != false)
                    appModel.currentSubOrder.value?.samples?.removeAt(q - 1)
                else
                    appModel.currentSubOrder.value?.samples?.get(q - 1)?.toBeDeleted = true
        }
    }

}

@Composable
fun ButtonsSectionQuantity(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Row() {
            Text(
                text = stringResource(title).uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 18.sp),
                modifier = Modifier
                    .paddingFromBaseline(top = 40.dp, bottom = 0.dp)
                    .padding(horizontal = 16.dp)
            )
            content()
        }
        Spacer(Modifier.height(0.dp))
        Divider(
            modifier = modifier.height(2.dp),
            color = Accent200
        )
    }
}

@Composable
fun QuantitySelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel? = null
) {
    val observeInputForOrder by appModel!!.currentSubOrderMediator.observeAsState()

    observeInputForOrder?.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 16.dp)
        ) {
            if (first?.subOrder?.operationId != NoRecord.num)
                AndroidView(
                    modifier = modifier.width(224.dp),
                    factory = { context ->
                        NumberPicker(context).apply {
                            setOnValueChangedListener { picker, oldVal, newVal ->
                                appModel!!.currentSubOrder.value?.subOrder?.samplesCount = newVal
                                if (oldVal == ZeroValue.num || newVal == ZeroValue.num)
                                    filterAllAfterQuantity(appModel, newVal, true)
                                else
                                    filterAllAfterQuantity(appModel, newVal, true)
                            }
                            scaleX = 1f
                            scaleY = 1f
                            minValue = ZeroValue.num
                            maxValue = 10
                            this.value = first?.subOrder?.samplesCount ?: ZeroValue.num
                        }
                    },
                    update = {
                        it.value = first?.subOrder?.samplesCount ?: ZeroValue.num
                    }
                )
        }
    }
}

@Preview(name = "Light Mode Order", showBackground = true, widthDp = 409)
@Composable
fun MyPickerPreview() {
    ButtonsSectionQuantity(title = R.string.select_quantity) {
        QuantitySelection(
            modifier = Modifier.padding(top = 0.dp)
        )
    }
}