package com.simenko.qmapp.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.simenko.qmapp.domain.EmployeeId
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.OrderId
import com.simenko.qmapp.domain.SubOrderId
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SubOrderAddEditMode
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.domain.UserEditMode
import com.simenko.qmapp.domain.UserId
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.main.settings.Settings
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.ui.main.settings.SettingsViewModel
import com.simenko.qmapp.ui.main.team.employee.EmployeeComposition
import com.simenko.qmapp.ui.main.team.user.UserComposition
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeForm
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeViewModel
import com.simenko.qmapp.ui.main.team.forms.user.UserForm
import com.simenko.qmapp.ui.main.team.forms.user.UserViewModel
import com.simenko.qmapp.ui.sharedViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.createLoginActivityIntent
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    initiatedRoute: String,
    navController: NavHostController
) {
    NavHost(modifier = modifier, navController = navController, startDestination = initiatedRoute) {
        navigation(startDestination = Screen.Main.Team.Employees.routeWithArgKeys(), route = Screen.Main.Team.route) {
            composable(
                route = Screen.Main.Team.Employees.routeWithArgKeys(),
                arguments = listOf(
                    navArgument(EmployeeId.str) {
                        type = NavType.IntType
                        defaultValue = NoRecord.num
                    }
                )
            ) {
                val teamModel: TeamViewModel = hiltViewModel()
                (LocalContext.current as MainActivity).initTeamModel(teamModel)

                if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    it.arguments?.getInt(EmployeeId.str)?.let { id -> teamModel.setSelectedEmployeeRecord(id) }
                }

                QMAppTheme {
                    EmployeeComposition(onClickEdit = { id ->
                        teamModel.setSelectedEmployeeRecord(NoRecord.num)
                        teamModel.setAddEditMode(AddEditMode.EDIT_EMPLOYEE)
                        navController.navigate(Screen.Main.Team.EmployeeAddEdit.withArgs(id.toString()))
                    })
                }
            }
            composable(
                route = Screen.Main.Team.EmployeeAddEdit.routeWithArgKeys(),
                arguments = listOf(
                    navArgument(EmployeeId.str) {
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
                    EmployeeForm(employeeId = it.arguments?.getInt(EmployeeId.str) ?: NoRecord.num)
                }
            }
            composable(
                route = Screen.Main.Team.Users.routeWithArgKeys(),
                arguments = listOf(
                    navArgument(UserId.str) {
                        type = NavType.StringType
                        defaultValue = NoRecordStr.str
                    }
                )
            ) {
                val teamModel: TeamViewModel = it.sharedViewModel(navController = navController)
                (LocalContext.current as MainActivity).initTeamModel(teamModel)

                if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    it.arguments?.getString(UserId.str)?.let { id ->
                        println("Users - Authorized user is $id.")
                        teamModel.setSelectedUserRecord(id)
                    }
                }

                QMAppTheme {
                    UserComposition(
                        viewModel = teamModel,
                        onClickAuthorize = { id ->
                            teamModel.setSelectedUserRecord(NoRecordStr.str)
                            teamModel.setAddEditMode(AddEditMode.AUTHORIZE_USER)
                            navController.navigate(Screen.Main.Team.UserEdit.withArgs(id))
                        },
                        onClickEdit = { id ->
                            teamModel.setSelectedUserRecord(NoRecordStr.str)
                            teamModel.setAddEditMode(AddEditMode.EDIT_USER)
                            navController.navigate(Screen.Main.Team.UserEdit.withArgs(id))
                        }
                    )
                }
            }

            composable(
                route = Screen.Main.Team.UserEdit.routeWithArgKeys(),
                arguments = listOf(
                    navArgument(UserId.str) {
                        type = NavType.StringType
                        defaultValue = NoString.str
                    }
                )
            ) {
                val userModel: UserViewModel = hiltViewModel()
                (LocalContext.current as MainActivity).initUserModel(userModel)
                BackHandler {
                    userModel.setAddEditMode(AddEditMode.NO_MODE)
                    navController.popBackStack()
                }
                QMAppTheme {
                    UserForm(userId = it.arguments?.getString(UserId.str) ?: NoString.str)
                }
            }
        }
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

        navigation(
            route = Screen.Main.Settings.route,
            startDestination = Screen.Main.Settings.UserDetails.route
        ) {
            composable(route = Screen.Main.Settings.UserDetails.route) {
                println("Main Navigation - Settings has been build")
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
                    navArgument(UserEditMode.str) {
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
                            editMode = it.arguments?.getBoolean(UserEditMode.str) ?: false,
                            userDetailsModel = userDetailsModel,
                            editUserData = editUserLambda
                        )
                    }
                }
            }
        }
    }
}