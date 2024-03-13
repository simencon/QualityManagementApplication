package com.simenko.qmapp.ui.main.products.kinds.set

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import com.simenko.qmapp.domain.entities.products.DomainComponentKind
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.main.products.kinds.set.stages.ComponentStageKinds

@Composable
fun ProductKindSpecification(
    modifier: Modifier = Modifier,
    viewModel: ProductKindSpecificationViewModel = hiltViewModel()
) {
    val product by viewModel.productKind.collectAsStateWithLifecycle()
    val items by viewModel.componentKinds.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setComponentKindsVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setComponentKindsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteComponentKindClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditComponentKindClick(it) } }
    val onClickKeysLambda = remember<(ID) -> Unit> { { viewModel.onComponentKindKeysClick(it) } }
    val onClickCharacteristicsLambda = remember<(ID) -> Unit> { { viewModel.onComponentKindCharacteristicsClick(it) } }

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }

    val listState = rememberLazyListState()

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Product line", body = product.productLine.manufacturingProject.projectSubject ?: NoString.str)
        InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Product", body = product.productKind.productKindDesignation)
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
            items(items = items, key = { it.componentKind.id }) { componentKind ->
                ComponentKindCard(
                    viewModel = viewModel,
                    componentKind = componentKind,
                    onClickActions = onClickActionsLambda,
                    onClickDelete = onClickDeleteLambda,
                    onClickEdit = onClickEditLambda,
                    onClickDetails = onClickDetailsLambda,
                    onClickKeys = onClickKeysLambda,
                    onClickCharacteristics = onClickCharacteristicsLambda
                )
            }
        }
    }
}


@Composable
fun ComponentKindCard(
    viewModel: ProductKindSpecificationViewModel,
    componentKind: DomainComponentKind.DomainComponentKindComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickDetails: (ID) -> Unit,
    onClickKeys: (ID) -> Unit,
    onClickCharacteristics: (ID) -> Unit,
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = componentKind,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        ComponentKind(
            viewModel = viewModel,
            componentKind = componentKind,
            onClickDetails = onClickDetails,
            onClickKeys = onClickKeys,
            onClickCharacteristics = onClickCharacteristics
        )
    }
}

@Composable
fun ComponentKind(
    viewModel: ProductKindSpecificationViewModel,
    componentKind: DomainComponentKind.DomainComponentKindComplete,
    onClickDetails: (ID) -> Unit = {},
    onClickKeys: (ID) -> Unit,
    onClickCharacteristics: (ID) -> Unit,
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.90f)) {
                HeaderWithTitle(titleWight = 0.35f, title = "Component number:", text = componentKind.componentKind.componentKindOrder.toString())
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = componentKind.componentKind.componentKindDescription)
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(componentKind.componentKind.id) }) {
                Icon(
                    imageVector = if (componentKind.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (componentKind.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        ComponentKindDetails(viewModel = viewModel, componentKind = componentKind, onClickKeys = onClickKeys, onClickCharacteristics = onClickCharacteristics)
    }
}

@Composable
fun ComponentKindDetails(
    viewModel: ProductKindSpecificationViewModel,
    componentKind: DomainComponentKind.DomainComponentKindComplete,
    onClickKeys: (ID) -> Unit,
    onClickCharacteristics: (ID) -> Unit,
) {
    if (componentKind.detailsVisibility) {
        val containerColor = if (componentKind.isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer

        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = 0.dp), horizontalAlignment = Alignment.End) {
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Row(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(0.30f))
                Column(modifier = Modifier.weight(0.70f)) {
                    StatusChangeBtn(modifier = Modifier.fillMaxSize(), containerColor = containerColor, onClick = { onClickKeys(componentKind.componentKind.id) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Designations", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Show designations")
                        }
                    }
                    StatusChangeBtn(modifier = Modifier.fillMaxSize(), containerColor = containerColor, onClick = { onClickCharacteristics(componentKind.componentKind.id) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Characteristics", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Show designations")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
        }
        ComponentStageKinds(viewModel = viewModel)
    }
}