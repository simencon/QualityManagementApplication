package com.simenko.qmapp.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr

const val LOGGED_OUT_ROOT = "logged_out"
const val REGISTRATION_ROOT = "registration"
const val MAIN_ROUTE = "main"
const val TEAM_ROUTE = "team"
const val SETTINGS_ROUTE = "settings"

object NavRouteName {
    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
//    const val logged_out = "logged_out"

    const val initial_screen = "initial_screen"

//    const val registration = "registration"
    const val enter_details = "enter_details"
    const val terms_and_conditions = "terms_and_conditions"

    const val waiting_for_validation = "waiting_for_validation"

    const val log_in = "log_in"

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
//    const val main = "main"

    const val company_profile = "company_profile"

//    const val team = "team"
    const val employees = "employees"
    const val employee_add_edit = "employee_add_edit"
    const val users = "users"
    const val edit_user = "edit_user"
    const val requests = "requests"
    const val authorize_user = "authorize_user"

    const val company_structure = "company_structure"

    const val company_products = "company_products"

    const val all_investigations = "all_investigations"

    const val process_control = "process_control"

    const val order_add_edit = "order_add_edit"

    const val sub_order_add_edit = "sub_order_add_edit"

    const val scrap_level = "scrap_level"

//    const val settings = "settings"
    const val user_details = "user_details"
    const val edit_user_details = "edit_user_details"
    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
}

object NavArguments {
    const val domain = "https://qm.simple.com"

    const val fullName = "name"
    const val message = "message"

    const val employeeId = "employeeId"
    const val userId = "userId"

    const val orderId = "orderId"
    const val subOrderId = "subOrderId"
    const val subOrderAddEditMode = "subOrderAddEditMode"
}

