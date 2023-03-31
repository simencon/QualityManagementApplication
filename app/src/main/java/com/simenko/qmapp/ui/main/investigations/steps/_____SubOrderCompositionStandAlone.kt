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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.common.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivityForResult
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubOrdersStandAlone(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel,
    onListEnd: (FabPosition) -> Unit,
    createdRecord: CreatedRecord? = null,
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
) {
    val context = LocalContext.current

    val observeOrders by appModel.completeOrdersMediator.observeAsState() //have to start to observe here for further work with completeOrders while saving new subOrder status.
    val observeSubOrders by appModel.completeSubOrdersMediator.observeAsState()
    val showCurrentStatus by appModel.showWithStatus.observeAsState()
    val showOrderNumber by appModel.showOrderNumber.observeAsState()

    val onClickDetailsLambda = remember<(DomainSubOrderComplete) -> Unit> {
        {
            appModel.changeSubOrderDetailsVisibility(it.subOrder.id)
        }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var lookForRecord by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(lookForRecord) {
        if (observeSubOrders?.first != null && createdRecord != null)
            coroutineScope.launch {

                listState.scrollToSelectedItem(
                    list = observeSubOrders?.first!!.map { it.subOrder.id }.toList(),
                    selectedId = createdRecord.orderId
                )

                delay(200)

                val subOrder = observeSubOrders?.first!!.find {
                    it.subOrder.id == createdRecord.subOrderId
                }

                if (subOrder != null)
                    onClickDetailsLambda(subOrder)

            } else if (createdRecord != null && createdRecord.subOrderId != 0) {
            delay(50)
            lookForRecord = !lookForRecord
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main) {
            for (i in generateSequence(0) { it }) {
                checkIfEndOfList(listState, onListEnd)
                delay(50)
            }
        }
    }

    var clickCounter = 0

    observeSubOrders?.apply {
        if (observeSubOrders!!.first != null) {
            LazyColumn(
                modifier = modifier,
                state = listState
            ) {
                items(items = observeSubOrders!!.first!!) { subOrder ->
                    if (showCurrentStatus != null && showOrderNumber != null)
                        if (subOrder.subOrder.statusId == showCurrentStatus || showCurrentStatus == 0)
                            if (subOrder.orderShort.order.orderNumber.toString()
                                    .contains(showOrderNumber!!) || showOrderNumber == "0"
                            )
                                if (subOrder.orderShort.order.orderTypeId == 3)
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
                                            viewModel = appModel,
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
                                                    appModel.changeCompleteSubOrdersExpandState(it)
                                                }
                                            },
                                            showStatusDialog = showStatusDialog
                                        )
                                    }
                }
            }
        }
    }
}