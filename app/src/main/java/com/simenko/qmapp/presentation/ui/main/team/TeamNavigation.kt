package com.simenko.qmapp.presentation.ui.main.team

import android.content.Intent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.simenko.qmapp.presentation.ui.main.team.employee.Employees
import com.simenko.qmapp.presentation.ui.main.team.forms.employee.EmployeeForm
import com.simenko.qmapp.presentation.ui.main.team.forms.user.UserForm
import com.simenko.qmapp.presentation.ui.main.team.user.Users
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.navigation.Route.Companion.DOMAIN

inline fun <reified T : Any> NavGraphBuilder.teamNavigation() {
    navigation<T>(startDestination = Route.Main.Team.Requests()) {
        composable<Route.Main.Team.Employees> { backStackEntry ->
            val employeeId = backStackEntry.toRoute<Route.Main.Team.Employees>().employeeId
            val teamModel: TeamViewModel = hiltViewModel()
            Employees(viewModel = teamModel, employeeId = employeeId, onClickEdit = { id -> teamModel.onEmployeeAddEdictClick(id) })
        }
        composable<Route.Main.Team.Users> { backStackEntry ->
            val userId = backStackEntry.toRoute<Route.Main.Team.Users>().userId
            val teamModel: TeamViewModel = hiltViewModel()
            Users(viewModel = teamModel, userId = userId, isUsersPage = true)
        }
        composable<Route.Main.Team.Requests> { backStackEntry ->
            val userId = backStackEntry.toRoute<Route.Main.Team.Requests>().userId
            val teamModel: TeamViewModel = hiltViewModel()
            Users(viewModel = teamModel, userId = userId, isUsersPage = false)
        }

        composable<Route.Main.Team.EmployeeAddEdit> { backStackEntry ->
            val employeeId = backStackEntry.toRoute<Route.Main.Team.EmployeeAddEdit>().employeeId
            EmployeeForm(employeeId = employeeId)
        }
        composable<Route.Main.Team.EditUser> { backStackEntry ->
            val userId = backStackEntry.toRoute<Route.Main.Team.EditUser>().userId
            UserForm(userId = userId)
        }
        composable<Route.Main.Team.AuthorizeUser>(
            deepLinks = listOf(navDeepLink<Route.Main.Team.AuthorizeUser>(DOMAIN) { action = Intent.ACTION_VIEW })
        ) { backStackEntry ->
            val userId = backStackEntry.toRoute<Route.Main.Team.AuthorizeUser>().userId
            UserForm(userId = userId)
        }
    }
}