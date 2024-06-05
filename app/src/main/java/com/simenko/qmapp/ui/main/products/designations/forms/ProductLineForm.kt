package com.simenko.qmapp.ui.main.products.designations.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.entities.products.DomainProductLine
import com.simenko.qmapp.ui.navigation.Route

@Composable
fun ProductLineForm(
    modifier: Modifier = Modifier,
    viewModel: ProductLineViewModel,
    route: Route.Main.ProductLines.ProductLineKeys.AddEditProductLineKey
) {
    val productLine by viewModel.productLine.collectAsStateWithLifecycle(DomainProductLine())
}