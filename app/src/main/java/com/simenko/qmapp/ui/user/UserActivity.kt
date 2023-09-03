package com.simenko.qmapp.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.FalseStr
import com.simenko.qmapp.repository.NoState
import com.simenko.qmapp.repository.UserAuthoritiesNotVerifiedState
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UnregisteredState
import com.simenko.qmapp.repository.UserLoggedInState
import com.simenko.qmapp.repository.UserLoggedOutState
import com.simenko.qmapp.repository.UserNeedToVerifyEmailState
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.createMainActivityIntent
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.login.LoginViewModel
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel
import com.simenko.qmapp.ui.user.verification.WaitingForVerificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.util.Locale
import javax.inject.Inject

fun createLoginActivityIntent(
    context: Context
): Intent {
    val intent = Intent(context, UserActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    return intent
}

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class UserActivity : ComponentActivity() {
    @Inject
    lateinit var userRepository: UserRepository
    val viewModel: UserViewModel by viewModels()
    private lateinit var regModel: RegistrationViewModel
    private lateinit var enterDetModel: EnterDetailsViewModel
    private lateinit var verificationModel: WaitingForVerificationViewModel
    private lateinit var loginModel: LoginViewModel

    private lateinit var navController: NavHostController

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QMAppTheme {
                val observerLoadingProcess by viewModel.isLoadingInProgress.collectAsStateWithLifecycle()

                navController = rememberNavController()
                val userState by viewModel.userState.collectAsStateWithLifecycle()

                LaunchedEffect(userState) {
                    userState.let { state ->
                        when (state) {
                            is NoState -> {
                                navController.navigate(Screen.LoggedOut.InitialScreen.route) { popUpTo(0) { inclusive = true } }
                                viewModel.updateCurrentUserState()
                            }

                            is UnregisteredState -> {
                                viewModel.updateLoadingState(Pair(false, null))
                                navController.navigate(Screen.LoggedOut.Registration.route) { popUpTo(0) { inclusive = true } }
                            }

                            is UserNeedToVerifyEmailState -> {
                                viewModel.updateLoadingState(Pair(false, null))
                                navController.navigate(Screen.LoggedOut.WaitingForValidation.withArgs(state.msg)) { popUpTo(0) { inclusive = true } }
                                delay(5000)
                                viewModel.updateCurrentUserState()
                            }

                            is UserAuthoritiesNotVerifiedState -> {
                                viewModel.updateLoadingState(Pair(false, null))
                                navController.navigate(Screen.LoggedOut.WaitingForValidation.withArgs(state.msg)) { popUpTo(0) { inclusive = true } }
                                delay(5000)
                                viewModel.updateCurrentUserState()
                            }

                            is UserLoggedOutState -> {
                                viewModel.updateLoadingState(Pair(false, null))
                                navController.navigate(Screen.LoggedOut.LogIn.route) { popUpTo(0) { inclusive = true } }
                            }

                            is UserLoggedInState -> {
                                ContextCompat.startActivity(navController.context, createMainActivityIntent(navController.context), null)
                            }

                            is UserErrorState -> {}
                        }
                    }
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(text = stringResource(R.string.app_name).uppercase(Locale.getDefault()))
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                ) {
                    val pullRefreshState = rememberPullRefreshState(
                        refreshing = observerLoadingProcess,
                        onRefresh = {}
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                        ) {
                            Navigation(
                                navController,
                                Screen.LoggedOut.InitialScreen.route
                            )
                        }
                        PullRefreshIndicator(
                            refreshing = observerLoadingProcess,
                            state = pullRefreshState,
                            modifier = Modifier
                                .padding(it)
                                .align(Alignment.TopCenter),
                            backgroundColor = MaterialTheme.colorScheme.onSecondary,
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }

    fun initRegModel(regModel: RegistrationViewModel) {
        this.regModel = regModel
        this.regModel.initUserViewModel(viewModel)
    }

    fun initEnterDetModel(enterDetModel: EnterDetailsViewModel) {
        this.enterDetModel = enterDetModel
    }

    fun initVerificationModel(verificationModel: WaitingForVerificationViewModel) {
        this.verificationModel = verificationModel
        this.verificationModel.initUserViewModel(viewModel)
    }

    fun initLoginModel(loginModel: LoginViewModel) {
        this.loginModel = loginModel
        this.loginModel.initUserViewModel(viewModel)
    }
}