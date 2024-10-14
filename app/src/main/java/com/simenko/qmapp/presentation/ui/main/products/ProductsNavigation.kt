package com.simenko.qmapp.presentation.ui.main.products

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.simenko.qmapp.presentation.ui.main.products.characteristics.CharacteristicsMain
import com.simenko.qmapp.presentation.ui.main.products.characteristics.CharacteristicsViewModel
import com.simenko.qmapp.presentation.ui.main.products.characteristics.forms.characteristic.CharacteristicForm
import com.simenko.qmapp.presentation.ui.main.products.characteristics.forms.characteristic.CharacteristicViewModel
import com.simenko.qmapp.presentation.ui.main.products.characteristics.forms.group.CharGroupForm
import com.simenko.qmapp.presentation.ui.main.products.characteristics.forms.group.CharGroupViewModel
import com.simenko.qmapp.presentation.ui.main.products.characteristics.forms.metric.MetricForm
import com.simenko.qmapp.presentation.ui.main.products.characteristics.forms.metric.MetricViewModel
import com.simenko.qmapp.presentation.ui.main.products.characteristics.forms.sub_group.CharSubGroupViewModel
import com.simenko.qmapp.presentation.ui.main.products.characteristics.forms.sub_group.CharacteristicSubGroupForm
import com.simenko.qmapp.presentation.ui.main.products.kinds.ProductKinds
import com.simenko.qmapp.presentation.ui.main.products.kinds.ProductKindsViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.designations.ProductKindKeys
import com.simenko.qmapp.presentation.ui.main.products.kinds.designations.ProductKindKeysViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.steps.ProductKindProducts
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.ProductKindSpecification
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.ProductKindSpecificationViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.designations.ComponentKindKeys
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.designations.ComponentKindKeysViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.stages.designations.ComponentStageKindKeys
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.stages.designations.ComponentStageKindKeysViewModel
import com.simenko.qmapp.presentation.ui.main.products.designations.ProductLineKeys
import com.simenko.qmapp.presentation.ui.main.products.designations.ProductLineKeysViewModel
import com.simenko.qmapp.presentation.ui.main.products.designations.forms.ProductLineKeyForm
import com.simenko.qmapp.presentation.ui.main.products.designations.forms.ProductLineKeyViewModel
import com.simenko.qmapp.presentation.ui.main.products.forms.ProductLineForm
import com.simenko.qmapp.presentation.ui.main.products.forms.ProductLineViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.characteristics.ProductKindCharacteristicsMain
import com.simenko.qmapp.presentation.ui.main.products.kinds.characteristics.ProductKindCharacteristicsViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.forms.ProductKindForm
import com.simenko.qmapp.presentation.ui.main.products.kinds.forms.ProductKindViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component.existing_component.ProductComponentDialog
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component.existing_component.ProductComponentViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component.new_component.ComponentForm
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component.new_component.ComponentViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component_stage.existing_component_stage.ComponentComponentStageDialog
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component_stage.existing_component_stage.ComponentComponentStageViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component_stage.new_component_stage.ComponentStageViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component_stage.new_component_stage.ComponentStageForm
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.product.existing_product.ProductKindProductDialog
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.product.existing_product.ProductKindProductViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.product.new_product.ProductForm
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.product.new_product.ProductViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.version.copy_version.ItemVersionsViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.version.copy_version.VersionSingleChoiceDialog
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.versions.VersionTolerances
import com.simenko.qmapp.presentation.ui.main.products.kinds.list.versions.VersionTolerancesViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.characteristics.ComponentKindCharacteristicsMain
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.characteristics.ComponentKindCharacteristicsViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.forms.ComponentKindForm
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.forms.ComponentKindViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.stages.characteristics.ComponentStageKindCharacteristicsMain
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.stages.characteristics.ComponentStageKindCharacteristicsViewModel
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.stages.forms.ComponentStageKindForm
import com.simenko.qmapp.presentation.ui.main.products.kinds.set.stages.forms.ComponentStageKindViewModel
import com.simenko.qmapp.navigation.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi

