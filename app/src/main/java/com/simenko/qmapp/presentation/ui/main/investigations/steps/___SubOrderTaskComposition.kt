package com.simenko.qmapp.presentation.ui.main.investigations.steps

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.simenko.qmapp.other.Constants.ACTION_ITEM_SIZE
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.theme.QMAppTheme
import com.simenko.qmapp.presentation.ui.common.ContentWithTitle
import com.simenko.qmapp.presentation.ui.common.HeaderWithTitle
import com.simenko.qmapp.presentation.ui.common.StatusChangeBtn
import com.simenko.qmapp.presentation.ui.common.StatusWithPercentage
import com.simenko.qmapp.presentation.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.utils.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubOrderTasksFlowColumn(
    invModel: InvestigationsViewModel = hiltViewModel(),
    parentId: ID = 0,
) {
    val items by invModel.tasks.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { invModel.setTasksVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { invModel.setTasksVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { invModel.deleteSubOrderTask(it) } }
    val onClickStatusLambda = remember<(DomainSubOrderTaskComplete, ID?) -> Unit> {
        { subOrderComplete, completedById -> invModel.showStatusUpdateDialog(currentSubOrderTask = subOrderComplete, performerId = completedById) }
    }

    Column {
        FlowRow {
            items.forEach { task ->
                if (task.subOrderTask.subOrderId == parentId) {
                    Box(Modifier.fillMaxWidth()) {
                        SubOrderTaskCard(
                            task = task,
                            onClickDetails = { onClickDetailsLambda(it) },
                            onClickActions = { onClickActionsLambda(it) },
                            onClickDelete = { onClickDeleteLambda(it) },
                            onClickStatus = { taskComplete, completedById -> onClickStatusLambda(taskComplete, completedById) }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SubOrderTaskCard(
    task: DomainSubOrderTaskComplete,
    onClickDetails: (ID) -> Unit,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickStatus: (DomainSubOrderTaskComplete, ID?) -> Unit
) {
    val transitionState = remember { MutableTransitionState(task.isExpanded).apply { targetState = !task.isExpanded } }
    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { (if (task.isExpanded) CARD_OFFSET * 2 else 0f).dp() },
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
        Row(Modifier.padding(all = (DEFAULT_SPACE / 2).dp)) {
            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(task.subOrderTask.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )
            IconButton(
                modifier = Modifier.size(ACTION_ITEM_SIZE.dp),
                onClick = {},
                content = { Icon(imageVector = Icons.Filled.AttachFile, contentDescription = "attach file action") }
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .padding(horizontal = DEFAULT_SPACE.dp, vertical = (DEFAULT_SPACE / 2).dp)
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(task.subOrderTask.id) { detectTapGestures(onDoubleTap = { onClickActions(task.subOrderTask.id) }) },
        ) {
            SubOrderTask(
                subOrderTask = task,
                onClickDetails = onClickDetails,
                onClickStatus = onClickStatus
            )
        }
    }
}

@Composable
fun SubOrderTask(
    subOrderTask: DomainSubOrderTaskComplete = DomainSubOrderTaskComplete(),
    onClickDetails: (ID) -> Unit = {},
    onClickStatus: (DomainSubOrderTaskComplete, ID?) -> Unit
) {
    val containerColor = when (subOrderTask.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Row(
        modifier = Modifier
            .padding(all = DEFAULT_SPACE.dp)
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp)
                .weight(0.90f),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(0.54f)) {
                    ContentWithTitle(
                        title = "Group:",
                        contentTextSize = 12.sp,
                        value = subOrderTask.characteristic.characteristicSubGroup.charGroup.charGroup.ishElement ?: NoString.str,
                        titleWight = 0.35f
                    )
                    Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                    ContentWithTitle(
                        title = "Sub group:",
                        contentTextSize = 12.sp,
                        value = subOrderTask.characteristic.characteristicSubGroup.charSubGroup.ishElement ?: NoString.str,
                        titleWight = 0.35f
                    )
                }
                StatusChangeBtn(modifier = Modifier.weight(weight = 0.46f), containerColor = containerColor, onClick = { onClickStatus(subOrderTask, subOrderTask.subOrderTask.completedById) }) {
                    StatusWithPercentage(
                        status = Pair(subOrderTask.subOrderTask.statusId, subOrderTask.status.statusDescription),
                        result = Triple(subOrderTask.taskResult.isOk, subOrderTask.taskResult.total, subOrderTask.taskResult.good),
                        onlyInt = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            HeaderWithTitle(
                titleWight = 0.253f,
                title = "Characteristic:",
                text = subOrderTask.characteristic.characteristic.charDescription ?: NoString.str
            )
        }
        IconButton(onClick = { onClickDetails(subOrderTask.subOrderTask.id) }, modifier = Modifier.weight(weight = 0.10f)) {
            Icon(
                imageVector = if (subOrderTask.detailsVisibility) Icons.AutoMirrored.Filled.NavigateBefore else Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = if (subOrderTask.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more),
            )
        }
    }
}

@Preview(name = "Light Mode SubOrderTask", showBackground = true, widthDp = 409)
@Composable
fun MySubOrderTaskPreview() {
    QMAppTheme {
        SubOrderTask(
            onClickStatus = { _, _ -> }
        )
    }
}