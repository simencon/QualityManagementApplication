package com.simenko.qmapp.ui.neworder.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.DomainOrdersType
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.NewItemViewModel
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400

private fun filterAllAfterTypes(appModel: NewItemViewModel, type: DomainOrdersType) {
    appModel.filterWithOneParent(
        appModel.investigationReasonsMutable,
        appModel.investigationReasons,
        -1
    )
    appModel.filterWithOneParent(
        appModel.customersMutable,
        appModel.customers,
        0
    )
    appModel.filterWithOneParent(
        appModel.teamMembersMutable,
        appModel.teamMembers,
        0
    )
    appModel.selectSingleRecord(appModel.investigationTypesMutable, type)
}

@Composable
fun TypesSelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel,
    actionType: ActionType
) {
    val inputList by appModel.investigationTypesMediator.observeAsState()

    inputList?.apply {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.height(60.dp)
        ) {
            items(first!!.size) { item ->
                if (actionType == ActionType.EDIT_ORDER && first!![item].isSelected) {
                    filterAllAfterTypes(appModel, first!![item])
                }
                InvestigationTypeCard(
                    inputForOrder = first!![item],
                    modifier = modifier,
                    onClick = {
                        filterAllAfterTypes(appModel, it)
                    }
                )
            }
        }
    }
}

@Composable
fun InvestigationTypeCard(
    inputForOrder: DomainOrdersType,
    modifier: Modifier = Modifier,
    onClick: (DomainOrdersType) -> Unit
) {
    val btnBackgroundColor = if (inputForOrder.isSelected) Primary900 else StatusBar400
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
            onClick = { onClick(inputForOrder) }
        ) {
            Text(
                text = inputForOrder.typeDescription ?: "-"
            )
        }
    }
}