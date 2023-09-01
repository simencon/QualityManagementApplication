package com.simenko.qmapp.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.repository.UserAuthoritiesNotVerifiedState
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UserInitialState
import com.simenko.qmapp.repository.UserLoggedInState
import com.simenko.qmapp.repository.UserLoggedOutState
import com.simenko.qmapp.repository.UserNeedToVerifyEmailState
import com.simenko.qmapp.repository.UserRegisteredState
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.MainActivityCompose
import com.simenko.qmapp.ui.main.launchMainActivityCompose
import com.simenko.qmapp.ui.main.mainActivityIntent
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

internal const val INITIAL_ROUTE = "INITIATED_ROUTE"
fun createLoginActivityIntent(
    context: Context,
    initiateRoute: String
): Intent {
    val intent = Intent(context, LoginActivity::class.java)
    intent.putExtra(INITIAL_ROUTE, initiateRoute)
    return intent
}

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository
    val viewModel: UserViewModel by viewModels()

    private lateinit var navController: NavHostController

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initiateRoute = intent.extras?.getString(INITIAL_ROUTE) ?: ""
        setContent {
            QMAppTheme {
                navController = rememberNavController()
                val scope = rememberCoroutineScope()
                var navigateToProperScreen by rememberSaveable { mutableStateOf(false) }

                LaunchedEffect(navigateToProperScreen) {
                    if (navigateToProperScreen) {
                        scope.launch { navigateToProperScreen() }
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
                            Screen.LoggedOut.InitialScreen.route,
                            logInSuccess = { startActivity(mainActivityIntent(this@LoginActivity)) }
                        )
                        navigateToProperScreen = true
                    }
                }
            }
        }
    }

    private suspend fun navigateToProperScreen() {
        userRepository.getActualUserState().let { state ->
            when (state) {
                is UserInitialState -> navController.navigate(Screen.LoggedOut.Registration.route) { popUpTo(0) }
                is UserNeedToVerifyEmailState -> navController.navigate(Screen.LoggedOut.WaitingForValidation.withArgs(state.msg)) { popUpTo(0) }
                is UserAuthoritiesNotVerifiedState -> navController.navigate(Screen.LoggedOut.WaitingForValidation.withArgs(state.msg)) { popUpTo(0) }
                is UserLoggedOutState -> navController.navigate(Screen.LoggedOut.LogIn.route) { popUpTo(0) }
                is UserLoggedInState -> startActivity(mainActivityIntent(this@LoginActivity))
                is UserErrorState, is UserRegisteredState -> {}
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        lifecycleScope.launch {
            userRepository.getActualUserState()
        }
    }
}