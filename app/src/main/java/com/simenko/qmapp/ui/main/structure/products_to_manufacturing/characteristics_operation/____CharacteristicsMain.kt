package com.simenko.qmapp.ui.main.structure.products_to_manufacturing.characteristics_operation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.dialog.CharacteristicChoiceDialog
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings

@Composable
fun OperationCharacteristicsMain(
    modifier: Modifier = Modifier,
    viewModel: OperationCharacteristicsViewModel = hiltViewModel(),
    route: Route.Main.CompanyStructure.OperationCharacteristics
) {
    val manufacturingOperation by viewModel.manufacturingOperation.collectAsStateWithLifecycle(DomainManufacturingOperation.DomainManufacturingOperationComplete())

    val isAddItemDialogVisible by viewModel.isAddItemDialogVisible.collectAsStateWithLifecycle()
    val charGroups by viewModel.charGroupsFilter.collectAsStateWithLifecycle(emptyList())
    val charSubGroups by viewModel.charSubGroupsFilter.collectAsStateWithLifecycle(emptyList())
    val items by viewModel.charsFilter.collectAsStateWithLifecycle(emptyList())
    val addIsEnabled by viewModel.isReadyToAdd.collectAsStateWithLifecycle(false)

    LaunchedEffect(Unit) { viewModel.onEntered(route) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
    ) {
        if (isAddItemDialogVisible) CharacteristicChoiceDialog(
            items = items,
            charGroups = charGroups,
            onSelectCharGroup = { viewModel.onSelectCharGroup(it) },
            charSubGroups = charSubGroups,
            onSelectCharSubGroup = { viewModel.onSelectCharSubGroup(it) },
            addIsEnabled = addIsEnabled,
            onDismiss = { viewModel.setAddItemDialogVisibility(false) },
            onItemSelect = { viewModel.onSelectChar(it) }
        ) {
            viewModel.onAddItemClick()
        }
        Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
            Spacer(modifier = Modifier.height(10.dp))
            InfoLine(
                modifier = modifier
                    .padding(start = DEFAULT_SPACE.dp)
                    .fillMaxWidth(), title = "Department", body = concatTwoStrings(manufacturingOperation.lineWithParents.depAbbr, manufacturingOperation.lineWithParents.depName)
            )
            InfoLine(
                modifier = modifier
                    .padding(start = DEFAULT_SPACE.dp)
                    .fillMaxWidth(), title = "Sub department", body = concatTwoStrings(manufacturingOperation.lineWithParents.subDepAbbr, manufacturingOperation.lineWithParents.subDepDesignation)
            )
            InfoLine(
                modifier = modifier
                    .padding(start = DEFAULT_SPACE.dp)
                    .fillMaxWidth(), title = "Channel", body = concatTwoStrings(manufacturingOperation.lineWithParents.channelAbbr, manufacturingOperation.lineWithParents.channelDesignation)
            )
            InfoLine(
                modifier = modifier
                    .padding(start = DEFAULT_SPACE.dp)
                    .fillMaxWidth(), title = "Line", body = concatTwoStrings(manufacturingOperation.lineWithParents.lineAbbr, manufacturingOperation.lineWithParents.lineDesignation)
            )
            InfoLine(
                modifier = modifier
                    .padding(start = DEFAULT_SPACE.dp)
                    .fillMaxWidth(), title = "Operation", body = concatTwoStrings(manufacturingOperation.operation.operationAbbr, manufacturingOperation.operation.operationDesignation)
            )
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            CharGroups(viewModel = viewModel)
        }
    }
}