package com.simenko.qmapp.ui.main.investigations.orders

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.common.ACTION_ITEM_SIZE
import com.simenko.qmapp.ui.main.common.ANIMATION_DURATION
import com.simenko.qmapp.ui.main.common.ActionsRow
import com.simenko.qmapp.ui.main.common.CARD_OFFSET
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
    modifier: Modifier = Modifier,
    parentId: Int = 0,
    appModel: QualityManagementViewModel
) {
    val observeSubOrderTasks by appModel.completeSubOrderTasksMediator.observeAsState()
    var clickCounter = 0

    observeSubOrderTasks?.apply {
        if (observeSubOrderTasks!!.first != null) {
            FlowRow(modifier = modifier) {
                observeSubOrderTasks!!.first!!.forEach { subOrder ->
                    if (subOrder.subOrderTask.subOrderId == parentId) {

                        Box(Modifier.fillMaxWidth()) {
                            ActionsRow(
                                actionIconSize = ACTION_ITEM_SIZE.dp,
                                onDelete = {},
                                onEdit = {},
                                onFavorite = {}
                            )

                            SubOrderTaskCard(
                                modifier = modifier,
                                subOrderTask = subOrder,
                                onClickDetails = { it ->
                                    appModel.changeCompleteSubOrderTasksDetailsVisibility(it)
                                },
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
                                        appModel.changeCompleteSubOrderTasksExpandState(it)
                                    }
                                }
                            )
                        }
                        Divider(thickness = 4.dp, color = Color.Transparent)
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SubOrderTaskCard(
    modifier: Modifier = Modifier,
    subOrderTask: DomainSubOrderTaskComplete,
    onClickDetails: (DomainSubOrderTaskComplete) -> Unit,
    cardOffset: Float,
    onChangeExpandState: (DomainSubOrderTaskComplete) -> Unit,
) {
    val transitionState = remember {
        MutableTransitionState(subOrderTask.isExpanded).apply {
            targetState = !subOrderTask.isExpanded
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (subOrderTask.isExpanded) cardOffset else 0f },
    )

    val cardBgColor by transition.animateColor(
        label = "cardBgColorTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = {
            if (subOrderTask.isExpanded) Accent200 else
                if(subOrderTask.measurementsVisibility) {
                    _level_3_record_color_details
                } else {
                    _level_3_record_color
                }
        }
    )

    val cardElevation by transition.animateDp(
        label = "cardElevation",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (subOrderTask.isExpanded) 40.dp else 2.dp }
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor,
        ),
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetTransition.roundToInt(), 0) }
            .clickable { onChangeExpandState(subOrderTask) },
        elevation = CardDefaults.cardElevation(cardElevation),
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
                    modifier = Modifier.padding(
                        top = 0.dp,
                        start = 0.dp,
                        end = 0.dp,
                        bottom = 4.dp
                    ),
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
                    modifier = Modifier.padding(
                        top = 0.dp,
                        start = 0.dp,
                        end = 0.dp,
                        bottom = 4.dp
                    ),
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