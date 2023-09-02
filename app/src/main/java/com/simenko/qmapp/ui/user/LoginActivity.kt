package com.simenko.qmapp.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.repository.NoState
import com.simenko.qmapp.repository.UserAuthoritiesNotVerifiedState
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UnregisteredState
import com.simenko.qmapp.repository.UserLoggedInState
import com.simenko.qmapp.repository.UserLoggedOutState
import com.simenko.qmapp.repository.UserNeedToVerifyEmailState
import com.simenko.qmapp.repository.UserRegisteredState
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.mainActivityIntent
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.login.LoginViewModel
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel
import com.simenko.qmapp.ui.user.verification.WaitingForVerificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

internal const val INITIAL_ROUTE = "INITIATED_ROUTE"
fun createLoginActivityIntent(
    context: Context,
    initiateRoute: String
): Intent {
    val intent = Intent(context, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra(INITIAL_ROUTE, initiateRoute)
    return intent
}

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private var initialRoute: String = EmptyString.str

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

        initialRoute = intent.extras?.getString(INITIAL_ROUTE) ?: Screen.LoggedOut.InitialScreen.route

        setContent {
            QMAppTheme {
                navController = rememberNavController()
                val userState by viewModel.userState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) { viewModel.updateCurrentUserState() }

                LaunchedEffect(userState) {
                    userState.let { state ->
                        when (state) {
                            is NoState -> navController.navigate(Screen.LoggedOut.InitialScreen.route) { popUpTo(0) { inclusive = true } }
                            is UnregisteredState -> navController.navigate(Screen.LoggedOut.Registration.route) { popUpTo(0) { inclusive = true } }
                            is UserRegisteredState -> regModel.showUserExistDialog()
                            is UserNeedToVerifyEmailState -> navController
                                .navigate(Screen.LoggedOut.WaitingForValidation.withArgs(state.msg)) { popUpTo(0) { inclusive = true } }

                            is UserAuthoritiesNotVerifiedState -> navController
                                .navigate(Screen.LoggedOut.WaitingForValidation.withArgs(state.msg)) { popUpTo(0) { inclusive = true } }

                            is UserLoggedOutState -> navController.navigate(Screen.LoggedOut.LogIn.route) { popUpTo(0) { inclusive = true } }
                            is UserLoggedInState -> {
                                startActivity(mainActivityIntent(this@LoginActivity))
                                this@LoginActivity.finish()
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