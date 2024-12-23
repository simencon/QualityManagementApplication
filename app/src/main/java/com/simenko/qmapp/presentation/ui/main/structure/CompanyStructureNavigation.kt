package com.simenko.qmapp.presentation.ui.main.structure

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.simenko.qmapp.presentation.ui.main.structure.forms.channel.ChannelForm
import com.simenko.qmapp.presentation.ui.main.structure.forms.channel.ChannelViewModel
import com.simenko.qmapp.presentation.ui.main.structure.forms.department.DepartmentForm
import com.simenko.qmapp.presentation.ui.main.structure.forms.department.DepartmentViewModel
import com.simenko.qmapp.presentation.ui.main.structure.forms.line.LineForm
import com.simenko.qmapp.presentation.ui.main.structure.forms.line.LineViewModel
import com.simenko.qmapp.presentation.ui.main.structure.forms.operation.OperationForm
import com.simenko.qmapp.presentation.ui.main.structure.forms.operation.OperationViewModel
import com.simenko.qmapp.presentation.ui.main.structure.forms.sub_department.SubDepartmentForm
import com.simenko.qmapp.presentation.ui.main.structure.forms.sub_department.SubDepartmentViewModel
import com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.characteristics_operation.OperationCharacteristicsMain
import com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.characteristics_operation.OperationCharacteristicsViewModel
import com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.item_keys_channel.ChannelItemKeys
import com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.item_keys_channel.ChannelItemKeysViewModel
import com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.item_kinds_sub_department.SubDepartmentItemKinds
import com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.item_kinds_sub_department.SubDepartmentItemKindsViewModel
import com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.items_line.LineItems
import com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.items_line.LineItemsViewModel
import com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.product_lines_department.DepartmentProductLines
import com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.product_lines_department.DepartmentProductLinesViewModel
import com.simenko.qmapp.presentation.ui.main.structure.steps.CompanyStructure
import com.simenko.qmapp.navigation.Route

inline fun <reified T : Any> NavGraphBuilder.companyStructureNavigation(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = Route.Main.CompanyStructure.StructureView()) {
        composable<Route.Main.CompanyStructure.StructureView> {
            val viewModel: com.simenko.qmapp.presentation.ui.main.structure.CompanyStructureViewModel = hiltViewModel()
            CompanyStructure(mainScreenPadding = mainScreenPadding, viewModel = viewModel, it.toRoute())
        }
        composable<Route.Main.CompanyStructure.DepartmentAddEdit> {
            val viewModel: DepartmentViewModel = hiltViewModel()
            DepartmentForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.DepartmentProductLines> {
            val viewModel: DepartmentProductLinesViewModel = hiltViewModel()
            DepartmentProductLines(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.SubDepartmentAddEdit> {
            val viewModel: SubDepartmentViewModel = hiltViewModel()
            SubDepartmentForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.SubDepartmentItemKinds> {
            val viewModel: SubDepartmentItemKindsViewModel = hiltViewModel()
            SubDepartmentItemKinds(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.ChannelAddEdit> {
            val viewModel: ChannelViewModel = hiltViewModel()
            ChannelForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.ChannelItemKeys> {
            val viewModel: ChannelItemKeysViewModel = hiltViewModel()
            ChannelItemKeys(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.LineAddEdit> {
            val viewModel: LineViewModel = hiltViewModel()
            LineForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.LineItems> {
            val viewModel: LineItemsViewModel = hiltViewModel()
            LineItems(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.OperationAddEdit> {
            val viewModel: OperationViewModel = hiltViewModel()
            OperationForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.OperationCharacteristics> {
            val viewModel: OperationCharacteristicsViewModel = hiltViewModel()
            OperationCharacteristicsMain(viewModel = viewModel, route = it.toRoute())
        }
    }
}