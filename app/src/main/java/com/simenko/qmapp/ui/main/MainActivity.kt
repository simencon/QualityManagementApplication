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
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.ui.common.StateChangedEffect
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.ui.main.main.AppBar
import com.simenko.qmapp.ui.main.main.DrawerBody
import com.simenko.qmapp.ui.main.main.DrawerHeader
import com.simenko.qmapp.ui.main.main.MainActivityBase
import com.simenko.qmapp.ui.main.main.MenuItem
import com.simenko.qmapp.ui.main.main.TopTabs
import com.simenko.qmapp.ui.navigation.MainScreen
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.utils.BaseFilter
import com.simenko.qmapp.works.SyncEntitiesWorker
import com.simenko.qmapp.works.SyncPeriods
import com.simenko.qmapp.works.WorkerKeys
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

internal const val INITIAL_ROUTE = "INITIATED_ROUTE"

fun createMainActivityIntent(context: Context, route: String = MenuItem.getStartingDrawerMenuItem().id): Intent {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(INITIAL_ROUTE, route)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    return intent
}

@AndroidEntryPoint
class MainActivity : MainActivityBase() {

    @Inject
    lateinit var workManager: WorkManager
    private lateinit var syncLastHourOneTimeWork: OneTimeWorkRequest
    private lateinit var syncLastDayOneTimeWork: OneTimeWorkRequest
    private lateinit var analytics: FirebaseAnalytics

    private lateinit var initialRoute: String

    @OptIn(ExperimentalMaterialApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialRoute = intent.extras?.getString(INITIAL_ROUTE) ?: MenuItem.getStartingDrawerMenuItem().id

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
            val selectedDrawerMenuItemId by viewModel.selectedDrawerMenuItemId.collectAsStateWithLifecycle()

            val topBarSetup by viewModel.topBarSetup.collectAsStateWithLifecycle()
            val drawerMenuState by viewModel.drawerMenuState.collectAsStateWithLifecycle()
            val searchBarState by viewModel.searchBarState.collectAsStateWithLifecycle()
            val actionsMenuState by viewModel.actionsMenuState.collectAsStateWithLifecycle()

            val topTabsSetup by viewModel.topTabsSetup.collectAsStateWithLifecycle()

            StateChangedEffect(
                topScreenChannel = viewModel.topScreenChannel,
                onLoadingStateIntent = { viewModel.updateLoadingState(it) },
                onEndOfListIntent = { viewModel.onEndOfList(it) },
                onTopBadgeStateIntent = { p1, p2 -> topTabsSetup.setBadgeContent(p1, p2) },
                onTopScreenSetupIntent = { p1, p2, p3, p4 -> viewModel.setupTopScreen(p1, p2, p3, p4) },
                onTopScreenSetupDevIntent = { p1, p2, p3, p4 -> viewModel.setupTopScreenDev(p1, p2, p3, p4) }
            )

            QMAppTheme {
                val scope = rememberCoroutineScope()
                navController = rememberNavController()

                val selectedContextMenuItemId = rememberSaveable { mutableStateOf(MenuItem.getStartingActionsFilterMenuItem().id) }

                val fabPosition by viewModel.fabPosition.collectAsStateWithLifecycle()

                val refreshAction by viewModel.refreshAction.collectAsStateWithLifecycle()
                val observerLoadingProcess by viewModel.isLoadingInProgress.collectAsStateWithLifecycle()
                val observerIsNetworkError by viewModel.isErrorMessage.collectAsStateWithLifecycle()


                val backStackEntry = navController.currentBackStackEntryAsState()

                BackHandler(enabled = drawerMenuState.isOpen, onBack = { scope.launch { topBarSetup.onNavBtnClick?.let { it(false) } } })
                BackHandler(enabled = searchBarState, onBack = { topBarSetup.onSearchBtnClick?.let { it(false) } })
                BackHandler(enabled = !drawerMenuState.isOpen && !searchBarState) { this@MainActivity.moveTaskToBack(true) }



                val addEditMode by viewModel.addEditMode.collectAsStateWithLifecycle()
                val addEditAction by viewModel.addEditAction.collectAsStateWithLifecycle()
                val filterAction by viewModel.filterAction.collectAsStateWithLifecycle()
                val onTabSelectedLambda = remember<(SelectedNumber, Int) -> Unit> {
                    { tabId, tabIndex ->
                        viewModel.setSelectedTabIndex(
                            onTabSelectedLambda(backStackEntry, tabIndex) { filterAction(BaseFilter(statusId = tabId.num)) }
                        )
                    }
                }

                LaunchedEffect(backStackEntry.value?.destination?.route) {
                    super.selectProperTab(backStackEntry)?.let { viewModel.setSelectedTabIndex(it) }
                    super.setProperAddEditMode(backStackEntry)
                }

                ModalNavigationDrawer(
                    gesturesEnabled = addEditMode == AddEditMode.NO_MODE.ordinal,
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
                                onDrawerItemClick = { id ->
                                    scope.launch { topBarSetup.onNavBtnClick?.let { it(false) } }
                                    super.onDrawerItemClick(selectedDrawerMenuItemId, id)?.let { viewModel.setSelectedTabIndex(it) }
                                }
                            )
                        }
                    },
                    content = {
                        Scaffold(
                            topBar = {
                                AppBar(
                                    topBarSetup = topBarSetup,

                                    drawerState = drawerMenuState,

                                    searchBarState = searchBarState,

                                    actionsMenuState = actionsMenuState,

                                    selectedActionsMenuItemId = selectedContextMenuItemId,

                                    onActionsMenuItemClick = { f, a -> selectedContextMenuItemId.value = super.onActionsMenuItemClick(f, a) }
                                )
                            },
                            floatingActionButton = {
                                if (super.showFab(backStackEntry, addEditMode))
                                    FloatingActionButton(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        onClick = { super.onFabClick(backStackEntry, addEditMode, addEditAction) },
                                        content = { super.FabContent(addEditMode = addEditMode) }
                                    )
                            },
                            floatingActionButtonPosition = fabPosition
                        ) {
                            val pullRefreshState = rememberPullRefreshState(
                                refreshing = observerLoadingProcess,
                                onRefresh = { super.onPullRefresh(backStackEntry, refreshAction) }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(all = 0.dp)
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(it)
                                ) {
                                    TopTabs(
                                        topTabsSetup,
                                        onTabSelectedLambda
                                    )
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
                                viewModel.onNetworkErrorShown()
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
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
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
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
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