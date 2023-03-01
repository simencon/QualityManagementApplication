package com.simenko.qmapp.ui.main.investigations.orders

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.common.ACTION_ITEM_SIZE
import com.simenko.qmapp.ui.main.common.ANIMATION_DURATION
import com.simenko.qmapp.ui.main.common.ActionsRow
import com.simenko.qmapp.ui.main.common.CARD_OFFSET
import com.simenko.qmapp.ui.neworder.NewItemType
import com.simenko.qmapp.ui.neworder.launchNewItemActivity
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
        orderReason = DomainMeasurementReason(1, "Налагоджульник", "FLI", 1),
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
        subOrdersVisibility = false
    )
}

private const val TAG = "OrderComposition"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestigationsAll(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel,
    context: Context
) {
    QMAppTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(end = 29.dp),
                    onClick = {
                        launchNewItemActivity(context, NewItemType.NEW_INVESTIGATION)
                    },
                    content = {
                        androidx.compose.material.Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            content = { padding ->
                OrdersLiveData(
                    Modifier
                        .fillMaxSize()
                        .padding(vertical = 2.dp, horizontal = 4.dp), appModel
                )
            }
        )


    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrdersLiveData(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel
) {
    val context = LocalContext.current

    val observerLoadingProcess by appModel.isLoadingInProgress.observeAsState()
    val observerIsNetworkError by appModel.isNetworkError.observeAsState()

    val listState = rememberLazyListState()
    val observeOrders by appModel.completeOrdersMediator.observeAsState()
    var clickCounter = 0

    val pullRefreshState = rememberPullRefreshState(
        observerLoadingProcess!!,
        { appModel.refreshOrdersFromRepository() })

    Box(Modifier.pullRefresh(pullRefreshState)) {
        observeOrders?.apply {
            if (observeOrders!!.first != null) {
                LazyColumn(
                    modifier = modifier,
                    state = listState
                ) {
                    items(items = observeOrders!!.first!!) { order ->
                        Box(Modifier.fillMaxWidth()) {
                            ActionsRow(
                                order = order,
                                actionIconSize = ACTION_ITEM_SIZE.dp,
                                onDelete = {
                                    appModel.deleteOrder(it)
                                },
                                onEdit = {},
                                onFavorite = {}
                            )

                            OrderCard(
                                viewModel = appModel,
                                order = order,
                                onClickDetails = { it ->
                                    appModel.changeCompleteOrdersDetailsVisibility(it)
                                },
                                modifier = modifier,
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
                                        appModel.changeCompleteOrdersExpandState(it)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        PullRefreshIndicator(
            observerLoadingProcess!!,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = ProgressIndicatorDefaults.circularColor
        )
    }

    if (observerIsNetworkError == true) {
        Toast.makeText(context, "Network error!", Toast.LENGTH_SHORT).show()
        appModel.onNetworkErrorShown()
    }

}

fun LazyListState.isScrolledToTheEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun OrderCard(
    viewModel: QualityManagementViewModel,
    order: DomainOrderComplete,
    onClickDetails: (DomainOrderComplete) -> Unit,
    modifier: Modifier = Modifier,
    cardOffset: Float,
    onChangeExpandState: (DomainOrderComplete) -> Unit,
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
            if (order.isExpanded) Accent200 else _level_1_record_color
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
//                ToDo - think how to make swiping more natural
//            .pointerInput(Unit) {
//                detectHorizontalDragGestures { _, dragAmount ->
//                    onChangeExpandState(order)
//                }
//            }
            .clickable { onChangeExpandState(order) },
        elevation = CardDefaults.cardElevation(cardElevation),
    ) {
        Order(
            viewModel = viewModel,
            modifier = modifier,
            order = order,
            onClickDetails = { onClickDetails(order) }
        )
    }
}

@Composable
fun Order(
    viewModel: QualityManagementViewModel? = null,
    modifier: Modifier = Modifier,
    order: DomainOrderComplete = getOrders()[0],
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
                    Text(
                        text = order.orderStatus.statusDescription ?: "",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.61f)
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

        OrderDetails(viewModel = viewModel, modifier = modifier, order = order)
    }
}

@Composable
fun OrderDetails(
    viewModel: QualityManagementViewModel? = null,
    modifier: Modifier = Modifier,
    order: DomainOrderComplete = getOrders()[0],
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
        SubOrdersFlowColumn(
            parentId = order.order.id,
            appModel = viewModel!!,
            modifier = Modifier
        )
    }
}

@Preview(name = "Light Mode Order", showBackground = true, widthDp = 409)
@Composable
fun MyOrderPreview() {
    QMAppTheme {
        Order(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.5.dp)
        )
    }
}