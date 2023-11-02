package com.simenko.qmapp.ui.main.products

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.composable
import com.simenko.qmapp.ui.navigation.navigation

fun NavGraphBuilder.productsNavigation(mainScreenPadding: PaddingValues) {
    navigation(startDestination = Route.Main.Products.ProductsView) {
        composable(destination = Route.Main.Products.ProductsView) {
            val viewModel: ProductsViewModel = hiltViewModel()
        }
        composable(destination = Route.Main.Products.ProductProjectAddEdit) {
        }
        composable(destination = Route.Main.Products.ProductKindAddEdit) {
        }
        composable(destination = Route.Main.Products.ComponentKindAddEdit) {
        }
        composable(destination = Route.Main.Products.ComponentStageKindAddEdit) {
        }
    }
}