inline fun <reified T : Route> NavGraphBuilder.productsNavigation(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = Route.Main.ProductLines.ProductLinesList()) {
        composable<Route.Main.ProductLines.ProductLinesList> {
            val viewModel: ProductLinesViewModel = hiltViewModel()
            ProductLines(viewModel = viewModel, route = it.toRoute())
        }

        composable<Route.Main.ProductLines.AddEditProductLine> {
            val viewModel: ProductLineViewModel = hiltViewModel()
            ProductLineForm(viewModel = viewModel, route = it.toRoute())
        }

        productLineKeysNavigation<Route.Main.ProductLines.ProductLineKeys>()
        productKindNavigation<Route.Main.ProductLines.ProductKinds>(mainScreenPadding)
        productLineCharacteristicsNavigation<Route.Main.ProductLines.Characteristics>(mainScreenPadding)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <reified T : Route> NavGraphBuilder.productLineKeysNavigation() {
    navigation<T>(startDestination = Route.Main.ProductLines.ProductLineKeys.ProductLineKeysList()) {
        composable<Route.Main.ProductLines.ProductLineKeys.ProductLineKeysList> {
            val viewModel: ProductLineKeysViewModel = hiltViewModel()
            ProductLineKeys(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductLineKeys.AddEditProductLineKey> {
            val viewModel: ProductLineKeyViewModel = hiltViewModel()
            ProductLineKeyForm(viewModel = viewModel, route = it.toRoute())
        }
    }
}

/**
 * Product designations navigation
 * */
inline fun <reified T : Any> NavGraphBuilder.productKindNavigation(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = Route.Main.ProductLines.ProductKinds.ProductKindsList()) {
        composable<Route.Main.ProductLines.ProductKinds.ProductKindsList> {
            val viewModel: ProductKindsViewModel = hiltViewModel()
            ProductKinds(viewModel = viewModel, route = it.toRoute())
        }

        composable<Route.Main.ProductLines.ProductKinds.AddEditProductKind> {
            val viewModel: ProductKindViewModel = hiltViewModel()
            ProductKindForm(viewModel = viewModel, route = it.toRoute())
        }

        productKindKeysNavigation<Route.Main.ProductLines.ProductKinds.ProductKindKeys>()
        productKindCharacteristicsNavigation<Route.Main.ProductLines.ProductKinds.ProductKindCharacteristics>()
        productKindSpecificationNavigation<Route.Main.ProductLines.ProductKinds.ProductSpecification>()
        productKindProductsNavigation<Route.Main.ProductLines.ProductKinds.Products>(mainScreenPadding)
    }
}

/**
 * Product kinds navigation
 * */
inline fun <reified T : Route> NavGraphBuilder.productKindKeysNavigation() {
    navigation<T>(startDestination = Route.Main.ProductLines.ProductKinds.ProductKindKeys.ProductKindKeysList()) {
        composable<Route.Main.ProductLines.ProductKinds.ProductKindKeys.ProductKindKeysList> {
            val viewModel: ProductKindKeysViewModel = hiltViewModel()
            ProductKindKeys(viewModel = viewModel, route = it.toRoute())
        }
    }
}

inline fun <reified T : Route> NavGraphBuilder.productKindCharacteristicsNavigation() {
    navigation<T>(startDestination = Route.Main.ProductLines.ProductKinds.ProductKindCharacteristics.ProductKindCharacteristicsList()) {
        composable<Route.Main.ProductLines.ProductKinds.ProductKindCharacteristics.ProductKindCharacteristicsList> {
            val viewModel: ProductKindCharacteristicsViewModel = hiltViewModel()
            ProductKindCharacteristicsMain(viewModel = viewModel, route = it.toRoute())
        }
    }
}

inline fun <reified T : Route> NavGraphBuilder.productKindSpecificationNavigation() {
    navigation<T>(startDestination = Route.Main.ProductLines.ProductKinds.ProductSpecification.ProductSpecificationList()) {
        composable<Route.Main.ProductLines.ProductKinds.ProductSpecification.ProductSpecificationList> {
            val viewModel: ProductKindSpecificationViewModel = hiltViewModel()
            ProductKindSpecification(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductKinds.ProductSpecification.AddEditComponentKind> {
            val viewModel: ComponentKindViewModel = hiltViewModel()
            ComponentKindForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductKinds.ProductSpecification.AddEditComponentStageKind> {
            val viewModel: ComponentStageKindViewModel = hiltViewModel()
            ComponentStageKindForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentKindKeys.ComponentKindKeysList> {
            val viewModel: ComponentKindKeysViewModel = hiltViewModel()
            ComponentKindKeys(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentKindCharacteristics.ComponentKindCharacteristicsList> {
            val viewModel: ComponentKindCharacteristicsViewModel = hiltViewModel()
            ComponentKindCharacteristicsMain(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindKeys.ComponentStageKindKeysList> {
            val viewModel: ComponentStageKindKeysViewModel = hiltViewModel()
            ComponentStageKindKeys(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindCharacteristics.ComponentStageKindCharacteristicsList> {
            val viewModel: ComponentStageKindCharacteristicsViewModel = hiltViewModel()
            ComponentStageKindCharacteristicsMain(viewModel = viewModel, route = it.toRoute())
        }
    }
}

inline fun <reified T : Route> NavGraphBuilder.productKindProductsNavigation(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = Route.Main.ProductLines.ProductKinds.Products.ProductsList()) {
        composable<Route.Main.ProductLines.ProductKinds.Products.ProductsList> {
            val viewModel: com.simenko.qmapp.presentation.ui.main.products.kinds.list.ProductListViewModel = hiltViewModel()
            ProductKindProducts(mainScreenPadding = mainScreenPadding, viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductKinds.Products.AddEditProduct> {
            val viewModel: ProductViewModel = hiltViewModel()
            ProductForm(viewModel = viewModel, route = it.toRoute())
        }
        dialog<Route.Main.ProductLines.ProductKinds.Products.AddProductKindProduct> {
            val viewModel: ProductKindProductViewModel = hiltViewModel()
            ProductKindProductDialog(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductKinds.Products.AddEditComponent> {
            val viewModel: ComponentViewModel = hiltViewModel()
            ComponentForm(viewModel = viewModel, route = it.toRoute())
        }
        dialog<Route.Main.ProductLines.ProductKinds.Products.AddProductComponent> {
            val viewModel: ProductComponentViewModel = hiltViewModel()
            ProductComponentDialog(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductKinds.Products.AddEditComponentStage> {
            val viewModel: ComponentStageViewModel = hiltViewModel()
            ComponentStageForm(viewModel = viewModel, route = it.toRoute())
        }
        dialog<Route.Main.ProductLines.ProductKinds.Products.AddComponentComponentStage> {
            val viewModel: ComponentComponentStageViewModel = hiltViewModel()
            ComponentComponentStageDialog(viewModel = viewModel, route = it.toRoute())
        }
        dialog<Route.Main.ProductLines.ProductKinds.Products.CopyItemVersion> {
            val viewModel: ItemVersionsViewModel = hiltViewModel()
            VersionSingleChoiceDialog(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.ProductKinds.Products.VersionTolerancesDetails> {
            val viewModel: VersionTolerancesViewModel = hiltViewModel()
            VersionTolerances(mainScreenPadding = mainScreenPadding, viewModel = viewModel, route = it.toRoute())
        }
    }
}

/**
 * Product characteristics navigation
 * */
inline fun <reified T : Route> NavGraphBuilder.productLineCharacteristicsNavigation(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = Route.Main.ProductLines.Characteristics.CharacteristicGroupList()) {
        composable<Route.Main.ProductLines.Characteristics.CharacteristicGroupList> {
            val viewModel: CharacteristicsViewModel = hiltViewModel()
            CharacteristicsMain(mainScreenPadding = mainScreenPadding, viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.ProductLines.Characteristics.AddEditCharGroup> {
            val viewModel: CharGroupViewModel = hiltViewModel()
            CharGroupForm(viewModel = viewModel, route = it.toRoute())

        }
        composable<Route.Main.ProductLines.Characteristics.AddEditCharSubGroup> {
            val viewModel: CharSubGroupViewModel = hiltViewModel()
            CharacteristicSubGroupForm(viewModel = viewModel, route = it.toRoute())
        }

        composable<Route.Main.ProductLines.Characteristics.AddEditChar> {
            val viewModel: CharacteristicViewModel = hiltViewModel()
            CharacteristicForm(viewModel = viewModel, route = it.toRoute())
        }

        composable<Route.Main.ProductLines.Characteristics.AddEditMetric> {
            val viewModel: MetricViewModel = hiltViewModel()
            MetricForm(viewModel = viewModel, route = it.toRoute())
        }
    }
}

