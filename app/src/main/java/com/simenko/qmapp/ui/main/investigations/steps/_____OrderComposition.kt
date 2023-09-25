package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Constants.ACTION_ITEM_SIZE
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.other.Constants.CARDS_PADDING
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.common.TopLevelSingleRecordDetails
import com.simenko.qmapp.ui.common.TopLevelSingleRecordHeader
import com.simenko.qmapp.ui.dialogs.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.StringUtils.getMillisecondsDate
import com.simenko.qmapp.utils.StringUtils.getStringDate
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.*
import kotlin.math.round
import kotlin.math.roundToInt

private const val TAG = "OrderComposition"

@Composable
fun Orders(
    modifier: Modifier = Modifier
) {
    val invModel: InvestigationsViewModel = hiltViewModel()
    Log.d(TAG, "InvestigationsViewModel: $invModel")

    val createdRecord by invModel.createdRecord.collectAsStateWithLifecycle(CreatedRecord())

    val items by invModel.ordersSF.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(Int) -> Unit> { { invModel.setCurrentOrderVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(Int) -> Unit> { { invModel.setCurrentOrderVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(Int) -> Unit> { { invModel.deleteOrder(it) } }
    val onClickEditLambda = remember<(Int) -> Unit> { { invModel.onEditInvClick(it) } }

    val listState = rememberLazyListState()

    val needScrollToItem by remember {
        derivedStateOf {
            createdRecord.orderId != NoRecord.num
        }
    }

    if (needScrollToItem) {
        val coroutineScope = rememberCoroutineScope()
        SideEffect {
            coroutineScope.launch {

                listState.scrollToSelectedItem(
                    list = items.map { it.order.id }.toList(),
                    selectedId = createdRecord.orderId
                )

                delay(25)

                val order = items.find {
                    it.order.id == createdRecord.orderId
                }

                if (order != null) {
                    if (!order.detailsVisibility)
                        onClickDetailsLambda(order.order.id)
                    invModel.resetCreatedOrderId()
                }
            }
        }
    }

    val lastVisibleItemKey by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.key
        }
    }

    if (!listState.isScrollInProgress) lastVisibleItemKey?.let { invModel.setLastVisibleItemKey(it) }

    val lastItemIsVisible by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    if (lastItemIsVisible) invModel.onListEnd(true) else invModel.onListEnd(false)

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(items = items, key = { it.order.id }) { order ->
            Log.d(TAG, "OrdersLog: ${order.order.orderNumber}")
            OrderCard(
                order = order,
                onClickDetails = { onClickDetailsLambda(it) },
                modifier = modifier.padding(CARDS_PADDING),
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) }
            )
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    order: DomainOrderComplete = DomainOrderComplete(),
    onClickDetails: (Int) -> Unit,
    onClickActions: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickEdit: (Int) -> Unit
) {
    Log.d(TAG, "OrderCardLog: ${order.order.orderNumber}")
    val transitionState = remember {
        MutableTransitionState(order.isExpanded).apply {
            targetState = !order.isExpanded
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (order.isExpanded) CARD_OFFSET.dp() else 0f },
    )
    val containerColor = when (order.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when (order.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (order.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.surfaceVariant
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(order.order.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )

            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = { onClickEdit(order.order.id) },
                content = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit action") }
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(order.order.id) {
                    detectTapGestures(
                        onDoubleTap = { onClickActions(order.order.id) }
                    )
                }
        ) {
            Order(
                modifier = modifier,

                orderId = order.order.id,

                orderNumber = order.order.orderNumber.toString(),
                statusId = order.order.statusId,
                statusDescription = order.orderStatus.statusDescription ?: "-",
                isOk = order.orderResult.isOk ?: true,
                total = order.orderResult.total,
                good = order.orderResult.good,
                typeDescription = order.orderType.typeDescription ?: "-",
                reasonFormalDescription = order.orderReason.reasonFormalDescript ?: "-",
                customerDepAbbr = order.customer.depAbbr ?: "-",

                detailsVisibility = order.detailsVisibility,
                placerFullName = order.orderPlacer.fullName,
                createdDate = order.order.createdDate,
                completedDate = order.order.completedDate,

                onClickDetails = { onClickDetails(it) }
            )
        }
    }
}

@Composable
fun Order(
    modifier: Modifier = Modifier,

    orderId: Int = 0,

    orderNumber: String = "",
    statusId: Int = 0,
    statusDescription: String = "",
    isOk: Boolean = true,
    total: Int? = 1,
    good: Int? = 1,
    typeDescription: String = "",
    reasonFormalDescription: String = "",
    customerDepAbbr: String = "",

    detailsVisibility: Boolean = false,
    placerFullName: String = "",
    createdDate: Long = getMillisecondsDate("2022-12-15T22:24:43.666+02:00")!!,
    completedDate: Long? = getMillisecondsDate("2022-12-15T22:24:43.666+02:00"),
    onClickDetails: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .padding(top = 0.dp, start = 4.dp, end = 4.dp, bottom = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 0.dp, start = 4.dp, end = 4.dp, bottom = 0.dp)
                    .weight(0.90f),
            ) {
                Row(
                    modifier = Modifier.padding(
                        top = 0.dp,
                        start = 0.dp,
                        end = 0.dp,
                        bottom = 4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Num.:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.11f)
                            .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = orderNumber,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.15f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = "Status:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.13f)
                            .padding(top = 5.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                            .weight(weight = 0.61f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = statusDescription,
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                        )
                        if (statusId == 3) {
                            Text(
                                text = "(",
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                            )
                            Icon(
                                imageVector = if (isOk) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = if (isOk) {
                                    stringResource(R.string.show_less)
                                } else {
                                    stringResource(R.string.show_more)
                                },
                                modifier = Modifier.padding(
                                    top = 0.dp,
                                    start = 0.dp,
                                    end = 0.dp,
                                    bottom = 0.dp
                                ),
                                tint = if (isOk) {
                                    Color.Green
                                } else {
                                    Color.Red
                                },
                            )
                            val conformity = (total?.toFloat()?.let {
                                good?.toFloat()
                                    ?.div(it)
                            }?.times(100)) ?: 0.0f

                            Text(
                                text = when {
                                    !conformity.isNaN() -> {
                                        (round(conformity * 10) / 10).toString() + "%"
                                    }

                                    else -> {
                                        ""
                                    }
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                            )
                            Text(
                                text = ")",
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                            )
                        }
                    }
                }
                TopLevelSingleRecordHeader("Type/reason:", StringUtils.concatTwoStrings(typeDescription, reasonFormalDescription))
                TopLevelSingleRecordHeader("Customer:", customerDepAbbr)
            }
            IconButton(
                onClick = { onClickDetails(orderId) },
                modifier = Modifier
                    .weight(weight = 0.10f)
                    .padding(0.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (detailsVisibility) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    },
                    modifier = Modifier.padding(0.dp)
                )
            }
        }

        OrderDetails(
            modifier = modifier,
            orderId = orderId,
            detailsVisibility = detailsVisibility,
            placerFullName = placerFullName,
            createdDate = createdDate,
            completedDate = completedDate
        )
    }
}

@Composable
fun OrderDetails(
    modifier: Modifier = Modifier,
    orderId: Int = NoRecord.num,
    detailsVisibility: Boolean = false,
    placerFullName: String = "",
    createdDate: Long = NoRecord.num.toLong(),
    completedDate: Long? = null
) {

    if (detailsVisibility) {
        Divider(modifier = modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        TopLevelSingleRecordDetails("Initiated by:", placerFullName, modifier)
        TopLevelSingleRecordDetails("Initiation date:", getStringDate(createdDate) ?: NoString.str, modifier)
        TopLevelSingleRecordDetails("Completion date:", getStringDate(completedDate) ?: NoString.str, modifier)
        SubOrdersFlowColumn(modifier = Modifier, parentId = orderId)
    }
}