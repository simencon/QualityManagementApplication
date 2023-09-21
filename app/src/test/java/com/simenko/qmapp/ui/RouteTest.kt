package com.simenko.qmapp.ui

import com.google.common.truth.Truth
import com.simenko.qmapp.domain.FalseStr
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.ui.Route.Companion.withArgs
import com.simenko.qmapp.ui.Route.Companion.withOpts
import org.junit.Test

class RouteTest {
    @Test
    fun `with args function returns proper link with arguments`() {
        /**
         * Terms and conditions link check
         * */
        val termsAndConditions = Route.LoggedOut.Registration.TermsAndConditions.link
        val termsAndConditionsWithArgs = termsAndConditions.withArgs("Roman Semenyshyn")
        Truth.assertThat(termsAndConditionsWithArgs).isEqualTo("${NavRouteName.terms_and_conditions}/Roman Semenyshyn")
        /**
         * Waiting for validation link check
         * */
        val waitingForValidation = Route.LoggedOut.WaitingForValidation.link
        val waitingForValidationWithArgs = waitingForValidation.withArgs("wait for validation")
        Truth.assertThat(waitingForValidationWithArgs).isEqualTo("${NavRouteName.waiting_for_validation}/wait for validation")
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
        /**
         * Order add/edit link check
         * */
        val orderAddEdit = Route.Main.OrderAddEdit.link
        val orderAddEditWithArgs = orderAddEdit.withArgs(NoRecordStr.str)
        Truth.assertThat(orderAddEditWithArgs).isEqualTo("${NavRouteName.order_add_edit}/${NoRecordStr.str}")
        /**
         * Sub order add/edit link check
         * */
        val subOrderAddEdit = Route.Main.SubOrderAddEdit.link
        val subOrderAddEditWithArgs = subOrderAddEdit.withArgs(NoRecordStr.str, NoRecordStr.str, FalseStr.str)
        Truth.assertThat(subOrderAddEditWithArgs).isEqualTo("${NavRouteName.sub_order_add_edit}/${NoRecordStr.str}/${NoRecordStr.str}/${FalseStr.str}")
    }

    @Test
    fun `with opts function returns link with optional arguments`() {
        /**
         * All investigations link check
         * */
        val invAll = Route.Main.Inv.link
        var invAllWithOptArgs = invAll.withOpts(NoRecordStr.str, NoRecordStr.str)

        Truth.assertThat(invAllWithOptArgs)
            .isEqualTo("${NavRouteName.all_investigations}?${NavArguments.orderId}=${NoRecordStr.str}&${NavArguments.subOrderId}=${NoRecordStr.str}")

        invAllWithOptArgs = invAll.withOpts()
        Truth.assertThat(invAllWithOptArgs).isEqualTo(NavRouteName.all_investigations)
        /**
         * Process control link check
         * */
        val processControl = Route.Main.ProcessControl.link
        var processControlWithOptArgs = processControl.withOpts(NoRecordStr.str, NoRecordStr.str)

        Truth.assertThat(processControlWithOptArgs)
            .isEqualTo("${NavRouteName.process_control}?${NavArguments.orderId}=${NoRecordStr.str}&${NavArguments.subOrderId}=${NoRecordStr.str}")

        processControlWithOptArgs = processControl.withOpts()
        Truth.assertThat(processControlWithOptArgs).isEqualTo(NavRouteName.process_control)
    }
}