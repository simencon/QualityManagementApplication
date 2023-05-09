package com.simenko.qmapp.ui.main.investigations.steps

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.common.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivityForResult
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubOrdersStandAlone(
    modifier: Modifier = Modifier,
    onListEnd: (FabPosition) -> Unit,
    createdRecord: CreatedRecord? = null,
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
) {
    val context = LocalContext.current
    val appModel = (context as MainActivity).investigationsModel

    val items = appModel.subOrders

    appModel.addSubOrdersToSnapShot(-1, "0")

    val onClickDetailsLambda = remember<(DomainSubOrderComplete) -> Unit> {
        {
            appModel.changeSubOrderDetailsVisibility(it.subOrder.id)
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


    var clickCounter = 0

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(items = items, key = { it.subOrder.id }) { subOrder ->
            if (subOrder.orderShort.order.orderTypeId == 3) // means to show only Process Control
                Box(Modifier.fillMaxWidth()) {
                    ActionsRow(
                        subOrder = subOrder,
                        actionIconSize = ACTION_ITEM_SIZE.dp,
                        onDeleteSubOrder = {
                            appModel.deleteSubOrder(it)
                        },
                        onEdit = {
                            launchNewItemActivityForResult(
                                context as MainActivity,
                                ActionType.EDIT_SUB_ORDER_STAND_ALONE.ordinal,
                                subOrder.subOrder.orderId,
                                subOrder.subOrder.id
                            )
                        }
                    )

                    SubOrderCard(
                        modifier = modifier,
                        appModel = appModel,
                        subOrder = subOrder,
                        onClickDetails = { it ->
                            onClickDetailsLambda(it)
                        },
                        cardOffset = CARD_OFFSET.dp(),
                        onChangeExpandState = {
                            clickCounter++
                            if (clickCounter == 1) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(200)
                                    clickCounter--
                                }
                            }
                            if (clickCounter == 2) {
                                clickCounter = 0
                                appModel.changeCompleteSubOrdersExpandState(it.subOrder.id)
                            }
                        }
                    )
                }
        }
    }
}