package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.common.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivityForResult
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.*
import kotlin.math.round
import kotlin.math.roundToInt

private const val TAG = "OrderComposition"

@Composable
fun Orders(
    modifier: Modifier = Modifier,
    appModel: InvestigationsViewModel,
    onListEnd: (FabPosition) -> Unit,
    createdRecord: CreatedRecord? = null,
) {
    val context = LocalContext.current

    val items by appModel.ordersSF.collectAsState(initial = listOf())

    val onClickDetailsLambda = remember<(Int) -> Unit> {
        {
            appModel.setOrderDetailsVisibility(it)
        }
    }

    val onClickActionsLambda = remember<(Int) -> Unit> {
        {
            appModel.setOrderActionsVisibility(it)
        }
    }

    val onClickDeleteLambda = remember<(Int) -> Unit> {
        {
            appModel.deleteOrder(it)
        }
    }

    val onClickEditLambda = remember<(Int) -> Unit> {
        {
            launchNewItemActivityForResult(
                context as MainActivity,
                ActionType.EDIT_ORDER.ordinal,
                it
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
                    list = items.map { it.order.id }.toList(),
                    selectedId = createdRecord!!.orderId
                )

                delay(200)

                val order = items.find {
                    it.order.id == createdRecord.orderId
                }

                if (order != null && !order.detailsVisibility) {
                    onClickDetailsLambda(order.order.id)
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
        items(items = items, key = { it.order.id }) { order ->

            OrderCard(
                order = order,
                onClickDetails = {
                    onClickDetailsLambda(it)
                },
                modifier = modifier,
                cardOffset = CARD_OFFSET.dp(),
                onClickActions = {
                    onClickActionsLambda(it.order.id)
                },
                onClickDelete = {
                    onClickDeleteLambda(it)
                },
                onClickEdit = {
                    onClickEditLambda(it)
                }
            )
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    order: DomainOrderComplete,
    onClickDetails: (Int) -> Unit,
    cardOffset: Float,
    onClickActions: (DomainOrderComplete) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickEdit: (Int) -> Unit
) {
    Log.d(TAG, "OrderCard: ${order.order.orderNumber}")
    val transitionState = remember {
        MutableTransitionState(order.isExpanded).apply {
            targetState = !order.isExpanded
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (order.isExpanded) cardOffset else 0f },
    )

    val cardBgColor =
        when (order.isExpanded) {
            true -> Accent200
            false -> {
                when (order.detailsVisibility) {
                    true -> _level_1_record_color_details
                    else -> _level_1_record_color
                }
            }
        }


    Box(Modifier.fillMaxWidth()) {
        Log.d(TAG, "OrderCard: Action row for ${order.order.orderNumber}")
        Row(Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = {
                    onClickDelete(order.order.id)
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
                    onClickEdit(order.order.id)
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
                        onDoubleTap = { onClickActions(order) }
                    )
                },
            elevation = CardDefaults.cardElevation(
                when (order.isExpanded) {
                    true -> 40.dp
                    false -> 0.dp
                }
            ),
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
                reasonFormalDescript = order.orderReason.reasonFormalDescript ?: "-",
                customerDepAbbr = order.customer.depAbbr ?: "-",

                detailsVisibility = order.detailsVisibility,
                placerFullName = order.orderPlacer.fullName,
                createdDate = order.order.createdDate.toString(),
                completedDate = order.order.completedDate,

                onClickDetails = { onClickDetails(order.order.id) }
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
    reasonFormalDescript: String = "",
    customerDepAbbr: String = "",

    detailsVisibility: Boolean = false,
    placerFullName: String = "",
    createdDate: String = "2022-12-15T22:24:43.666",
    completedDate: String? = "2022-12-15T22:24:43.666",
    onClickDetails: () -> Unit = {}
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
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp
                        ),
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
                        text = "Type/reason:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.22f)
                            .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = StringUtils.concatTwoStrings(typeDescription, reasonFormalDescript),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.78f)
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
                        text = "Customer:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.22f)
                            .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = customerDepAbbr,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.78f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
            }
            IconButton(
                onClick = onClickDetails,
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
    orderId: Int = 0,
    detailsVisibility: Boolean = false,
    placerFullName: String = "",
    createdDate: String = "",
    completedDate: String? = ""
) {

    if (detailsVisibility) {

        Divider(modifier = modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)

        Row(
            modifier = modifier.padding(start = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Ordered by:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.35f)
            )
            Text(
                text = placerFullName,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.65f)
                    .padding(start = 3.dp)
            )
        }
        Row(
            modifier = modifier.padding(start = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Order date:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.35f)
            )
            Text(
                text = StringUtils.getDateTime(createdDate),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.65f)
                    .padding(start = 3.dp)
            )
        }
        Row(
            modifier = modifier.padding(start = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Completion date:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.35f)
            )
            Text(
                text = StringUtils.getDateTime(completedDate),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.65f)
                    .padding(start = 3.dp)
            )
        }

        SubOrdersFlowColumn(
            modifier = Modifier,
            parentId = orderId
        )
    }
}

@Preview(name = "Light Mode Order", showBackground = true, widthDp = 409)
@Composable
fun MyOrderPreview() {
    QMAppTheme {
//        Order(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 1.5.dp)
//        )
    }
}

fun getOrders() = List(30) { i ->

    DomainOrderComplete(
        order = getOrder(i),
        orderType = getType(),
        orderReason = getReason(),
        customer = DomainDepartment(
            1,
            "ГШСК№1",
            "Група шліфувально-складальних ліній",
            1,
            "Manufacturing",
            1,
            1
        ),
        orderPlacer = DomainTeamMember(
            id = 1,
            departmentId = 1,
            department = "ГШСК№1",
            email = "roman.semenyshyn@skf.com",
            fullName = "Роман Семенишин",
            jobRole = "Quality Manager",
            roleLevelId = 1,
            passWord = "13050513",
            companyId = 1,
            detailsVisibility = false
        ),
        orderStatus = DomainOrdersStatus(1, "In Progress"),
        detailsVisibility = true,
        subOrdersVisibility = false,
        orderResult = getOrderResult()
    )
}

fun getOrder(i: Int) = DomainOrder(
    id = i,
    1,
    1,
    orderNumber = (100..300).random(),
    1,
    1,
    1,
    "2022-12-15T22:24:43",
    "2022-12-15T22:24:43"
)

fun getType() = DomainOrdersType(
    1,
    "Incoming Inspection"
)

fun getReason() = DomainReason(
    1,
    "Налагоджульник",
    "FLI",
    1
)

fun getOrderResult() = DomainOrderResult(
    id = 0,
    isOk = true,
    good = 10,
    total = 10
)