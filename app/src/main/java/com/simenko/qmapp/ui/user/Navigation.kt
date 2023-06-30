package com.simenko.qmapp.ui.user

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.termsandconditions.TermsAndConditions
import com.simenko.qmapp.ui.theme.QMAppTheme

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.EnterDetails.route) {
        composable(route = Screen.EnterDetails.route) {
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
            route = Screen.TermsAndConditions.route + "/{name}",
            arguments = listOf(
                navArgument("user") {
                    type = NavType.StringType
                    defaultValue = "Roman"
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.EnterDetails.route)
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

sealed class Screen(val route: String) {
    object EnterDetails : Screen("enter_details")
    object TermsAndConditions : Screen("terms_and_conditions")
    object WaitingForVerification : Screen("waiting_for_verification")

    fun withArgs(vararg args: String) = buildString {
        append(route)
        args.forEach { arg ->
            append("/$arg")
        }
    }
}