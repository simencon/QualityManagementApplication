package com.simenko.qmapp.ui.main.team

import android.content.Intent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.simenko.qmapp.ui.main.team.employee.Employees
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeForm
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeViewModel
import com.simenko.qmapp.ui.main.team.forms.user.UserForm
import com.simenko.qmapp.ui.main.team.user.Users
import com.simenko.qmapp.ui.navigation.RouteCompose
import com.simenko.qmapp.ui.navigation.RouteCompose.Companion.DOMAIN

inline fun <reified T : Any> NavGraphBuilder.teamNavigation() {
    navigation<T>(startDestination = RouteCompose.Main.Team.Requests()) {
        composable<RouteCompose.Main.Team.Employees> {
            val teamModel: TeamViewModel = hiltViewModel()
            Employees(viewModel = teamModel, onClickEdit = { id -> teamModel.onEmployeeAddEdictClick(id) })
        }
        composable<RouteCompose.Main.Team.EmployeeAddEdit> {
            val employeeModel: EmployeeViewModel = hiltViewModel()
            EmployeeForm(viewModel = employeeModel)
        }
        composable<RouteCompose.Main.Team.Users> {
            val teamModel: TeamViewModel = hiltViewModel()
            Users(viewModel = teamModel, isUsersPage = true)
        }
        composable<RouteCompose.Main.Team.Requests> {
            val teamModel: TeamViewModel = hiltViewModel()
            Users(viewModel = teamModel, isUsersPage = false)
        }
        composable<RouteCompose.Main.Team.EditUser> { backStackEntry ->
            val userId = backStackEntry.toRoute<RouteCompose.Main.Team.EditUser>().userId
            UserForm(userId = userId)
        }
        composable<RouteCompose.Main.Team.AuthorizeUser>(
            deepLinks = listOf(navDeepLink<RouteCompose.Main.Team.AuthorizeUser>(DOMAIN) { action = Intent.ACTION_VIEW })
        ) { backStackEntry ->
            val userId = backStackEntry.toRoute<RouteCompose.Main.Team.AuthorizeUser>().userId
            UserForm(userId = userId)
        }
    }
}