package com.simenko.qmapp.ui.main.team

import android.annotation.SuppressLint
import android.widget.Toast

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainEmployeeComplete
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.common.TopLevelSingleRecordDetails
import com.simenko.qmapp.ui.common.TopLevelSingleRecordMainHeader
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.dp
import kotlin.math.roundToInt

@Composable
fun EmployeeComposition(
    appModel: TeamViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val items by appModel.employees.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda: (Int) -> Unit = { appModel.setCurrentEmployeeVisibility(dId = SelectedNumber(it)) }
    val onClickActionsLambda = remember<(Int) -> Unit> { { appModel.setCurrentEmployeeVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(Int) -> Unit> { { appModel.deleteRecord(it) } }
    val onClickEditLambda =
        remember<(Int, String) -> Unit> { { p1, p2 -> Toast.makeText(context, "id = $p1, name = $p2", Toast.LENGTH_LONG).show() } }
    val listState = rememberLazyListState()

    val lastItemIsVisible by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    if (lastItemIsVisible) appModel.onListEnd(FabPosition.Center) else appModel.onListEnd(FabPosition.End)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        items(items = items, key = { it.teamMember.id }) { teamMember ->
            EmployeeCard(
                teamMember = teamMember,
                onClickDetails = { onClickDetailsLambda(it) },
                onDoubleClick = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { p1, p2 -> onClickEditLambda(p1, p2) }
            )
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun EmployeeCard(
    teamMember: DomainEmployeeComplete,
    onClickDetails: (Int) -> Unit,
    onDoubleClick: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickEdit: (Int, String) -> Unit
) {
    val transitionState = remember {
        MutableTransitionState(teamMember.isExpanded).apply {
            targetState = !teamMember.isExpanded
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = Constants.ANIMATION_DURATION) },
        targetValueByState = { if (teamMember.isExpanded) CARD_OFFSET.dp() else 0f },
    )
    val containerColor = when (teamMember.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when (teamMember.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (teamMember.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.surfaceVariant
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(teamMember.teamMember.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )

            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickEdit(teamMember.teamMember.id, teamMember.teamMember.fullName) },
                content = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit action") }
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(teamMember.teamMember.id) {
                    detectTapGestures(onDoubleTap = { onDoubleClick(teamMember.teamMember.id) })
                },
        ) {
            Employee(
                item = teamMember,
                onClickDetails = onClickDetails,
                modifier = Modifier.padding(Constants.CARDS_PADDING)
            )
        }
    }
}

@Composable
fun Employee(
    item: DomainEmployeeComplete,
    onClickDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        TopLevelSingleRecordMainHeader(modifier, item, item.detailsVisibility, { onClickDetails(it.toInt()) })

        if (item.detailsVisibility) {
            TopLevelSingleRecordDetails("Job role:", item.teamMember.jobRole, modifier, 0.2f)
            TopLevelSingleRecordDetails("Department:", item.department?.depName ?: "-", modifier, 0.2f)
            TopLevelSingleRecordDetails("Email:", StringUtils.getMail(item.teamMember.email), modifier, 0.2f)
        }
    }
}