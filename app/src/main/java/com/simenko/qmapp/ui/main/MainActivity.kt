package com.simenko.qmapp.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.simenko.qmapp.ui.BaseActivity
import com.simenko.qmapp.ui.main.main.content.*
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.AppNavigatorImpl.Companion.subscribeNavigationEvents
import com.simenko.qmapp.ui.navigation.MainScreen
import com.simenko.qmapp.ui.navigation.NavigationIntent
import com.simenko.qmapp.ui.navigation.RouteCompose
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    companion object {
        fun createMainActivityIntent(context: Context) = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    @Inject
    lateinit var appNavigator: AppNavigator

    private lateinit var analytics: FirebaseAnalytics

    val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalMaterialApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchAndActivateRemoteConfigValues()

        if (
            ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        analytics = Firebase.analytics

        setContent {
            val topBarSetup by viewModel.topBarSetup.collectAsStateWithLifecycle()
            val topTabsSetup by viewModel.topTabsSetup.collectAsStateWithLifecycle()
            val fabSetup by viewModel.fabSetup.collectAsStateWithLifecycle()
            val pullRefreshSetup by viewModel.pullRefreshSetup.collectAsStateWithLifecycle()

            QMAppTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                LaunchedEffect(key1 = Unit) {
                    appNavigator.navigationChannel.subscribeNavigationEvents(this, navController)
                }

                val drawerMenuState by topBarSetup.drawerMenuState.collectAsStateWithLifecycle()
                val searchBarState by topBarSetup.searchBarState.collectAsStateWithLifecycle()

                val observerLoadingProcess by pullRefreshSetup.isLoadingInProgress.collectAsStateWithLifecycle()
                val observerIsNetworkError by pullRefreshSetup.isErrorMessage.collectAsStateWithLifecycle()

                BackHandler(enabled = drawerMenuState.isOpen, onBack = { scope.launch { topBarSetup.onNavMenuClick?.invoke(false) } })
                BackHandler(enabled = searchBarState, onBack = { topBarSetup.setSearchBarState(false) })
                BackHandler(enabled = !drawerMenuState.isOpen && !searchBarState) { this@MainActivity.moveTaskToBack(true) }

                fun onDrawerItemClick(id: String) {
                    if (id != topBarSetup.link) {
                        when (id) {
                            RouteCompose.Main.Team::class.qualifiedName -> viewModel.onDrawerMenuTeamSelected()
                            RouteCompose.Main.CompanyStructure::class.qualifiedName -> viewModel.onDrawerMenuCompanyStructureSelected()
                            RouteCompose.Main.ProductLines::class.qualifiedName -> viewModel.onDrawerMenuProductsSelected()
                            RouteCompose.Main.AllInvestigations::class.qualifiedName -> viewModel.onDrawerMenuInvSelected()
                            RouteCompose.Main.ProcessControl::class.qualifiedName -> viewModel.onDrawerMenuProcessControlSelected()
                            RouteCompose.Main.Settings::class.qualifiedName -> viewModel.onDrawerMenuSettingsSelected()
                            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                        }
                    }
                    scope.launch { topBarSetup.onNavMenuClick?.invoke(false) }
                }

                ModalNavigationDrawer(
                    gesturesEnabled = topBarSetup.navIcon == Icons.Filled.Menu,
                    drawerState = drawerMenuState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                        ) {
                            DrawerHeader(userInfo = viewModel.userInfo)
                            DrawerBody(
                                selectedItemId = topBarSetup.link,
                                onDrawerItemClick = { id -> onDrawerItemClick(id) }
                            )
                        }
                    },
                    content = {
                        Scaffold(
                            topBar = { AppBar(topBarSetup = topBarSetup) },
                            floatingActionButton = {
                                if (fabSetup.fabAction != null && fabSetup.fabIcon != null && fabSetup.isFabVisible)
                                    FloatingActionButton(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        onClick = { fabSetup.fabAction!!.invoke() },
                                        content = {
                                            Icon(
                                                imageVector = fabSetup.fabIcon!!,
                                                contentDescription = "Floating action button",
                                                tint = MaterialTheme.colorScheme.onTertiary
                                            )
                                        }
                                    )
                            },
                            floatingActionButtonPosition = fabSetup.fabPosition
                        ) {

                            val pullRefreshState = rememberPullRefreshState(
                                refreshing = observerLoadingProcess,
                                onRefresh = { pullRefreshSetup.refreshAction?.invoke() }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(all = 0.dp)
                                    .pullRefresh(pullRefreshState)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(it)
                                ) {
                                    TopTabs(topTabsSetup)
                                    MainScreen(navController, it)
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
                            if (observerIsNetworkError != null) {
                                Toast.makeText(this, observerIsNetworkError, Toast.LENGTH_SHORT).show()
                                pullRefreshSetup.onNetworkErrorShown()
                            }
                        }
                    }
                )
            }
        }
    }


    private fun fetchAndActivateRemoteConfigValues() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Fetch failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }
}