package com.simenko.qmapp.ui.main.team

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.simenko.qmapp.domain.EmployeeId
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.UserId
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.AddEditMode
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.team.employee.EmployeeComposition
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeForm
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeViewModel
import com.simenko.qmapp.ui.main.team.forms.user.UserForm
import com.simenko.qmapp.ui.main.team.forms.user.UserViewModel
import com.simenko.qmapp.ui.main.team.user.UserComposition
import com.simenko.qmapp.ui.sharedViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.teamNavigation(navController: NavHostController) {
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
                    println("Users - Interacted record is $id.")
                    teamModel.setSelectedUserRecord(id)
                }
            }

            QMAppTheme {
                UserComposition(
                    viewModel = teamModel,
                    isUsersPage = true,
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
            route = Screen.Main.Team.Requests.routeWithArgKeys(),
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
                    println("Users - Interacted record is $id.")
                    teamModel.setSelectedUserRecord(id)
                }
            }

            QMAppTheme {
                UserComposition(
                    viewModel = teamModel,
                    isUsersPage = false,
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
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "${Screen.Domain.route}/${Screen.Main.Team.route}/${Screen.Main.Team.UserEdit.routeWithArgKeys()}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument(UserId.str) {
                    type = NavType.StringType
                    defaultValue = NoRecordStr.str
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
                UserForm(userId = it.arguments?.getString(UserId.str) ?: NoRecordStr.str)
            }
        }
    }
}