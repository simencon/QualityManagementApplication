package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.utils.StringUtils
import com.google.accompanist.flowlayout.FlowRow
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Constants.ACTION_ITEM_SIZE
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.dialogs.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivityForResult
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
    val appModel = (context as MainActivity).investigationsModel

    val parentOrderTypeId by appModel.showSubOrderWithOrderType.observeAsState()
    val createdRecord by appModel.createdRecord.collectAsStateWithLifecycle(CreatedRecord())

    val items by appModel.subOrdersSF.collectAsStateWithLifecycle(listOf())

    val coroutineScope = rememberCoroutineScope()

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
                ActionType.EDIT_SUB_ORDER.ordinal,
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

    LaunchedEffect(createdRecord) {
        if (createdRecord.subOrderId != NoRecord.num)
            coroutineScope.launch {
                delay(200)
                val subOrder = items.find {
                    it.subOrder.id == createdRecord.subOrderId
                }
                if (subOrder != null) {
                    onClickDetailsLambda(subOrder.subOrder.id)
                    appModel.resetCreatedSubOrderId()
                }
            }
    }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow(modifier = modifier) {
            items.forEach { subOrder ->
                if (subOrder.subOrder.orderId == parentId) {

                    SubOrderCard(
                        modifier = modifier,
                        parentOrderTypeId = parentOrderTypeId ?: NoRecord,
                        subOrder = subOrder,
                        onClickDetails = { it ->
                            onClickDetailsLambda(it)
                        },
                        cardOffset = CARD_OFFSET.dp(),
                        onClickActions = { it ->
                            onClickActionsLambda(it)
                        },
                        onClickDelete = { it -> onClickDeleteLambda(it) },
                        onClickEdit = { orderId, subOrderId ->
                            onClickEditLambda(
                                orderId,
                                subOrderId
                            )
                        },
                        onClickStatus = { subOrderComplete, completedById ->
                            onClickStatusLambda(
                                subOrderComplete,
                                completedById
                            )
                        }
                    )

                    Divider(thickness = 4.dp, color = Color.Transparent)
                }
            }
        }
        Divider(modifier = modifier.height(0.dp))
        FloatingActionButton(
            containerColor = _level_2_record_color,
            modifier = Modifier.padding(vertical = 4.dp),
            onClick = {
                launchNewItemActivityForResult(
                    context as MainActivity,
                    ActionType.ADD_SUB_ORDER.ordinal,
                    parentId
                )
            },
            content = {
                androidx.compose.material.Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Primary900
                )
            }
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SubOrderCard(
    modifier: Modifier = Modifier,
    parentOrderTypeId: SelectedNumber,
    subOrder: DomainSubOrderComplete,
    onClickDetails: (Int) -> Unit,
    cardOffset: Float,
    onClickActions: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickEdit: (Int, Int) -> Unit,
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

    val cardBgColor =
        when (subOrder.isExpanded) {
            true -> Accent200
            false -> {
                when (subOrder.detailsVisibility) {
                    true -> _level_2_record_color_details
                    else -> _level_2_record_color
                }
            }
        }

    Box(Modifier.fillMaxWidth()) {

        Row(Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = {
                    onClickDelete(subOrder.subOrder.id)
                },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        tint = PrimaryVariant900,
                        contentDescription = "delete action",
                    )
                }
            )

            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = {
                    onClickEdit(subOrder.subOrder.orderId, subOrder.subOrder.id)
                },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        tint = PrimaryVariant900,
                        contentDescription = "edit action",
                    )
                },
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = cardBgColor,
            ),
            modifier = modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { onClickActions(subOrder.subOrder.id) }
                    )
                },
            elevation = CardDefaults.cardElevation(
                when (subOrder.isExpanded) {
                    true -> 40.dp
                    false -> 0.dp
                }
            ),
        ) {
            SubOrder(
                modifier = modifier,
                parentOrderTypeId = parentOrderTypeId,
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
    parentOrderTypeId: SelectedNumber,
    subOrder: DomainSubOrderComplete = DomainSubOrderComplete(),
    onClickDetails: (Int) -> Unit = {},
    onClickStatus: (DomainSubOrderComplete, Int?) -> Unit,
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
                                text = when (parentOrderTypeId == OrderTypeProcessOnly) {
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
                        if (parentOrderTypeId == OrderTypeProcessOnly)
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = _level_2_record_color,
                            contentColor = Primary900
                        )
                    )
                }
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
                        text = "Process:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.20f)
                            .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = StringUtils.concatFourStrings(
                            subOrder.department.depAbbr,
                            subOrder.subDepartment.subDepAbbr,
                            subOrder.channel.channelAbbr,
                            subOrder.line.lineAbbr
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.80f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
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
                        text = "Product:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.20f)
                            .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = StringUtils.concatTwoStrings1(
                            StringUtils.concatTwoStrings3(
                                subOrder.itemVersionComplete.itemComplete.key.componentKey,
                                subOrder.itemVersionComplete.itemComplete.item.itemDesignation
                            ), subOrder.itemVersionComplete.itemVersion.versionDescription
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.80f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
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
                        text = "Operation:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.20f)
                            .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = StringUtils.concatTwoStrings2(
                            subOrder.operation.operationAbbr,
                            subOrder.operation.operationDesignation
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.80f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
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
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ordered by:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.22f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = subOrder.orderedBy.fullName,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.78f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Created:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.22f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = getStringDate(subOrder.subOrder.createdDate)?: NoString.str,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.78f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Completed by:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.22f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = subOrder.completedBy?.fullName ?: "-",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.78f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Completed:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.22f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = getStringDate(subOrder.subOrder.completedDate)?: NoString.str,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.78f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }

        SubOrderTasksFlowColumn(
            modifier = Modifier,
            parentId = subOrder.subOrder.id
        )
    }
}

/*@Preview(name = "Light Mode SubOrder", showBackground = true, widthDp = 409)
@Composable
fun MySubOrderPreview() {
    QMAppTheme {
        SubOrder(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp, horizontal = 0.dp),
            onClickStatus = {}
        )
    }
}*/