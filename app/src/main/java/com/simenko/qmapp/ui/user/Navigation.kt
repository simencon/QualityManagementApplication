package com.simenko.qmapp.ui.user

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.termsandconditions.TermsAndConditions
import com.simenko.qmapp.ui.theme.QMAppTheme

@Composable
fun Navigation(
    initiatedRoute: String
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = initiatedRoute) {
        navigation(
            startDestination = Screen.EnterDetails.destination,
            route = Screen.EnterDetails.route()
        ) {
            composable(route = Screen.EnterDetails.destination) {
                QMAppTheme {
                    EnterDetails(
                        modifier = Modifier
                            .padding(all = 0.dp)
                            .fillMaxSize(),
                        navController = navController
                    )
                }
            }
            composable(
                route = Screen.TermsAndConditions.destination + "/{name}",
                arguments = listOf(
                    navArgument("user") {
                        type = NavType.StringType
                        defaultValue = "Roman"
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.EnterDetails.destination)
                }
                val registrationViewModel = hiltViewModel<RegistrationViewModel>(parentEntry)
                QMAppTheme {
                    TermsAndConditions(
                        modifier = Modifier
                            .padding(all = 0.dp)
                            .fillMaxWidth(),
                        navController = navController,
                        user = backStackEntry.arguments?.getString("name"),
                        registrationViewModel = registrationViewModel
                    )
                }
            }
        }
    }
}

sealed class Screen(val destination: String) {
    object Home : Screen("home")
    object EnterDetails : Screen("enter_details")
    object TermsAndConditions : Screen("terms_and_conditions")
    object LogIn : Screen("log_in")
    object WaitingForEmailVerification : Screen("waiting_for_email_verification")
    object WaitingForVerificationByOrganisation : Screen("waiting_for_verification_by_organisation")

    fun route() = buildString {
        append(destination)
        append("_")
        append("route")
    }

    fun withArgs(vararg args: String) = buildString {
        append(destination)
        args.forEach { arg ->
            append("/$arg")
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    Log.d("Navigation", "sharedViewModel: $navGraphRoute")
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}