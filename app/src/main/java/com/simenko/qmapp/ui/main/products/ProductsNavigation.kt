package com.simenko.qmapp.ui.main.products

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.simenko.qmapp.ui.main.products.characteristics.CharacteristicsMain
import com.simenko.qmapp.ui.main.products.characteristics.CharacteristicsViewModel
import com.simenko.qmapp.ui.main.products.characteristics.forms.sub_group.CharSubGroupViewModel
import com.simenko.qmapp.ui.main.products.characteristics.forms.sub_group.CharacteristicSubGroupForm
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
import com.simenko.qmapp.ui.main.products.kinds.characteristics.ProductKindCharacteristicsMain
import com.simenko.qmapp.ui.main.products.kinds.characteristics.ProductKindCharacteristicsViewModel
import com.simenko.qmapp.ui.main.products.kinds.list.versions.VersionTolerances
import com.simenko.qmapp.ui.main.products.kinds.list.versions.VersionTolerancesViewModel
import com.simenko.qmapp.ui.main.products.kinds.set.characteristics.ComponentKindCharacteristicsMain
import com.simenko.qmapp.ui.main.products.kinds.set.characteristics.ComponentKindCharacteristicsViewModel
import com.simenko.qmapp.ui.main.products.kinds.set.stages.characteristics.ComponentStageKindCharacteristicsMain
import com.simenko.qmapp.ui.main.products.kinds.set.stages.characteristics.ComponentStageKindCharacteristicsViewModel
import com.simenko.qmapp.ui.navigation.RouteCompose

inline fun <reified T : Any> NavGraphBuilder.productsNavigation(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = RouteCompose.Main.ProductLines.ProductLinesList()) {
        composable<RouteCompose.Main.ProductLines.ProductLinesList> {
            val viewModel: ProductsViewModel = hiltViewModel()
            ProductLines(viewModel = viewModel, route = it.toRoute())
        }



        composable<RouteCompose.Main.ProductLines.ProductLineKeys.ProductLineKeysList> {
            val viewModel: ProductLineKeysViewModel = hiltViewModel()
            ProductLineKeys(viewModel = viewModel, route = it.toRoute())
        }


        composable<RouteCompose.Main.ProductLines.ProductKinds.ProductKindsList> {
            val viewModel: ProductKindsViewModel = hiltViewModel()
            ProductKinds(viewModel = viewModel, route = it.toRoute())
        }
        composable<RouteCompose.Main.ProductLines.ProductKinds.ProductKindKeys.ProductKindKeysList> {
            val viewModel: ProductKindKeysViewModel = hiltViewModel()
            ProductKindKeys(viewModel = viewModel, route = it.toRoute())
        }
        composable<RouteCompose.Main.ProductLines.ProductKinds.ProductKindCharacteristics.ProductKindCharacteristicsList> {
            val viewModel: ProductKindCharacteristicsViewModel = hiltViewModel()
            ProductKindCharacteristicsMain(viewModel = viewModel, route = it.toRoute())
        }
        composable<RouteCompose.Main.ProductLines.ProductKinds.ProductSpecification.ProductSpecificationList> {
            val viewModel: ProductKindSpecificationViewModel = hiltViewModel()
            ProductKindSpecification(viewModel = viewModel, route = it.toRoute())
        }
        composable<RouteCompose.Main.ProductLines.ProductKinds.ProductSpecification.ComponentKindKeys> {
            val viewModel: ComponentKindKeysViewModel = hiltViewModel()
            ComponentKindKeys(viewModel = viewModel)
        }
        composable<RouteCompose.Main.ProductLines.ProductKinds.ProductSpecification.ComponentKindCharacteristics> {
            val viewModel: ComponentKindCharacteristicsViewModel = hiltViewModel()
            ComponentKindCharacteristicsMain(viewModel = viewModel)
        }
        composable<RouteCompose.Main.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindKeys> {
            val viewModel: ComponentStageKindKeysViewModel = hiltViewModel()
            ComponentStageKindKeys(viewModel = viewModel)
        }
        composable<RouteCompose.Main.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindCharacteristics> {
            val viewModel: ComponentStageKindCharacteristicsViewModel = hiltViewModel()
            ComponentStageKindCharacteristicsMain(viewModel = viewModel)
        }
        composable<RouteCompose.Main.ProductLines.ProductKinds.Products.ProductsList> {
            val viewModel: ProductListViewModel = hiltViewModel()
            ProductKindProducts(mainScreenPadding = mainScreenPadding, viewModel = viewModel)
        }
        composable<RouteCompose.Main.ProductLines.ProductKinds.Products.VersionTolerances.VersionTolerancesDetails> {
            val viewModel: VersionTolerancesViewModel = hiltViewModel()
            VersionTolerances(mainScreenPadding = mainScreenPadding, viewModel = viewModel)
        }



        composable<RouteCompose.Main.ProductLines.Characteristics.CharacteristicsList> {
            val viewModel: CharacteristicsViewModel = hiltViewModel()
            CharacteristicsMain(mainScreenPadding = mainScreenPadding, viewModel = viewModel, route = it.toRoute())
        }
        composable<RouteCompose.Main.ProductLines.Characteristics.CharSubGroupAddEdit> {
            val viewModel: CharSubGroupViewModel = hiltViewModel()
            CharacteristicSubGroupForm(viewModel = viewModel)
        }
    }
}