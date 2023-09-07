package com.simenko.qmapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.simenko.qmapp.domain.CurrentEmployeeIdKey
import com.simenko.qmapp.domain.CurrentOrderIdKey
import com.simenko.qmapp.domain.CurrentSubOrderIdKey
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.SubOrderAddEditModeKey
import com.simenko.qmapp.domain.UserEditModeKey

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
            object Employees : Screen("employees")
            object EmployeeAddEdit : Screen("employee_add_edit", "/{${CurrentEmployeeIdKey.str}}")
            object Users : Screen("users")
            object UserEdit : Screen("user_edit")
        }
        object CompanyStructure : Screen("company_structure")
        object CompanyProducts : Screen("company_products")
        object Inv : Screen("all_investigations", "/{${CurrentOrderIdKey.str}}/{${CurrentSubOrderIdKey.str}}")
        object ProcessControl : Screen("process_control", "/{${CurrentOrderIdKey.str}}/{${CurrentSubOrderIdKey.str}}")
        object OrderAddEdit : Screen("order_add_edit", "/{${CurrentOrderIdKey.str}}")
        object SubOrderAddEdit : Screen("sub_order_add_edit", "/{${CurrentOrderIdKey.str}}/{${CurrentSubOrderIdKey.str}}/{${SubOrderAddEditModeKey.str}}")
        object ScrapLevel : Screen("scrap_level")
        object Settings : Screen("settings") {
            object UserDetails : Screen("user_details")
            object EditUserDetails : Screen("edit_user_details", "/{${UserEditModeKey.str}}")
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
