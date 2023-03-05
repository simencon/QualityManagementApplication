package com.simenko.qmapp.ui.neworder.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.DomainOrdersType
import com.simenko.qmapp.ui.common.scrollToSelectedItem
import com.simenko.qmapp.ui.neworder.FilteringMode
import com.simenko.qmapp.ui.neworder.NewItemViewModel
import com.simenko.qmapp.ui.neworder.filterWithOneParentM
import com.simenko.qmapp.ui.neworder.selectSingleRecord
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400
import kotlinx.coroutines.launch

private const val TAG = "InputInvestigationTypeComposition"

fun filterAllAfterTypes(appModel: NewItemViewModel, selectedId: Int, clear: Boolean = false) {

    appModel.investigationReasonsMutable.filterWithOneParentM(
        appModel.investigationReasons,
        FilteringMode.ADD_ALL,
        appModel.pairedTrigger
    )
    appModel.customersMutable.filterWithOneParentM(
        appModel.customers,
        FilteringMode.REMOVE_ALL,
        appModel.pairedTrigger
    )
    appModel.teamMembersMutable.filterWithOneParentM(
        appModel.teamMembers,
        FilteringMode.REMOVE_ALL,
        appModel.pairedTrigger
    )
    selectSingleRecord(appModel.investigationTypesMutable, appModel.pairedTrigger, selectedId)

    if (clear) {
        appModel.currentOrder.value?.reasonId = 0
        appModel.currentOrder.value?.customerId = 0
        appModel.currentOrder.value?.orderedById = 0
    }
}

@Composable
fun TypesSelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel
) {
    val observeInputForOrder by appModel.investigationTypesMediator.observeAsState()
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
                InvestigationTypeCard(
                    inputForOrder = first!![item],
                    modifier = modifier,
                    onClick = {
                        appModel.currentOrder.value?.orderTypeId = it.id
                        filterAllAfterTypes(appModel, it.id, true)
                    }
                )
            }
        }

        if (first != null && appModel.currentOrder.value != null)
            coroutineScope.launch {
                gritState.scrollToSelectedItem(
                    list = first!!.map { it.id }.toList(),
                    selectedId = appModel.currentOrder.value!!.orderTypeId,
                )
            }
    }
}

@Composable
fun InvestigationTypeCard(
    inputForOrder: DomainOrdersType,
    modifier: Modifier = Modifier,
    onClick: (DomainOrdersType) -> Unit
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
            onClick = { onClick(inputForOrder) }
        ) {
            Text(
                text = inputForOrder.typeDescription ?: "-"
            )
        }
    }
}