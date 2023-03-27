package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.common.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SubOrderTasksFlowColumn(
    modifier: Modifier = Modifier,
    parentId: Int = 0,
    appModel: QualityManagementViewModel,
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
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
                                onDeleteOrder = {},
                                onEdit = {},
                                onFavorite = {}
                            )

                            SubOrderTaskCard(
                                modifier = modifier,
                                subOrderTask = subOrder,
                                onClickDetails = { it ->
                                    appModel.changeTaskDetailsVisibility(it.subOrderTask.id)
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
                                },
                                showStatusDialog = showStatusDialog
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
    showStatusDialog: (Int, DialogFor, Int?) -> Unit
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
                if (subOrderTask.detailsVisibility) {
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
            onClickDetails = { onClickDetails(subOrderTask) },
            showStatusDialog = showStatusDialog
        )
    }
}

@Composable
fun SubOrderTask(
    modifier: Modifier = Modifier,
    onClickDetails: () -> Unit = {},
    subOrderTask: DomainSubOrderTaskComplete = getSubOrderTasks()[0],
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
                    Column(
                        modifier = Modifier
                            .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                            .weight(0.54f),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                        ) {
                            Text(
                                text = "Group:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(weight = 0.35f)
                                    .padding(top = 2.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                            )
                            Text(
                                text = subOrderTask.characteristic.characteristicGroup.ishElement
                                    ?: "-",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(weight = 0.65f)
                                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                        ) {
                            Text(
                                text = "Sub group:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(weight = 0.35f)
                                    .padding(top = 2.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                            )
                            Text(
                                text = subOrderTask.characteristic.characteristicSubGroup.ishElement
                                    ?: "-",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(weight = 0.65f)
                                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                            )
                        }

                    }

                    TextButton(
                        modifier = Modifier
                            .weight(weight = 0.46f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp),
                        onClick = { showStatusDialog(subOrderTask.subOrderTask.id, DialogFor.CHARACTERISTIC, subOrderTask.subOrderTask.completedById) },
                        content = {
                            Text(
                                text = subOrderTask.status.statusDescription ?: "-",
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                            )
                            if(subOrderTask.subOrderTask.statusId == 3) {
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
                                    imageVector = if (subOrderTask.taskResult.isOk != false) Icons.Filled.Check else Icons.Filled.Close,
                                    contentDescription = if (subOrderTask.taskResult.isOk != false) {
                                        stringResource(R.string.show_less)
                                    } else {
                                        stringResource(R.string.show_more)
                                    },
                                    modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                                    tint = if (subOrderTask.taskResult.isOk != false) {
                                        Color.Green
                                    } else {
                                        Color.Red
                                    },
                                )
                                val conformity = (subOrderTask.taskResult.total?.toFloat()?.let {
                                    subOrderTask.taskResult.good?.toFloat()
                                        ?.div(it)
                                }?.times(100))?:0.0f

                                Text(
                                    text = when {
                                        !conformity.isNaN() -> {conformity.roundToInt().toString() + "%"}
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
                        },
                        enabled = true,
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        border = null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = _level_3_record_color,
                            contentColor = Primary900
                        )
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
                            .weight(weight = 0.253f)
                            .padding(top = 4.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = subOrderTask.characteristic.characteristic.charDescription ?: "-",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.747f)
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
                    imageVector = if (subOrderTask.detailsVisibility) Icons.Filled.NavigateBefore else Icons.Filled.NavigateNext/*NavigateBefore*/,
                    contentDescription = if (subOrderTask.detailsVisibility) {
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
                .padding(vertical = 0.dp, horizontal = 0.dp),
            showStatusDialog = { a, b, c -> statusDialog(a, b, c) }
        )
    }
}

fun getSubOrderTasks() = List(30) {
    DomainSubOrderTaskComplete(
        subOrderTask = DomainSubOrderTask(
            id = 1,
            statusId = 1,
            completedDate = "",
            createdDate = "",
            subOrderId = 1,
            charId = 1
        ),
        characteristic = DomainCharacteristicComplete(
            characteristic = getCharacteristic(),
            characteristicGroup = getCharacteristicGroup(),
            characteristicSubGroup = getCharacteristicSubGroup()
        ),
        subOrder = getSubOrder(),
        status = DomainOrdersStatus(
            id = 1,
            statusDescription = "In Progress"
        ),
        taskResult = getTaskResult()
    )
}

fun getCharacteristic() = DomainCharacteristic(
    id = 1,
    ishCharId = 1,
    charOrder = 1,
    charDescription = "Шорсткість отвору внутрішнього кількця",
    charDesignation = "Ra d",
    projectId = 1,
    ishSubChar = 1,
    sampleRelatedTime = 0.12,
    measurementRelatedTime = 0.21
)

fun getCharacteristicGroup() = DomainElementIshModel(
    id = 1,
    ishElement = "Microgeometry"
)

fun getCharacteristicSubGroup() = DomainIshSubCharacteristic(
    id = 1,
    ishElement = "Roughness",
    measurementGroupRelatedTime = 0.24
)

fun getTaskResult() = DomainTaskResult(
    id = 0,
    isOk = true,
    good = 10,
    total = 10
)