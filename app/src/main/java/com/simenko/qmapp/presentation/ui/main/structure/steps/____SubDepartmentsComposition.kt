package com.simenko.qmapp.presentation.ui.main.structure.steps

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.ContentWithTitle
import com.simenko.qmapp.presentation.ui.common.HeaderWithTitle
import com.simenko.qmapp.presentation.ui.common.ItemCard
import com.simenko.qmapp.presentation.ui.common.StatusChangeBtn
import com.simenko.qmapp.presentation.ui.main.structure.CompanyStructureViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubDepartments(viewModel: CompanyStructureViewModel = hiltViewModel()) {

    val departmentVisibility by viewModel.departmentsVisibility.collectAsStateWithLifecycle()
    val items by viewModel.subDepartments.collectAsStateWithLifecycle(listOf())

    val onClickAddLambda = remember<(ID) -> Unit> { { viewModel.onAddSubDepartmentClick(it) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setSubDepartmentsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteSubDepartmentClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditSubDepartmentClick(it) } }
    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setSubDepartmentsVisibility(dId = SelectedNumber(it)) } }
    val onClickProductsLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onSubDepartmentProductsClick(it) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(1, true) }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow {
            items.forEach { subDepartment ->
                SubDepartmentCard(
                    viewModel = viewModel,
                    subDepartment = subDepartment,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                    onClickEdit = { onClickEditLambda(it) },
                    onClickDetails = { onClickDetailsLambda(it) },
                    onClickProducts = { onClickProductsLambda(it) }
                )
            }
        }
        HorizontalDivider(modifier = Modifier.height(0.dp))
        FloatingActionButton(
            modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = { onClickAddLambda(departmentVisibility.first.num) },
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
        )
    }
}

@Composable
fun SubDepartmentCard(
    viewModel: CompanyStructureViewModel,
    subDepartment: DomainSubDepartment,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickDetails: (ID) -> Unit,
    onClickProducts: (Pair<ID, ID>) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = subDepartment,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        SubDepartment(
            viewModel = viewModel,
            subDepartment = subDepartment,
            onClickDetails = { onClickDetails(it) },
            onClickProducts = { onClickProducts(it) }
        )
    }
}

@Composable
fun SubDepartment(
    viewModel: CompanyStructureViewModel,
    subDepartment: DomainSubDepartment,
    onClickDetails: (ID) -> Unit = {},
    onClickProducts: (Pair<ID, ID>) -> Unit
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(0.90f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = subDepartment.subDepOrder?.toString() ?: NoString.str)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.39f, title = "Sub department:", text = subDepartment.subDepAbbr ?: NoString.str)
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(subDepartment.id) }) {
                Icon(
                    imageVector = if (subDepartment.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (subDepartment.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        SubDepartmentDetails(viewModel = viewModel, subDepartment = subDepartment, onClickProducts = onClickProducts)
    }
}

@Composable
fun SubDepartmentDetails(
    viewModel: CompanyStructureViewModel,
    subDepartment: DomainSubDepartment,
    onClickProducts: (Pair<ID, ID>) -> Unit
) {
    val containerColor = when (subDepartment.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.primaryContainer
    }

    if (subDepartment.detailsVisibility) {
        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp)) {
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Complete name:", value = subDepartment.subDepDesignation ?: NoString.str, titleWight = 0.26f)

            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            Row(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(0.30f))
                Column(modifier = Modifier.weight(0.70f)) {
                    StatusChangeBtn(
                        modifier = Modifier.fillMaxWidth(), containerColor = containerColor, onClick = { onClickProducts(subDepartment.depId to subDepartment.id) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Product item kinds", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Product item kinds")
                        }
                    }
                }
            }
        }
        Channels(viewModel = viewModel)
    }
}