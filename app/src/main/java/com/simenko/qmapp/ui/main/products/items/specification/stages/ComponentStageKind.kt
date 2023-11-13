package com.simenko.qmapp.ui.main.products.items.specification.stages

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NavigateNext
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainComponentStageKind
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.main.products.items.specification.ProductKindSpecificationViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentStageKinds(
    viewModel: ProductKindSpecificationViewModel = hiltViewModel()
) {
    val componentKindsVisibility by viewModel.componentKindsVisibility.collectAsStateWithLifecycle()
    val items by viewModel.componentStageKinds.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setComponentStageKindsVisibility(dId = SelectedNumber(it)) } }
    val onClickAddLambda = remember<(ID) -> Unit> { { viewModel.onAddComponentStageKindClick(it) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setComponentStageKindsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteComponentStageKindClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditComponentStageKindClick(it) } }
    val onClickKeysLambda = remember<(ID) -> Unit> { { viewModel.onComponentStageKindKeysClick(it) } }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow {
            items.forEach { item ->
                ComponentStageKindCard(
                    componentStageKind = item,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                    onClickEdit = { onClickEditLambda(it) },
                    onClickDetails = { onClickDetailsLambda(it) },
                    onClickKeys = { onClickKeysLambda(it) }
                )
            }
        }
        Divider(modifier = Modifier.height(0.dp))
        FloatingActionButton(
            modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onClick = { onClickAddLambda(componentKindsVisibility.first.num) },
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add characteristic") }
        )
    }
}

@Composable
fun ComponentStageKindCard(
    componentStageKind: DomainComponentStageKind.DomainComponentStageKindComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickDetails: (ID) -> Unit,
    onClickKeys: (ID) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = componentStageKind,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        ComponentStageKind(
            componentStageKind = componentStageKind,
            onClickDetails = onClickDetails,
            onClickKeys = onClickKeys
        )
    }
}

@Composable
fun ComponentStageKind(
    componentStageKind: DomainComponentStageKind.DomainComponentStageKindComplete,
    onClickDetails: (ID) -> Unit,
    onClickKeys: (ID) -> Unit
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.90f)) {
                HeaderWithTitle(titleWight = 0.48f, title = "Component stage number:", text = componentStageKind.componentStageKind.componentStageOrder.toString())
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = componentStageKind.componentStageKind.componentStageDescription)
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(componentStageKind.componentStageKind.id) }) {
                Icon(
                    imageVector = if (componentStageKind.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (componentStageKind.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        ComponentStageKindDetails(componentStageKind = componentStageKind, onClickKeys = onClickKeys)
    }
}

@Composable
fun ComponentStageKindDetails(
    componentStageKind: DomainComponentStageKind.DomainComponentStageKindComplete,
    onClickKeys: (ID) -> Unit
) {
    if (componentStageKind.detailsVisibility) {
        val containerColor = when (componentStageKind.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.primaryContainer
        }
        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = (DEFAULT_SPACE / 2).dp)) {
            Divider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
            Row(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(0.20f))
                Column(modifier = Modifier.weight(0.80f)) {
                    StatusChangeBtn(modifier = Modifier.fillMaxWidth(), containerColor = containerColor, onClick = { onClickKeys(componentStageKind.componentStageKind.id) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Component stage designations", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(imageVector = Icons.Filled.NavigateNext, contentDescription = "Show specification")
                        }
                    }
                }
            }
        }
    }
}