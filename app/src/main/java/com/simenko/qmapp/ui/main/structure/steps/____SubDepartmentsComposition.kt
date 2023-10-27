package com.simenko.qmapp.ui.main.structure.steps

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.main.structure.CompanyStructureViewModel
import com.simenko.qmapp.utils.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubDepartments(viewModel: CompanyStructureViewModel = hiltViewModel()) {

    val departmentVisibility by viewModel.departmentsVisibility.collectAsStateWithLifecycle()
    val items by viewModel.subDepartments.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(Int) -> Unit> { { viewModel.setSubDepartmentsVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(Int) -> Unit> { { viewModel.setSubDepartmentsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(Int) -> Unit> { { viewModel.onDeleteSubDepartmentClick(it) } }
    val onClickAddLambda = remember<(Int) -> Unit> { { viewModel.onAddSubDepartmentClick(it) } }
    val onClickEditLambda = remember<(Pair<Int, Int>) -> Unit> { { viewModel.onEditSubDepartmentClick(it) } }
    val onClickProductsLambda = remember<(Int) -> Unit> { { viewModel.onSubDepartmentProductsClick(it) } }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow {
            items.forEach { subDepartment ->
                SubDepartmentCard(
                    viewModel = viewModel,
                    subDepartment = subDepartment,
                    onClickDetails = { onClickDetailsLambda(it) },
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                    onClickEdit = { onClickEditLambda(it) },
                    onClickProducts = { onClickProductsLambda(it) }
                )
            }
        }
        Divider(modifier = Modifier.height(0.dp))
        FloatingActionButton(
            modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = { onClickAddLambda(departmentVisibility.first.num) },
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SubDepartmentCard(
    viewModel: CompanyStructureViewModel,
    subDepartment: DomainSubDepartment,
    onClickDetails: (Int) -> Unit,
    onClickActions: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickEdit: (Pair<Int, Int>) -> Unit,
    onClickProducts: (Int) -> Unit
) {
    val transitionState = remember { MutableTransitionState(subDepartment.isExpanded).apply { targetState = !subDepartment.isExpanded } }
    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = Constants.ANIMATION_DURATION) },
        targetValueByState = { (if (subDepartment.isExpanded) Constants.CARD_OFFSET * 2 else 0f).dp() },
    )

    val containerColor = when (subDepartment.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.primaryContainer
    }

    val borderColor = when (subDepartment.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (subDepartment.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.primaryContainer
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(all = (DEFAULT_SPACE / 2).dp)) {
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(subDepartment.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickEdit(Pair(subDepartment.depId, subDepartment.id)) },
                content = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit action") },
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
                .pointerInput(subDepartment.id) { detectTapGestures(onDoubleTap = { onClickActions(subDepartment.id) }) }
        ) {
            SubDepartment(
                viewModel = viewModel,
                subDepartment = subDepartment,
                onClickDetails = { onClickDetails(it) },
                onClickProducts = { onClickProducts(it) }
            )
        }
    }
}

@Composable
fun SubDepartment(
    viewModel: CompanyStructureViewModel,
    subDepartment: DomainSubDepartment,
    onClickDetails: (Int) -> Unit = {},
    onClickProducts: (Int) -> Unit
) {
    val containerColor = when (subDepartment.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.primaryContainer
    }

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(0.72f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = subDepartment.subDepOrder?.toString() ?: NoString.str)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.39f, title = "Sub department:", text = subDepartment.subDepAbbr ?: NoString.str)
            }
            StatusChangeBtn(modifier = Modifier.weight(weight = 0.28f), containerColor = containerColor, onClick = { onClickProducts(subDepartment.id) }) {
                Text(
                    text = "Products",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(subDepartment.id) }) {
                Icon(
                    imageVector = if (subDepartment.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (subDepartment.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        SubDepartmentDetails(viewModel = viewModel, subDepartment = subDepartment)
    }
}

@Composable
fun SubDepartmentDetails(
    viewModel: CompanyStructureViewModel,
    subDepartment: DomainSubDepartment
) {
    if (subDepartment.detailsVisibility) {
        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp)) {
            Divider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Complete name:", value = subDepartment.subDepDesignation?: NoString.str, titleWight = 0.26f)
        }
        Channels(viewModel = viewModel)
    }
}