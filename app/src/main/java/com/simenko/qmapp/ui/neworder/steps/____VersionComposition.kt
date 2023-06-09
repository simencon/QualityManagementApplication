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
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.DomainItemVersionComplete
import com.simenko.qmapp.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings1
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings3
import kotlinx.coroutines.launch

private const val TAG = "InputInvestigationTypeComposition"

fun filterAllAfterVersions(appModel: NewItemViewModel, selectedId: Any, clear: Boolean = false) {

    appModel.operationsMutable.performFiltration(
        s = appModel.operations,
        action = FilteringMode.ADD_BY_PARENT_ID_FROM_META_TABLE,
        trigger = appModel.pairedTrigger,
        p1Id = appModel.currentSubOrder.value?.subOrder?.lineId ?: NoRecord.num,
        p2Id = appModel.currentSubOrder.value?.subOrder?.itemPreffix ?: NoString.str,
        m = appModel.inputForOrder,
        step = FilteringStep.OPERATIONS
    )

    appModel.characteristicsMutable.performFiltration(
        action = FilteringMode.REMOVE_ALL,
        trigger = appModel.pairedTrigger
    )
    selectSingleRecord(appModel.itemVersionsCompleteMutable, appModel.pairedTrigger, selectedId)

    if (clear) {
        appModel.currentSubOrder.value?.subOrder?.operationId = NoRecord.num
        appModel.currentSubOrder.value?.subOrder?.samplesCount = ZeroValue.num
        appModel.currentSubOrder.value?.samples?.removeIf { it.isNewRecord }
        appModel.currentSubOrder.value?.samples?.forEach { it.toBeDeleted = true }
        appModel.currentSubOrder.value?.subOrderTasks?.removeIf { it.isNewRecord }
        appModel.currentSubOrder.value?.subOrderTasks?.forEach { it.toBeDeleted = true }
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
                        appModel.currentSubOrder.value?.subOrder?.itemPreffix = it.itemVersion.fId
                        appModel.currentSubOrder.value?.subOrder?.itemTypeId = it.itemComplete.item.id
                        appModel.currentSubOrder.value?.subOrder?.itemVersionId = it.itemVersion.id
                        filterAllAfterVersions(
                            appModel,
                            it.itemVersion.fId,
                            true
                        )
                    }
                )
            }
        }

        if (first != null && appModel.currentSubOrder.value != null)
            coroutineScope.launch {
                gritState.scrollToSelectedItem(
                    list = first!!.map { it.itemVersion.id }.toList(),
                    selectedId = appModel.currentSubOrder.value?.subOrder!!.itemVersionId, //to scroll is enough only versionId
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
            onClick = { onClick(input) },

            ) {
            Text(
                text = concatTwoStrings1(
                    concatTwoStrings3(
                        input.itemComplete.key.componentKey,
                        input.itemComplete.item.itemDesignation
                    ), input.itemVersion.versionDescription
                ),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}