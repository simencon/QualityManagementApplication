package com.simenko.qmapp.ui.neworder.steps

import android.widget.NumberPicker
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.ui.neworder.*
import java.util.*

@Composable
fun QuantitySelection(
    modifier: Modifier = Modifier
) {
    val viewModel: NewItemViewModel = hiltViewModel()
    val currentSubOrder by viewModel.subOrder.collectAsStateWithLifecycle()

    val onSelectLambda = remember<(Int) -> Unit> { { viewModel.selectSubOrderItemsCount(it) } }

    currentSubOrder.let {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 16.dp)
        ) {
            if (it.subOrder.operationId != NoRecord.num)
                AndroidView(
                    modifier = modifier.width(224.dp),
                    factory = { context ->
                        NumberPicker(context).apply {
                            setOnValueChangedListener { _, oldVal, newVal ->
                                if (oldVal != newVal) onSelectLambda(newVal)
                            }
                            scaleX = 1f
                            scaleY = 1f
                            minValue = ZeroValue.num
                            maxValue = 10
                            this.value = it.subOrder.samplesCount ?: ZeroValue.num
                        }
                    },
                    update = { picker ->
                        picker.value = it.subOrder.samplesCount ?: ZeroValue.num
                    }
                )
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
        Row {
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
        Divider(modifier = modifier.height(2.dp), color = MaterialTheme.colorScheme.secondary)
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