package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Constants.ACTION_ITEM_SIZE
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.common.StatusWithPercentage
import com.simenko.qmapp.ui.dialogs.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils.getStringDate
import com.simenko.qmapp.utils.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubOrders(
    invModel: InvestigationsViewModel = hiltViewModel(),
    parentId: ID = NoRecord.num
) {
    val items by invModel.subOrdersSF.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { invModel.setSubOrdersVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { invModel.setSubOrdersVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { invModel.onDeleteSubOrderClick(it) } }
    val onClickAddLambda = remember<(ID) -> Unit> { { invModel.onAddSubOrderClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { invModel.onEditSubOrderClick(it) } }

    val onClickStatusLambda = remember<(DomainSubOrderComplete, ID?) -> Unit> {
        { subOrderComplete, completedById -> invModel.showStatusUpdateDialog(currentSubOrder = subOrderComplete, performerId = completedById) }
    }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow {
            items.forEach { subOrder ->
                if (subOrder.subOrder.orderId == parentId) {
                    SubOrderCard(
                        invModel = invModel,
                        processControlOnly = false,
                        subOrder = subOrder,
                        onClickDetails = { onClickDetailsLambda(it) },
                        onClickActions = { onClickActionsLambda(it) },
                        onClickDelete = { onClickDeleteLambda(it) },
                        onClickEdit = { onClickEditLambda(it) },
                        onClickStatus = { subOrderComplete, completedById -> onClickStatusLambda(subOrderComplete, completedById) }
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.height(0.dp))
        FloatingActionButton(
            modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = { onClickAddLambda(parentId) },
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SubOrderCard(
    invModel: InvestigationsViewModel,
    processControlOnly: Boolean,
    subOrder: DomainSubOrderComplete,
    onClickDetails: (ID) -> Unit,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickStatus: (DomainSubOrderComplete, ID?) -> Unit
) {
    val transitionState = remember { MutableTransitionState(subOrder.isExpanded).apply { targetState = !subOrder.isExpanded } }
    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { (if (subOrder.isExpanded) CARD_OFFSET * 2 else 0f).dp() },
    )

    val containerColor = when (subOrder.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.primaryContainer
    }

    val borderColor = when (subOrder.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (subOrder.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.primaryContainer
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(all = (DEFAULT_SPACE / 2).dp)) {
            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(subOrder.subOrder.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )
            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = { onClickEdit(Pair(subOrder.subOrder.orderId, subOrder.subOrder.id)) },
                content = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit action") },
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .padding(horizontal = DEFAULT_SPACE.dp, vertical = (DEFAULT_SPACE / 2).dp)
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(subOrder.subOrder.id) { detectTapGestures(onDoubleTap = { onClickActions(subOrder.subOrder.id) }) }
        ) {
            SubOrder(
                invModel = invModel,
                processControlOnly = processControlOnly,
                subOrder = subOrder,
                onClickDetails = { onClickDetails(it) },
                onClickStatus = { subOrderComplete, completedById -> onClickStatus(subOrderComplete, completedById) }
            )
        }
    }
}

@Composable
fun SubOrder(
    invModel: InvestigationsViewModel,
    processControlOnly: Boolean,
    subOrder: DomainSubOrderComplete = DomainSubOrderComplete(),
    onClickDetails: (ID) -> Unit = {},
    onClickStatus: (DomainSubOrderComplete, ID?) -> Unit,
) {
    val containerColor = when (subOrder.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.primaryContainer
    }

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.90f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(0.54f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HeaderWithTitle(
                                modifier = Modifier.weight(0.5f),
                                titleWight = 0.40f,
                                title = "Num.:",
                                text = if (processControlOnly) subOrder.orderShort.order.orderNumber.toString() else subOrder.subOrder.subOrderNumber.toString()
                            )
                            HeaderWithTitle(
                                modifier = Modifier
                                    .padding(start = DEFAULT_SPACE.dp)
                                    .weight(0.5f),
                                titleWight = 0.66f,
                                title = "Quantity:",
                                text = subOrder.subOrder.samplesCount.toString()
                            )
                        }
                        if (processControlOnly) {
                            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                            HeaderWithTitle(
                                titleWight = 0.27f,
                                title = "Reason:",
                                text = subOrder.orderShort.orderReason.reasonFormalDescript
                            )
                        }
                    }
                    StatusChangeBtn(modifier = Modifier.weight(weight = 0.46f), containerColor = containerColor, onClick = { onClickStatus(subOrder, subOrder.subOrder.completedById) }) {
                        StatusWithPercentage(
                            status = Pair(subOrder.subOrder.statusId, subOrder.status.statusDescription),
                            result = Triple(subOrder.subOrderResult.isOk, subOrder.subOrderResult.total, subOrder.subOrderResult.good),
                            onlyInt = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(
                    title = "Process:",
                    value = StringUtils.concatFourStrings(
                        subOrder.department.depAbbr,
                        subOrder.subDepartment.subDepAbbr,
                        subOrder.channel.channelAbbr,
                        subOrder.line.lineAbbr
                    ),
                    titleWight = 0.2f
                )
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(
                    title = "Product:",
                    value = StringUtils.concatTwoStrings1(
                        StringUtils.concatTwoStrings3(subOrder.itemVersionComplete.itemComplete.key.componentKey, subOrder.itemVersionComplete.itemComplete.item.itemDesignation),
                        subOrder.itemVersionComplete.itemVersion.versionDescription
                    ),
                    titleWight = 0.2f
                )
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(
                    title = "Operation:",
                    value = StringUtils.concatTwoStrings2(subOrder.operation.operationAbbr, subOrder.operation.operationDesignation),
                    titleWight = 0.2f
                )
            }
            IconButton(onClick = { onClickDetails(subOrder.subOrder.id) }, modifier = Modifier.weight(weight = 0.10f)) {
                Icon(
                    imageVector = if (subOrder.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (subOrder.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        SubOrderDetails(
            invModel = invModel,
            subOrder = subOrder
        )
    }
}

@Composable
fun SubOrderDetails(
    invModel: InvestigationsViewModel,
    subOrder: DomainSubOrderComplete = DomainSubOrderComplete()
) {
    if (subOrder.detailsVisibility) {
        Column(modifier = Modifier.padding(all = DEFAULT_SPACE.dp)) {
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Ordered by:", value = subOrder.orderedBy.fullName, titleWight = 0.22f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Created:", value = getStringDate(subOrder.subOrder.createdDate), titleWight = 0.22f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Completed by:", value = subOrder.completedBy?.fullName ?: NoString.str, titleWight = 0.22f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Completed:", value = getStringDate(subOrder.subOrder.completedDate), titleWight = 0.22f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        }
        SubOrderTasksFlowColumn(invModel = invModel, parentId = subOrder.subOrder.id)
    }
}