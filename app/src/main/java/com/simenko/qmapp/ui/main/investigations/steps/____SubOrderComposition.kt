package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import com.simenko.qmapp.utils.StringUtils
import com.google.accompanist.flowlayout.FlowRow
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Constants.ACTION_ITEM_SIZE
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.common.SecondLevelSingleRecordDetails
import com.simenko.qmapp.ui.common.SecondLevelSingleRecordHeader
import com.simenko.qmapp.ui.dialogs.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils.getStringDate
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val TAG = "SubOrderComposition"

@Composable
fun SubOrdersFlowColumn(
    modifier: Modifier = Modifier,
    parentId: Int = 0
) {
    val context = LocalContext.current
    val invModel: InvestigationsViewModel = hiltViewModel()
    Log.d(TAG, "InvestigationsViewModel: $invModel")

    val createdRecord by invModel.createdRecord.collectAsStateWithLifecycle(CreatedRecord())

    val items by invModel.subOrdersSF.collectAsStateWithLifecycle(listOf())

    val coroutineScope = rememberCoroutineScope()

    val onClickDetailsLambda = remember<(Int) -> Unit> { { invModel.setCurrentSubOrderVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(Int) -> Unit> { { invModel.setCurrentSubOrderVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(Int) -> Unit> { { invModel.deleteSubOrder(it) } }
    val onClickAddLambda = remember<(Int) -> Unit> {
        {
            invModel.setAddEditMode(AddEditMode.ADD_SUB_ORDER)
            invModel.navController.navigate(Screen.Main.SubOrderAddEdit.withArgs(it.toString(), NoRecordStr.str, FalseStr.str))
        }
    }
    val onClickEditLambda = remember<(Pair<Int, Int>) -> Unit> {
        {
            invModel.setAddEditMode(AddEditMode.EDIT_SUB_ORDER)
            invModel.navController.navigate(Screen.Main.SubOrderAddEdit.withArgs(it.first.toString(), it.second.toString(), FalseStr.str))

            invModel.navController.currentBackStackEntry.let { bs ->
                println("bsArguments/investigationsKey = ${bs?.arguments?.getBoolean(ToProcessControlScreen.str)}")
            }
        }
    }

    val onClickStatusLambda = remember<(DomainSubOrderComplete, Int?) -> Unit> {
        { subOrderComplete, completedById ->
            invModel.showStatusUpdateDialog(
                currentSubOrder = subOrderComplete,
                performerId = completedById
            )
        }
    }

    LaunchedEffect(createdRecord) {
        if (createdRecord.subOrderId != NoRecord.num)
            coroutineScope.launch {
                delay(200)
                val subOrder = items.find {
                    it.subOrder.id == createdRecord.subOrderId
                }
                if (subOrder != null) {
                    onClickDetailsLambda(subOrder.subOrder.id)
                    invModel.resetCreatedSubOrderId()
                }
            }
    }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow(modifier = modifier) {
            items.forEach { subOrder ->
                if (subOrder.subOrder.orderId == parentId) {

                    SubOrderCard(
                        modifier = modifier,
                        subOrder = subOrder,
                        onClickDetails = { onClickDetailsLambda(it) },
                        cardOffset = CARD_OFFSET.dp(),
                        onClickActions = { onClickActionsLambda(it) },
                        onClickDelete = { onClickDeleteLambda(it) },
                        onClickEdit = { onClickEditLambda(it) },
                        onClickStatus = { subOrderComplete, completedById -> onClickStatusLambda(subOrderComplete, completedById) }
                    )

                    Divider(thickness = 4.dp, color = Color.Transparent)
                }
            }
        }
        Divider(modifier = modifier.height(0.dp))
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(vertical = 4.dp),
            onClick = { onClickAddLambda(parentId) },
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SubOrderCard(
    modifier: Modifier = Modifier,
    processControlOnly: Boolean = false,
    subOrder: DomainSubOrderComplete,
    onClickDetails: (Int) -> Unit,
    cardOffset: Float,
    onClickActions: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickEdit: (Pair<Int, Int>) -> Unit,
    onClickStatus: (DomainSubOrderComplete, Int?) -> Unit
) {
    Log.d(TAG, "SubOrderCard: ${subOrder.orderShort.order.orderNumber}")

    val transitionState = remember {
        MutableTransitionState(subOrder.isExpanded).apply {
            targetState = !subOrder.isExpanded
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (subOrder.isExpanded) cardOffset else 0f },
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

        Row(Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
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
            modifier = modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(subOrder.subOrder.id) {
                    detectTapGestures(
                        onDoubleTap = { onClickActions(subOrder.subOrder.id) }
                    )
                }
        ) {
            SubOrder(
                modifier = modifier,
                processControlOnly = processControlOnly,
                subOrder = subOrder,
                onClickDetails = { onClickDetails(it) },
                onClickStatus = { subOrderComplete, completedById ->
                    onClickStatus(
                        subOrderComplete,
                        completedById
                    )
                }
            )
        }
    }
}

@Composable
fun SubOrder(
    modifier: Modifier = Modifier,
    processControlOnly: Boolean,
    subOrder: DomainSubOrderComplete = DomainSubOrderComplete(),
    onClickDetails: (Int) -> Unit = {},
    onClickStatus: (DomainSubOrderComplete, Int?) -> Unit,
) {
    val containerColor = when (subOrder.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.primaryContainer
    }

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
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                            .weight(0.54f),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Num.:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(weight = 0.2f)
                                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                            )
                            Text(
                                text = when (processControlOnly) {
                                    false -> subOrder.subOrder.subOrderNumber.toString()
                                    else -> subOrder.orderShort.order.orderNumber.toString()
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(weight = 0.3f)
                                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                            )
                            Text(
                                text = "Quantity:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(weight = 0.33f)
                                    .padding(top = 5.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                            )
                            Text(
                                text = subOrder.subOrder.samplesCount.toString(),
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(weight = 0.17f)
                                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                            )
                        }
                        if (processControlOnly)
                            Row(
                                modifier = Modifier
                                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Reason:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(weight = 0.27f)
                                        .padding(
                                            top = 5.dp,
                                            start = 0.dp,
                                            end = 0.dp,
                                            bottom = 0.dp
                                        )
                                )
                                Text(
                                    text = subOrder.orderShort.orderReason.reasonFormalDescript
                                        ?: "",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(weight = 0.73f)
                                        .padding(
                                            top = 0.dp,
                                            start = 3.dp,
                                            end = 0.dp,
                                            bottom = 0.dp
                                        )
                                )
                            }

                    }

                    TextButton(
                        modifier = Modifier
                            .weight(weight = 0.46f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp),
                        onClick = {
                            onClickStatus(
                                subOrder,
                                subOrder.subOrder.completedById
                            )
                        },
                        content = {
                            Text(
                                text = subOrder.status.statusDescription ?: "-",
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                            )
                            if (subOrder.subOrder.statusId == 3) {
                                Text(
                                    text = "(",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(
                                            top = 0.dp,
                                            start = 3.dp,
                                            end = 0.dp,
                                            bottom = 0.dp
                                        )
                                )
                                Icon(
                                    imageVector = if (subOrder.subOrderResult.isOk != false) Icons.Filled.Check else Icons.Filled.Close,
                                    contentDescription = if (subOrder.subOrderResult.isOk != false) {
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
                                    tint = if (subOrder.subOrderResult.isOk != false) {
                                        Color.Green
                                    } else {
                                        Color.Red
                                    },
                                )
                                val conformity = (subOrder.subOrderResult.total?.toFloat()?.let {
                                    subOrder.subOrderResult.good?.toFloat()
                                        ?.div(it)
                                }?.times(100)) ?: 0.0f

                                Text(
                                    text = when {
                                        !conformity.isNaN() -> {
                                            conformity.roundToInt().toString() + "%"
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
                                        .padding(
                                            top = 0.dp,
                                            start = 3.dp,
                                            end = 0.dp,
                                            bottom = 0.dp
                                        )
                                )
                                Text(
                                    text = ")",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(
                                            top = 0.dp,
                                            start = 3.dp,
                                            end = 0.dp,
                                            bottom = 0.dp
                                        )
                                )
                            }
                        },
                        enabled = true,
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        border = null,
                        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColorFor(containerColor))
                    )
                }
                SecondLevelSingleRecordHeader(
                    "Process:",
                    StringUtils.concatFourStrings(
                        subOrder.department.depAbbr,
                        subOrder.subDepartment.subDepAbbr,
                        subOrder.channel.channelAbbr,
                        subOrder.line.lineAbbr
                    )
                )
                SecondLevelSingleRecordHeader(
                    "Product:",
                    StringUtils.concatTwoStrings1(
                        StringUtils.concatTwoStrings3(
                            subOrder.itemVersionComplete.itemComplete.key.componentKey,
                            subOrder.itemVersionComplete.itemComplete.item.itemDesignation
                        ), subOrder.itemVersionComplete.itemVersion.versionDescription
                    )
                )
                SecondLevelSingleRecordHeader(
                    "Operation:",
                    StringUtils.concatTwoStrings2(
                        subOrder.operation.operationAbbr,
                        subOrder.operation.operationDesignation
                    )
                )
            }
            IconButton(
                onClick = { onClickDetails(subOrder.subOrder.id) },
                modifier = Modifier
                    .weight(weight = 0.10f)
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (subOrder.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (subOrder.detailsVisibility) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    },
                    modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            }
        }

        SubOrderDetails(
            modifier = modifier,
            subOrder = subOrder
        )
    }
}

@Composable
fun SubOrderDetails(
    modifier: Modifier = Modifier,
    subOrder: DomainSubOrderComplete = DomainSubOrderComplete()
) {
    if (subOrder.detailsVisibility) {
        Divider(modifier = modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        SecondLevelSingleRecordDetails("Ordered by:", subOrder.orderedBy.fullName)
        SecondLevelSingleRecordDetails("Created:", getStringDate(subOrder.subOrder.createdDate) ?: NoString.str)
        SecondLevelSingleRecordDetails("Completed by:", subOrder.completedBy?.fullName ?: "-")
        SecondLevelSingleRecordDetails("Completed:", getStringDate(subOrder.subOrder.completedDate) ?: NoString.str)
        SubOrderTasksFlowColumn(modifier = Modifier, parentId = subOrder.subOrder.id)
    }
}