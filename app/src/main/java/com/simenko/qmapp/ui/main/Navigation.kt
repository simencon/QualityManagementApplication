package com.simenko.qmapp.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.navigation.navigation
import com.simenko.qmapp.domain.CurrentEmployeeIdKey
import com.simenko.qmapp.domain.CurrentOrderIdKey
import com.simenko.qmapp.domain.CurrentSubOrderIdKey
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SubOrderAddEditModeKey
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.domain.UserEditModeKey
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.main.settings.Settings
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.ui.main.settings.SettingsViewModel
import com.simenko.qmapp.ui.main.team.EmployeeComposition
import com.simenko.qmapp.ui.main.team.UserComposition
import com.simenko.qmapp.ui.main.team.forms.EmployeeForm
import com.simenko.qmapp.ui.main.team.forms.EmployeeViewModel
import com.simenko.qmapp.ui.sharedViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.createLoginActivityIntent
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    initiatedRoute: String,
    navController: NavHostController
) {
    NavHost(modifier = modifier, navController = navController, startDestination = initiatedRoute) {
        navigation(startDestination = Screen.Main.Team.Employees.route, route = Screen.Main.Team.route) {
            composable(route = Screen.Main.Team.Employees.route) {
                val teamModel: TeamViewModel = hiltViewModel()
                (LocalContext.current as MainActivity).initTeamModel(teamModel)
                QMAppTheme {
                    EmployeeComposition()
                }
            }
            composable(
                route = Screen.Main.Team.EmployeeAddEdit.routeWithArgKeys(),
                arguments = listOf(
                    navArgument(CurrentEmployeeIdKey.str) {
                        type = NavType.IntType
                        defaultValue = NoRecord.num
                    }
                )
            ) {
                val employeeModel: EmployeeViewModel = hiltViewModel()
                (LocalContext.current as MainActivity).initEmployeeModel(employeeModel)
                BackHandler {
                    employeeModel.setAddEditMode(AddEditMode.NO_MODE)
                    navController.popBackStack()
                }
                QMAppTheme {
                    EmployeeForm(employeeId = it.arguments?.getInt(CurrentEmployeeIdKey.str) ?: NoRecord.num)
                }
            }
            composable(route = Screen.Main.Team.Users.route) {
                val teamViewModel: TeamViewModel = it.sharedViewModel(navController = navController)
                (LocalContext.current as MainActivity).initTeamModel(teamViewModel)
                QMAppTheme {
                    UserComposition(teamViewModel)
                }
            }
        }
        composable(
            route = Screen.Main.Inv.routeWithArgKeys(),
            arguments = listOf(
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
                it.arguments?.getInt(CurrentOrderIdKey.str) ?: NoRecord.num,
                it.arguments?.getInt(CurrentSubOrderIdKey.str) ?: NoRecord.num
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
                it.arguments?.getInt(CurrentOrderIdKey.str) ?: NoRecord.num,
                it.arguments?.getInt(CurrentSubOrderIdKey.str) ?: NoRecord.num
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
                    orderId = it.arguments?.getInt(CurrentOrderIdKey.str) ?: NoRecord.num
                )
            }
        }

        composable(
            route = Screen.Main.SubOrderAddEdit.routeWithArgKeys(),
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
            newOrderModel.setSubOrderStandAlone(it.arguments?.getBoolean(SubOrderAddEditModeKey.str) ?: false)
            BackHandler {
                newOrderModel.setAddEditMode(AddEditMode.NO_MODE)
                navController.popBackStack()
            }
            QMAppTheme {
                SubOrderForm(
                    record = Pair(
                        it.arguments?.getInt(CurrentOrderIdKey.str) ?: NoRecord.num,
                        it.arguments?.getInt(CurrentSubOrderIdKey.str) ?: NoRecord.num
                    )
                )
            }
        }

        navigation(
            route = Screen.Main.Settings.route,
            startDestination = Screen.Main.Settings.UserDetails.route
        ) {
            composable(route = Screen.Main.Settings.UserDetails.route) {
                val userDetailsModel: EnterDetailsViewModel = hiltViewModel()
                val settingsModel: SettingsViewModel = hiltViewModel()
                val activity = (LocalContext.current as MainActivity)
                activity.initSettingsModel(settingsModel)
                settingsModel.initUserDetailsModel(userDetailsModel)
                QMAppTheme {
                    Settings(
                        modifier = Modifier
                            .padding(all = 0.dp)
                            .fillMaxWidth(),
                        onLogOut = {
                            startActivity(navController.context, createLoginActivityIntent(navController.context), null)
                        },
                        onEditUserData = {
                            userDetailsModel.resetToInitialState()
                            settingsModel.setAddEditMode(AddEditMode.ACCOUNT_EDIT)
                            navController.navigate(Screen.Main.Settings.EditUserDetails.withArgs(TrueStr.str)) { launchSingleTop = true }
                        }
                    )
                }
            }
            composable(
                route = Screen.Main.Settings.EditUserDetails.routeWithArgKeys(),
                arguments = listOf(
                    navArgument(UserEditModeKey.str) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                val settingsViewModel: SettingsViewModel = it.sharedViewModel(navController = navController)
                val userDetailsModel: EnterDetailsViewModel = it.sharedViewModel(navController = navController)
                settingsViewModel.validateUserData = { userDetailsModel.validateInput() }
                BackHandler {
                    navController.popBackStack(Screen.Main.Settings.UserDetails.route, inclusive = false)
                    settingsViewModel.setAddEditMode(AddEditMode.NO_MODE)
                }
                val editUserLambda = remember {
                    {
                        navController.popBackStack(Screen.Main.Settings.UserDetails.route, inclusive = false)
                        settingsViewModel.setAddEditMode(AddEditMode.NO_MODE)
                        settingsViewModel.editUserData()
                    }
                }
                QMAppTheme {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        EnterDetails(
                            navController = navController,
                            editMode = it.arguments?.getBoolean(UserEditModeKey.str) ?: false,
                            userDetailsModel = userDetailsModel,
                            editUserData = editUserLambda
                        )
                    }
                }
            }
        }
    }
}