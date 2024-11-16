package com.simenko.qmapp.presentation.ui.main.investigations

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.presentation.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.presentation.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.presentation.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.navigation.Route

inline fun <reified T : Route> NavGraphBuilder.allInvestigations(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = Route.Main.AllInvestigations.AllInvestigationsList()) {
        composable<Route.Main.AllInvestigations.AllInvestigationsList> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Main.AllInvestigations.AllInvestigationsList>()
            val viewModel: InvestigationsViewModel = hiltViewModel()
            InvestigationsMainComposition(mainScreenPadding = mainScreenPadding, invModel = viewModel, isPcOnly = false, orderId = route.orderId, subOrderId = route.subOrderId)
        }
        composable<Route.Main.AllInvestigations.OrderAddEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Main.AllInvestigations.OrderAddEdit>()
            val viewModel: NewItemViewModel = hiltViewModel()
            OrderForm(viewModel = viewModel, isPcOnly = false, orderId = route.orderId, subOrderId = NoRecord.num)
        }
        composable<Route.Main.AllInvestigations.SubOrderAddEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Main.AllInvestigations.SubOrderAddEdit>()
            val viewModel: NewItemViewModel = hiltViewModel()
            SubOrderForm(viewModel = viewModel, isPcOnly = false, orderId = route.orderId, subOrderId = route.subOrderId)
        }
    }
}

inline fun <reified T : Route> NavGraphBuilder.processControl(mainScreenPadding: PaddingValues) {
    navigation<T>(startDestination = Route.Main.ProcessControl.ProcessControlList()) {
        composable<Route.Main.ProcessControl.ProcessControlList> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Main.ProcessControl.ProcessControlList>()
            val viewModel: InvestigationsViewModel = hiltViewModel()
//            InvestigationsWithDetails(invModel = viewModel, isPcOnly = true, orderId = route.orderId, subOrderId = route.subOrderId)
            InvestigationsMainComposition(mainScreenPadding = mainScreenPadding, invModel = viewModel, isPcOnly = true, orderId = route.orderId, subOrderId = route.subOrderId)
        }
        composable<Route.Main.ProcessControl.SubOrderAddEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Main.ProcessControl.SubOrderAddEdit>()
            val viewModel: NewItemViewModel = hiltViewModel()
            SubOrderForm(viewModel = viewModel, isPcOnly = true, orderId = route.orderId, subOrderId = route.subOrderId)
        }
    }
}