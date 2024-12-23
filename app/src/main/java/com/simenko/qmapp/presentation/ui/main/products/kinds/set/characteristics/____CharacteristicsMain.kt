package com.simenko.qmapp.presentation.ui.main.products.kinds.set.characteristics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.products.DomainComponentKind
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.common.dialog.CharacteristicChoiceDialog
import com.simenko.qmapp.navigation.Route

@Composable
fun ComponentKindCharacteristicsMain(
    modifier: Modifier = Modifier,
    viewModel: ComponentKindCharacteristicsViewModel = hiltViewModel(),
    route: Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentKindCharacteristics.ComponentKindCharacteristicsList
) {
    val componentKind by viewModel.componentKind.collectAsStateWithLifecycle(DomainComponentKind.DomainComponentKindComplete())

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
                modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp),
                title = "Product line",
                body = componentKind.productKind.productLine.manufacturingProject.projectSubject ?: NoString.str
            )
            InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Product", body = componentKind.productKind.productKind.productKindDesignation)
            InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Component", body = componentKind.componentKind.componentKindDescription)
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            CharGroups(viewModel = viewModel)
        }
    }
}