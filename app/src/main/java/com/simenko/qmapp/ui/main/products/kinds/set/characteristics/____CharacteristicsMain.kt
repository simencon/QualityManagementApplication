package com.simenko.qmapp.ui.main.products.kinds.set.characteristics

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
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.ui.common.InfoLine

@Composable
fun ComponentKindCharacteristicsMain(
    modifier: Modifier = Modifier,
    viewModel: ComponentKindCharacteristicsViewModel = hiltViewModel()
) {
    val componentKind by viewModel.productKind.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Product line", body = componentKind.productKind.productLine.manufacturingProject.projectSubject ?: NoString.str)
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Product", body = componentKind.productKind.productKind.productKindDesignation)
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Component", body = componentKind.componentKind.componentKindDescription)
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        CharGroups(viewModel = viewModel)
    }
}