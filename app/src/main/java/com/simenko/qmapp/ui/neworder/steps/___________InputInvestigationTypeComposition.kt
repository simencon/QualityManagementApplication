package com.simenko.qmapp.ui.neworder.steps

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.simenko.qmapp.domain.DomainOrdersType
import com.simenko.qmapp.ui.common.OnLifecycleEvent
import com.simenko.qmapp.ui.neworder.NewItemViewModel
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "InputInvestigationTypeComposition"

fun filterAllAfterTypes(appModel: NewItemViewModel, selectedId: Int, clear: Boolean = false) {
    appModel.filterWithOneParent(
        appModel.investigationReasonsMutable,
        appModel.investigationReasons,
        -1
    )
    appModel.filterWithOneParent(
        appModel.customersMutable,
        appModel.customers,
        0
    )
    appModel.filterWithOneParent(
        appModel.teamMembersMutable,
        appModel.teamMembers,
        0
    )
    appModel.selectSingleRecord(appModel.investigationTypesMutable, selectedId)

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
    val inputList by appModel.investigationTypesMediator.observeAsState()
    val listState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    var selectedIndex by remember {
        mutableStateOf(0)
    }

    inputList?.apply {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.height(60.dp)
        ) {
            items(first!!.size) { item ->
                Log.d(TAG, "TypesSelection: ${appModel.currentOrder.value?.orderTypeId}")
                if(first!![item].id == appModel.currentOrder.value?.orderTypeId)
                    selectedIndex = item
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
        OnLifecycleEvent { owner, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.d(TAG, "TypesSelection: $event / index = $selectedIndex")
                        coroutineScope.launch {
                            // Animate scroll to the 10th item
                            delay(500)
                            listState.animateScrollToItem(index = selectedIndex)
                        }
                    }
                }
                else -> {
                }
            }
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