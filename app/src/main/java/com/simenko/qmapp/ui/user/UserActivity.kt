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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.navigation.InitialScreen
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.login.LoginViewModel
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel
import com.simenko.qmapp.ui.user.verification.WaitingForVerificationViewModel
import dagger.hilt.android.AndroidEntryPoint
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
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var regModel: RegistrationViewModel
    private lateinit var enterDetModel: EnterDetailsViewModel
    private lateinit var verificationModel: WaitingForVerificationViewModel
    private lateinit var loginModel: LoginViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QMAppTheme {
                val observerLoadingProcess by userViewModel.isLoadingInProgress.collectAsStateWithLifecycle()

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
                            InitialScreen(userViewModel = userViewModel)
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
        this.regModel.initUserViewModel(userViewModel)
    }

    fun initEnterDetModel(enterDetModel: EnterDetailsViewModel) {
        this.enterDetModel = enterDetModel
    }

    fun initVerificationModel(verificationModel: WaitingForVerificationViewModel) {
        this.verificationModel = verificationModel
        this.verificationModel.initUserViewModel(userViewModel)
    }

    fun initLoginModel(loginModel: LoginViewModel) {
        this.loginModel = loginModel
        this.loginModel.initUserViewModel(userViewModel)
    }
}