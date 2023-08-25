package com.simenko.qmapp.ui.main.investigations.steps

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.DomainSubOrderComplete
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.dialogs.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivityForResult
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.*

private const val TAG = "SubOrdersStandAlone"

@Composable
fun SubOrdersStandAlone(
    modifier: Modifier = Modifier,
    onListEnd: (FabPosition) -> Unit
) {
    val context = LocalContext.current
    val appModel: InvestigationsViewModel = hiltViewModel()

    val parentOrderTypeId by appModel.showSubOrderWithOrderType.observeAsState()
    val createdRecord by appModel.createdRecord.collectAsStateWithLifecycle(CreatedRecord())
    val items by appModel.subOrdersSF.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(Int) -> Unit> {
        {
            appModel.setCurrentSubOrderVisibility(dId = SelectedNumber(it))
        }
    }

    val onClickActionsLambda = remember<(Int) -> Unit> {
        {
            appModel.setCurrentSubOrderVisibility(aId = SelectedNumber(it))
        }
    }

    val onClickDeleteLambda = remember<(Int) -> Unit> {
        {
            appModel.deleteSubOrder(it)
        }
    }

    val onClickEditLambda = remember<(Int, Int) -> Unit> {
        { orderId, subOrderId ->
            launchNewItemActivityForResult(
                context as MainActivity,
                ActionType.EDIT_SUB_ORDER_STAND_ALONE.ordinal,
                orderId,
                subOrderId
            )
        }
    }

    val onClickStatusLambda = remember<(DomainSubOrderComplete, Int?) -> Unit> {
        { subOrderComplete, completedById ->
            appModel.showStatusUpdateDialog(
                currentSubOrder = subOrderComplete,
                performerId = completedById
            )
        }
    }

    val listState = rememberLazyListState()

    val needScrollToItem by remember {
        derivedStateOf {
            createdRecord.subOrderId != NoRecord.num
        }
    }

    if (needScrollToItem) {
        val coroutineScope = rememberCoroutineScope()
        SideEffect {
            coroutineScope.launch {
                listState.scrollToSelectedItem(
                    list = items.map { it.subOrder.id }.toList(),
                    selectedId = createdRecord.orderId
                )

                delay(200)

                val subOrder = items.find {
                    it.subOrder.id == createdRecord.subOrderId
                }

                if (subOrder != null && !subOrder.detailsVisibility) {
                    onClickDetailsLambda(subOrder.subOrder.id)
                    appModel.resetCreatedSubOrderId()
                }
            }
        }
    }

    val lastItemIsVisible by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    if (lastItemIsVisible) onListEnd(FabPosition.Center) else onListEnd(FabPosition.End)

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(items = items, key = { it.subOrder.id }) { subOrder ->
            Log.d(TAG, "SubOrdersStandAlone: ${subOrder.orderShort.order.orderNumber}")
            SubOrderCard(
                modifier = modifier,
                parentOrderTypeId = parentOrderTypeId ?: NoRecord,
                subOrder = subOrder,
                onClickDetails = { it ->
                    onClickDetailsLambda(it)
                },
                cardOffset = CARD_OFFSET.dp(),
                onClickActions = {
                    onClickActionsLambda(it)
                },
                onClickDelete = { it -> onClickDeleteLambda(it) },
                onClickEdit = { orderId, subOrderId -> onClickEditLambda(orderId, subOrderId) },
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