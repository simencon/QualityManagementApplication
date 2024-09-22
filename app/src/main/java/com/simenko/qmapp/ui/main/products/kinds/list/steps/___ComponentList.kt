package com.simenko.qmapp.ui.main.products.kinds.list.steps

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainProductComponent
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.main.products.kinds.list.ProductListViewModel
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings3

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentList(viewModel: ProductListViewModel = hiltViewModel()) {
    val versionsForItem by viewModel.versionsForItem.collectAsStateWithLifecycle()
    val items by viewModel.components.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setComponentsVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setComponentsVisibility(aId = SelectedNumber(it)) } }
    val onClickAddLambda = remember { { viewModel.onAddComponentClick() } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteComponentClick(it) } }
    val onClickEditLambda = remember<(ID) -> Unit> { { viewModel.onEditComponentClick(it) } }
    val onClickVersionsLambda = remember<(ID) -> Unit> { { viewModel.onVersionsClick(ComponentPref.char.toString() + it) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(2, true) }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow {
            items.forEach { item ->
                ComponentCard(
                    viewModel = viewModel,
                    versionsForItem = versionsForItem.str,
                    component = item,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                    onClickEdit = { onClickEditLambda(it) },
                    onClickDetails = { onClickDetailsLambda(it) },
                    onClickVersions = { onClickVersionsLambda(it) }
                )
            }
        }
        HorizontalDivider(modifier = Modifier.height(0.dp))
        FloatingActionButton(
            modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = { onClickAddLambda() },
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
        )
    }
}


@Composable
fun ComponentCard(
    viewModel: ProductListViewModel,
    versionsForItem: String,
    component: DomainProductComponent.DomainProductComponentComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (ID) -> Unit,
    onClickDetails: (ID) -> Unit,
    onClickVersions: (ID) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = DEFAULT_SPACE.dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = component,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = { onClickEdit(it.second) },
        contentColors = Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        Component(
            viewModel = viewModel,
            versionsForItem = versionsForItem,
            component = component,
            onClickDetails = { onClickDetails(it) },
            onClickVersions = { onClickVersions(it) }
        )
    }
}

@Composable
fun Component(
    viewModel: ProductListViewModel,
    versionsForItem: String,
    component: DomainProductComponent.DomainProductComponentComplete,
    onClickDetails: (ID) -> Unit = {},
    onClickVersions: (ID) -> Unit
) {
    val borderColor = if (versionsForItem == ComponentPref.char.toString() + component.component.component.component.id) MaterialTheme.colorScheme.outline else null
    val containerColor = if (component.isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.90f)) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
                    HeaderWithTitle(modifier = Modifier.weight(0.52f), titleWight = 0.35f, title = "Quantity:", text = component.productComponent.countOfComponents.toString() + " pcs")
                    StatusChangeBtn(
                        modifier = Modifier.weight(0.48f),
                        borderColor = borderColor,
                        containerColor = containerColor,
                        onClick = { onClickVersions(component.component.component.component.id) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.height(15.dp),
                                imageVector = Icons.Filled.Circle,
                                contentDescription = "Is filled",
                                tint = if (component.component.versions.isNotEmpty()) Color.Green else Color.Red,
                            )
                            Text(text = "Versions", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(imageVector = if (borderColor == null) Icons.AutoMirrored.Filled.NavigateNext else Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Show versions")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.2275f, title = "Component:", text = component.component.component.let { concatTwoStrings3(it.key.componentKey, it.component.componentDesignation) })
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(component.component.component.component.id) }) {
                Icon(
                    imageVector = if (component.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (component.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        ComponentDetails(viewModel = viewModel, component = component)
    }
}

@Composable
fun ComponentDetails(
    viewModel: ProductListViewModel,
    component: DomainProductComponent.DomainProductComponentComplete,
) {
    if (component.detailsVisibility) {
        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = 0.dp)) {
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
        }
        ComponentStageKindList(viewModel = viewModel)
    }
}