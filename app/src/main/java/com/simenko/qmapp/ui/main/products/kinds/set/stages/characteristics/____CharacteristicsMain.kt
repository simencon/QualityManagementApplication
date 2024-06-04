package com.simenko.qmapp.ui.main.products.kinds.set.stages.characteristics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.simenko.qmapp.domain.entities.products.DomainComponentStageKind
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.navigation.Route

@Composable
fun ComponentStageKindCharacteristicsMain(
    modifier: Modifier = Modifier,
    viewModel: ComponentStageKindCharacteristicsViewModel = hiltViewModel(),
    route: Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindCharacteristics.ComponentStageKindCharacteristicsList
) {
    val componentKind by viewModel.productKind.collectAsStateWithLifecycle(DomainComponentStageKind.DomainComponentStageKindComplete())

    LaunchedEffect(Unit) { viewModel.onEntered(route) }

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(
            modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp),
            title = "Product line",
            body = componentKind.componentKind.productKind.productLine.manufacturingProject.projectSubject ?: NoString.str
        )
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Product", body = componentKind.componentKind.productKind.productKind.productKindDesignation)
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Component", body = componentKind.componentKind.componentKind.componentKindDescription)
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Component stage", body = componentKind.componentStageKind.componentStageDescription)
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        CharGroups(viewModel = viewModel)
    }
}