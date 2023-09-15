package com.simenko.qmapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.simenko.qmapp.domain.EmployeeId
import com.simenko.qmapp.domain.OrderId
import com.simenko.qmapp.domain.SubOrderId
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.SubOrderAddEditMode
import com.simenko.qmapp.domain.UserEditMode
import com.simenko.qmapp.domain.UserId

sealed class Screen(val route: String, private val argsKeys: String = EmptyString.str) {

    object LoggedOut : Screen("logged_out") {
        object InitialScreen: Screen("initial_screen")
        object Registration : Screen("registration") {
            object EnterDetails : Screen("enter_details")
            object TermsAndConditions : Screen("terms_and_conditions")
        }

        object WaitingForValidation : Screen("waiting_for_validation")

        object LogIn : Screen("log_in")
    }

    object Main : Screen("main") {
        object CompanyProfile : Screen("company_profile")
        object Team : Screen("team") {
            object Employees : Screen("employees", "/{${EmployeeId.str}}")
            object EmployeeAddEdit : Screen("employee_add_edit", "/{${EmployeeId.str}}")
            object Users : Screen("users", "/{${UserId.str}}")
            object UserEdit : Screen("user_edit", "/{${UserId.str}}")
        }
        object CompanyStructure : Screen("company_structure")
        object CompanyProducts : Screen("company_products")
        object Inv : Screen("all_investigations", "/{${OrderId.str}}/{${SubOrderId.str}}")
        object ProcessControl : Screen("process_control", "/{${OrderId.str}}/{${SubOrderId.str}}")
        object OrderAddEdit : Screen("order_add_edit", "/{${OrderId.str}}")
        object SubOrderAddEdit : Screen("sub_order_add_edit", "/{${OrderId.str}}/{${SubOrderId.str}}/{${SubOrderAddEditMode.str}}")
        object ScrapLevel : Screen("scrap_level")
        object Settings : Screen("settings") {
            object UserDetails : Screen("user_details")
            object EditUserDetails : Screen("edit_user_details", "/{${UserEditMode.str}}")
        }
    }

    fun withArgs(vararg args: String) = buildString {
        append(route)
        args.forEach { arg ->
            append("/$arg")
        }
    }

    fun routeWithArgKeys() = buildString {
        append(route)
        append(argsKeys)
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.startDestinationRoute ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry?.viewModel(): T? = this?.let {
    viewModel(viewModelStoreOwner = it)
}
