package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.other.Constants.ACTION_ITEM_SIZE
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.StatusWithPercentage
import com.simenko.qmapp.ui.dialogs.*
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubOrderTasksFlowColumn(
    modifier: Modifier = Modifier,
    invModel: InvestigationsViewModel = hiltViewModel(),
    parentId: Int = 0,
) {
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
                        modifier = Modifier.padding(Constants.CARDS_PADDING),
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
            .padding(start = 4.dp, end = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp)
                    .weight(0.90f),
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.54f)) {
                        ContentWithTitle(
                            title = "Group:",
                            contentTextSize = 12.sp,
                            value = subOrderTask.characteristic.characteristicGroup.ishElement ?: NoString.str,
                            titleWight = 0.35f
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ContentWithTitle(
                            title = "Sub group:",
                            contentTextSize = 12.sp,
                            value = subOrderTask.characteristic.characteristicSubGroup.ishElement ?: NoString.str,
                            titleWight = 0.35f
                        )
                    }

                    TextButton(
                        modifier = Modifier
                            .weight(weight = 0.46f)
                            .padding(start = 3.dp),
                        onClick = { onClickStatus(subOrderTask, subOrderTask.subOrderTask.completedById) },
                        content = {
                            StatusWithPercentage(
                                status = Pair(subOrderTask.subOrderTask.statusId, subOrderTask.status.statusDescription),
                                result = Triple(subOrderTask.taskResult.isOk, subOrderTask.taskResult.total, subOrderTask.taskResult.good),
                                onlyInt = true
                            )
                        },
                        enabled = true,
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        border = null,
                        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColorFor(containerColor))
                    )
                }

                HeaderWithTitle(
                    modifier = Modifier.padding(bottom = 4.dp),
                    titleWight = 0.253f,
                    title = "Characteristic:",
                    text = subOrderTask.characteristic.characteristic.charDescription ?: NoString.str
                )
            }
            IconButton(
                onClick = { onClickDetails(subOrderTask.subOrderTask.id) },
                modifier = Modifier
                    .weight(weight = 0.10f)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (subOrderTask.detailsVisibility) Icons.Filled.NavigateBefore else Icons.Filled.NavigateNext,
                    contentDescription = if (subOrderTask.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more),
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
            onClickStatus = { _, _ -> }
        )
    }
}