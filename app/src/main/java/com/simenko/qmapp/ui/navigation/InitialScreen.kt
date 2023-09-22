package com.simenko.qmapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.UserActivity
import com.simenko.qmapp.ui.user.UserViewModel
import com.simenko.qmapp.ui.user.login.LogIn
import com.simenko.qmapp.ui.user.login.LoginViewModel
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel
import com.simenko.qmapp.ui.user.registration.termsandconditions.TermsAndConditions
import com.simenko.qmapp.ui.user.verification.WaitingForVerification
import com.simenko.qmapp.ui.user.verification.WaitingForVerificationViewModel

@Composable
fun InitialScreen(
    userViewModel: UserViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    NavigationEffects(
        navigationChannel = userViewModel.navigationChannel,
        navHostController = navController
    )

    QMAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = Route.LoggedOut.InitialScreen
            ) {
                navigation(startDestination = Route.LoggedOut.Registration.EnterDetails) {
                    composable(destination = Route.LoggedOut.Registration.EnterDetails) {
                        val regModel: RegistrationViewModel = hiltViewModel()
                        (LocalContext.current as UserActivity).initRegModel(regModel)
                        val enterDetModel: EnterDetailsViewModel = hiltViewModel()
                        (LocalContext.current as UserActivity).initEnterDetModel(enterDetModel)

                        EnterDetails()
                    }

                    composable(destination = Route.LoggedOut.Registration.TermsAndConditions) {
                        val regModel: RegistrationViewModel = it.sharedViewModel(navController = navController)

                        TermsAndConditions(
                            regModel = regModel,
                            user = it.arguments?.getString(NavArguments.fullName),
                            onDismiss = {
                                regModel.hideUserExistDialog()
                            },
                            onChangeEmail = {
                                regModel.hideUserExistDialog()
                                userViewModel.appNavigator.tryNavigateBack()
                            },
                            onLogin = {
                                regModel.hideUserExistDialog()
                                userViewModel.appNavigator.tryNavigateTo(Route.LoggedOut.LogIn.link)
                            }
                        )
                    }
                }

                composable(destination = Route.LoggedOut.WaitingForValidation) {
                    val verificationModel: WaitingForVerificationViewModel = hiltViewModel()
                    (LocalContext.current as UserActivity).initVerificationModel(verificationModel)

                    WaitingForVerification(message = it.arguments?.getString(NavArguments.message))
                }
                composable(destination = Route.LoggedOut.LogIn) {
                    val loginModel: LoginViewModel = hiltViewModel()
                    (LocalContext.current as UserActivity).initLoginModel(loginModel)

                    LogIn()
                }
            }
        }
    }
}