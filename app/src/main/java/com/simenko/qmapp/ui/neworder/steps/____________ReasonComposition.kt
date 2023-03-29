package com.simenko.qmapp.ui.neworder.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.DomainReason
import com.simenko.qmapp.ui.common.scrollToSelectedItem
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400
import kotlinx.coroutines.launch

fun filterAllAfterReasons(appModel: NewItemViewModel, selectedId: Int, clear: Boolean = false) {
    appModel.customersMutable.performFiltration(
        appModel.customers,
        FilteringMode.ADD_ALL,
        appModel.pairedTrigger
    )
    appModel.orderPlacersMutable.performFiltration(
        appModel.teamMembers,
        FilteringMode.REMOVE_ALL,
        appModel.pairedTrigger
    )
    selectSingleRecord(appModel.investigationReasonsMutable, appModel.pairedTrigger, selectedId)

    if (clear) {
        appModel.currentOrder.value?.customerId = 0
        appModel.currentOrder.value?.orderedById = 0
    }
}

fun filterAllAfterReasonsForSubOrderStandAlone(appModel: NewItemViewModel, selectedId: Int, clear: Boolean = false) {
    appModel.departmentsMutable.performFiltration(
        s = appModel.departments,
        action = FilteringMode.ADD_ALL_FROM_META_TABLE,
        trigger = appModel.pairedTrigger,
        m = appModel.inputForOrder
    )
    selectSingleRecord(appModel.departmentsMutable, appModel.pairedTrigger)

    appModel.subDepartmentsMutable.performFiltration(
        action = FilteringMode.REMOVE_ALL,
        trigger = appModel.pairedTrigger
    )
    appModel.subOrderPlacersMutable.performFiltration(
        action = FilteringMode.REMOVE_ALL,
        trigger = appModel.pairedTrigger
    )
    appModel.channelsMutable.performFiltration(
        action = FilteringMode.REMOVE_ALL,
        trigger = appModel.pairedTrigger
    )
    appModel.linesMutable.performFiltration(
        action = FilteringMode.REMOVE_ALL,
        trigger = appModel.pairedTrigger
    )
    appModel.itemVersionsCompleteMutable.performFiltration(
        action = FilteringMode.REMOVE_ALL,
        trigger = appModel.pairedTrigger
    )
    appModel.operationsMutable.performFiltration(
        action = FilteringMode.REMOVE_ALL,
        trigger = appModel.pairedTrigger
    )
    appModel.characteristicsMutable.performFiltration(
        action = FilteringMode.REMOVE_ALL,
        trigger = appModel.pairedTrigger
    )
    selectSingleRecord(appModel.investigationReasonsMutable, appModel.pairedTrigger, selectedId)

    if (clear) {
        appModel.currentSubOrder.value?.subOrder?.departmentId = 0
        appModel.currentSubOrder.value?.subOrder?.subDepartmentId = 0
        appModel.currentSubOrder.value?.subOrder?.orderedById = 0
        appModel.currentSubOrder.value?.subOrder?.channelId = 0
        appModel.currentSubOrder.value?.subOrder?.lineId = 0
        appModel.currentSubOrder.value?.subOrder?.itemPreffix = ""
        appModel.currentSubOrder.value?.subOrder?.itemTypeId = 0
        appModel.currentSubOrder.value?.subOrder?.itemVersionId = 0
        appModel.currentSubOrder.value?.subOrder?.operationId = 0
        appModel.currentSubOrder.value?.subOrder?.samplesCount = 0
        appModel.currentSubOrder.value?.samples?.removeIf { it.isNewRecord }
        appModel.currentSubOrder.value?.samples?.forEach {it.toBeDeleted = true}
        appModel.currentSubOrder.value?.subOrderTasks?.removeIf { it.isNewRecord }
        appModel.currentSubOrder.value?.subOrderTasks?.forEach {it.toBeDeleted = true}
    }
}

@Composable
fun ReasonsSelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel,
    actionType: ActionType = ActionType.ADD_ORDER
) {
    val observeInputForOrder by appModel.investigationReasonsMediator.observeAsState()
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
                InvestigationReasonCard(
                    inputForOrder = first!![item],
                    modifier = modifier.padding(top = 0.dp),
                    onClick = {
                        if (actionType == ActionType.ADD_SUB_ORDER_STAND_ALONE) {
                            appModel.currentSubOrder.value?.order?.reasonId = it.id
                            filterAllAfterReasonsForSubOrderStandAlone(appModel, it.id, true)
                        }
                        else {
                            appModel.currentOrder.value?.reasonId = it.id
                            filterAllAfterReasons(appModel, it.id, true)
                        }
                    }
                )
            }
        }

        if (first != null && appModel.currentOrder.value != null)
            coroutineScope.launch {
                gritState.scrollToSelectedItem(
                    list = first!!.map { it.id }.toList(),
                    selectedId = appModel.currentOrder.value!!.reasonId,
                )
            }
    }
}

@Composable
fun InvestigationReasonCard(
    inputForOrder: DomainReason,
    modifier: Modifier = Modifier,
    onClick: (DomainReason) -> Unit
) {

    val btnBackgroundColor = if (inputForOrder.isSelected) Primary900 else StatusBar400
    val btnContentColor = if (inputForOrder.isSelected) Color.White else Color.Black
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
                .width(224.dp)
                .height(56.dp),
            onClick = {
                onClick(inputForOrder)
            }
        ) {
            Text(
                text = inputForOrder.reasonDescription ?: "-"
            )
        }
    }
}