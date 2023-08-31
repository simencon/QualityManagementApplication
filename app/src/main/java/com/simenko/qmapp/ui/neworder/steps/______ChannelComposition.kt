package com.simenko.qmapp.ui.neworder.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel
import com.simenko.qmapp.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.ui.neworder.*

@Composable
fun ChannelsSelection(
    modifier: Modifier = Modifier
) {
    val viewModel: NewItemViewModel = hiltViewModel()
    val gritState = rememberLazyGridState()

    val items by viewModel.subOrderChannels.collectAsStateWithLifecycle()
    val currentSubOrder by viewModel.subOrder.collectAsStateWithLifecycle()

    val onSelectLambda = remember<(Int) -> Unit> { { viewModel.selectSubOrderChannel(it) } }

    LaunchedEffect(items) {
        gritState.scrollToSelectedItem(
            list = items.map { it.id }.toList(),
            selectedId = currentSubOrder.subOrder.channelId,
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
            ChannelCard(input = item, onClick = { onSelectLambda(it) })
        }
    }
}

@Composable
fun ChannelCard(
    input: DomainManufacturingChannel,
    onClick: (Int) -> Unit
) {
    ItemToSelect(Triple(input.id, input.channelAbbr ?: "-", input.isSelected), onClick)
}