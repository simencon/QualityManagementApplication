package com.simenko.qmapp.ui.main.structure

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.simenko.qmapp.ui.main.structure.forms.channel.ChannelForm
import com.simenko.qmapp.ui.main.structure.forms.channel.ChannelViewModel
import com.simenko.qmapp.ui.main.structure.forms.line.LineForm
import com.simenko.qmapp.ui.main.structure.forms.line.LineViewModel
import com.simenko.qmapp.ui.main.structure.forms.operation.OperationForm
import com.simenko.qmapp.ui.main.structure.forms.operation.OperationViewModel
import com.simenko.qmapp.ui.main.structure.forms.subdepartment.SubDepartmentForm
import com.simenko.qmapp.ui.main.structure.forms.subdepartment.SubDepartmentViewModel
import com.simenko.qmapp.ui.main.structure.steps.CompanyStructure
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.composable
import com.simenko.qmapp.ui.navigation.navigation

fun NavGraphBuilder.companyStructureNavigation(mainScreenPadding: PaddingValues) {
    navigation(startDestination = Route.Main.CompanyStructure.StructureView) {
        composable(destination = Route.Main.CompanyStructure.StructureView) {
            val viewModel: CompanyStructureViewModel = hiltViewModel()
            CompanyStructure(mainScreenPadding = mainScreenPadding, viewModel = viewModel)
        }
        composable(destination = Route.Main.CompanyStructure.SubDepartmentAddEdit) {
            val viewModel: SubDepartmentViewModel = hiltViewModel()
            SubDepartmentForm(viewModel = viewModel)
        }
        composable(destination = Route.Main.CompanyStructure.ChannelAddEdit) {
            val viewModel: ChannelViewModel = hiltViewModel()
            ChannelForm(viewModel = viewModel)
        }
        composable(destination = Route.Main.CompanyStructure.LineAddEdit) {
            val viewModel: LineViewModel = hiltViewModel()
            LineForm(viewModel = viewModel)
        }
        composable(destination = Route.Main.CompanyStructure.OperationAddEdit) {
            val viewModel: OperationViewModel = hiltViewModel()
            OperationForm(viewModel = viewModel)
        }
    }
}