sealed class Route(
    val link: String,
    val arguments: List<NamedNavArgument> = emptyList(),
    val deepLinks: List<NavDeepLink> = emptyList(),
    val route: String = EmptyString.str
) {
    object LoggedOut : Route( link = LOGGED_OUT_ROOT) {
        object InitialScreen : Route(link = NavRouteName.initial_screen, route = LOGGED_OUT_ROOT)
        object Registration : Route(link = REGISTRATION_ROOT, route = LOGGED_OUT_ROOT) {
            object EnterDetails : Route(link = NavRouteName.enter_details, route = REGISTRATION_ROOT)
            object TermsAndConditions : Route(
                link = "${NavRouteName.terms_and_conditions}/{${NavArguments.fullName}}",
                arguments = listOf(
                    navArgument(name = NavArguments.fullName) {
                        type = NavType.StringType
                        defaultValue = "Roman"
                        nullable = true
                    }
                ),
                route = REGISTRATION_ROOT
            )
        }

        object WaitingForValidation : Route(
            link = "${NavRouteName.waiting_for_validation}/{${NavArguments.message}}",
            arguments = listOf(
                navArgument(NavArguments.message) {
                    type = NavType.StringType
                    defaultValue = "Verification link was sent to your email"
                    nullable = true
                }
            ),
            route = LOGGED_OUT_ROOT
        )

        object LogIn : Route(link = NavRouteName.log_in, route = LOGGED_OUT_ROOT)
    }

    object Main : Route(link = MAIN_ROUTE) {
        object CompanyProfile : Route(link = NavRouteName.company_profile, route = MAIN_ROUTE)
        object Team : Route(link = TEAM_ROUTE, route = MAIN_ROUTE) {
            object Employees : Route(
                link = "${NavRouteName.employees}${arg(NavArguments.employeeId)}",
                arguments = listOf(
                    navArgument(name = NavArguments.employeeId) {
                        type = NavType.IntType
                        defaultValue = NoRecord.num
                    }
                ),
                route = TEAM_ROUTE
            )

            object EmployeeAddEdit : Route(
                link = "${NavRouteName.employee_add_edit}${arg(NavArguments.employeeId)}",
                arguments = listOf(
                    navArgument(name = NavArguments.employeeId) {
                        type = NavType.IntType
                        defaultValue = NoRecord.num
                    }
                ),
                route = TEAM_ROUTE
            )

            object Users : Route(
                link = "${NavRouteName.users}${arg(NavArguments.userId)}",
                arguments = listOf(
                    navArgument(name = NavArguments.userId) {
                        type = NavType.StringType
                        defaultValue = NoRecordStr.str
                    }
                ),
                route = TEAM_ROUTE
            )

            object EditUser : Route(
                link = "${NavRouteName.edit_user}${arg(NavArguments.userId)}",
                arguments = listOf(
                    navArgument(name = NavArguments.userId) {
                        type = NavType.StringType
                        defaultValue = NoRecordStr.str
                    }
                ),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$TEAM_ROUTE/${NavRouteName.users}/$link"
                        action = Intent.ACTION_VIEW
                    }
                ),
                route = TEAM_ROUTE
            )

            object Requests : Route(
                link = "${NavRouteName.requests}${arg(NavArguments.userId)}",
                arguments = listOf(
                    navArgument(name = NavArguments.userId) {
                        type = NavType.StringType
                        defaultValue = NoRecordStr.str
                    }
                ),
                route = TEAM_ROUTE
            )

            object AuthorizeUser : Route(
                link = "${NavRouteName.authorize_user}${arg(NavArguments.userId)}",
                arguments = listOf(
                    navArgument(name = NavArguments.userId) {
                        type = NavType.StringType
                        defaultValue = NoRecordStr.str
                    }
                ),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$TEAM_ROUTE/${NavRouteName.users}/$link"
                        action = Intent.ACTION_VIEW
                    }
                ),
                route = TEAM_ROUTE
            )
        }

        object CompanyStructure : Route(link = NavRouteName.company_structure, route = MAIN_ROUTE)
        object CompanyProducts : Route(link = NavRouteName.company_products, route = MAIN_ROUTE)
        object Inv : Route(
            link = "${NavRouteName.all_investigations}?${opt(NavArguments.orderId)}&${opt(NavArguments.subOrderId)}",
            arguments = listOf(
                navArgument(NavArguments.orderId) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(NavArguments.subOrderId) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                }
            ),
            route = MAIN_ROUTE
        )

        object ProcessControl : Route(
            link = "${NavRouteName.process_control}?${opt(NavArguments.orderId)}&${opt(NavArguments.subOrderId)}",
            arguments = listOf(
                navArgument(NavArguments.orderId) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(NavArguments.subOrderId) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                }
            ),
            route = MAIN_ROUTE
        )

        object OrderAddEdit : Route(
            link = "${NavRouteName.order_add_edit}${arg(NavArguments.orderId)}",
            arguments = listOf(
                navArgument(NavArguments.orderId) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                }
            ),
            route = MAIN_ROUTE
        )

        object SubOrderAddEdit : Route(
            link = "${NavRouteName.sub_order_add_edit}${arg(NavArguments.orderId)}${arg(NavArguments.subOrderId)}${arg(NavArguments.subOrderAddEditMode)}",
            arguments = listOf(
                navArgument(NavArguments.orderId) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(NavArguments.subOrderId) {
                    type = NavType.IntType
                    defaultValue = NoRecord.num
                },
                navArgument(NavArguments.subOrderAddEditMode) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
            route = MAIN_ROUTE
        )

        object ScrapLevel : Route(link = NavRouteName.scrap_level, route = MAIN_ROUTE)

        object Settings : Route(link = SETTINGS_ROUTE, route = MAIN_ROUTE) {
            object UserDetails : Route(link = NavRouteName.user_details, route = SETTINGS_ROUTE)
            object EditUserDetails : Route(link = NavRouteName.edit_user_details, route = SETTINGS_ROUTE)
        }
    }

    fun withArgs(vararg args: String) = link.withArgs(*args)

    fun withOpts(vararg args: String) = link.withOpts(*args)

    companion object {
        fun opt(p: String) = "$p={$p}"
        fun arg(p: String) = "/{$p}"

        fun String.withArgs(vararg args: String): String {
            val link = this
            return buildString {
                append(link.split("/")[0])
                args.forEach { arg ->
                    append("/$arg")
                }
            }
        }

        private fun String.getParamsNames(): List<String> {
            val list = mutableListOf<String>()
            val rawList = this.split('?')[1].split('&')
            rawList.forEach { item ->
                if (item.find { it == '{' } != null) {
                    list.add(item.substringAfter('{').substringBefore('}'))
                }
            }
            return list.toList()
        }

        fun String.withOpts(vararg args: String): String {
            val link = this
            val list = link.getParamsNames()
            var index = 0

            return buildString {
                append(link.split("?")[0])
                args.forEach { arg ->
                    if (index == 0) {
                        append("?${list[index]}=$arg")
                        index++
                    } else {
                        append("&${list[index]}=$arg")
                        index++
                    }
                }
            }
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