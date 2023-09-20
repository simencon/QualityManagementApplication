package com.simenko.qmapp.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.ui.NavArguments
import com.simenko.qmapp.ui.Route
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.ui.main.settings.settingsNavigation
import com.simenko.qmapp.ui.main.team.teamNavigation
import com.simenko.qmapp.ui.theme.QMAppTheme

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    initiatedRoute: String,
    navController: NavHostController
) {
    NavHost(modifier = modifier, navController = navController, startDestination = initiatedRoute) {
        teamNavigation(navController)
        composable(route = Route.Main.Inv.link, arguments = Route.Main.Inv.arguments) {
            val invModel: InvestigationsViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initInvModel(invModel)
            invModel.setCreatedRecord(
                it.arguments?.getInt(NavArguments.orderId) ?: NoRecord.num,
                it.arguments?.getInt(NavArguments.subOrderId) ?: NoRecord.num
            )
            QMAppTheme {
                InvestigationsMainComposition(
                    modifier = Modifier.padding(all = 0.dp),
                    processControlOnly = false
                )
            }
        }
        composable(route = Route.Main.ProcessControl.link, arguments = Route.Main.ProcessControl.arguments) {
            val invModel: InvestigationsViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initInvModel(invModel)
            invModel.setCreatedRecord(
                it.arguments?.getInt(NavArguments.orderId) ?: NoRecord.num,
                it.arguments?.getInt(NavArguments.subOrderId) ?: NoRecord.num
            )
            QMAppTheme {
                InvestigationsMainComposition(
                    modifier = Modifier.padding(all = 0.dp),
                    processControlOnly = true
                )
            }
        }

        composable(route = Route.Main.OrderAddEdit.link, arguments = Route.Main.OrderAddEdit.arguments) {
            val newOrderModel: NewItemViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initNewOrderModel(newOrderModel)
            BackHandler { navController.popBackStack() }
            QMAppTheme {
                OrderForm(
                    orderId = it.arguments?.getInt(NavArguments.orderId) ?: NoRecord.num
                )
            }
        }

        composable(route = Route.Main.SubOrderAddEdit.link, arguments = Route.Main.SubOrderAddEdit.arguments) {
            val newOrderModel: NewItemViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initNewOrderModel(newOrderModel)
            newOrderModel.setSubOrderStandAlone(it.arguments?.getBoolean(NavArguments.subOrderAddEditMode) ?: false)
            BackHandler { navController.popBackStack() }
            QMAppTheme {
                SubOrderForm(
                    record = Pair(
                        it.arguments?.getInt(NavArguments.orderId) ?: NoRecord.num,
                        it.arguments?.getInt(NavArguments.subOrderId) ?: NoRecord.num
                    )
                )
            }
        }

        settingsNavigation(navController)
    }
}