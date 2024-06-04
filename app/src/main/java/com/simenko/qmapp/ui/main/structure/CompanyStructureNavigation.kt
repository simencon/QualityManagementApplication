package com.simenko.qmapp.ui.main.structure

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.simenko.qmapp.ui.main.structure.forms.channel.ChannelForm
import com.simenko.qmapp.ui.main.structure.forms.channel.ChannelViewModel
import com.simenko.qmapp.ui.main.structure.forms.department.DepartmentForm
import com.simenko.qmapp.ui.main.structure.forms.department.DepartmentViewModel
import com.simenko.qmapp.ui.main.structure.forms.line.LineForm
import com.simenko.qmapp.ui.main.structure.forms.line.LineViewModel
import com.simenko.qmapp.ui.main.structure.forms.operation.OperationForm
import com.simenko.qmapp.ui.main.structure.forms.operation.OperationViewModel
import com.simenko.qmapp.ui.main.structure.forms.subdepartment.SubDepartmentForm
import com.simenko.qmapp.ui.main.structure.forms.subdepartment.SubDepartmentViewModel
import com.simenko.qmapp.ui.main.structure.steps.CompanyStructure
import com.simenko.qmapp.ui.navigation.Route

inline fun <reified T : Any> NavGraphBuilder.companyStructureNavigation(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = Route.Main.CompanyStructure.StructureView()) {
        composable<Route.Main.CompanyStructure.StructureView> {
            val viewModel: CompanyStructureViewModel = hiltViewModel()
            CompanyStructure(mainScreenPadding = mainScreenPadding, viewModel = viewModel, it.toRoute())
        }
        composable<Route.Main.CompanyStructure.DepartmentAddEdit> {
            val viewModel: DepartmentViewModel = hiltViewModel()
            DepartmentForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.SubDepartmentAddEdit> {
            val viewModel: SubDepartmentViewModel = hiltViewModel()
            SubDepartmentForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.ChannelAddEdit> {
            val viewModel: ChannelViewModel = hiltViewModel()
            ChannelForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.LineAddEdit> {
            val viewModel: LineViewModel = hiltViewModel()
            LineForm(viewModel = viewModel, route = it.toRoute())
        }
        composable<Route.Main.CompanyStructure.OperationAddEdit> {
            val viewModel: OperationViewModel = hiltViewModel()
            OperationForm(viewModel = viewModel, route = it.toRoute())
        }
    }
}