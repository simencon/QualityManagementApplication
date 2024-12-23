package com.simenko.qmapp.presentation.ui.main.products.kinds.set.designations

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
import com.simenko.qmapp.domain.entities.products.DomainComponentKind
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.main.products.designations.KeyCard
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.presentation.ui.common.dialog.SingleChoiceDialog

@Composable
fun ComponentKindKeys(
    modifier: Modifier = Modifier,
    viewModel: ComponentKindKeysViewModel = hiltViewModel(),
    route: Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentKindKeys.ComponentKindKeysList
) {
    val componentKind by viewModel.componentKind.collectAsStateWithLifecycle(DomainComponentKind.DomainComponentKindComplete())
    val items by viewModel.componentKindKeys.collectAsStateWithLifecycle(listOf())

    val availableItems by viewModel.availableKeys.collectAsStateWithLifecycle(listOf())
    val isAddItemDialogVisible by viewModel.isAddItemDialogVisible.collectAsStateWithLifecycle()
    val searchString by viewModel.itemToAddSearchStr.collectAsStateWithLifecycle()

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setComponentKindKeysVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteComponentKindKeyClick(it) } }

    LaunchedEffect(Unit) { viewModel.onEntered(route) }

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
            InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Product line", body = componentKind.productKind.productLine.manufacturingProject.projectSubject ?: NoString.str)
            InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Product", body = componentKind.productKind.productKind.productKindDesignation)
            InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Component", body = componentKind.componentKind.componentKindDescription)
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                items(items = items, key = { it.componentKindKey.id }) { key ->
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