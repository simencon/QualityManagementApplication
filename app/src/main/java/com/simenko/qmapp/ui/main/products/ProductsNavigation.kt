package com.simenko.qmapp.ui.main.products

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.simenko.qmapp.ui.main.products.characteristics.CharacteristicsMain
import com.simenko.qmapp.ui.main.products.characteristics.CharacteristicsViewModel
import com.simenko.qmapp.ui.main.products.items.ProductKinds
import com.simenko.qmapp.ui.main.products.items.ProductKindsViewModel
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
    }
}