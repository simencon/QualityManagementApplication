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
import com.simenko.qmapp.ui.NavArguments
import com.simenko.qmapp.ui.Route
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
        composable(route = Route.LoggedOut.InitialScreen.link) {
            QMAppTheme {
                InitialScreen()
            }
        }
        navigation(
            startDestination = Route.LoggedOut.Registration.EnterDetails.link,
            route = Route.LoggedOut.Registration.link
        ) {
            composable(route = Route.LoggedOut.Registration.EnterDetails.link, arguments = Route.LoggedOut.Registration.EnterDetails.arguments) {
                val regModel: RegistrationViewModel = hiltViewModel()
                (LocalContext.current as UserActivity).initRegModel(regModel)
                val enterDetModel: EnterDetailsViewModel = hiltViewModel()
                (LocalContext.current as UserActivity).initEnterDetModel(enterDetModel)
                QMAppTheme {
                    EnterDetails(
                        navController = navController,
                        editMode = it.arguments?.getBoolean(NavArguments.userEditMode) ?: false
                    )
                }
            }
            composable(route = Route.LoggedOut.Registration.TermsAndConditions.link, arguments = Route.LoggedOut.Registration.TermsAndConditions.arguments) {
                val regModel: RegistrationViewModel = it.sharedViewModel(navController = navController)
                QMAppTheme {
                    TermsAndConditions(
                        regModel = regModel,
                        user = it.arguments?.getString(NavArguments.name),
                        onDismiss = {
                            regModel.hideUserExistDialog()
                        },
                        onChangeEmail = {
                            regModel.hideUserExistDialog()
                            navController.popBackStack()
                        },
                        onLogin = {
                            regModel.hideUserExistDialog()
                            navController.navigate(Route.LoggedOut.LogIn.link)
                        }
                    )
                }
            }
        }
        composable(route = Route.LoggedOut.WaitingForValidation.link, arguments = Route.LoggedOut.WaitingForValidation.arguments) {
            val verificationModel: WaitingForVerificationViewModel = hiltViewModel()
            (LocalContext.current as UserActivity).initVerificationModel(verificationModel)
            QMAppTheme {
                WaitingForVerification(message = it.arguments?.getString(NavArguments.message))
            }
        }
        composable(route = Route.LoggedOut.LogIn.link) {
            val loginModel: LoginViewModel = hiltViewModel()
            (LocalContext.current as UserActivity).initLoginModel(loginModel)
            QMAppTheme {
                LogIn()
            }
        }
    }
}