package com.simenko.qmapp.ui.main.team

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.simenko.qmapp.ui.main.team.employee.Employees
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeForm
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeViewModel
import com.simenko.qmapp.ui.main.team.forms.user.UserForm
import com.simenko.qmapp.ui.main.team.forms.user.UserViewModel
import com.simenko.qmapp.ui.main.team.user.Users
import com.simenko.qmapp.ui.navigation.RouteCompose

inline fun <reified T : Any> NavGraphBuilder.teamNavigation() {
    navigation<T>(startDestination = RouteCompose.Main.Team.Users()) {
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
        editUser<RouteCompose.Main.Team.AuthorizeUser>()
        editUser<RouteCompose.Main.Team.EditUser>()
    }
}

inline fun <reified T : Any> NavGraphBuilder.editUser() {
    composable<T> {
        val userModel: UserViewModel = hiltViewModel()
        UserForm(viewModel = userModel)
    }
}