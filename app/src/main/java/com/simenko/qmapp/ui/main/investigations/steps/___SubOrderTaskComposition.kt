package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Constants.ACTION_ITEM_SIZE
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.dialogs.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.dp
import kotlin.math.roundToInt

private const val TAG = "SubOrderTaskComposition"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubOrderTasksFlowColumn(
    modifier: Modifier = Modifier,
    parentId: Int = 0,
) {
    val invModel: InvestigationsViewModel = hiltViewModel()
    Log.d(TAG, "InvestigationsViewModel: $invModel")

    val items by invModel.tasksSF.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(Int) -> Unit> {
        {
            invModel.setTasksVisibility(dId = SelectedNumber(it))
            println("selected task is: $it")
        }
    }

    val onClickActionsLambda = remember<(Int) -> Unit> { { invModel.setTasksVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(Int) -> Unit> { { invModel.deleteSubOrderTask(it) } }
    val onClickStatusLambda = remember<(DomainSubOrderTaskComplete, Int?) -> Unit> {
        { subOrderComplete, completedById -> invModel.showStatusUpdateDialog(currentSubOrderTask = subOrderComplete, performerId = completedById) }
    }

    FlowRow(modifier = modifier) {
        items.forEach { task ->
            if (task.subOrderTask.subOrderId == parentId) {

                Box(Modifier.fillMaxWidth()) {
                    SubOrderTaskCard(
                        modifier = modifier,
                        appModel = invModel,
                        task = task,
                        onClickDetails = { onClickDetailsLambda(it) },
                        cardOffset = CARD_OFFSET.dp(),
                        onClickActions = { onClickActionsLambda(it) },
                        onClickDelete = { onClickDeleteLambda(it) },
                        onClickStatus = { taskComplete, completedById -> onClickStatusLambda(taskComplete, completedById) }
                    )
                }
                Divider(thickness = 4.dp, color = Color.Transparent)
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SubOrderTaskCard(
    modifier: Modifier = Modifier,
    appModel: InvestigationsViewModel? = null,
    task: DomainSubOrderTaskComplete,
    onClickDetails: (Int) -> Unit,
    cardOffset: Float,
    onClickActions: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickStatus: (DomainSubOrderTaskComplete, Int?) -> Unit
) {
    val transitionState = remember {
        MutableTransitionState(task.isExpanded).apply {
            targetState = !task.isExpanded
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (task.isExpanded) cardOffset else 0f },
    )

    val containerColor = when (task.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.tertiaryContainer
    }

    val borderColor = when (task.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (task.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.tertiaryContainer
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(task.subOrderTask.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )
            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = {},
                content = { Icon(imageVector = Icons.Filled.AttachFile, contentDescription = "edit action") }
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(task.subOrderTask.id) {
                    detectTapGestures(
                        onDoubleTap = { onClickActions(task.subOrderTask.id) }
                    )
                },
        ) {
            SubOrderTask(
                modifier = modifier,
                subOrderTask = task,
                onClickDetails = onClickDetails,
                onClickStatus = onClickStatus
            )
        }
    }
}

@Composable
fun SubOrderTask(
    modifier: Modifier = Modifier,
    onClickDetails: (Int) -> Unit = {},
    subOrderTask: DomainSubOrderTaskComplete = DomainSubOrderTaskComplete(),
    onClickStatus: (DomainSubOrderTaskComplete, Int?) -> Unit
) {
    val containerColor = when (subOrderTask.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.tertiaryContainer
    }

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
                        onClick = { onClickStatus(subOrderTask, subOrderTask.subOrderTask.completedById) },
                        content = {
                            Text(
                                text = subOrderTask.status.statusDescription ?: "-",
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                            )
                            if (subOrderTask.subOrderTask.statusId == 3) {
                                Text(
                                    text = "(",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(
                                            top = 0.dp,
                                            start = 3.dp,
                                            end = 0.dp,
                                            bottom = 0.dp
                                        )
                                )
                                Icon(
                                    imageVector = if (subOrderTask.taskResult.isOk != false) Icons.Filled.Check else Icons.Filled.Close,
                                    contentDescription = if (subOrderTask.taskResult.isOk != false) {
                                        stringResource(R.string.show_less)
                                    } else {
                                        stringResource(R.string.show_more)
                                    },
                                    modifier = Modifier.padding(
                                        top = 0.dp,
                                        start = 0.dp,
                                        end = 0.dp,
                                        bottom = 0.dp
                                    ),
                                    tint = if (subOrderTask.taskResult.isOk != false) {
                                        Color.Green
                                    } else {
                                        Color.Red
                                    },
                                )
                                val conformity = (subOrderTask.taskResult.total?.toFloat()?.let {
                                    subOrderTask.taskResult.good?.toFloat()
                                        ?.div(it)
                                }?.times(100)) ?: 0.0f

                                Text(
                                    text = when {
                                        !conformity.isNaN() -> {
                                            conformity.roundToInt().toString() + "%"
                                        }

                                        else -> {
                                            ""
                                        }
                                    },
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(
                                            top = 0.dp,
                                            start = 3.dp,
                                            end = 0.dp,
                                            bottom = 0.dp
                                        )
                                )
                                Text(
                                    text = ")",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(
                                            top = 0.dp,
                                            start = 3.dp,
                                            end = 0.dp,
                                            bottom = 0.dp
                                        )
                                )
                            }
                        },
                        enabled = true,
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        border = null,
                        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColorFor(containerColor))
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
                onClick = { onClickDetails(subOrderTask.subOrderTask.id) },
                modifier = Modifier
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
            onClickStatus = { a, b -> }
        )
    }
}