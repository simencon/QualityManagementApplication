package com.simenko.qmapp.ui.main.team

import android.content.Intent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
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
        composable<RouteCompose.Main.Team.Employees> { backStackEntry ->
            val employeeId = backStackEntry.toRoute<RouteCompose.Main.Team.Employees>().employeeId
            val teamModel: TeamViewModel = hiltViewModel()
            Employees(viewModel = teamModel, employeeId = employeeId, onClickEdit = { id -> teamModel.onEmployeeAddEdictClick(id) })
        }
        composable<RouteCompose.Main.Team.Users> { backStackEntry ->
            val userId = backStackEntry.toRoute<RouteCompose.Main.Team.Users>().userId
            val teamModel: TeamViewModel = hiltViewModel()
            Users(viewModel = teamModel, userId = userId, isUsersPage = true)
        }
        composable<RouteCompose.Main.Team.Requests> { backStackEntry ->
            val userId = backStackEntry.toRoute<RouteCompose.Main.Team.Requests>().userId
            val teamModel: TeamViewModel = hiltViewModel()
            Users(viewModel = teamModel, userId = userId, isUsersPage = false)
        }

        composable<RouteCompose.Main.Team.EmployeeAddEdit> { backStackEntry ->
            val employeeId = backStackEntry.toRoute<RouteCompose.Main.Team.EmployeeAddEdit>().employeeId
            EmployeeForm(employeeId = employeeId)
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