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
import com.simenko.qmapp.ui.theme.level_2_record_color
import com.simenko.qmapp.utils.StringUtils

fun getSubOrders() = List(30) { i ->

    DomainSubOrderComplete(
        subOrder = DomainSubOrder(
            id = 1,
            orderId = 1,
            subOrderNumber = (100..300).random(),
            orderedById = 1,
            completedById = 1,
            statusId = 1,
            createdDate = "",
            completedDate = "",
            departmentId = 1,
            subDepartmentId = 1,
            channelId = 1,
            lineId = 1,
            operationId = 1,
            itemPreffix = "c",
            itemTypeId = 1,
            itemVersionId = 1,
            samplesCount = (1..10).random()
        ),
        orderedBy = DomainTeamMember(
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
        completedBy = DomainTeamMember(
            id = 1,
            departmentId = 1,
            department = "ГШСК№1",
            email = "roman.semenyshyn@skf.com",
            fullName = "Дмитро Ліщук",
            jobRole = "Quality Manager",
            roleLevelId = 1,
            passWord = "13050513",
            companyId = 1,
            detailsVisibility = false
        ),
        status = DomainOrdersStatus(1, "ToDo"),
        department = DomainDepartment(
            id = 1,
            depAbbr = "ГШСК№1",
            depName = "Група шліфувально-складальних ліній",
            depManager = 1,
            depOrganization = "Manufacturing",
            depOrder = 1,
            companyId = 1
        ),
        subDepartment = DomainSubDepartment(
            id = 1,
            subDepAbbr = "ДБШ",
            depId = 1,
            subDepDesignation = "Дільниця безцетрової обробки",
            subDepOrder = 1,
            channelsVisibility = false
        ),
        channel = DomainManufacturingChannel(
            id = 1,
            channelAbbr = "ДБШ 1",
            subDepId = 1,
            channelDesignation = "Канал 1 нової дільниці безцентрового шліфування",
            channelOrder = 1,
            linesVisibility = false
        ),
        line = DomainManufacturingLine(
            id = 1,
            lineAbbr = "IR",
            chId = 1,
            lineDesignation = "Лінія обробки торців IR",
            lineOrder = 1,
            operationVisibility = false
        ),
        operation = DomainManufacturingOperation(
            id = 1,
            operationAbbr = "T",
            lineId = 1,
            operationDesignation = "Шліфування торців",
            operationOrder = 1,
            detailsVisibility = false
        ),
        detailsVisibility = true,
        tasksVisibility = true
    )
}

@Composable
fun SubOrdersLiveData(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel
) {
    val observeSubOrders by appModel.completeSubOrdersMediator.observeAsState()

    observeSubOrders?.apply {
        if (observeSubOrders!!.first != null) {
            LazyColumn(modifier = modifier) {
                items(items = observeSubOrders!!.first!!) { subOrder ->
                    SubOrderCard(
                        subOrder = subOrder,
                        onClickDetails = { it ->
                            appModel.changeCompleteSubOrdersDetailsVisibility(it)
                        },
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
fun SubOrderCard(
    subOrder: DomainSubOrderComplete,
    onClickDetails: (DomainSubOrderComplete) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = level_2_record_color,
        ),
        modifier = modifier
    ) {
        SubOrder(
            modifier = modifier,
            subOrder = subOrder,
            onClickDetails = { onClickDetails(subOrder) }
        )
    }
}

@Composable
fun SubOrder(
    modifier: Modifier = Modifier,
    onClickDetails: () -> Unit = {},
    subOrder: DomainSubOrderComplete = getSubOrders()[0]
) {
    Column(
        modifier = Modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dep./Area:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.18f)
                    .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = StringUtils.concatTwoStrings(
                    subOrder.department.depAbbr,
                    subOrder.subDepartment.subDepAbbr
                ),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.42f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = "Num.:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    textAlign = TextAlign.Right
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.10f)
                    .padding(top = 7.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = subOrder.subOrder.subOrderNumber.toString(),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.20f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
            IconButton(
                onClick = onClickDetails, modifier = Modifier
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
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chanel/Line:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.18f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = StringUtils.concatTwoStrings(
                    subOrder.channel.channelAbbr,
                    subOrder.line.lineAbbr
                ),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.42f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
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
                    .weight(weight = 0.10f)
                    .padding(top = 5.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = subOrder.status.statusDescription ?: "-",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.30f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Product:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.18f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
//                ToDo change it when all data available
                text = "IR-33213/VK806/VU1006",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.42f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = "Ver.:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    textAlign = TextAlign.Right
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.10f)
                    .padding(top = 5.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
//                ToDo change when all data available
                text = "V.8",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.30f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Operation:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.18f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = StringUtils.concatTwoStrings2(
                    subOrder.operation.operationAbbr,
                    subOrder.operation.operationDesignation
                ),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.82f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quantity:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.18f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = subOrder.subOrder.samplesCount.toString(),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.82f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        if (subOrder.detailsVisibility) {
            Divider(modifier = modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Row(
                modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ordered by:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 0.18f)
                        .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
                Text(
                    text = subOrder.orderedBy.fullName,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 0.82f)
                        .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                )
            }
            Row(
                modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Created:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 0.18f)
                        .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
                Text(
                    text = StringUtils.getDateTime(subOrder.subOrder.createdDate),
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 0.82f)
                        .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                )
            }
            Row(
                modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Completed by:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 0.18f)
                        .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
                Text(
                    text = subOrder.completedBy?.fullName ?: "-",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 0.82f)
                        .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                )
            }
            Row(
                modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Completed:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 0.18f)
                        .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
                Text(
                    text = StringUtils.getDateTime(subOrder.subOrder.completedDate),
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 0.82f)
                        .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                )
            }
        }
    }
}

@Preview(name = "Light Mode SubOrder", showBackground = true, widthDp = 409)
@Composable
fun MySubOrderPreview() {
    QMAppTheme {
        SubOrder(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp, horizontal = 0.dp)
        )
    }
}