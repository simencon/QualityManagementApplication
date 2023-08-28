package com.simenko.qmapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

sealed class Screen(val route: String) {

    object LoggedOut: Screen("logged_out") {
        object Registration : Screen("registration") {
            object EnterDetails : Screen("enter_details")
            object TermsAndConditions : Screen("terms_and_conditions")
        }

        object WaitingForValidation : Screen("waiting_for_validation")

        object LogIn : Screen("log_in")
    }

    object Main : Screen("main") {
        object CompanyProfile: Screen("company_profile")
        object Employees: Screen("employees")
        object CompanyStructure: Screen("company_structure")
        object CompanyProducts: Screen("company_products")
        object AllInvestigations: Screen("all_investigations")
        object ProcessControl: Screen("process_control")
        object ScrapLevel: Screen("scrap_level")
        object Settings: Screen("settings")
        object NewOrder : Screen("new_order")
    }

    fun withArgs(vararg args: String) = buildString {
        append(route)
        args.forEach { arg ->
            append("/$arg")
        }
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
