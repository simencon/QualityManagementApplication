package com.simenko.qmapp.ui.neworder.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.DomainDepartment
import com.simenko.qmapp.domain.DomainTeamMember
import com.simenko.qmapp.ui.neworder.NewItemViewModel
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400

@Composable
fun PlacersSelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel,
    parentId: Int
) {
    val observeInputForOrder by appModel.teamMembersMediator.observeAsState()
    val inputList = arrayListOf<DomainTeamMember>()

    observeInputForOrder?.apply {
        if (observeInputForOrder!!.first != null) {
            inputList.clear()

            observeInputForOrder!!.first!!.filter { it.id > parentId }.forEach { input ->
                if (inputList.find { it.id == input.id } == null) {
                    inputList.add(input)
                }
            }
        }
    }

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.height(60.dp)
    ) {
        items(inputList.size) { item ->
            InvestigationPlacerCard(inputList[item], modifier)
        }
    }
}

@Composable
fun InvestigationPlacerCard(
    inputForOrder: DomainTeamMember,
    modifier: Modifier = Modifier
) {

    var checked by rememberSaveable { mutableStateOf(false) }
    val btnBackgroundColor = if (checked) Primary900 else StatusBar400
    val btnContentColor = if (checked) Color.White else Color.Black
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
            onClick = {
                checked = !checked
            }
        ) {
            Text(
                text = inputForOrder.fullName ?: "-"
            )
        }
    }
}