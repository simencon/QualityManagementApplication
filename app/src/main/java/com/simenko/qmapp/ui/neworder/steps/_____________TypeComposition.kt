package com.simenko.qmapp.ui.neworder.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.entities.DomainOrdersType
import com.simenko.qmapp.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.ui.neworder.NewItemViewModel
import com.simenko.qmapp.ui.theme.Primary
import com.simenko.qmapp.ui.theme.TertiaryContainer

@Composable
fun TypesSelection(
    modifier: Modifier = Modifier
) {
    val viewModel: NewItemViewModel = hiltViewModel()
    val gritState = rememberLazyGridState()

    val items by viewModel.orderTypes.collectAsStateWithLifecycle()
    val currentOrder by viewModel.currentOrderSF.collectAsStateWithLifecycle()

    val onSelectLambda = remember<(Int) -> Unit> { { viewModel.selectOrderType(it) } }

    LaunchedEffect(currentOrder) {
        gritState.scrollToSelectedItem(
            list = items.map {it.id }.toList(),
            selectedId = currentOrder.orderTypeId,
        )
    }

    items.let { it ->
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            state = gritState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.height(60.dp)
        ) {
            items(it.size) { item ->
                InvestigationTypeCard(
                    inputForOrder = it[item],
                    onClick = { onSelectLambda(it) }
                )
            }
        }
    }
}

@Composable
fun InvestigationTypeCard(
    inputForOrder: DomainOrdersType,
    onClick: (Int) -> Unit
) {
    val btnBackgroundColor = if (inputForOrder.isSelected) Primary else TertiaryContainer
    val btnContentColor = if (inputForOrder.isSelected) Color.White else Color.Black
    val btnColors = ButtonDefaults.buttonColors(
        contentColor = btnContentColor,
        containerColor = btnBackgroundColor
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            colors = btnColors,
            modifier = Modifier
                .width(224.dp)
                .height(56.dp),
            onClick = { onClick(inputForOrder.id) }
        ) {
            Text(
                text = inputForOrder.typeDescription ?: "-"
            )
        }
    }
}