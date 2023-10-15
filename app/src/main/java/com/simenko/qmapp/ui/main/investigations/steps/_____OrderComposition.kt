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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
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
import com.simenko.qmapp.ui.common.StatusWithPercentage
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
import kotlin.math.roundToInt

private const val TAG = "OrderComposition"

@Composable
fun Orders(
    modifier: Modifier = Modifier,
    invModel: InvestigationsViewModel = hiltViewModel()
) {
    val scrollToRecord by invModel.scrollToRecord.collectAsStateWithLifecycle()
    val items by invModel.orders.collectAsStateWithLifecycle()

    val onClickDetailsLambda = remember<(Int) -> Unit> { { invModel.setOrdersVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(Int) -> Unit> { { invModel.setOrdersVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(Int) -> Unit> { { invModel.deleteOrder(it) } }
    val onClickEditLambda = remember<(Int) -> Unit> { { invModel.onEditInvClick(it) } }

    val listState = rememberLazyListState()
    LaunchedEffect(scrollToRecord) {
        scrollToRecord?.let { record ->
            record.first.getContentIfNotHandled()?.let { orderId ->
                invModel.channel.trySend(
                    this.launch { listState.scrollToSelectedItem(list = items.map { it.order.id }.toList(), selectedId = orderId) }
                )
            }
        }
    }
    val lastItemIsVisible by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1 } }
    LaunchedEffect(lastItemIsVisible) {
        if (lastItemIsVisible) invModel.mainPageHandler?.onListEnd?.invoke(true) else invModel.mainPageHandler?.onListEnd?.invoke(false)
    }

    val lastVisibleItemKey by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.key } }
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) lastVisibleItemKey?.let { invModel.setLastVisibleItemKey(it) }
    }

    LazyColumn(modifier = modifier, state = listState) {
        items(items = items, key = { it.order.id }) { order ->
            OrderCard(
                order = order,
                invModel = invModel,
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
    invModel: InvestigationsViewModel = hiltViewModel(),
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
                .pointerInput(order.order.id) { detectTapGestures(onDoubleTap = { onClickActions(order.order.id) }) }
        ) {
            Order(
                modifier = modifier,
                invModel = invModel,
                order = order,
                onClickDetails = { onClickDetails(it) }
            )
        }
    }
}

@Composable
fun Order(
    modifier: Modifier = Modifier,
    invModel: InvestigationsViewModel = hiltViewModel(),
    order: DomainOrderComplete,
    onClickDetails: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
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
                    modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 4.dp),
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
                        text = order.order.orderNumber.toString(),
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.15f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = "Status:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.13f)
                            .padding(top = 5.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                            .weight(weight = 0.61f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusWithPercentage(
                            status = Pair(order.order.statusId, order.orderStatus.statusDescription),
                            result = Triple(order.orderResult.isOk, order.orderResult.total, order.orderResult.good)
                        )
                    }
                }
                TopLevelSingleRecordHeader(
                    title = "Type/reason:",
                    value = StringUtils.concatTwoStrings(order.orderType.typeDescription ?: NoString.str, order.orderReason.reasonFormalDescript ?: NoString.str)
                )
                TopLevelSingleRecordHeader("Customer:", order.customer.depAbbr ?: NoString.str)
            }
            IconButton(
                onClick = { onClickDetails(order.order.id) },
                modifier = Modifier
                    .weight(weight = 0.10f)
                    .padding(0.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (order.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (order.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more),
                    modifier = Modifier.padding(0.dp)
                )
            }
        }

        OrderDetails(
            modifier = modifier,
            invModel = invModel,
            orderId = order.order.id,
            detailsVisibility = order.detailsVisibility,
            placerFullName = order.orderPlacer.fullName,
            createdDate = order.order.createdDate,
            completedDate = order.order.completedDate
        )
    }
}

@Composable
fun OrderDetails(
    modifier: Modifier = Modifier,
    invModel: InvestigationsViewModel = hiltViewModel(),
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
        SubOrdersFlowColumn(modifier = Modifier, invModel = invModel, parentId = orderId)
    }
}