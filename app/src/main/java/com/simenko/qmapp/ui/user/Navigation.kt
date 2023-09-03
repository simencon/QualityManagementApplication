package com.simenko.qmapp.ui.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.simenko.qmapp.domain.CurrentOrderIdKey
import com.simenko.qmapp.domain.FalseStr
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.domain.UserEditModeKey
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.sharedViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.termsandconditions.TermsAndConditions
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.login.LogIn
import com.simenko.qmapp.ui.user.login.LoginViewModel
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel
import com.simenko.qmapp.ui.user.verification.WaitingForVerification
import com.simenko.qmapp.ui.user.verification.WaitingForVerificationViewModel

@Composable
fun Navigation(
    navController: NavHostController,
    initiatedRoute: String
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
            startDestination = Screen.LoggedOut.Registration.EnterDetails.route + "/${FalseStr.str}",
            route = Screen.LoggedOut.Registration.route
        ) {
            composable(
                route = Screen.LoggedOut.Registration.EnterDetails.route +"/{${UserEditModeKey.str}}",
                arguments = listOf(
                    navArgument(UserEditModeKey.str) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                val regModel: RegistrationViewModel = hiltViewModel()
                (LocalContext.current as UserActivity).initRegModel(regModel)
                val enterDetModel: EnterDetailsViewModel = hiltViewModel()
                (LocalContext.current as UserActivity).initEnterDetModel(enterDetModel)
                QMAppTheme {
                    EnterDetails(
                        navController = navController,
                        editMode = it.arguments?.getBoolean(UserEditModeKey.str) ?: false
                    )
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
                val regModel: RegistrationViewModel = it.sharedViewModel(navController = navController)
                QMAppTheme {
                    TermsAndConditions(
                        regModel = regModel,
                        user = it.arguments?.getString("name"),
                        onDismiss = {
                            regModel.hideUserExistDialog()
                        },
                        onChangeEmail = {
                            regModel.hideUserExistDialog()
                            navController.popBackStack()
                        },
                        onLogin = {
                            regModel.hideUserExistDialog()
                            navController.navigate(Screen.LoggedOut.LogIn.route)
                        }
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
            val verificationModel: WaitingForVerificationViewModel = hiltViewModel()
            (LocalContext.current as UserActivity).initVerificationModel(verificationModel)
            QMAppTheme {
                WaitingForVerification(message = it.arguments?.getString("message"))
            }
        }
        composable(route = Screen.LoggedOut.LogIn.route) {
            val loginModel: LoginViewModel = hiltViewModel()
            (LocalContext.current as UserActivity).initLoginModel(loginModel)
            QMAppTheme {
                LogIn()
            }
        }
    }
}