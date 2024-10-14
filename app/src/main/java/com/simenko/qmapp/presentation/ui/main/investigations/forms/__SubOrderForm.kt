package com.simenko.qmapp.presentation.ui.main.investigations.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.ButtonsSectionQuantity
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.ChannelsSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.CharacteristicsSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.DepartmentsSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.LinesSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.OperationsSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.QuantitySelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.ReasonsSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.SubDepartmentsSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.SubOrderPlacersSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.VersionsSelection

@Composable
fun SubOrderForm(
    modifier: Modifier = Modifier,
    viewModel: NewItemViewModel = hiltViewModel(),
    isPcOnly: Boolean, orderId: ID, subOrderId: ID
) {
    LaunchedEffect(Unit) { viewModel.onEntered(isPcOnly, orderId, subOrderId) }

    Box {
        Column(
            modifier.verticalScroll(rememberScrollState())
        ) {
            if (isPcOnly) {
                ButtonsSection(title = R.string.select_reason) { ReasonsSelection(modifier = Modifier.padding(top = 0.dp), isPcOnly = isPcOnly) }
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