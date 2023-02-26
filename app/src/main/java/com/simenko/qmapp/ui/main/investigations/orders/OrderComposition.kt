package com.simenko.qmapp.ui.main.investigations.orders

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.theme.level_1_record_color
import com.simenko.qmapp.utils.StringUtils

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
        orderStatus = DomainOrdersStatus(1, "ToDo"),
        detailsVisibility = true,
        subOrdersVisibility = false
    )
}

@Composable
fun OrdersLiveData(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel
) {
    val observeOrders by appModel.completeOrdersMediator.observeAsState()

    observeOrders?.apply {
        if (observeOrders!!.first != null) {
            LazyColumn(modifier = modifier) {
                items(items = observeOrders!!.first!!) { order ->
                    OrderCard(
                        viewModel = appModel,
                        order = order,
                        onClickDetails = { it ->
                            appModel.changeCompleteOrdersDetailsVisibility(it)
                        },
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    viewModel: QualityManagementViewModel,
    order: DomainOrderComplete,
    onClickDetails: (DomainOrderComplete) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = level_1_record_color,
        ),
        modifier = modifier
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
    viewModel: QualityManagementViewModel,
    modifier: Modifier = Modifier,
    order: DomainOrderComplete = getOrders()[0],
    onClickDetails: () -> Unit = {}
) {

    Column(
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Order type:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.25f)
            )
            Text(
                text = order.orderType.typeDescription ?: "",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    baselineShift = BaselineShift(0.3f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.65f)
                    .padding(start = 3.dp)
            )

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
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Customer:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.25f)
            )
            Text(
                text = order.customer.depAbbr ?: "",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.38f)
                    .padding(start = 3.dp)
            )
            Text(
                text = "Num.:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    textAlign = TextAlign.Right,
                    baselineShift = BaselineShift(0.3f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.12f)
            )
            Text(
                text = order.order.orderNumber.toString(),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.25f)
                    .padding(start = 3.dp)
            )
        }
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Order reason:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.25f)
            )
            Text(
                text = order.orderReason.reasonFormalDescript ?: "",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.38f)
                    .padding(start = 3.dp)
            )
            Text(
                text = "Status:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    textAlign = TextAlign.Right
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.12f)
            )
            Text(
                text = order.orderStatus.statusDescription ?: "",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.25f)
                    .padding(start = 3.dp)
            )
        }

        if (order.detailsVisibility) {

            Divider(modifier = modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)

            Row(
                modifier = modifier.padding(start = 10.dp),
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
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 0.65f)
                        .padding(start = 3.dp)
                )
            }
            Row(
                modifier = modifier.padding(start = 10.dp),
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
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 0.65f)
                        .padding(start = 3.dp)
                )
            }
            Row(
                modifier = modifier.padding(start = 10.dp),
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
                appModel = viewModel,
                modifier = Modifier
            )
        }
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