package com.simenko.qmapp.ui.neworder.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainCharacteristic
import com.simenko.qmapp.domain.entities.DomainSubOrderTask
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.utils.InvStatuses

fun filterAllAfterCharacteristics(
    appModel: NewItemViewModel,
    selectedId: Int = NoRecord.num,
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
                appModel.currentSubOrder.value?.subOrderTasks?.find {
                    it.charId == selectedId && it.subOrderId == (subOrderId ?: NoRecord.num)
                }.let {
                    if (it == null)
                        appModel.currentSubOrder.value?.subOrderTasks?.add(
                            DomainSubOrderTask().copy(
                                statusId = InvStatuses.TO_DO.statusId,
                                subOrderId = subOrderId ?: NoRecord.num,
                                charId = selectedId,
                                isNewRecord = true
                            )
                        )
                    else
                        it.toBeDeleted = false
                }
            }

            false -> {
                val index = appModel.currentSubOrder.value?.subOrderTasks?.indexOfFirst {
                    it.charId == selectedId
                }
                if (index != null) {
                    if (appModel.currentSubOrder.value?.subOrderTasks?.get(index)!!.isNewRecord)
                        appModel.currentSubOrder.value?.subOrderTasks?.removeAt(index)
                    else
                        appModel.currentSubOrder.value?.subOrderTasks?.get(index)!!.toBeDeleted = true
                }
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
    modifier: Modifier = Modifier
) {
    val viewModel: NewItemViewModel = hiltViewModel()
    val gritState = rememberLazyGridState()

    val items by viewModel.subOrderCharacteristics.collectAsStateWithLifecycle()

    val onSelectLambda = remember<(Int) -> Unit> { { viewModel.selectSubOrderCharacteristic(it) } }

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        state = gritState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.height(60.dp)
    ) {
        items(items = items, key = { it.id }) { item ->
            CharacteristicCard(input = item, onClick = { onSelectLambda(it) })
        }
    }
}

@Composable
fun CharacteristicCard(
    input: DomainCharacteristic,
    onClick: (Int) -> Unit
) {
    val btnColors = ButtonDefaults.buttonColors(
        contentColor = if (input.isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        containerColor = if (input.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            colors = btnColors,
            elevation = ButtonDefaults.buttonElevation(4.dp, 4.dp, 4.dp, 4.dp, 4.dp),
            modifier = Modifier
                .width(168.dp)
                .height(56.dp),
            onClick = { onClick(input.id) }
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