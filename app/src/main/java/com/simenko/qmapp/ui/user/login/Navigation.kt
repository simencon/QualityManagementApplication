package com.simenko.qmapp.ui.user.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.login.views.LogIn
import com.simenko.qmapp.ui.user.login.views.WaitingForVerification

@Composable
fun Navigation(
    logInSuccess: () -> Unit,
    onClickUnregister: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.WaitingForEmailVerification.route) {
        composable(route = Screen.WaitingForEmailVerification.route) {
            QMAppTheme {
                WaitingForVerification(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxSize(),
                    navController = navController
                )
            }
        }
        composable(route = Screen.LogIn.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.WaitingForEmailVerification.route)
            }
            val logInViewModel = hiltViewModel<LoginViewModel>(parentEntry)
            QMAppTheme {
                LogIn(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxWidth(),
                    navController = navController,
                    logInViewModel = logInViewModel,
                    logInSuccess = logInSuccess,
                    onClickUnregister = onClickUnregister,
                )
            }
        }
    }
}

sealed class Screen(val route: String) {
    object LogIn : Screen("log_in")
    object WaitingForEmailVerification : Screen("waiting_for_email_verification")
    object WaitingForVerificationByOrganisation : Screen("waiting_for_verification_by_organisation")

    fun withArgs(vararg args: String) = buildString {
        append(route)
        args.forEach { arg ->
            append("/$arg")
        }
    }
}