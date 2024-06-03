package com.simenko.qmapp.ui

import com.google.common.truth.Truth
import com.simenko.qmapp.domain.FalseStr
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.ui.navigation.NavArguments
import com.simenko.qmapp.ui.navigation.NavRouteName
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.Route.Companion.withArgs
import com.simenko.qmapp.ui.navigation.Route.Companion.withOpts
import org.junit.Test

class RouteTest {
    @Test
    fun `with args function returns proper link with arguments`() {
        /**
         * Employees link check
         * */
        val employees = Route.Main.Team.Employees.link
        val employeesWithArgs = employees.withArgs(NoRecordStr.str)
        Truth.assertThat(employeesWithArgs).isEqualTo("${NavRouteName.employees}/${NoRecordStr.str}")
        /**
         * Employee add/edit link check
         * */
        val employeeAddEdit = Route.Main.Team.EmployeeAddEdit.link
        val employeeAddEditWithArgs = employeeAddEdit.withArgs(NoRecordStr.str)
        Truth.assertThat(employeeAddEditWithArgs).isEqualTo("${NavRouteName.employee_add_edit}/${NoRecordStr.str}")
        /**
         * Users link check
         * */
        val users = Route.Main.Team.Users.link
        val usersWithArgs = users.withArgs(NoRecordStr.str)
        Truth.assertThat(usersWithArgs).isEqualTo("${NavRouteName.users}/${NoRecordStr.str}")
        /**
         * User edit link check
         * */
        val usersEdit = Route.Main.Team.EditUser.link
        val usersEditEditWithArgs = usersEdit.withArgs(NoRecordStr.str)
        Truth.assertThat(usersEditEditWithArgs).isEqualTo("${NavRouteName.edit_user}/${NoRecordStr.str}")
        /**
         * Requests link check
         * */
        val requests = Route.Main.Team.Requests.link
        val requestsWithArgs = requests.withArgs(NoRecordStr.str)
        Truth.assertThat(requestsWithArgs).isEqualTo("${NavRouteName.requests}/${NoRecordStr.str}")
        /**
         * Authorize user request link check
         * */
        val authorizeUser = Route.Main.Team.AuthorizeUser.link
        val authorizeUserWithArgs = authorizeUser.withArgs(NoRecordStr.str)
        Truth.assertThat(authorizeUserWithArgs).isEqualTo("${NavRouteName.authorize_user}/${NoRecordStr.str}")
    }
}