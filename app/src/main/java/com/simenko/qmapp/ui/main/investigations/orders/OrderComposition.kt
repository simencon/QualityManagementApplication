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
            orderStatus = DomainOrdersStatus(1, "In Progress"),
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
                        modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 4.dp),
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
                        modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 4.dp),
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
                        modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 4.dp),
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