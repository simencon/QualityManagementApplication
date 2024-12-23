package com.simenko.qmapp.presentation.ui.main.products.kinds.set.stages.designations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainComponentStageKind
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.main.products.designations.KeyCard
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.presentation.ui.common.dialog.SingleChoiceDialog

@Composable
fun ComponentStageKindKeys(
    modifier: Modifier = Modifier,
    viewModel: ComponentStageKindKeysViewModel = hiltViewModel(),
    route: Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindKeys.ComponentStageKindKeysList
) {
    val componentStageKind by viewModel.componentStageKind.collectAsStateWithLifecycle(DomainComponentStageKind.DomainComponentStageKindComplete())
    val items by viewModel.componentStageKindKeys.collectAsStateWithLifecycle(listOf())

    val availableItems by viewModel.availableKeys.collectAsStateWithLifecycle(listOf())
    val isAddItemDialogVisible by viewModel.isAddItemDialogVisible.collectAsStateWithLifecycle()
    val searchString by viewModel.itemToAddSearchStr.collectAsStateWithLifecycle()

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setComponentStageKindKeysVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteComponentStageKindKeyClick(it) } }

    LaunchedEffect(Unit) { viewModel.onEntered(route = route) }

    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
    ) {
        if (isAddItemDialogVisible) SingleChoiceDialog(
            items = availableItems,
            addIsEnabled = availableItems.any { it.isSelected },
            onDismiss = { viewModel.setAddItemDialogVisibility(false) },
            searchString = searchString,
            onSearch = viewModel::setItemToAddSearchStr,
            onItemSelect = { viewModel.onItemSelect(it) },
            onAddClick = { viewModel.onAddItemClick() }
        )

        Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
            Spacer(modifier = Modifier.height(10.dp))
            InfoLine(
                modifier = modifier.padding(start = DEFAULT_SPACE.dp),
                title = "Product line",
                body = componentStageKind.componentKind.productKind.productLine.manufacturingProject.projectSubject ?: NoString.str
            )
            InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Product", body = componentStageKind.componentKind.productKind.productKind.productKindDesignation)
            InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Component", body = componentStageKind.componentKind.componentKind.componentKindDescription)
            InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Component stage", body = componentStageKind.componentStageKind.componentStageDescription)
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                items(items = items, key = { it.componentStageKindKey.id }) { key ->
                    KeyCard(
                        key = key.key,
                        onClickActions = { onClickActionsLambda(it) },
                        onClickDelete = { onClickDeleteLambda(it) },
                        actionButtonsImages = arrayOf(Icons.Filled.Delete),
                    )
                }
            }
        }
    }
}