package com.simenko.qmapp.ui.main.products

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.simenko.qmapp.ui.main.products.characteristics.CharacteristicsMain
import com.simenko.qmapp.ui.main.products.characteristics.CharacteristicsViewModel
import com.simenko.qmapp.ui.main.products.items.ProductKinds
import com.simenko.qmapp.ui.main.products.items.ProductKindsViewModel
import com.simenko.qmapp.ui.main.products.items.keys.ProductKindKeys
import com.simenko.qmapp.ui.main.products.items.keys.ProductKindKeysViewModel
import com.simenko.qmapp.ui.main.products.items.list.ProductList
import com.simenko.qmapp.ui.main.products.items.list.ProductListViewModel
import com.simenko.qmapp.ui.main.products.items.specification.ProductKindSpecification
import com.simenko.qmapp.ui.main.products.items.specification.ProductKindSpecificationViewModel
import com.simenko.qmapp.ui.main.products.items.specification.keys.ComponentKindKeys
import com.simenko.qmapp.ui.main.products.items.specification.keys.ComponentKindKeysViewModel
import com.simenko.qmapp.ui.main.products.items.specification.stages.keys.ComponentStageKindKeys
import com.simenko.qmapp.ui.main.products.items.specification.stages.keys.ComponentStageKindKeysViewModel
import com.simenko.qmapp.ui.main.products.keys.ProductLineKeys
import com.simenko.qmapp.ui.main.products.keys.ProductLineKeysViewModel
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.composable
import com.simenko.qmapp.ui.navigation.navigation

fun NavGraphBuilder.productsNavigation(mainScreenPadding: PaddingValues) {
    navigation(startDestination = Route.Main.Products.ProductLines) {
        composable(destination = Route.Main.Products.ProductLines) {
            val viewModel: ProductsViewModel = hiltViewModel()
            ProductLines(viewModel = viewModel)
        }
        composable(destination = Route.Main.Products.ProductLines.ProductLineKeys) {
            val viewModel: ProductLineKeysViewModel = hiltViewModel()
            ProductLineKeys(viewModel = viewModel)
        }
        composable(destination = Route.Main.Products.ProductLines.Characteristics) {
            val viewModel: CharacteristicsViewModel = hiltViewModel()
            CharacteristicsMain(mainScreenPadding = mainScreenPadding ,viewModel = viewModel)
        }
        composable(destination = Route.Main.Products.ProductLines.ProductKinds) {
            val viewModel: ProductKindsViewModel = hiltViewModel()
            ProductKinds(viewModel = viewModel)
        }
        composable(destination = Route.Main.Products.ProductLines.ProductKinds.ProductKindKeys) {
            val viewModel: ProductKindKeysViewModel = hiltViewModel()
            ProductKindKeys(viewModel = viewModel)
        }
        composable(destination = Route.Main.Products.ProductLines.ProductKinds.ProductSpecification) {
            val viewModel: ProductKindSpecificationViewModel = hiltViewModel()
            ProductKindSpecification(viewModel = viewModel)
        }
        composable(destination = Route.Main.Products.ProductLines.ProductKinds.ProductList) {
            val viewModel: ProductListViewModel = hiltViewModel()
            ProductList(viewModel = viewModel)
        }
        composable(destination = Route.Main.Products.ProductLines.ProductKinds.ProductSpecification.ComponentKindKeys) {
            val viewModel: ComponentKindKeysViewModel = hiltViewModel()
            ComponentKindKeys(viewModel = viewModel)
        }
        composable(destination = Route.Main.Products.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindKeys) {
            val viewModel: ComponentStageKindKeysViewModel = hiltViewModel()
            ComponentStageKindKeys(viewModel = viewModel)
        }
    }
}