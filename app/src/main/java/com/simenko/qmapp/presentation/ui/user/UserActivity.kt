package com.simenko.qmapp.presentation.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.R
import com.simenko.qmapp.presentation.ui.BaseActivity
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.InitialScreen
import com.simenko.qmapp.presentation.theme.QMAppTheme
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
class UserActivity : BaseActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    private val userViewModel: UserViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition { userViewModel.isLoadingInProgress.value }
        }

        setContent {
            QMAppTheme {
                val navController = rememberNavController()

                LaunchedEffect(key1 = Unit) {
                    appNavigator.subscribeNavigationEvents(this, navController)
                }

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
                            InitialScreen(navController = navController, userViewModel = userViewModel)
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
}