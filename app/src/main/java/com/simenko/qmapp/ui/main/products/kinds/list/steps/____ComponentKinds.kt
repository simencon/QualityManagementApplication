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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainComponentKind
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.main.products.kinds.list.ProductListViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentKindList(viewModel: ProductListViewModel = hiltViewModel()) {
    val items by viewModel.componentKinds.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setComponentKindsVisibility(dId = SelectedNumber(it)) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(1, true) }

    FlowRow(modifier = Modifier.padding(bottom = (DEFAULT_SPACE / 2).dp), horizontalArrangement = Arrangement.End, verticalArrangement = Arrangement.Center) {
        items.forEach { item ->
            ComponentKindCard(
                viewModel = viewModel,
                componentKind = item,
                onClickDetails = { onClickDetailsLambda(it) }
            )
        }
    }
}

@Composable
fun ComponentKindCard(
    viewModel: ProductListViewModel,
    componentKind: DomainComponentKind.DomainComponentKindComplete,
    onClickDetails: (ID) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = DEFAULT_SPACE.dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = componentKind,
        onClickActions = {},
        onClickDelete = {},
        onClickEdit = {},
        contentColors = Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = emptyArray(),
    ) {
        ComponentKind(
            viewModel = viewModel,
            componentKind = componentKind,
            onClickDetails = { onClickDetails(it) }
        )
    }
}

@Composable
fun ComponentKind(
    viewModel: ProductListViewModel,
    componentKind: DomainComponentKind.DomainComponentKindComplete,
    onClickDetails: (ID) -> Unit = {}
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.90f)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        modifier = Modifier.height(15.dp),
                        imageVector = Icons.Filled.Circle,
                        contentDescription = "Is filled",
                        tint = if (componentKind.hasComponents) Color.Green else Color.Red,
                    )
                    Spacer(modifier = Modifier.width((DEFAULT_SPACE * 2).dp))
                    HeaderWithTitle(modifier = Modifier.weight(0.85f), titleWight = 0.39f, title = "Component number:", text = componentKind.componentKind.componentKindOrder.toString())
                }

                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(modifier = Modifier.fillMaxWidth(), titleFirst = false, titleWight = 0f, text = componentKind.componentKind.componentKindDescription)
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(componentKind.componentKind.id) }) {
                Icon(
                    imageVector = if (componentKind.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (componentKind.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        ComponentKindDetails(viewModel = viewModel, product = componentKind)
    }
}

@Composable
fun ComponentKindDetails(
    viewModel: ProductListViewModel,
    product: DomainComponentKind.DomainComponentKindComplete
) {
    if (product.detailsVisibility) {
        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = 0.dp)) {
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
        }
        ComponentList(viewModel = viewModel)
    }
}