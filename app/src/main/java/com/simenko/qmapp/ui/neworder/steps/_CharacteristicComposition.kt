package com.simenko.qmapp.ui.neworder.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.domain.DomainCharacteristic
import com.simenko.qmapp.domain.DomainManufacturingOperation
import com.simenko.qmapp.domain.DomainSubOrderTask
import com.simenko.qmapp.ui.common.scrollToSelectedItem
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400
import com.simenko.qmapp.utils.StringUtils
import kotlinx.coroutines.launch

private const val TAG = "InputInvestigationTypeComposition"

fun filterAllAfterCharacteristics(
    appModel: NewItemViewModel,
    selectedId: Int = 0,
    clear: Boolean = false
) {
    if (clear) {
        when (changeRecordSelection(
            appModel.characteristicsMutable,
            appModel.pairedTrigger,
            selectedId
        )) {
            true -> {
                val subOrderId = appModel.currentSubOrder.value?.subOrder?.id
                appModel.currentSubOrder.value?.subOrderTasks?.add(
                    getEmptySubOrderTask(
                        selectedId,
                        subOrderId ?: 0
                    )
                )
            }
            false -> {
                val index =
                    appModel.currentSubOrder.value?.subOrderTasks?.indexOfFirst { it.charId == selectedId }
                if (index != null)
                    appModel.currentSubOrder.value?.subOrderTasks?.removeAt(index)
            }
        }
    } else {
        appModel.currentSubOrder.value?.subOrderTasks?.forEach { record ->
            appModel.characteristicsMutable.value?.find { it.id == record.charId }?.isSelected = true
        }
        appModel.pairedTrigger.value = !(appModel.pairedTrigger.value as Boolean)
    }
}

@Composable
fun CharacteristicsSelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel
) {
    val observeInputForOrder by appModel.characteristicsMediator.observeAsState()
    val gritState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    observeInputForOrder?.apply {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            state = gritState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.height(60.dp)
        ) {
            items(first!!.size) { item ->
                CharacteristicCard(
                    input = first!![item],
                    modifier = modifier,
                    onClick = {
//                        appModel.currentSubOrder.value?.operationId = it.id
                        filterAllAfterCharacteristics(appModel, it.id, true)
                    }
                )
            }
        }

//        if (first != null && appModel.currentSubOrder.value != null)
//            coroutineScope.launch {
//                gritState.scrollToSelectedItem(
//                    list = first!!.map { it.id }.toList(),
//                    selectedId = appModel.currentSubOrder.value!!.operationId,
//                )
//            }
    }
}

@Composable
fun CharacteristicCard(
    input: DomainCharacteristic,
    modifier: Modifier = Modifier,
    onClick: (DomainCharacteristic) -> Unit
) {
    val btnBackgroundColor = if (input.isSelected) Primary900 else StatusBar400
    val btnContentColor = if (input.isSelected) Color.White else Color.Black
    val btnColors = ButtonDefaults.buttonColors(
        contentColor = btnContentColor,
        containerColor = btnBackgroundColor
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            colors = btnColors,
            modifier = Modifier
                .width(168.dp)
                .height(56.dp),
            onClick = { onClick(input) }
        ) {
            Text(
                text = input.charDescription ?: "-",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}