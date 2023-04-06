package com.simenko.qmapp.ui.main.team


import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.DomainOrderComplete
import com.simenko.qmapp.ui.common.ANIMATION_DURATION
import com.simenko.qmapp.ui.common.CARD_OFFSET
import com.simenko.qmapp.ui.common.DialogFor
import com.simenko.qmapp.ui.main.CreatedRecord
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.theme.Accent200
import com.simenko.qmapp.ui.theme._level_1_record_color
import com.simenko.qmapp.ui.theme._level_1_record_color_details
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val TAG = "TeamComposition"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersComposition(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel,
    onListEnd: (FabPosition) -> Unit,
    createdRecord: CreatedRecord? = null,
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
) {
    Log.d(TAG, "TeamMembersLiveData: Parent is build!")

    val itemsVM by appModel.orders.observeAsState()
    if (itemsVM != null)
        appModel.addOrdersToSnapShot(itemsVM!!)

    val items = appModel.ordersS

    val onClickDetailsLambda: (Int) -> Unit = {
        appModel.changeOrdersDetailsVisibility(it)
    }
    val onChangeExpandStateLambda = remember<(Int) -> Unit> {
        {
            appModel.changeCompleteOrdersExpandState(it)
        }
    }
    val listState = rememberLazyListState()

    var clickCounter = 0

    LazyColumn(
        state = listState,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        items(items = items, key = { it.order.id }
        ) { order ->
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
            orderDescription = "${order.order.orderNumber?:"-"} /" +
                    " ${order.orderType.typeDescription?:"-"} /" +
                    " ${order.orderReason.reasonDescription} /" +
                    " ${order.customer.depAbbr}",
            status = order.orderStatus.statusDescription,
            orderedBy = order.orderPlacer.fullName?:"-",
            orderedDate = order.order.createdDate,
            detailsVisibility = order.detailsVisibility,
            onClickDetails = {
                onClickDetails(order.order.id)
            }
        )
    }
}

private const val columnOneWeight = 0.25f
private const val columnSecondWeight = 0.75f

@Composable
fun Order(
    orderDescription: String,
    status: String?,
    orderedBy: String,
    orderedDate: String,
    detailsVisibility: Boolean,
    onClickDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Log.d(TAG, "Order: $orderDescription")

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = orderDescription,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )

            IconButton(onClick = onClickDetails) {
                Icon(
                    imageVector = if (detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (detailsVisibility) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    }
                )
            }
        }

        if (detailsVisibility) {

            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status:",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnOneWeight)
                        .padding(start = 8.dp)
                )
                Text(
                    text = StringUtils.getMail(status),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnSecondWeight)
                        .padding(start = 16.dp)
                )
            }

            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ordered by:",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnOneWeight)
                        .padding(start = 8.dp)
                )
                Text(
                    text = orderedBy,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnSecondWeight)
                        .padding(start = 16.dp)
                )
            }

            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ordered date:",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnOneWeight)
                        .padding(start = 8.dp, bottom = 16.dp)
                )
                Text(
                    text = orderedDate,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnSecondWeight)
                        .padding(start = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}