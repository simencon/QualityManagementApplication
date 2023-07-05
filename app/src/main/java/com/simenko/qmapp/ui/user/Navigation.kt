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
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.termsandconditions.TermsAndConditions
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.login.LogIn
import com.simenko.qmapp.ui.user.verification.WaitingForVerification

private const val TAG = "Navigation"

@Composable
fun Navigation(
    initiatedRoute: String,
    logInSuccess: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = initiatedRoute) {
        navigation(
            startDestination = Screen.Registration.EnterDetails.route,
            route = Screen.Registration.route
        ) {
            composable(route = Screen.Registration.EnterDetails.route) {
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
                route = Screen.Registration.TermsAndConditions.route + "/{name}",
                arguments = listOf(
                    navArgument("user") {
                        type = NavType.StringType
                        defaultValue = "Roman"
                        nullable = true
                    }
                )
            ) {
                QMAppTheme {
                    TermsAndConditions(
                        modifier = Modifier
                            .padding(all = 0.dp)
                            .fillMaxWidth(),
                        navController = navController,
                        user = it.arguments?.getString("name"),
                        registrationViewModel = it.sharedViewModel(navController = navController)
                    )
                }
            }
        }
        composable(
            route = Screen.WaitingForEmailVerification.route + "/{message}",
            arguments = listOf(
                navArgument("message") {
                    type = NavType.StringType
                    defaultValue = "Verification link was sent to your email"
                    nullable = true
                }
            )
        ) {
            QMAppTheme {
                WaitingForVerification(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxSize(),
                    navController = navController,
                    logInSuccess = logInSuccess,
                    message = it.arguments?.getString("message")
                )
            }
        }
        composable(route = Screen.LogIn.route) {
            QMAppTheme {
                LogIn(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxWidth(),
                    navController = navController,
                    logInSuccess = logInSuccess
                )
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Registration : Screen("registration") {
        object EnterDetails : Screen("enter_details")
        object TermsAndConditions : Screen("terms_and_conditions")
    }

    object WaitingForEmailVerification : Screen("waiting_for_email_verification")
    object WaitingForVerificationByOrganisation : Screen("waiting_for_verification_by_organisation")
    object LogIn : Screen("log_in")

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