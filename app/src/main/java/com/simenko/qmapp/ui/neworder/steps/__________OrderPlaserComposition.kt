package com.simenko.qmapp.ui.neworder.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.entities.DomainTeamMember
import com.simenko.qmapp.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.ui.neworder.ItemToSelect
import com.simenko.qmapp.ui.neworder.NewItemViewModel

@Composable
fun PlacersSelection(
    modifier: Modifier = Modifier
) {
    val viewModel: NewItemViewModel = hiltViewModel()
    val gritState = rememberLazyGridState()

    val items by viewModel.orderPlacers.collectAsStateWithLifecycle()
    val currentOrder by viewModel.currentOrderSF.collectAsStateWithLifecycle()

    val onSelectLambda = remember<(Int) -> Unit> { { viewModel.selectOrderPlacer(it) } }

    LaunchedEffect(currentOrder) {
        gritState.scrollToSelectedItem(
            list = items.map { it.id }.toList(),
            selectedId = currentOrder.orderedById,
        )
    }


    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        state = gritState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.height(60.dp)
    ) {
        items(items = items, key = { it.id }) { item ->
            InvestigationPlacerCard(
                inputForOrder = item,
                onClick = { onSelectLambda(it) }
            )
        }
    }
}

@Composable
fun InvestigationPlacerCard(
    inputForOrder: DomainTeamMember,
    onClick: (Int) -> Unit
) {
    ItemToSelect(Triple(inputForOrder.id, inputForOrder.fullName, inputForOrder.isSelected), onClick)
}