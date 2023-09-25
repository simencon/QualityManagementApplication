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
import androidx.navigation.compose.rememberNavController
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
import kotlinx.coroutines.launch

@Composable
fun InitialScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    NavigationEffects(
        navigationChannel = userViewModel.navigationChannel,
        navHostController = navController
    )

    QMAppTheme {
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

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = Route.LoggedOut.InitialScreen
            ) {
                composable(destination = Route.LoggedOut.InitialScreen) {
                    LoadingScreen()
                }

                navigation(startDestination = Route.LoggedOut.Registration.EnterDetails) {
                    composable(destination = Route.LoggedOut.Registration.EnterDetails) {
                        val enterDetModel: EnterDetailsViewModel = hiltViewModel()
                        EnterDetails(viewModel = enterDetModel)
                    }

                    composable(destination = Route.LoggedOut.Registration.TermsAndConditions) {
                        val regModel: RegistrationViewModel = hiltViewModel()

                        TermsAndConditions(
                            viewModel = regModel,
                            user = it.arguments?.getString(NavArguments.fullName),
                            onDismiss = { regModel.hideUserExistDialog() },
                            onChangeEmail = { regModel.onChangeRegistrationEmailClick() },
                            onLogin = { regModel.onProceedToLoginClick() }
                        )
                    }
                }

                composable(destination = Route.LoggedOut.WaitingForValidation) {
                    val verificationModel: WaitingForVerificationViewModel = hiltViewModel()
                    WaitingForVerification(viewModel = verificationModel, message = it.arguments?.getString(NavArguments.message))
                }

                composable(destination = Route.LoggedOut.LogIn) {
                    val loginModel: LoginViewModel = hiltViewModel()

                    LogIn(loginModel)
                }
            }
        }
    }
}