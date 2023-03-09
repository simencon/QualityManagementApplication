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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simenko.qmapp.domain.DomainItemVersionComplete
import com.simenko.qmapp.domain.DomainManufacturingLine
import com.simenko.qmapp.ui.common.scrollToSelectedItem
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400
import kotlinx.coroutines.launch

private const val TAG = "InputInvestigationTypeComposition"

private fun getProductVersionInput(model: NewItemViewModel): LiveData<List<DomainItemVersionComplete>> {
    val list: MutableList<DomainItemVersionComplete> = mutableListOf()

    model.itemVersionsCompleteP.value?.let { list.addAll(it) }
    model.itemVersionsCompleteC.value?.let { list.addAll(it) }
    model.itemVersionsCompleteS.value?.let { list.addAll(it) }

    return MutableLiveData(list.toList())
}

fun filterAllAfterLines(appModel: NewItemViewModel, selectedId: Int, clear: Boolean = false) {

    appModel.itemVersionsCompleteMutable.performFiltration(
        s = getProductVersionInput(appModel),
        action = FilteringMode.ADD_BY_PARENT_ID_FROM_META_TABLE,
        trigger = appModel.pairedTrigger,
        p1Id = selectedId,
        m = appModel.inputForOrder,
        step = FilteringStep.ITEM_VERSIONS
    )
    appModel.operationsMutable.performFiltration(
        action = FilteringMode.REMOVE_ALL,
        trigger = appModel.pairedTrigger
    )
    appModel.characteristicsMutable.performFiltration(
        action = FilteringMode.REMOVE_ALL,
        trigger = appModel.pairedTrigger
    )
    selectSingleRecord(appModel.linesMutable, appModel.pairedTrigger, selectedId)

    if (clear) {
        appModel.currentSubOrder.value?.itemPreffix = ""
        appModel.currentSubOrder.value?.itemTypeId = 0
        appModel.currentSubOrder.value?.itemVersionId = 0
        appModel.currentSubOrder.value?.operationId = 0
        appModel.currentSubOrder.value?.samplesCount = 0
    }
}

@Composable
fun LinesSelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel
) {
    val observeInputForOrder by appModel.linesMediator.observeAsState()
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
                LineCard(
                    input = first!![item],
                    modifier = modifier,
                    onClick = {
                        appModel.currentSubOrder.value?.lineId = it.id
                        filterAllAfterLines(appModel, it.id, true)
                    }
                )
            }
        }

        if (first != null && appModel.currentSubOrder.value != null)
            coroutineScope.launch {
                gritState.scrollToSelectedItem(
                    list = first!!.map { it.id }.toList(),
                    selectedId = appModel.currentSubOrder.value!!.lineId,
                )
            }
    }
}

@Composable
fun LineCard(
    input: DomainManufacturingLine,
    modifier: Modifier = Modifier,
    onClick: (DomainManufacturingLine) -> Unit
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
                .width(224.dp)
                .height(56.dp),
            onClick = { onClick(input) }
        ) {
            Text(
                text = input.lineAbbr ?: "-"
            )
        }
    }
}