package com.simenko.qmapp.presentation.ui.main.structure.steps

import android.annotation.SuppressLint
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
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation.DomainManufacturingOperationComplete
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.ContentWithTitle
import com.simenko.qmapp.presentation.ui.common.HeaderWithTitle
import com.simenko.qmapp.presentation.ui.common.ItemCard
import com.simenko.qmapp.presentation.ui.common.StatusChangeBtn
import com.simenko.qmapp.presentation.ui.main.structure.CompanyStructureViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Operations(viewModel: CompanyStructureViewModel = hiltViewModel()) {

    val linesVisibility by viewModel.linesVisibility.collectAsStateWithLifecycle()
    val items by viewModel.operations.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setOperationsVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setOperationsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteOperationClick(it) } }
    val onClickAddLambda = remember<(ID) -> Unit> { { viewModel.onAddOperationClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditOperationClick(it) } }
    val onClickProductsLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onOperationProductsClick(it) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(4, true) }

    FlowRow(horizontalArrangement = Arrangement.End, verticalArrangement = Arrangement.Center) {
        items.forEach { operation ->
            OperationCard(
                operation = operation,
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) },
                onClickDetails = { onClickDetailsLambda(it) },
                onClickProducts = { onClickProductsLambda(it) }
            )
        }
        HorizontalDivider(modifier = Modifier.height(0.dp))
        FloatingActionButton(
            modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = { onClickAddLambda(linesVisibility.first.num) },
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun OperationCard(
    operation: DomainManufacturingOperationComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickDetails: (ID) -> Unit,
    onClickProducts: (Pair<ID, ID>) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = operation,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        Operation(
            operation = operation,
            onClickDetails = { onClickDetails(it) },
            onClickProducts = { onClickProducts(it) }
        )
    }
}

@Composable
fun Operation(
    operation: DomainManufacturingOperationComplete,
    onClickDetails: (ID) -> Unit = {},
    onClickProducts: (Pair<ID, ID>) -> Unit
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(0.60f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = operation.operation.operationOrder.toString())
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.34f, title = "Equipment:", text = operation.operation.equipment.let { if (it.isNullOrEmpty()) NoString.str else it })
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(operation.operation.id) }) {
                Icon(
                    imageVector = if (operation.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (operation.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        OperationDetails(operation = operation, onClickProducts = onClickProducts)
    }
}

@Composable
fun OperationDetails(
    operation: DomainManufacturingOperationComplete,
    onClickProducts: (Pair<ID, ID>) -> Unit
) {
    val containerColor = when (operation.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.primaryContainer
    }

    if (operation.detailsVisibility) {
        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = 0.dp)) {
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Operation:", value = operation.operation.operationAbbr, titleWight = 0.23f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Comp. name:", value = operation.operation.operationDesignation, titleWight = 0.23f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Previous operation/operations:", value = EmptyString.str, titleWight = 0.99f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            operation.previousOperations.let {
                if (it.isEmpty())
                    ContentWithTitle(Modifier.padding(bottom = DEFAULT_SPACE.dp), title = EmptyString.str, value = NoString.str, titleWight = 0.01f)
                else
                    it.forEach { previousOperation ->
                        ContentWithTitle(
                            Modifier.padding(bottom = DEFAULT_SPACE.dp),
                            title = EmptyString.str,
                            contentTextSize = 12.sp,
                            value = "${previousOperation.depAbbr}/${previousOperation.subDepAbbr}/${previousOperation.channelAbbr}/${previousOperation.lineAbbr}/${previousOperation.equipment}(${previousOperation.operationAbbr})",
                            titleWight = 0.01f
                        )
                    }
            }

            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            Row(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(0.20f))
                Column(modifier = Modifier.weight(0.80f)) {
                    StatusChangeBtn(
                        modifier = Modifier.fillMaxWidth(), containerColor = containerColor, onClick = { onClickProducts(operation.operation.lineId to operation.operation.id) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Characteristics (functions)", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Product item kinds")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
        }
    }
}