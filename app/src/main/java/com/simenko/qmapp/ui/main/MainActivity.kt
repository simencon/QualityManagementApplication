package com.simenko.qmapp.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.ui.main.main.content.*
import com.simenko.qmapp.ui.navigation.MainScreen
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.works.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

internal const val INITIAL_ROUTE = "INITIATED_ROUTE"

fun createMainActivityIntent(context: Context, route: String = DrawerMenuItems.startingDrawerMenuItem.tag): Intent {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(INITIAL_ROUTE, route)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    return intent
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var workManager: WorkManager
    private lateinit var syncLastHourOneTimeWork: OneTimeWorkRequest
    private lateinit var syncLastDayOneTimeWork: OneTimeWorkRequest
    private lateinit var analytics: FirebaseAnalytics

    private lateinit var initialRoute: String

    val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navController: NavHostController

    @OptIn(ExperimentalMaterialApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialRoute = intent.extras?.getString(INITIAL_ROUTE) ?: DrawerMenuItems.startingDrawerMenuItem.tag

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
                val scope = rememberCoroutineScope()
                navController = rememberNavController()

                val selectedDrawerMenuItemId by topBarSetup.selectedDrawerMenuItemId.collectAsStateWithLifecycle()
                val drawerMenuState by topBarSetup.drawerMenuState.collectAsStateWithLifecycle()
                val searchBarState by topBarSetup.searchBarState.collectAsStateWithLifecycle()
                val isFabVisible by fabSetup.isFabVisible.collectAsStateWithLifecycle()
                val fabPosition by fabSetup.fabPosition.collectAsStateWithLifecycle()

                val observerLoadingProcess by pullRefreshSetup.isLoadingInProgress.collectAsStateWithLifecycle()
                val observerIsNetworkError by pullRefreshSetup.isErrorMessage.collectAsStateWithLifecycle()

                BackHandler(enabled = drawerMenuState.isOpen, onBack = { scope.launch { topBarSetup.setDrawerMenuState(false) } })
                BackHandler(enabled = searchBarState, onBack = { topBarSetup.setSearchBarState(false) })
                BackHandler(enabled = !drawerMenuState.isOpen && !searchBarState) { this@MainActivity.moveTaskToBack(true) }

                fun onDrawerItemClick(id: String) {
                    if (id != selectedDrawerMenuItemId) {
                        topBarSetup.setDrawerMenuItemId(id)
                        when (id) {
                            Route.Main.Team.link -> viewModel.onDrawerMenuTeamSelected()
                            Route.Main.Inv.link -> viewModel.onDrawerMenuInvSelected()
                            Route.Main.ProcessControl.link -> viewModel.onDrawerMenuProcessControlSelected()
                            Route.Main.Settings.link -> viewModel.onDrawerMenuSettingsSelected()
                            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                        }
                    }
                    scope.launch { topBarSetup.setDrawerMenuState(false) }
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
                                selectedItemId = selectedDrawerMenuItemId,
                                onDrawerItemClick = { id -> onDrawerItemClick(id) }
                            )
                        }
                    },
                    content = {
                        Scaffold(
                            topBar = { AppBar(topBarSetup = topBarSetup) },
                            floatingActionButton = {
                                if (fabSetup.fabAction != null && fabSetup.fabIcon != null && isFabVisible)
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
                            floatingActionButtonPosition = fabPosition
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
                                    MainScreen(
                                        viewModel,
                                        navController
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

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun prepareOneTimeWorks() {
        syncLastHourOneTimeWork = OneTimeWorkRequestBuilder<SyncEntitiesWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                Data.Builder()
                    .putLong(WorkerKeys.LATEST_MILLIS, SyncPeriods.LAST_HOUR.latestMillis)
                    .putLong(WorkerKeys.EXCLUDE_MILLIS, SyncPeriods.LAST_HOUR.excludeMillis)
                    .build()
            )
            .setInitialDelay(Duration.ofSeconds(5))
            .build()

        syncLastDayOneTimeWork = OneTimeWorkRequestBuilder<SyncEntitiesWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInitialDelay(Duration.ofSeconds(5))
            .build()
    }
}

data class CreatedRecord(
    val orderId: Int = NoRecord.num,
    val subOrderId: Int = NoRecord.num
)