package com.simenko.qmapp.ui.main.products.items.list

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
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainComponentComponentStage
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings3

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentStageList(viewModel: ProductListViewModel = hiltViewModel()) {
    val componentsVisibility by viewModel.componentsVisibility.collectAsStateWithLifecycle()
    val componentStageKindsVisibility by viewModel.componentStageKindsVisibility.collectAsStateWithLifecycle()
    val items by viewModel.componentStages.collectAsStateWithLifecycle(listOf())

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setComponentStagesVisibility(aId = SelectedNumber(it)) } }
    val onClickAddLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onAddComponentStageClick(it) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteComponentStageClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditComponentStageClick(it) } }
    val onClickVersionsLambda = remember<(ID) -> Unit> { { viewModel.onComponentStageVersionsClick(it) } }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow {
            items.forEach { item ->
                ComponentStageCard(
                    componentStage = item,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                    onClickEdit = { onClickEditLambda(it) },
                    onClickVersions = { onClickVersionsLambda(it) }
                )
            }
        }
        Divider(modifier = Modifier.height(0.dp))
        FloatingActionButton(
            modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onClick = { onClickAddLambda(Pair(componentsVisibility.first.num, componentStageKindsVisibility.first.num)) },
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
        )
    }
}


@Composable
fun ComponentStageCard(
    componentStage: DomainComponentComponentStage.DomainComponentComponentStageComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickVersions: (ID) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = DEFAULT_SPACE.dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = componentStage,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        ComponentStage(
            componentStage = componentStage,
            onClickVersions = { onClickVersions(it) }
        )
    }
}

@Composable
fun ComponentStage(
    componentStage: DomainComponentComponentStage.DomainComponentComponentStageComplete,
    onClickVersions: (ID) -> Unit
) {
    val containerColor = when (componentStage.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    HeaderWithTitle(modifier = Modifier.weight(0.65f), titleWight = 0.5f, title = "Quantity:", text = NoString.str)
                    StatusChangeBtn(modifier = Modifier.weight(0.35f), containerColor = containerColor, onClick = { onClickVersions(componentStage.componentComponentStage.componentStageId) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Versions", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(imageVector = Icons.Filled.NavigateNext, contentDescription = "Show versions")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(
                    titleWight = 0.325f,
                    title = "Component stage:",
                    text = componentStage.componentStage.componentStage.let { concatTwoStrings3(it.key.componentKey, it.componentStage.componentInStageDescription) }
                )
            }
        }
    }
}
