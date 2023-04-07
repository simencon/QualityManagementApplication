package com.simenko.qmapp.ui.main.team


import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.DomainOrderComplete
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.main.investigations.steps.SubOrdersFlowColumn
import com.simenko.qmapp.utils.StringUtils

private const val TAG = "TeamComposition"

@Composable
fun OrdersComposition(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel
) {
    Log.d(TAG, "TeamMembersLiveData: Parent is build!")

    val itemsVM by appModel.orders.observeAsState()
    if (itemsVM != null)
        appModel.addOrdersToSnapShot(itemsVM!!)

    val items = appModel.ordersS

    val onClickDetailsLambda: (Int) -> Unit = {
        appModel.changeOrdersDetailsVisibility(it)
    }
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        items(items = items, key = { it.order.id }
        ) { teamMember ->
            OrderCard(
                order = teamMember,
                onClickDetails = { onClickDetailsLambda(it) }
            )
        }
    }
}

@Composable
fun OrderCard(
    order: DomainOrderComplete,
    onClickDetails: (Int) -> Unit
) {
    Log.d(TAG, "OrderCard: ${order.order.orderNumber}")
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(0.3f),
        ),

        ) {
        Order(
            modifier = Modifier,

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
        )
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

    onClickDetails: () -> Unit,
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
        Log.d(TAG, "Order: $orderNumber")

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = orderNumber,
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

        OrderDetails(
            modifier = modifier,
            orderId = orderId,
            detailsVisibility = detailsVisibility,
            placerFullName = placerFullName,
            createdDate = createdDate,
            completedDate = completedDate,
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
    completedDate: String? = "",
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
    }
}