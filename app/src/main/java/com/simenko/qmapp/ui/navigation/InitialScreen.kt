package com.simenko.qmapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.simenko.qmapp.repository.NoState
import com.simenko.qmapp.repository.UnregisteredState
import com.simenko.qmapp.repository.UserAuthoritiesNotVerifiedState
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UserLoggedInState
import com.simenko.qmapp.repository.UserLoggedOutState
import com.simenko.qmapp.repository.UserNeedToVerifyEmailState
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.LoadingScreen
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
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    QMAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val context = LocalContext.current
            val userState by userViewModel.userState.collectAsStateWithLifecycle()

            LaunchedEffect(userState) {
                userState.let { state ->
                    when (state) {
                        is NoState -> userViewModel.onStateIsNoState()
                        is UnregisteredState -> userViewModel.onStateIsUnregisteredState()
                        is UserNeedToVerifyEmailState -> userViewModel.onStateIsUserNeedToVerifyEmailState(state.msg)
                        is UserAuthoritiesNotVerifiedState -> userViewModel.onStateIsUserAuthoritiesNotVerifiedState(state.msg)
                        is UserLoggedOutState -> userViewModel.onStateIsUserLoggedOutState()
                        is UserLoggedInState -> userViewModel.onStateIsUserLoggedInState(context)
                        is UserErrorState -> {}
                    }
                }
            }

            NavHost(navController = navController, startDestination = RouteCompose.LoggedOut.InitialScreen, route = RouteCompose.LoggedOut::class) {

                composable<RouteCompose.LoggedOut.InitialScreen> {
                    LoadingScreen()
                }

                navigation<RouteCompose.LoggedOut.Registration>(startDestination = RouteCompose.LoggedOut.Registration.EnterDetails()) {
                    composable<RouteCompose.LoggedOut.Registration.EnterDetails> { backStackEntry ->
                        val isUserEditMode = backStackEntry.toRoute<RouteCompose.Main.Settings.EditUserDetails>().userEditMode
                        val enterDetModel: EnterDetailsViewModel = hiltViewModel()
                        EnterDetails(viewModel = enterDetModel, editMode = isUserEditMode)
                    }

                    composable<RouteCompose.LoggedOut.Registration.TermsAndConditions> {
                        val args = it.toRoute<RouteCompose.LoggedOut.Registration.TermsAndConditions>()
                        val regModel: RegistrationViewModel = hiltViewModel()

                        TermsAndConditions(
                            viewModel = regModel,
                            user = args.fullName,
                            onDismiss = { regModel.hideUserExistDialog() },
                            onChangeEmail = { regModel.onChangeRegistrationEmailClick() },
                            onLogin = { regModel.onProceedToLoginClick() }
                        )
                    }
                }

                composable<RouteCompose.LoggedOut.WaitingForValidation> {
                    val verificationModel: WaitingForVerificationViewModel = hiltViewModel()
                    WaitingForVerification(viewModel = verificationModel, message = it.arguments?.getString(NavArguments.message))
                }

                composable<RouteCompose.LoggedOut.LogIn> {
                    val loginModel: LoginViewModel = hiltViewModel()

                    LogIn(loginModel)
                }
            }
        }
    }
}