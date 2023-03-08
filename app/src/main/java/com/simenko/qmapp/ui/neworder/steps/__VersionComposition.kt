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
import com.simenko.qmapp.domain.DomainItemVersionComplete
import com.simenko.qmapp.ui.common.scrollToSelectedItem
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400
import com.simenko.qmapp.utils.StringUtils
import kotlinx.coroutines.launch

private const val TAG = "InputInvestigationTypeComposition"

fun filterAllAfterVersions(appModel: NewItemViewModel, selectedId: Any, clear: Boolean = false) {

    selectSingleRecord(appModel.itemVersionsCompleteMutable, appModel.pairedTrigger, selectedId)

    if (clear) {
        appModel.currentSubOrder.value?.operationId = 0
        appModel.currentSubOrder.value?.samplesCount = null
    }
}

@Composable
fun VersionsSelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel
) {
    val observeInputForOrder by appModel.itemVersionsMediator.observeAsState()
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
                VersionCard(
                    input = first!![item],
                    modifier = modifier,
                    onClick = {
                        appModel.currentSubOrder.value?.itemPreffix = it.getItemPrefix()
                        appModel.currentSubOrder.value?.itemTypeId = it.itemComplete.item.id
                        appModel.currentSubOrder.value?.itemVersionId = it.itemVersion.id
                        filterAllAfterVersions(appModel, StringUtils.concatTwoStrings4(it.getItemPrefix(),it.itemVersion.id.toString()), true)
                    }
                )
            }
        }

        if (first != null && appModel.currentSubOrder.value != null)
            coroutineScope.launch {
                gritState.scrollToSelectedItem(
                    list = first!!.map { it.itemVersion.id }.toList(),
                    selectedId = appModel.currentSubOrder.value!!.itemVersionId, //todo must be string
                )
            }
    }
}

@Composable
fun VersionCard(
    input: DomainItemVersionComplete,
    modifier: Modifier = Modifier,
    onClick: (DomainItemVersionComplete) -> Unit
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
                text = StringUtils.concatTwoStrings3(input.itemComplete.key.componentKey, input.itemComplete.item.itemDesignation)
            )
        }
    }
}