package com.simenko.qmapp.ui.user.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.Home
import com.simenko.qmapp.ui.user.login.views.LogIn
import com.simenko.qmapp.ui.user.login.views.WaitingForVerification

@Composable
fun Navigation(
    initiatedRoute: String,
    logInSuccess: () -> Unit,
    onClickUnregister: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.destination) {
        composable(Screen.Home.destination) {
            QMAppTheme {
                Home(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxSize(),
                    navController = navController,
                    route = initiatedRoute
                )
            }
        }
        navigation(
            startDestination = Screen.WaitingForEmailVerification.destination,
            route = Screen.WaitingForEmailVerification.route()
        ) {
            composable(route = Screen.WaitingForEmailVerification.destination) {
                QMAppTheme {
                    WaitingForVerification(
                        modifier = Modifier
                            .padding(all = 0.dp)
                            .fillMaxSize(),
                        navController = navController,
                        logInSuccess = logInSuccess
                    )
                }
            }
        }
        navigation(
            startDestination = Screen.LogIn.destination,
            route = Screen.LogIn.route()
        ) {
            composable(route = Screen.LogIn.destination) {
                QMAppTheme {
                    LogIn(
                        modifier = Modifier
                            .padding(all = 0.dp)
                            .fillMaxWidth(),
                        navController = navController,
                        logInSuccess = logInSuccess,
                        onClickUnregister = onClickUnregister,
                    )
                }
            }
        }
    }
}

sealed class Screen(val destination: String) {
    object Home : Screen("home")
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