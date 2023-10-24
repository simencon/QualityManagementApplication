package com.simenko.qmapp.ui.main.structure

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import com.simenko.qmapp.ui.main.structure.forms.operation.OperationForm
import com.simenko.qmapp.ui.main.structure.forms.operation.OperationViewModel
import com.simenko.qmapp.ui.main.structure.steps.CompanyStructure
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.composable
import com.simenko.qmapp.ui.navigation.navigation

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.companyStructureNavigation(mainScreenPadding: PaddingValues) {
    navigation(startDestination = Route.Main.CompanyStructure.StructureView) {
        composable(destination = Route.Main.CompanyStructure.StructureView) {
            val viewModel: CompanyStructureViewModel = hiltViewModel()
//            if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
//                viewModel.setCompositionStage(CompositionStage.COMPOSED)
//            }
            CompanyStructure(mainScreenPadding = mainScreenPadding, viewModel = viewModel)
        }
        composable(destination = Route.Main.CompanyStructure.OperationAddEdit) {
            val viewModel: OperationViewModel = hiltViewModel()
            OperationForm(viewModel = viewModel)
        }
    }
}