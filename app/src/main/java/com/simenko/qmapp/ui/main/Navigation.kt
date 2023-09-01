package com.simenko.qmapp.ui.main

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.simenko.qmapp.domain.CurrentOrderIdKey
import com.simenko.qmapp.domain.CurrentSubOrderIdKey
import com.simenko.qmapp.domain.ToProcessControlScreen
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SubOrderAddEditModeKey
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.main.settings.Settings
import com.simenko.qmapp.ui.main.team.TeamComposition
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.createLoginActivityIntent
import com.simenko.qmapp.utils.StringUtils.getBoolean

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    mainScreenPadding: PaddingValues,
    initiatedRoute: String,
    navController: NavHostController
) {
    NavHost(modifier = modifier, navController = navController, startDestination = initiatedRoute) {
        composable(route = Screen.Main.Employees.route) {
            val invModel: TeamViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initTeamModel(invModel)
            QMAppTheme {
                TeamComposition()
            }
        }
        composable(
            route = Screen.Main.Inv.route + "/{${ToProcessControlScreen.str}}/{${CurrentOrderIdKey.str}}/{${CurrentSubOrderIdKey.str}}",
            arguments = listOf(
                navArgument(ToProcessControlScreen.str) {
                    type = NavType.BoolType
                    defaultValue = getBoolean(MenuItem.getStartingDrawerMenuItem().id)
                },
                navArgument(CurrentOrderIdKey.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(CurrentSubOrderIdKey.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                }
            )
        ) {
            val invModel: InvestigationsViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initInvModel(invModel)
            invModel.setCreatedRecord(
                it.arguments?.getInt(CurrentOrderIdKey.str)?: NoRecord.num,
                it.arguments?.getInt(CurrentSubOrderIdKey.str)?: NoRecord.num
            )
            QMAppTheme {
                InvestigationsMainComposition(
                    modifier = Modifier.padding(all = 0.dp),
                    mainScreenPadding = mainScreenPadding,
                    processControlOnly = it.arguments?.getBoolean(ToProcessControlScreen.str)?:false
                )
            }
        }

        composable(
            route = Screen.Main.OrderAddEdit.route + "/{${CurrentOrderIdKey.str}}",
            arguments = listOf(
                navArgument(CurrentOrderIdKey.str) {
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
                    orderId = it.arguments?.getInt(CurrentOrderIdKey.str)?: NoRecord.num
                )
            }
        }

        composable(
            route = Screen.Main.SubOrderAddEdit.route + "/{${CurrentOrderIdKey.str}}" + "/{${CurrentSubOrderIdKey.str}}" + "/{${SubOrderAddEditModeKey.str}}",
            arguments = listOf(
                navArgument(CurrentOrderIdKey.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(CurrentSubOrderIdKey.str) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(SubOrderAddEditModeKey.str) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            val newOrderModel: NewItemViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initNewOrderModel(newOrderModel)
            newOrderModel.setSubOrderStandAlone(it.arguments?.getBoolean(SubOrderAddEditModeKey.str)?: false)
            BackHandler {
                navController.popBackStack()
                newOrderModel.setAddEditMode(AddEditMode.NO_MODE)
            }
            QMAppTheme {
                SubOrderForm(
                    record = Pair(
                        it.arguments?.getInt(CurrentOrderIdKey.str)?: NoRecord.num,
                        it.arguments?.getInt(CurrentSubOrderIdKey.str)?: NoRecord.num
                    )
                )
            }
        }

        composable(route = Screen.Main.Settings.route) {
            QMAppTheme {
                Settings(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxWidth(),
                    onClick = { route ->
                        val intent = createLoginActivityIntent(navController.context, route)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(navController.context, intent, null)
                    }
                )
            }
        }
    }
}