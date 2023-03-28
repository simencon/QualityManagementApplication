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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.simenko.qmapp.ui.neworder.launchNewItemActivity
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

    val observeOrders by appModel.completeOrdersMediator.observeAsState()
    val showCurrentStatus by appModel.showWithStatus.observeAsState()
    val showOrderNumber by appModel.showOrderNumber.observeAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var lookForRecord by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(lookForRecord) {
        if (observeOrders?.first != null && createdRecord != null)
            coroutineScope.launch {

                listState.scrollToSelectedItem(
                    list = observeOrders?.first!!.map { it.order.id }.toList(),
                    selectedId = createdRecord.orderId
                )

                delay(200)

                val order = observeOrders?.first!!.find {
                    it.order.id == createdRecord.orderId
                }

                if (order != null)
                    appModel.changeOrderDetailsVisibility(order.order.id)

            } else if (createdRecord != null && createdRecord.orderId != 0) {
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

    observeOrders?.apply {
        if (observeOrders!!.first != null) {
            LazyColumn(
                modifier = modifier,
                state = listState
            ) {
                items(items = observeOrders!!.first!!) { order ->
                    if(showCurrentStatus!=null && showOrderNumber != null)
                    if(order.order.statusId == showCurrentStatus || showCurrentStatus == 0)
                    if(order.order.orderNumber.toString().contains(showOrderNumber!!) || showOrderNumber == "0")
                    Box(Modifier.fillMaxWidth()) {
                        ActionsRow(
                            order = order,
                            actionIconSize = ACTION_ITEM_SIZE.dp,
                            onDeleteOrder = {
                                appModel.deleteOrder(it)
                            },
                            onEdit = {
                                launchNewItemActivity(
                                    context,
                                    ActionType.EDIT_ORDER,
                                    order.order.id
                                )
                            }
                        )

                        OrderCard(
                            viewModel = appModel,
                            order = order,
                            onClickDetails = { it ->
                                appModel.changeOrderDetailsVisibility(it.order.id)
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
                                    appModel.changeCompleteOrdersExpandState(it)
                                }
                            },
                            context = context,
                            createdRecord = createdRecord,
                            showStatusDialog = showStatusDialog
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun OrderCard(
    viewModel: QualityManagementViewModel,
    order: DomainOrderComplete,
    onClickDetails: (DomainOrderComplete) -> Unit,
    modifier: Modifier = Modifier,
    cardOffset: Float,
    onChangeExpandState: (DomainOrderComplete) -> Unit,
    context: Context,
    createdRecord: CreatedRecord? = null,
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
) {
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
            order = order,
            onClickDetails = { onClickDetails(order) },
            context = context,
            createdRecord = createdRecord,
            showStatusDialog = showStatusDialog
        )
    }
}

@Composable
fun Order(
    modifier: Modifier = Modifier,
    viewModel: QualityManagementViewModel? = null,
    order: DomainOrderComplete = getOrders()[0],
    onClickDetails: () -> Unit = {},
    context: Context,
    createdRecord: CreatedRecord? = null,
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
                            text = order.orderStatus.statusDescription ?: "",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                        )
                        if(order.order.statusId == 3) {
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
                                imageVector = if (order.orderResult.isOk != false) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = if (order.orderResult.isOk != false) {
                                    stringResource(R.string.show_less)
                                } else {
                                    stringResource(R.string.show_more)
                                },
                                modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                                tint = if (order.orderResult.isOk != false) {
                                    Color.Green
                                } else {
                                    Color.Red
                                },
                            )
                            val conformity = (order.orderResult.total?.toFloat()?.let {
                                order.orderResult.good?.toFloat()
                                    ?.div(it)
                            }?.times(100))?:0.0f

                            Text(
                                text = when {
                                    !conformity.isNaN() -> {(round(conformity * 10)/10).toString() + "%"}
                                    else -> {""}
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
                        text = StringUtils.concatTwoStrings(
                            order.orderType.typeDescription,
                            order.orderReason.reasonFormalDescript
                        ),
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
                        text = order.customer.depAbbr ?: "",
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
                onClick = onClickDetails, modifier = Modifier
                    .weight(weight = 0.10f)
                    .padding(0.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (order.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (order.detailsVisibility) {
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
            order = order,
            context = context,
            createdRecord = createdRecord,
            showStatusDialog = showStatusDialog
        )
    }
}

@Composable
fun OrderDetails(
    modifier: Modifier = Modifier,
    viewModel: QualityManagementViewModel? = null,
    order: DomainOrderComplete = getOrders()[0],
    context: Context,
    createdRecord: CreatedRecord? = null,
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
) {

    if (order.detailsVisibility) {

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
                text = order.orderPlacer.fullName,
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
                text = StringUtils.getDateTime(order.order.createdDate),
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
                text = StringUtils.getDateTime(order.order.completedDate),
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
                parentId = order.order.id,
                appModel = viewModel,
                context = context,
                createdRecord = createdRecord,
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
        order = DomainOrder(
            id = i,
            1,
            1,
            orderNumber = (100..300).random(),
            1,
            1,
            1,
            "2022-12-15T22:24:43",
            "2022-12-15T22:24:43"
        ),
        orderType = DomainOrdersType(1, "Incoming Inspection"),
        orderReason = DomainReason(1, "Налагоджульник", "FLI", 1),
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

fun getOrderResult() = DomainOrderResult(
    id = 0,
    isOk = true,
    good = 10,
    total = 10
)