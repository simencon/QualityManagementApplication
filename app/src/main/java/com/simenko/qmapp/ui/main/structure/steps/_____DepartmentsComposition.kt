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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.simenko.qmapp.domain.entities.DomainDepartmentComplete
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.main.structure.CompanyStructureViewModel
import com.simenko.qmapp.utils.dp
import kotlin.math.roundToInt

@Composable
fun Departments(
    modifier: Modifier = Modifier,
    viewModel: CompanyStructureViewModel = hiltViewModel()
) {
    val items by viewModel.departments.collectAsStateWithLifecycle()

    val onClickDetailsLambda = remember<(Int) -> Unit> { { viewModel.setDepartmentsVisibility(dId = SelectedNumber(it)) } }

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }

    LazyColumn(modifier = modifier, state = listState) {
        items(items = items, key = { it.department.id }) { department ->
            DepartmentCard(
                viewModel = viewModel,
                department = department,
                onClickDetails = { onClickDetailsLambda(it) },
                onClickActions = {},
                onClickDelete = {},
                onClickEdit = {}
            )
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DepartmentCard(
    modifier: Modifier = Modifier,
    viewModel: CompanyStructureViewModel,
    department: DomainDepartmentComplete,
    onClickDetails: (Int) -> Unit,
    onClickActions: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickEdit: (Int) -> Unit
) {
    val transitionState = remember { MutableTransitionState(department.isExpanded).apply { targetState = !department.isExpanded } }
    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = Constants.ANIMATION_DURATION) },
        targetValueByState = { if (department.isExpanded) Constants.CARD_OFFSET.dp() else 0f },
    )

    val containerColor = when (department.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when (department.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (department.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.surfaceVariant
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(department.department.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )

            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickEdit(department.department.id) },
                content = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit action") }
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(department.department.id) { detectTapGestures(onDoubleTap = { onClickActions(department.department.id) }) }
                .padding(all = 3.dp)
        ) {
            Department(
                modifier = modifier.padding(Constants.CARDS_PADDING),
                viewModel = viewModel,
                department = department,
                onClickDetails = { onClickDetails(it) }
            )
        }
    }
}

@Composable
fun Department(
    modifier: Modifier = Modifier,
    viewModel: CompanyStructureViewModel = hiltViewModel(),
    department: DomainDepartmentComplete,
    onClickDetails: (Int) -> Unit = {},
    onClickProducts: (Int) -> Unit = {},
) {
    val containerColor = when (department.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    Column(
        modifier = Modifier
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            .padding(start = 4.dp, end = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp)
                    .weight(0.72f),
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderWithTitle(modifier = Modifier.weight(0.15f), titleWight = 0f, text = department.department.depOrder?.toString() ?: NoString.str)
                    Spacer(modifier = Modifier.height(4.dp))
                    HeaderWithTitle(modifier = Modifier.weight(0.85f), titleWight = 0.36f, title = "Department:", text = department.department.depAbbr ?: NoString.str)
                }
                ContentWithTitle(modifier = modifier, title = "Functions:", value = department.department.depOrganization ?: NoString.str, titleWight = 0.28f)
                Spacer(modifier = Modifier.height(4.dp))
            }
            TextButton(
                modifier = Modifier
                    .weight(weight = 0.28f)
                    .padding(start = 3.dp),
                onClick = { onClickProducts(department.department.id) },
                content = {
                    Text(
                        text = "Products",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                enabled = true,
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(4.dp),
                border = null,
                colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColorFor(containerColor))
            )
            IconButton(
                onClick = { onClickDetails(department.department.id) },
                modifier = Modifier
                    .weight(weight = 0.10f)
                    .padding(0.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (department.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (department.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }

        DepartmentDetails(
            modifier = modifier,
            viewModel = viewModel,
            department = department
        )
    }
}

@Composable
fun DepartmentDetails(
    modifier: Modifier = Modifier,
    viewModel: CompanyStructureViewModel,
    department: DomainDepartmentComplete
) {

    if (department.detailsVisibility) {
        Divider(modifier = modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        ContentWithTitle(modifier, title = "Complete name:", value = department.department.depName ?: NoString.str, titleWight = 0.25f)
        ContentWithTitle(modifier, title = "Dep. manager:", value = department.depManager.fullName, titleWight = 0.25f)
//        SubOrdersFlowColumn(modifier = Modifier, invModel = invModel, parentId = orderId)
    }
}