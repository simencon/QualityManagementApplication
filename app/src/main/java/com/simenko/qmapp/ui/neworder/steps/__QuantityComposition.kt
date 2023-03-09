package com.simenko.qmapp.ui.neworder.steps

import android.widget.NumberPicker
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.ui.theme.Accent200
import java.util.*

private const val TAG = "InputInvestigationTypeComposition"

fun filterAllAfterQuantity(appModel: NewItemViewModel, selectedId: Int, clear: Boolean = false) {

//    appModel.operationsMutable.performFiltration(
//        s = appModel.operations,
//        action = FilteringMode.ADD_BY_PARENT_ID_FROM_META_TABLE,
//        trigger = appModel.pairedTrigger,
//        pId = selectedId,
//        m = appModel.inputForOrder,
//        step = FilteringStep.OPERATIONS
//    )

    if (clear) {
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
            if (first!!.operationId != 0)
                AndroidView(
                    modifier = modifier.width(224.dp),
                    factory = { context ->
                        NumberPicker(context).apply {
                            setOnValueChangedListener { picker, oldVal, newVal ->
                                appModel!!.currentSubOrder.value!!.samplesCount = newVal
                                filterAllAfterQuantity(appModel, newVal, true)
                            }
                            scaleX = 1f
                            scaleY = 1f
                            minValue = 0
                            maxValue = 10
                            this.value = first!!.samplesCount ?: 0
                        }
                    },
                    update = {
                        it.value = first!!.samplesCount ?: 0
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