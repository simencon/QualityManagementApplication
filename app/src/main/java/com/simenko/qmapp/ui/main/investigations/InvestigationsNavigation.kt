package com.simenko.qmapp.ui.main.investigations

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.navigation.RouteCompose

inline fun <reified T : RouteCompose> NavGraphBuilder.allInvestigations(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = RouteCompose.Main.AllInvestigations.AllInvestigationsList()) {
        composable<RouteCompose.Main.AllInvestigations.AllInvestigationsList> {
            val viewModel: InvestigationsViewModel = hiltViewModel()
            InvestigationsMainComposition(mainScreenPadding = mainScreenPadding, invModel = viewModel)
        }
        composable<RouteCompose.Main.AllInvestigations.OrderAddEdit> {
            val viewModel: NewItemViewModel = hiltViewModel()
            OrderForm(viewModel = viewModel)
        }
        composable<RouteCompose.Main.AllInvestigations.SubOrderAddEdit> {
            val viewModel: NewItemViewModel = hiltViewModel()
            SubOrderForm(viewModel = viewModel)
        }
    }
}

inline fun <reified T : RouteCompose> NavGraphBuilder.processControl(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = RouteCompose.Main.ProcessControl.ProcessControlList()) {
        composable<RouteCompose.Main.ProcessControl.ProcessControlList> {
            val viewModel: InvestigationsViewModel = hiltViewModel()
            InvestigationsMainComposition(mainScreenPadding = mainScreenPadding, invModel = viewModel)
        }
        composable<RouteCompose.Main.ProcessControl.SubOrderAddEdit> {
            val viewModel: NewItemViewModel = hiltViewModel()
            SubOrderForm(viewModel = viewModel)
        }
    }
}