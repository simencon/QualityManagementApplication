package com.simenko.qmapp.presentation.ui.main.products.kinds.list.steps

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
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.ComponentStagePref
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainComponentComponentStage
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.HeaderWithTitle
import com.simenko.qmapp.presentation.ui.common.ItemCard
import com.simenko.qmapp.presentation.ui.common.StatusChangeBtn
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.ProductListViewModel
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings3

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentStageList(viewModel: ProductListViewModel = hiltViewModel()) {
    val versionsForItem by viewModel.versionsForItem.collectAsStateWithLifecycle()
    val items by viewModel.componentStages.collectAsStateWithLifecycle(listOf())

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setComponentStagesVisibility(aId = SelectedNumber(it)) } }
    val onClickAddLambda = remember { { viewModel.onAddComponentStageClick() } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteComponentStageClick(it) } }
    val onClickEditLambda = remember<(ID) -> Unit> { { viewModel.onEditComponentStageClick(it) } }
    val onClickVersionsLambda = remember<(ID) -> Unit> { { viewModel.onVersionsClick(ComponentStagePref.char.toString() + it); viewModel.setComponentStagesVisibility(dId = SelectedNumber(it)) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(4, true) }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow {
            items.forEach { item ->
                ComponentStageCard(
                    versionsForItem = versionsForItem.str,
                    componentStage = item,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                    onClickEdit = { onClickEditLambda(it) },
                    onClickVersions = { onClickVersionsLambda(it) }
                )
            }
        }
        HorizontalDivider(modifier = Modifier.height(0.dp))
        FloatingActionButton(
            modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onClick = { onClickAddLambda() },
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
        )
    }
}


@Composable
fun ComponentStageCard(
    versionsForItem: String,
    componentStage: DomainComponentComponentStage.DomainComponentComponentStageComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (ID) -> Unit,
    onClickVersions: (ID) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = DEFAULT_SPACE.dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = componentStage,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = { onClickEdit(it.second) },
        contentColors = Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        ComponentStage(
            versionsForItem = versionsForItem,
            componentStage = componentStage,
            onClickVersions = { onClickVersions(it) }
        )
    }
}

@Composable
fun ComponentStage(
    versionsForItem: String,
    componentStage: DomainComponentComponentStage.DomainComponentComponentStageComplete,
    onClickVersions: (ID) -> Unit
) {
    val borderColor = if (versionsForItem == ComponentStagePref.char.toString() + componentStage.componentComponentStage.stageKindStageId) MaterialTheme.colorScheme.outline else null
    val containerColor = if (componentStage.isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
                    HeaderWithTitle(
                        modifier = Modifier.weight(0.54f),
                        titleWight = 0.5f,
                        title = "Quantity:",
                        text = "${componentStage.componentComponentStage.quantity} ${componentStage.componentStage.componentStageKind.quantityUnits}"
                    )
                    StatusChangeBtn(
                        modifier = Modifier.weight(0.46f),
                        borderColor = borderColor,
                        containerColor = containerColor,
                        onClick = { onClickVersions(componentStage.componentStage.componentStage.componentStage.id) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.height(15.dp),
                                imageVector = Icons.Filled.Circle,
                                contentDescription = "Is filled",
                                tint = if (componentStage.componentStage.versions.isNotEmpty()) Color.Green else Color.Red,
                            )
                            Text(text = "Versions", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(imageVector = if (borderColor == null) Icons.AutoMirrored.Filled.NavigateNext else Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Show versions")
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
