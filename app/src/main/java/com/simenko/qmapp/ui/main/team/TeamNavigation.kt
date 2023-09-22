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
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.ui.navigation.NavArguments
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.team.employee.EmployeeComposition
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeForm
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeViewModel
import com.simenko.qmapp.ui.main.team.forms.user.UserForm
import com.simenko.qmapp.ui.main.team.forms.user.UserViewModel
import com.simenko.qmapp.ui.main.team.user.UserComposition
import com.simenko.qmapp.ui.navigation.sharedViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.teamNavigation(navController: NavHostController) {
    navigation(startDestination = Route.Main.Team.Employees.link, route = Route.Main.Team.link) {
        composable(route = Route.Main.Team.Employees.link, arguments = Route.Main.Team.Employees.arguments) {
            val teamModel: TeamViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initTeamModel(teamModel)

            if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
                it.arguments?.getInt(NavArguments.employeeId)?.let { id -> teamModel.setSelectedEmployeeRecord(id) }
            }

            QMAppTheme {
                EmployeeComposition(onClickEdit = { id ->
                    teamModel.setSelectedEmployeeRecord(NoRecord.num)
                    navController.navigate(Route.Main.Team.EmployeeAddEdit.withArgs(id.toString()))
                })
            }
        }
        composable(route = Route.Main.Team.EmployeeAddEdit.link, arguments = Route.Main.Team.EmployeeAddEdit.arguments) {
            val employeeModel: EmployeeViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initEmployeeModel(employeeModel)
            BackHandler { navController.popBackStack() }
            QMAppTheme {
                EmployeeForm(employeeId = it.arguments?.getInt(NavArguments.employeeId) ?: NoRecord.num)
            }
        }
        composable(route = Route.Main.Team.Users.link, arguments = Route.Main.Team.Users.arguments) {
            val teamModel: TeamViewModel = it.sharedViewModel(navController = navController)
            (LocalContext.current as MainActivity).initTeamModel(teamModel)

            if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
                it.arguments?.getString(NavArguments.userId)?.let { id -> teamModel.setSelectedUserRecord(id) }
            }

            QMAppTheme {
                UserComposition(
                    viewModel = teamModel,
                    isUsersPage = true,
                    onClickAuthorize = { id ->
                        teamModel.setSelectedUserRecord(NoRecordStr.str)
                        navController.navigate(Route.Main.Team.EditUser.withArgs(id))
                    },
                    onClickEdit = { id ->
                        teamModel.setSelectedUserRecord(NoRecordStr.str)
                        navController.navigate(Route.Main.Team.EditUser.withArgs(id))
                    }
                )
            }
        }
        composable(route = Route.Main.Team.Requests.link, arguments = Route.Main.Team.Requests.arguments) {
            val teamModel: TeamViewModel = it.sharedViewModel(navController = navController)
            (LocalContext.current as MainActivity).initTeamModel(teamModel)

            if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
                it.arguments?.getString(NavArguments.userId)?.let { id -> teamModel.setSelectedUserRecord(id) }
            }

            QMAppTheme {
                UserComposition(
                    viewModel = teamModel,
                    isUsersPage = false,
                    onClickAuthorize = { id ->
                        teamModel.setSelectedUserRecord(NoRecordStr.str)
                        navController.navigate(Route.Main.Team.AuthorizeUser.withArgs(id))
                    },
                    onClickEdit = { id ->
                        teamModel.setSelectedUserRecord(NoRecordStr.str)
                        navController.navigate(Route.Main.Team.EditUser.withArgs(id))
                    }
                )
            }
        }
        editUser(navController, Route.Main.Team.AuthorizeUser)
        editUser(navController, Route.Main.Team.EditUser)
    }
}

private fun NavGraphBuilder.editUser(navController: NavHostController, route: Route) {
    composable(
        route = route.link,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "${NavArguments.domain}/${Route.Main.Team.link}/${route.link}"
                action = Intent.ACTION_VIEW
            }
        ),
        arguments = route.arguments
    ) {
        val userModel: UserViewModel = hiltViewModel()
        (LocalContext.current as MainActivity).initUserModel(userModel)
        BackHandler { navController.popBackStack() }
        QMAppTheme {
            UserForm(userId = it.arguments?.getString(NavArguments.userId) ?: NoRecordStr.str)
        }
    }
}