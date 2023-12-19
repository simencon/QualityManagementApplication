package com.simenko.qmapp.ui.main.products

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.simenko.qmapp.ui.main.products.characteristics.CharacteristicsMain
import com.simenko.qmapp.ui.main.products.characteristics.CharacteristicsViewModel
import com.simenko.qmapp.ui.main.products.kinds.ProductKinds
import com.simenko.qmapp.ui.main.products.kinds.ProductKindsViewModel
import com.simenko.qmapp.ui.main.products.kinds.designations.ProductKindKeys
import com.simenko.qmapp.ui.main.products.kinds.designations.ProductKindKeysViewModel
import com.simenko.qmapp.ui.main.products.kinds.list.steps.ProductKindProducts
import com.simenko.qmapp.ui.main.products.kinds.list.ProductListViewModel
import com.simenko.qmapp.ui.main.products.kinds.set.ProductKindSpecification
import com.simenko.qmapp.ui.main.products.kinds.set.ProductKindSpecificationViewModel
import com.simenko.qmapp.ui.main.products.kinds.set.designations.ComponentKindKeys
import com.simenko.qmapp.ui.main.products.kinds.set.designations.ComponentKindKeysViewModel
import com.simenko.qmapp.ui.main.products.kinds.set.stages.designations.ComponentStageKindKeys
import com.simenko.qmapp.ui.main.products.kinds.set.stages.designations.ComponentStageKindKeysViewModel
import com.simenko.qmapp.ui.main.products.designations.ProductLineKeys
import com.simenko.qmapp.ui.main.products.designations.ProductLineKeysViewModel
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
            ProductKindProducts(mainScreenPadding = mainScreenPadding, viewModel = viewModel)
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