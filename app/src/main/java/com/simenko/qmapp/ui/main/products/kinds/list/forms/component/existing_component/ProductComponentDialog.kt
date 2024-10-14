package com.simenko.qmapp.ui.main.products.kinds.list.forms.component.existing_component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.ui.common.dialog.ComponentSingleChoiceDialog
import com.simenko.qmapp.ui.common.dialog.ItemSelectEnum
import com.simenko.qmapp.ui.navigation.Route

@Composable
fun ProductComponentDialog(viewModel: ProductComponentViewModel, route: Route.Main.ProductLines.ProductKinds.Products.AddProductComponent) {

    val designations by viewModel.availableDesignations.collectAsStateWithLifecycle(initialValue = emptyList())
    val products by viewModel.availableProducts.collectAsStateWithLifecycle(initialValue = emptyList())

    val searchValue by viewModel.searchValue.collectAsStateWithLifecycle()
    val items by viewModel.availableComponents.collectAsStateWithLifecycle(initialValue = emptyList())
    val quantityInProduct by viewModel.quantityInProduct.collectAsStateWithLifecycle()
    val isReadyToAdd by viewModel.isReadyToAdd.collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(key1 = Unit) {
        viewModel.onEntered(route)
    }

    ComponentSingleChoiceDialog(
        selectionOf = ItemSelectEnum.COMPONENT,
        items = items,
        designations = designations,
        onSelectDesignation = viewModel::onSelectDesignation,
        products = products,
        onSelectProduct = viewModel::onSelectProductKind,
        searchString = searchValue,
        onSearch = viewModel::onChangeSearchValue,
        quantity = quantityInProduct,
        onEnterQuantity = viewModel::onSetProductComponentQuantity,
        addIsEnabled = isReadyToAdd,
        onDismiss = {  viewModel.navBack() },
        onItemSelect = viewModel::onSelectComponent,
        onAddClick = { viewModel.makeComponentKindComponent() }
    )
}