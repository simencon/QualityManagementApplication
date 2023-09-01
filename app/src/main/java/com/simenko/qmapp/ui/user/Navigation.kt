package com.simenko.qmapp.ui.user

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.sharedViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.termsandconditions.TermsAndConditions
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.login.LogIn
import com.simenko.qmapp.ui.user.verification.WaitingForVerification
import javax.inject.Inject

@Composable
fun Navigation(
    navController: NavHostController,
    initiatedRoute: String,
    logInSuccess: () -> Unit
) {
    NavHost(navController = navController, startDestination = initiatedRoute) {
        composable(
            route = Screen.LoggedOut.InitialScreen.route
        ) {
            QMAppTheme {
                InitialScreen()
            }
        }
        navigation(
            startDestination = Screen.LoggedOut.Registration.EnterDetails.route,
            route = Screen.LoggedOut.Registration.route
        ) {
            composable(route = Screen.LoggedOut.Registration.EnterDetails.route) {
                QMAppTheme {
                    EnterDetails(navController = navController)
                }
            }
            composable(
                route = Screen.LoggedOut.Registration.TermsAndConditions.route + "/{name}",
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
            route = Screen.LoggedOut.WaitingForValidation.route + "/{message}",
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
        composable(route = Screen.LoggedOut.LogIn.route) {
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