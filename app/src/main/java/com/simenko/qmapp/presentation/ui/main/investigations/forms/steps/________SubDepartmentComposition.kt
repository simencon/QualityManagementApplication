package com.simenko.qmapp.presentation.ui.main.investigations.forms.steps

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
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.presentation.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.presentation.ui.main.investigations.forms.ItemToSelect
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel

@Composable
fun SubDepartmentsSelection(
    modifier: Modifier = Modifier
) {
    val viewModel: NewItemViewModel = hiltViewModel()
    val gritState = rememberLazyGridState()

    val items by viewModel.subOrderSubDepartments.collectAsStateWithLifecycle()
    val currentSubOrder by viewModel.subOrder.collectAsStateWithLifecycle()

    val onSelectLambda = remember<(ID) -> Unit> { { viewModel.selectSubOrderSubDepartment(it) } }

    LaunchedEffect(items) {
        gritState.scrollToSelectedItem(
            list = items.map { it.id }.toList(),
            selectedId = currentSubOrder.subOrder.subDepartmentId,
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
            SubDepartmentCard(input = item, onClick = { onSelectLambda(it) })
        }
    }
}

@Composable
fun SubDepartmentCard(
    input: DomainSubDepartment,
    onClick: (ID) -> Unit
) {
    ItemToSelect(Triple(input.id, input.subDepDesignation ?: "-", input.isSelected), onClick)
}