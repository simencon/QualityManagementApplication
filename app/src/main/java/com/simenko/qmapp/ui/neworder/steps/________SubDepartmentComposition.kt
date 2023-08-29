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
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.ui.theme.Primary
import com.simenko.qmapp.ui.theme.TertiaryContainer
import kotlinx.coroutines.launch

private const val TAG = "InputInvestigationTypeComposition"

fun filterAllAfterSubDepartments(appModel: NewItemViewModel, selectedId: Int, clear: Boolean = false) {
//    appModel.subOrderPlacersMutable.performFiltration(
//        s = appModel.teamMembers,
//        action = FilteringMode.ADD_BY_PARENT_ID,
//        trigger = appModel.pairedTrigger,
//        p1Id = appModel.currentSubOrder.value?.subOrder?.departmentId?:NoRecord.num
//    )
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
    selectSingleRecord(appModel.subDepartmentsMutable, appModel.pairedTrigger, selectedId)

    if (clear) {
        appModel.currentSubOrder.value?.subOrder?.orderedById = NoRecord.num
        appModel.currentSubOrder.value?.subOrder?.channelId = NoRecord.num
        appModel.currentSubOrder.value?.subOrder?.lineId = NoRecord.num
        appModel.currentSubOrder.value?.subOrder?.itemPreffix = NoString.str
        appModel.currentSubOrder.value?.subOrder?.itemTypeId = NoRecord.num
        appModel.currentSubOrder.value?.subOrder?.itemVersionId = NoRecord.num
        appModel.currentSubOrder.value?.subOrder?.operationId = NoRecord.num
        appModel.currentSubOrder.value?.subOrder?.samplesCount = ZeroValue.num
        appModel.currentSubOrder.value?.samples?.removeIf { it.isNewRecord }
        appModel.currentSubOrder.value?.samples?.forEach {it.toBeDeleted = true}
        appModel.currentSubOrder.value?.subOrderTasks?.removeIf { it.isNewRecord }
        appModel.currentSubOrder.value?.subOrderTasks?.forEach {it.toBeDeleted = true}
    }
}

@Composable
fun SubDepartmentsSelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel
) {
    val observeInputForOrder by appModel.subDepartmentsMediator.observeAsState()
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
                SubDepartmentCard(
                    input = first!![item],
                    modifier = modifier,
                    onClick = {
                        appModel.currentSubOrder.value?.subOrder?.subDepartmentId = it.id
                        filterAllAfterSubDepartments(appModel, it.id, true)
                    }
                )
            }
        }

        if (first != null && appModel.currentSubOrder.value != null)
            coroutineScope.launch {
                gritState.scrollToSelectedItem(
                    list = first!!.map { it.id }.toList(),
                    selectedId = appModel.currentSubOrder.value?.subOrder!!.subDepartmentId,
                )
            }
    }
}

@Composable
fun SubDepartmentCard(
    input: DomainSubDepartment,
    modifier: Modifier = Modifier,
    onClick: (DomainSubDepartment) -> Unit
) {
    val btnBackgroundColor = if (input.isSelected) Primary else TertiaryContainer
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
                text = input.subDepDesignation ?: "-",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}