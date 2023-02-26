package com.simenko.qmapp.ui.main.investigations.orders

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.theme.level_2_record_color
import com.simenko.qmapp.ui.theme.level_3_record_color
import com.simenko.qmapp.utils.StringUtils

fun getSubOrderTasks() = List(30) { i ->
    DomainSubOrderTaskComplete(
            subOrderTask = DomainSubOrderTask(
                    id = 1,
                    statusId = 1,
                    completedDate = "",
                    createdDate = "",
                    subOrderId = 1,
                    charId = 1
            ),
            characteristic = DomainCharacteristic(
                    id = 1,
                    ishCharId = 1,
                    charOrder = 1,
                    charDescription = "Шорсткість отвору внутрішнього кількця",
                    charDesignation = "Ra d",
                    projectId = 1,
                    ishSubChar = 1,
                    sampleRelatedTime = 0.12,
                    measurementRelatedTime = 0.21
            ),
            status = DomainOrdersStatus(
                    id = 1,
                    statusDescription = "In Progress"
            )
    )
}

@Composable
fun SubOrderTasksFlowColumn(
        parentId: Int = 0,
        modifier: Modifier = Modifier,
        appModel: QualityManagementViewModel
) {
    val observeSubOrderTasks by appModel.completeSubOrderTasksMediator.observeAsState()

    observeSubOrderTasks?.apply {
        if (observeSubOrderTasks!!.first != null) {
            FlowRow(modifier = modifier) {
                observeSubOrderTasks!!.first!!.forEach { subOrder ->
                    if (subOrder.subOrderTask.subOrderId == parentId) {
                        SubOrderTaskCard(
                                subOrderTask = subOrder,
                                onClickDetails = { it ->
                                    appModel.changeCompleteSubOrderTasksDetailsVisibility(it)
                                },
                                modifier = modifier
                        )
                        Divider(thickness = 4.dp, color = Color.Transparent)
                    }
                }
            }
        }
    }
}

@Composable
fun SubOrderTaskCard(
        subOrderTask: DomainSubOrderTaskComplete,
        onClickDetails: (DomainSubOrderTaskComplete) -> Unit,
        modifier: Modifier = Modifier
) {
    Card(
            colors = CardDefaults.cardColors(
                    containerColor = level_3_record_color,
            ),
            modifier = modifier
    ) {
        SubOrderTask(
                modifier = modifier,
                subOrderTask = subOrderTask,
                onClickDetails = { onClickDetails(subOrderTask) }
        )
    }
}

@Composable
fun SubOrderTask(
        modifier: Modifier = Modifier,
        onClickDetails: () -> Unit = {},
        subOrderTask: DomainSubOrderTaskComplete = getSubOrderTasks()[0]
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
                            text = "Char. group:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                    .weight(weight = 0.22f)
                                    .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
//                            ToDo change to real value when data is available (subOrderTask.characteristic.ishCharId.toString())
                            text = "Micro geometry",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                    .weight(weight = 0.38f)
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
                                    .weight(weight = 0.15f)
                                    .padding(top = 5.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                            text = subOrderTask.status.statusDescription ?: "-",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                    .weight(weight = 0.25f)
                                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
                Row(
                        modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = "Characteristic:",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                    .weight(weight = 0.28f)
                                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                            text = subOrderTask.characteristic.charDescription ?: "-",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                    .weight(weight = 0.72f)
                                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
            }
            IconButton(
                    onClick = onClickDetails, modifier = Modifier
                    .weight(weight = 0.10f)
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                        imageVector = if (subOrderTask.measurementsVisibility) Icons.Filled.NavigateBefore else Icons.Filled.NavigateNext/*NavigateBefore*/,
                        contentDescription = if (subOrderTask.measurementsVisibility) {
                            stringResource(R.string.show_less)
                        } else {
                            stringResource(R.string.show_more)
                        },
                        modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            }
        }
    }
}

@Preview(name = "Light Mode SubOrderTask", showBackground = true, widthDp = 409)
@Composable
fun MySubOrderTaskPreview() {
    QMAppTheme {
        SubOrderTask(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp, horizontal = 0.dp)
        )
    }
}