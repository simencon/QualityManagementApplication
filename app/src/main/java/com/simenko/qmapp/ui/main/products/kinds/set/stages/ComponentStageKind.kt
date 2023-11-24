package com.simenko.qmapp.ui.main.products.kinds.set.stages

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainComponentStageKind
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.main.products.kinds.set.ProductKindSpecificationViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentStageKinds(
    viewModel: ProductKindSpecificationViewModel = hiltViewModel()
) {
    val componentKindsVisibility by viewModel.componentKindsVisibility.collectAsStateWithLifecycle()
    val items by viewModel.componentStageKinds.collectAsStateWithLifecycle(listOf())

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
            onClickKeys = onClickKeys
        )
    }
}

@Composable
fun ComponentStageKind(
    componentStageKind: DomainComponentStageKind.DomainComponentStageKindComplete,
    onClickKeys: (ID) -> Unit
) {
    val containerColor = if (componentStageKind.isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer

    Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = DEFAULT_SPACE.dp, end = 0.dp, bottom = DEFAULT_SPACE.dp).animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(verticalAlignment = Alignment.Bottom) {
            HeaderWithTitle(modifier = Modifier.weight(0.55f), titleWight = 0.8f, title = "Component stage number:", text = componentStageKind.componentStageKind.componentStageOrder.toString())
            StatusChangeBtn(modifier = Modifier.weight(0.45f), containerColor = containerColor, onClick = { onClickKeys(componentStageKind.componentStageKind.id) }) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Designations", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Icon(imageVector = Icons.Filled.NavigateNext, contentDescription = "Show specification")
                }
            }
            Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
        }
        Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        HeaderWithTitle(modifier = Modifier.padding(end = DEFAULT_SPACE.dp), titleFirst = false, titleWight = 0f, text = componentStageKind.componentStageKind.componentStageDescription)
    }
}
