package com.simenko.qmapp.ui.main.structure.products_to_manufacturing.items_line

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.ComponentStagePref
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainManufacturingLine
import com.simenko.qmapp.domain.entities.products.DomainComponent
import com.simenko.qmapp.domain.entities.products.DomainComponentStage
import com.simenko.qmapp.domain.entities.products.DomainProduct
import com.simenko.qmapp.other.Constants.BOTTOM_ITEM_HEIGHT
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.dialog.SingleChoiceDialog
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings

@Composable
fun LineItems(
    modifier: Modifier = Modifier,
    viewModel: LineItemsViewModel,
    route: Route.Main.CompanyStructure.LineItems
) {
    val line by viewModel.line.collectAsStateWithLifecycle(DomainManufacturingLine.DomainManufacturingLineComplete())
    val itemKindPref by viewModel.itemKindPref.collectAsStateWithLifecycle()

    val productItems by viewModel.productItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val componentItems by viewModel.componentItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val stageItems by viewModel.stageItems.collectAsStateWithLifecycle(initialValue = emptyList())

    val availableItemKinds by viewModel.availableItems.collectAsStateWithLifecycle(listOf())

    val isAddItemDialogVisible by viewModel.isAddItemDialogVisible.collectAsStateWithLifecycle()
    val searchString by viewModel.itemToAddSearchStr.collectAsStateWithLifecycle()

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setItemsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteItem(it) } }

    LaunchedEffect(Unit) { viewModel.onEntered(route = route) }

    val listState = rememberLazyListState()

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(), title = "Department", body = concatTwoStrings(line.channelWithParents.depAbbr, line.channelWithParents.depName)
        )
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(), title = "Sub department", body = concatTwoStrings(line.channelWithParents.subDepAbbr, line.channelWithParents.subDepDesignation)
        )
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(), title = "Channel", body = concatTwoStrings(line.channelWithParents.channelAbbr, line.channelWithParents.channelDesignation)
        )
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(), title = "Line", body = concatTwoStrings(line.line.lineAbbr, line.line.lineDesignation)
        )
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)

        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(all = (DEFAULT_SPACE / 2).dp),
            state = listState
        ) {
            when (itemKindPref) {
                ProductPref.char -> {
                    items(items = productItems, key = { it.product.id }) { item ->
                        ProductItemCard(
                            productKind = item,
                            onClickActions = { onClickActionsLambda(it) },
                            onClickDelete = { onClickDeleteLambda(it) },
                        )
                    }
                }

                ComponentPref.char -> {
                    items(items = componentItems, key = { it.component.id }) { item ->
                        ComponentItemCard(
                            componentItem = item,
                            onClickActions = { onClickActionsLambda(it) },
                            onClickDelete = { onClickDeleteLambda(it) },
                        )
                    }
                }

                ComponentStagePref.char -> {
                    items(items = stageItems, key = { it.componentStage.id }) { item ->
                        StageItemCard(
                            stageItem = item,
                            onClickActions = { onClickActionsLambda(it) },
                            onClickDelete = { onClickDeleteLambda(it) },
                        )
                    }
                }
            }
            if (!(productItems.isEmpty() && componentItems.isEmpty() && stageItems.isEmpty())) {
                item {
                    Spacer(modifier = Modifier.height(BOTTOM_ITEM_HEIGHT.dp))
                }
            }
        }
    }

    if (isAddItemDialogVisible) {
        SingleChoiceDialog(
            items = availableItemKinds,
            addIsEnabled = availableItemKinds.any { it.getIsSelected() },
            onDismiss = { viewModel.setAddItemDialogVisibility(false) },
            searchString = searchString,
            onSearch = viewModel::setItemToAddSearchStr,
            onItemSelect = { viewModel.onItemSelect(it) },
            onAddClick = { viewModel.onAddItem() }
        )
    }
}

@Composable
fun ProductItemCard(
    productKind: DomainProduct.DomainProductComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = productKind,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete),
    ) {
        ProductItem(
            productItem = productKind,
        )
    }
}

@Composable
fun ProductItem(
    productItem: DomainProduct.DomainProductComplete,
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.54f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = productItem.key.componentKey)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = EmptyString.str, value = productItem.product.productDesignation)
            }
        }
    }
}

@Composable
fun ComponentItemCard(
    componentItem: DomainComponent.DomainComponentComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = componentItem,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        contentColors = Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete),
    ) {
        ComponentItem(
            componentItem = componentItem,
        )
    }
}

@Composable
fun ComponentItem(
    componentItem: DomainComponent.DomainComponentComplete,
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.54f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = componentItem.key.componentKey)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = EmptyString.str, value = componentItem.component.componentDesignation)
            }
        }
    }
}

@Composable
fun StageItemCard(
    stageItem: DomainComponentStage.DomainComponentStageComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = stageItem,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        contentColors = Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete),
    ) {
        StageItem(
            stageItem = stageItem,
        )
    }
}

@Composable
fun StageItem(
    stageItem: DomainComponentStage.DomainComponentStageComplete,
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.54f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = stageItem.key.componentKey)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = EmptyString.str, value = stageItem.componentStage.componentInStageDescription)
            }
        }
    }
}