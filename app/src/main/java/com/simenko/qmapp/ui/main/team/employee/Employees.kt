package com.simenko.qmapp.ui.main.team.employee

import android.annotation.SuppressLint

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainEmployeeComplete
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.SimpleRecordHeader
import com.simenko.qmapp.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.dp
import com.simenko.qmapp.utils.observeAsState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Employees(
    viewModel: TeamViewModel = hiltViewModel(),
    onClickEdit: (ID) -> Unit
) {
    val items by viewModel.employees.collectAsStateWithLifecycle(listOf())
    val scrollToRecord by viewModel.scrollToRecord.collectAsStateWithLifecycle(null)

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }

    val onClickDetailsLambda: (ID) -> Unit = { viewModel.setEmployeesVisibility(dId = SelectedNumber(it)) }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setEmployeesVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.deleteEmployee(it) } }
    val onClickEditLambda = remember<(ID) -> Unit> { { onClickEdit(it) } }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()

    LaunchedEffect(lifecycleState.value) {
        when(lifecycleState.value) {
            Lifecycle.Event.ON_RESUME -> viewModel.setIsComposed(true)
            Lifecycle.Event.ON_STOP -> viewModel.setIsComposed(false)
            else -> {}
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(scrollToRecord) {
        scrollToRecord?.let { record ->
            record.first.getContentIfNotHandled()?.let { employeeId ->
                viewModel.channel.trySend(this.launch { listState.scrollToSelectedItem(list = items.map { it.teamMember.id }.toList(), selectedId = employeeId) })
            }
        }
    }

    val lastItemIsVisible by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1 } }
    LaunchedEffect(lastItemIsVisible) {
        if (lastItemIsVisible) viewModel.mainPageHandler.onListEnd(true) else viewModel.mainPageHandler.onListEnd(false)
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
        items(items = items, key = { it.teamMember.id }) { teamMember ->
            EmployeeCard(
                teamMember = teamMember,
                onClickDetails = { onClickDetailsLambda(it) },
                onDoubleClick = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) }
            )
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun EmployeeCard(
    teamMember: DomainEmployeeComplete,
    onClickDetails: (ID) -> Unit,
    onDoubleClick: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (ID) -> Unit
) {
    val transitionState = remember { MutableTransitionState(teamMember.isExpanded).apply { targetState = !teamMember.isExpanded } }
    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = Constants.ANIMATION_DURATION) },
        targetValueByState = { (if (teamMember.isExpanded) CARD_OFFSET * 2 else 0f).dp() },
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
        Row(Modifier.padding(all = (DEFAULT_SPACE / 2).dp)) {
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(teamMember.teamMember.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickEdit(teamMember.teamMember.id) },
                content = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit action") }
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp)
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(teamMember.teamMember.id) { detectTapGestures(onDoubleTap = { onDoubleClick(teamMember.teamMember.id) }) },
        ) {
            Employee(item = teamMember, onClickDetails = onClickDetails)
        }
    }
}

@Composable
fun Employee(
    item: DomainEmployeeComplete,
    onClickDetails: (ID) -> Unit
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        SimpleRecordHeader(item, item.detailsVisibility) { onClickDetails(it.toLong()) }
        if (item.detailsVisibility) {
            Column(modifier = Modifier.padding(all = DEFAULT_SPACE.dp)) {
                val dep = item.department?.depAbbr + if (item.subDepartment?.subDepAbbr.isNullOrEmpty()) EmptyString.str else "/${item.subDepartment?.subDepAbbr}"
                HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(title = "Job role:", value = item.teamMember.jobRole, titleWight = 0.2f)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(title = "Department:", value = dep, titleWight = 0.2f)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(title = "Email:", value = StringUtils.getMail(item.teamMember.email), titleWight = 0.2f)
            }
        }
    }
}