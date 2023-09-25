package com.simenko.qmapp.ui.main.team

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
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
import com.simenko.qmapp.ui.navigation.composable
import com.simenko.qmapp.ui.navigation.navigation
import com.simenko.qmapp.ui.navigation.sharedViewModel

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.teamNavigation(navController: NavHostController) {
    navigation(startDestination = Route.Main.Team.Employees) {
        composable(destination = Route.Main.Team.Employees) {
            val teamModel: TeamViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initTeamModel(teamModel)

            if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
                it.arguments?.getInt(NavArguments.employeeId)?.let { id -> teamModel.setSelectedEmployeeRecord(id) }
            }
            EmployeeComposition(onClickEdit = { id -> teamModel.onEmployeeEdictClick(id) })
        }
        composable(destination = Route.Main.Team.EmployeeAddEdit) {
            val employeeModel: EmployeeViewModel = hiltViewModel()
            (LocalContext.current as MainActivity).initEmployeeModel(employeeModel)

            EmployeeForm(employeeId = it.arguments?.getInt(NavArguments.employeeId) ?: NoRecord.num)
        }
        composable(destination = Route.Main.Team.Users) {
            val teamModel: TeamViewModel = it.sharedViewModel(navController = navController)
            (LocalContext.current as MainActivity).initTeamModel(teamModel)

            if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
                it.arguments?.getString(NavArguments.userId)?.let { id -> teamModel.setSelectedUserRecord(id) }
            }

            UserComposition(
                viewModel = teamModel,
                isUsersPage = true,
                onClickAuthorize = {},
                onClickEdit = { id -> teamModel.onUserEditClick(id) }
            )
        }
        composable(destination = Route.Main.Team.Requests) {
            val teamModel: TeamViewModel = it.sharedViewModel(navController = navController)
            (LocalContext.current as MainActivity).initTeamModel(teamModel)

            if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED) {
                it.arguments?.getString(NavArguments.userId)?.let { id -> teamModel.setSelectedUserRecord(id) }
            }

            UserComposition(
                viewModel = teamModel,
                isUsersPage = false,
                onClickAuthorize = { id -> teamModel.onUserAuthorizeClick(id) },
                onClickEdit = {}
            )
        }
        editUser(Route.Main.Team.AuthorizeUser)
        editUser(Route.Main.Team.EditUser)
    }
}

private fun NavGraphBuilder.editUser(route: Route) {
    composable(destination = route) {
        val userModel: UserViewModel = hiltViewModel()
        (LocalContext.current as MainActivity).initUserModel(userModel)

        UserForm(userId = it.arguments?.getString(NavArguments.userId) ?: NoRecordStr.str)
    }
}