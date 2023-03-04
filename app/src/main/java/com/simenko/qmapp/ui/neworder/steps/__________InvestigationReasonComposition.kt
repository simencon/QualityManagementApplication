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
import com.simenko.qmapp.domain.DomainMeasurementReason
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.NewItemViewModel
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun filterAllAfterReasons(appModel: NewItemViewModel, selectedId: Int, clear: Boolean = false) {
    appModel.filterWithOneParent(
        appModel.customersMutable,
        appModel.customers,
        -1
    )
    appModel.filterWithOneParent(
        appModel.teamMembersMutable,
        appModel.teamMembers,
        0
    )
    appModel.selectSingleRecord(appModel.investigationReasonsMutable, selectedId)

    if (clear) {
        appModel.currentOrder.value?.customerId = 0
        appModel.currentOrder.value?.orderedById = 0
    }
}

@Composable
fun ReasonsSelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel
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
                        appModel.currentOrder.value?.reasonId = it.id
                        filterAllAfterReasons(appModel, it.id, true)
                    }
                )
            }
        }

        var index = 0
        first!!.forEach {
            if (it.id == appModel.currentOrder.value?.reasonId) {
                coroutineScope.launch {
                    delay(500)
                    gritState.animateScrollToItem(index = index)
                }
                return
            }
            index++
        }
    }
}

@Composable
fun InvestigationReasonCard(
    inputForOrder: DomainMeasurementReason,
    modifier: Modifier = Modifier,
    onClick: (DomainMeasurementReason)-> Unit
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