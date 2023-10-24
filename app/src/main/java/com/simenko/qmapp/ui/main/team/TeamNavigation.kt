package com.simenko.qmapp.ui.main.team

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.main.team.employee.Employees
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeForm
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeViewModel
import com.simenko.qmapp.ui.main.team.forms.user.UserForm
import com.simenko.qmapp.ui.main.team.forms.user.UserViewModel
import com.simenko.qmapp.ui.main.team.user.Users
import com.simenko.qmapp.ui.navigation.composable
import com.simenko.qmapp.ui.navigation.navigation

fun NavGraphBuilder.teamNavigation() {
    navigation(startDestination = Route.Main.Team.Employees) {
        composable(destination = Route.Main.Team.Employees) {
            val teamModel: TeamViewModel = hiltViewModel()
            Employees(viewModel = teamModel, onClickEdit = { id -> teamModel.onEmployeeAddEdictClick(id) })
        }
        composable(destination = Route.Main.Team.EmployeeAddEdit) {
            val employeeModel: EmployeeViewModel = hiltViewModel()
            EmployeeForm(viewModel = employeeModel)
        }
        composable(destination = Route.Main.Team.Users) {
            val teamModel: TeamViewModel = hiltViewModel()
            Users(viewModel = teamModel, isUsersPage = true)
        }
        composable(destination = Route.Main.Team.Requests) {
            val teamModel: TeamViewModel = hiltViewModel()
            Users(viewModel = teamModel, isUsersPage = false)
        }
        editUser(Route.Main.Team.AuthorizeUser)
        editUser(Route.Main.Team.EditUser)
    }
}

private fun NavGraphBuilder.editUser(route: Route) {
    composable(destination = route) {
        val userModel: UserViewModel = hiltViewModel()
        UserForm(viewModel = userModel)
    }
}