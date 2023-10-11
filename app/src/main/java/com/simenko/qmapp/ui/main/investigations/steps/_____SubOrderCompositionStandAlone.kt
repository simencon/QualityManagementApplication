package com.simenko.qmapp.ui.main.investigations.steps

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.DomainSubOrderComplete
import com.simenko.qmapp.other.Constants.CARDS_PADDING
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.dialogs.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.utils.BaseFilter
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.*

@Composable
fun SubOrdersStandAlone(
    modifier: Modifier = Modifier,
    invModel: InvestigationsViewModel = hiltViewModel()
) {
    val scrollToRecord by invModel.scrollToRecord.collectAsStateWithLifecycle()
    val items by invModel.subOrdersSF.collectAsStateWithLifecycle(listOf())

    LaunchedEffect(Unit) {
        invModel.setSubOrdersFilter(BaseFilter(typeId = ProcessControlOrderTypeId.num))
    }

    val onClickDetailsLambda = remember<(Int) -> Unit> { { invModel.setCurrentSubOrderVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(Int) -> Unit> { { invModel.setCurrentSubOrderVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(Int) -> Unit> { { invModel.deleteSubOrder(it) } }
    val onClickEditLambda = remember<(Pair<Int, Int>) -> Unit> { { invModel.onEditProcessControlClick(it) } }

    val onClickStatusLambda = remember<(DomainSubOrderComplete, Int?) -> Unit> {
        { subOrderComplete, completedById ->
            invModel.showStatusUpdateDialog(
                currentSubOrder = subOrderComplete,
                performerId = completedById
            )
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(scrollToRecord) {
        scrollToRecord?.let { record ->
            record.second.getContentIfNotHandled()?.let { subOrderId ->
                listState.scrollToSelectedItem(list = items.map { it.subOrder.id }.toList(), selectedId = subOrderId)

                delay(200)

                val subOrder = items.find { it.subOrder.id == subOrderId }
                if (subOrder != null && !subOrder.detailsVisibility) onClickDetailsLambda(subOrder.subOrder.id)
            }
        }
    }

    val lastItemIsVisible by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1 } }
    LaunchedEffect(lastItemIsVisible) {
        if (lastItemIsVisible) invModel.mainPageHandler.onListEnd(true) else invModel.mainPageHandler.onListEnd(false)
    }

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(items = items, key = { it.subOrder.id }) { subOrder ->
            SubOrderCard(
                modifier = modifier.padding(CARDS_PADDING),
                processControlOnly = true,
                subOrder = subOrder,
                onClickDetails = { onClickDetailsLambda(it) },
                cardOffset = CARD_OFFSET.dp(),
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) },
                onClickStatus = { subOrderComplete, completedById ->
                    onClickStatusLambda(
                        subOrderComplete,
                        completedById
                    )
                }
            )
        }
    }
}