package com.simenko.qmapp.ui.neworder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.neworder.steps.*

@Composable
fun SubOrderForm(
    modifier: Modifier = Modifier,
    subOrderStandAlone: Boolean
) {
    Box {
        Column(
            modifier.verticalScroll(rememberScrollState())
        ) {
            if (subOrderStandAlone) {
                ButtonsSection(title = R.string.select_reason) { ReasonsSelection(modifier = Modifier.padding(top = 0.dp)) }
            }
            ButtonsSection(title = R.string.select_department) { DepartmentsSelection(modifier = Modifier.padding(top = 0.dp)) }
            ButtonsSection(title = R.string.select_sub_department) { SubDepartmentsSelection(modifier = Modifier.padding(top = 0.dp)) }
            ButtonsSection(title = R.string.select_ordered_by) { SubOrderPlacersSelection(modifier = Modifier.padding(top = 0.dp)) }
            ButtonsSection(title = R.string.select_channel) { ChannelsSelection(modifier = Modifier.padding(top = 0.dp)) }
            ButtonsSection(title = R.string.select_line) { LinesSelection(modifier = Modifier.padding(top = 0.dp)) }
            ButtonsSection(title = R.string.select_item_type) { VersionsSelection(modifier = Modifier.padding(top = 0.dp)) }
            ButtonsSection(title = R.string.select_operation) { OperationsSelection(modifier = Modifier.padding(top = 0.dp)) }
            ButtonsSectionQuantity(title = R.string.select_quantity) { QuantitySelection(modifier = Modifier.padding(top = 0.dp)) }
            ButtonsSection(title = R.string.select_characteristics) { CharacteristicsSelection(modifier = Modifier.padding(top = 0.dp)) }
            Spacer(Modifier.height((16 + 56).dp))
        }
    }
}