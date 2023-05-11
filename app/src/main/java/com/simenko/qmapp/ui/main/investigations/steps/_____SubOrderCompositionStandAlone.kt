package com.simenko.qmapp.ui.main.investigations.steps

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.common.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivityForResult
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.*

@Composable
fun SubOrdersStandAlone(
    modifier: Modifier = Modifier,
    onListEnd: (FabPosition) -> Unit,
    createdRecord: CreatedRecord? = null,
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
) {
    val context = LocalContext.current
    val appModel = (context as MainActivity).investigationsModel

    val items by appModel.subOrdersSF.collectAsState(listOf())

    val onClickDetailsLambda = remember<(DomainSubOrderComplete) -> Unit> {
        {
            appModel.setSubOrderDetailsVisibility(it.subOrder.id)
        }
    }

    val onClickActionsLambda = remember<(DomainSubOrderComplete) -> Unit> {
        {
            appModel.setSubOrderActionsVisibility(it.subOrder.id)
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

    val listState = rememberLazyListState()

    val needScrollToItem by remember {
        derivedStateOf {
            createdRecord != null
        }
    }

    if (needScrollToItem) {
        val coroutineScope = rememberCoroutineScope()
        SideEffect {
            coroutineScope.launch {
                listState.scrollToSelectedItem(
                    list = items.map { it.subOrder.id }.toList(),
                    selectedId = createdRecord!!.orderId
                )

                delay(200)

                val subOrder = items.find {
                    it.subOrder.id == createdRecord.subOrderId
                }

                if (subOrder != null && !subOrder.detailsVisibility) {
                    onClickDetailsLambda(subOrder)
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
            if (subOrder.orderShort.order.orderTypeId == 3) // means to show only Process Control

                SubOrderCard(
                    modifier = modifier,
                    appModel = appModel,
                    subOrder = subOrder,
                    onClickDetails = { it ->
                        onClickDetailsLambda(it)
                    },
                    cardOffset = CARD_OFFSET.dp(),
                    onClickActions = {
                        onClickActionsLambda(it)
                    },
                    onClickDelete = { it -> onClickDeleteLambda(it) },
                    onClickEdit = { orderId, subOrderId -> onClickEditLambda(orderId, subOrderId) }
                )
        }
    }
}