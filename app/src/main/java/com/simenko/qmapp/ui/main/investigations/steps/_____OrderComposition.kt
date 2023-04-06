package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivityForResult
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.*
import kotlin.math.round
import kotlin.math.roundToInt

private const val TAG = "OrderComposition"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Orders(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel,
    onListEnd: (FabPosition) -> Unit,
    createdRecord: CreatedRecord? = null,
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
) {
    val context = LocalContext.current

    val observeOrders by appModel.orders.observeAsState()
    val showCurrentStatus by appModel.showWithStatus.observeAsState()
    val showOrderNumber by appModel.showOrderNumber.observeAsState()

    val items = appModel.ordersS
    if (observeOrders != null && showCurrentStatus != null && showOrderNumber != null)
        appModel.addOrdersToSnapShot(observeOrders!!, showCurrentStatus!!, showOrderNumber!!)

    val onClickDetailsLambda = remember<(Int) -> Unit> {
        {
            appModel.changeOrdersDetailsVisibility(it)
        }
    }

    val onChangeExpandStateLambda = remember<(Int) -> Unit> {
        {
            appModel.changeCompleteOrdersExpandState(it)
        }
    }

    val listState = rememberLazyListState()

    val needScrollToItem by remember {
        derivedStateOf {
            observeOrders != null && createdRecord != null
        }
    }

    if (needScrollToItem) {
        val coroutineScope = rememberCoroutineScope()
        SideEffect {
            coroutineScope.launch {
                listState.scrollToSelectedItem(
                    list = observeOrders!!.map { it.order.id }.toList(),
                    selectedId = createdRecord!!.orderId
                )

                delay(200)

                val order = observeOrders!!.find {
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

    var clickCounter = 0

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(items = items, key = {it.hashCode()}) { order ->
            Box(Modifier.fillMaxWidth()) {
                ActionsRow(
                    order = order,
                    actionIconSize = ACTION_ITEM_SIZE.dp,
                    onDeleteOrder = {
                        appModel.deleteOrder(it)
                    },
                    onEdit = {
                        launchNewItemActivityForResult(
                            context as MainActivity,
                            ActionType.EDIT_ORDER.ordinal,
                            order.order.id
                        )
                    }
                )
                OrderCard(
                    viewModel = appModel,
                    order = order,
                    onClickDetails = {
                        onClickDetailsLambda(it)
                    },
                    modifier = modifier,
                    cardOffset = CARD_OFFSET.dp(),
                    onChangeExpandState = {
                        clickCounter++
                        if (clickCounter == 1) {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(200)
                                clickCounter = 0
                            }
                        }
                        if (clickCounter == 2) {
                            clickCounter = 0
                            onChangeExpandStateLambda(it.order.id)
                        }
                    },
                    showStatusDialog = showStatusDialog
                )
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun OrderCard(
    viewModel: QualityManagementViewModel,
    order: DomainOrderComplete,
    onClickDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    cardOffset: Float,
    onChangeExpandState: (DomainOrderComplete) -> Unit,
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
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

    val cardBgColor by transition.animateColor(
        label = "cardBgColorTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = {
            if (order.isExpanded) Accent200 else
                if (order.detailsVisibility) {
                    _level_1_record_color_details
                } else {
                    _level_1_record_color
                }
        }
    )

    val cardElevation by transition.animateDp(
        label = "cardElevation",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (order.isExpanded) 40.dp else 2.dp }
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor,
        ),
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetTransition.roundToInt(), 0) }
            .clickable { onChangeExpandState(order) },
        elevation = CardDefaults.cardElevation(cardElevation),
    ) {
        Order(
            modifier = modifier,
            viewModel = viewModel,

            orderId = order.order.id,

            orderNumber = order.order.orderNumber.toString(),
            statusId = order.order.statusId,
            statusDescription = order.orderStatus.statusDescription?:"-",
            isOk = order.orderResult.isOk?:true,
            total = order.orderResult.total,
            good = order.orderResult.good,
            typeDescription = order.orderType.typeDescription?:"-",
            reasonFormalDescript = order.orderReason.reasonFormalDescript?:"-",
            customerDepAbbr = order.customer.depAbbr?:"-",

            detailsVisibility = order.detailsVisibility,
            placerFullName = order.orderPlacer.fullName,
            createdDate = order.order.createdDate,
            completedDate = order.order.completedDate,

            onClickDetails = { onClickDetails(order.order.id) },
            showStatusDialog = showStatusDialog
        )
    }
}

@Composable
fun Order(
    modifier: Modifier = Modifier,
    viewModel: QualityManagementViewModel? = null,

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

    onClickDetails: () -> Unit = {},
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
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
            viewModel = viewModel,
            modifier = modifier,
            orderId = orderId,
            detailsVisibility = detailsVisibility,
            placerFullName = placerFullName,
            createdDate = createdDate,
            completedDate = completedDate,
            showStatusDialog = showStatusDialog
        )
    }
}

@Composable
fun OrderDetails(
    modifier: Modifier = Modifier,
    viewModel: QualityManagementViewModel? = null,
    orderId: Int = 0,
    detailsVisibility: Boolean = false,
    placerFullName: String = "",
    createdDate: String = "",
    completedDate: String? = "",
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
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
        if (viewModel != null)
            SubOrdersFlowColumn(
                modifier = Modifier,
                parentId = orderId,
                appModel = viewModel,
                showStatusDialog = showStatusDialog
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