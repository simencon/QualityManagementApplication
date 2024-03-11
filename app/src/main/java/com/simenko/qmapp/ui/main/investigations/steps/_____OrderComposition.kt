package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Constants.ACTION_ITEM_SIZE
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.StatusWithPercentage
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.dialogs.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.StringUtils.getStringDate
import com.simenko.qmapp.utils.dp
import com.simenko.qmapp.utils.observeAsState
import kotlinx.coroutines.*
import kotlin.math.roundToInt

@Composable
fun Orders(
    modifier: Modifier = Modifier,
    viewModel: InvestigationsViewModel = hiltViewModel()
) {
    val scrollToRecord by viewModel.scrollToRecord.collectAsStateWithLifecycle(null)
    val items by viewModel.orders.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setOrdersVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setOrdersVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.deleteOrder(it) } }
    val onClickEditLambda = remember<(ID) -> Unit> { { viewModel.onEditInvClick(it) } }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()

    LaunchedEffect(lifecycleState.value) {
        when(lifecycleState.value) {
            Lifecycle.Event.ON_RESUME -> viewModel.setIsComposed(true)
            Lifecycle.Event.ON_STOP -> viewModel.setIsComposed(false)
            else -> {}
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(scrollToRecord) {
        scrollToRecord?.let { record ->
            record.first.getContentIfNotHandled()?.let { orderId ->
                viewModel.channel.trySend(this.launch { listState.scrollToSelectedItem(list = items.map { it.order.id }.toList(), selectedId = orderId) })
            }
        }
    }
    val lastItemIsVisible by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1 } }
    LaunchedEffect(lastItemIsVisible) {
        if (lastItemIsVisible) viewModel.mainPageHandler?.onListEnd?.invoke(true) else viewModel.mainPageHandler?.onListEnd?.invoke(false)
    }

    val lastVisibleItemKey by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.key } }
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) lastVisibleItemKey?.let { viewModel.setLastVisibleItemKey(it) }
    }

    LazyColumn(modifier = modifier, state = listState) {
        items(items = items, key = { it.order.id }) { order ->
            OrderCard(
                invModel = viewModel,
                order = order,
                onClickDetails = { onClickDetailsLambda(it) },
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
    invModel: InvestigationsViewModel = hiltViewModel(),
    order: DomainOrderComplete = DomainOrderComplete(),
    onClickDetails: (ID) -> Unit,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (ID) -> Unit
) {
    val transitionState = remember { MutableTransitionState(order.isExpanded).apply { targetState = !order.isExpanded } }
    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { (if (order.isExpanded) CARD_OFFSET * 2 else 0f).dp() },
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
        Row(Modifier.padding(all = (DEFAULT_SPACE / 2).dp)) {
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
            modifier = Modifier
                .padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp)
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(order.order.id) { detectTapGestures(onDoubleTap = { onClickActions(order.order.id) }) }
        ) {
            Order(
                invModel = invModel,
                order = order,
                onClickDetails = { onClickDetails(it) }
            )
        }
    }
}

@Composable
fun Order(
    invModel: InvestigationsViewModel = hiltViewModel(),
    order: DomainOrderComplete,
    onClickDetails: (Long) -> Unit = {}
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.90f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HeaderWithTitle(modifier = Modifier.weight(0.26f), titleWight = 0.42f, title = "Num.:", text = order.order.orderNumber.toString())
                    HeaderWithTitle(
                        modifier = Modifier
                            .padding(start = DEFAULT_SPACE.dp)
                            .weight(0.74f),
                        titleWight = 0.18f,
                        title = "Status:"
                    ) {
                        StatusWithPercentage(
                            status = Pair(order.order.statusId, order.orderStatus.statusDescription),
                            result = Triple(order.orderResult.isOk, order.orderResult.total, order.orderResult.good)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(
                    title = "Type/reason:",
                    value = StringUtils.concatTwoStrings(order.orderType.typeDescription ?: NoString.str, order.orderReason.reasonFormalDescript ?: NoString.str),
                    titleWight = 0.22f
                )
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(title = "Customer:", value = order.customer.depAbbr ?: NoString.str, titleWight = 0.22f)
            }
            IconButton(
                onClick = { onClickDetails(order.order.id) },
                modifier = Modifier.weight(weight = 0.10f)
            ) {
                Icon(
                    imageVector = if (order.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (order.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        OrderDetails(
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
    invModel: InvestigationsViewModel = hiltViewModel(),
    orderId: Long = NoRecord.num,
    detailsVisibility: Boolean = false,
    placerFullName: String = "",
    createdDate: Long = NoRecord.num.toLong(),
    completedDate: Long? = null
) {
    if (detailsVisibility) {
        Column(modifier = Modifier.padding(all = DEFAULT_SPACE.dp)) {
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Initiated by:", value = placerFullName, titleWight = 0.35f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Initiation date:", value = getStringDate(createdDate), titleWight = 0.35f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Completion date:", value = getStringDate(completedDate), titleWight = 0.35f)
            Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
        }
        SubOrders(invModel = invModel, parentId = orderId)
    }
}