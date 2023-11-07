package com.simenko.qmapp.ui.main.products

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
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
        composable(destination = Route.Main.Products.ProductLineAddEdit) {
        }
        composable(destination = Route.Main.Products.ProductLines.ProductLineKeys) {
            val viewModel: ProductLineKeysViewModel = hiltViewModel()
            ProductLineKeys(viewModel = viewModel)
        }
    }
}