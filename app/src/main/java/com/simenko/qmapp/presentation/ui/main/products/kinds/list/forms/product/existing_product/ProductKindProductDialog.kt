package com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.product.existing_product

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.presentation.ui.common.dialog.ProductSingleChoiceDialog
import com.simenko.qmapp.navigation.Route

@Composable
fun ProductKindProductDialog(viewModel: ProductKindProductViewModel, route: Route.Main.ProductLines.ProductKinds.Products.AddProductKindProduct) {

    val productKinds by viewModel.availableProductKinds.collectAsStateWithLifecycle(initialValue = emptyList())
    val designations by viewModel.availableDesignations.collectAsStateWithLifecycle(initialValue = emptyList())
    val searchValue by viewModel.searchValue.collectAsStateWithLifecycle()
    val items by viewModel.availableProducts.collectAsStateWithLifecycle(initialValue = emptyList())
    val isReadyToAdd by viewModel.isReadyToAdd.collectAsStateWithLifecycle(initialValue = false)


    LaunchedEffect(key1 = Unit) {
        viewModel.onEntered(route)
    }

    ProductSingleChoiceDialog(
        items = items,
        productLines = productKinds,
        onSelectProductLine = viewModel::onSelectProductKind,
        designations = designations,
        onSelectDesignation = viewModel::onSelectDesignation,
        searchString = searchValue,
        onSearch = viewModel::onChangeSearchValue,
        addIsEnabled = isReadyToAdd,
        onDismiss = viewModel::navBack,
        onItemSelect = viewModel::onSelectProduct,
        onAddClick = viewModel::makeRecord
    )
}