package com.simenko.qmapp.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.simenko.qmapp.domain.OrderId
import com.simenko.qmapp.domain.SubOrderId
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SubOrderAddEditMode
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.ui.main.main.AddEditMode
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
        composable(
            route = Screen.Main.Inv.routeWithArgKeys(),
            arguments = listOf(
                navArgument(OrderId.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(SubOrderId.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                }
            )
        ) {
            println("Main Navigation - Investigations has been build")
            val invModel: InvestigationsViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initInvModel(invModel)
            invModel.setCreatedRecord(
                it.arguments?.getInt(OrderId.str) ?: NoRecord.num,
                it.arguments?.getInt(SubOrderId.str) ?: NoRecord.num
            )
            QMAppTheme {
                InvestigationsMainComposition(
                    modifier = Modifier.padding(all = 0.dp),
                    processControlOnly = false
                )
            }
        }
        composable(
            route = Screen.Main.ProcessControl.routeWithArgKeys(),
            arguments = listOf(
                navArgument(OrderId.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(SubOrderId.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                }
            )
        ) {
            println("Main Navigation - ProcessControl has been build")
            val invModel: InvestigationsViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initInvModel(invModel)
            invModel.setCreatedRecord(
                it.arguments?.getInt(OrderId.str) ?: NoRecord.num,
                it.arguments?.getInt(SubOrderId.str) ?: NoRecord.num
            )
            QMAppTheme {
                InvestigationsMainComposition(
                    modifier = Modifier.padding(all = 0.dp),
                    processControlOnly = true
                )
            }
        }

        composable(
            route = Screen.Main.OrderAddEdit.routeWithArgKeys(),
            arguments = listOf(
                navArgument(OrderId.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                }
            )
        ) {
            val newOrderModel: NewItemViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initNewOrderModel(newOrderModel)
            BackHandler {
                navController.popBackStack()
                newOrderModel.setAddEditMode(AddEditMode.NO_MODE)
            }
            QMAppTheme {
                OrderForm(
                    orderId = it.arguments?.getInt(OrderId.str) ?: NoRecord.num
                )
            }
        }

        composable(
            route = Screen.Main.SubOrderAddEdit.routeWithArgKeys(),
            arguments = listOf(
                navArgument(OrderId.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(SubOrderId.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(SubOrderAddEditMode.str) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            val newOrderModel: NewItemViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initNewOrderModel(newOrderModel)
            newOrderModel.setSubOrderStandAlone(it.arguments?.getBoolean(SubOrderAddEditMode.str) ?: false)
            BackHandler {
                newOrderModel.setAddEditMode(AddEditMode.NO_MODE)
                navController.popBackStack()
            }
            QMAppTheme {
                SubOrderForm(
                    record = Pair(
                        it.arguments?.getInt(OrderId.str) ?: NoRecord.num,
                        it.arguments?.getInt(SubOrderId.str) ?: NoRecord.num
                    )
                )
            }
        }

        settingsNavigation(navController)
    }
}