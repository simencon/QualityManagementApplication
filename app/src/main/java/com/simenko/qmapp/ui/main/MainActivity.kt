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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
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
import com.simenko.qmapp.domain.FalseStr
import com.simenko.qmapp.domain.FirstTabId
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.SecondTabId
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.domain.ThirdTabId
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.settings.SettingsViewModel
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeViewModel
import com.simenko.qmapp.ui.main.team.forms.user.UserViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
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
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var workManager: WorkManager
    private lateinit var syncLastHourOneTimeWork: OneTimeWorkRequest
    private lateinit var syncLastDayOneTimeWork: OneTimeWorkRequest
    private lateinit var analytics: FirebaseAnalytics

    @Inject
    lateinit var userRepository: UserRepository
    val viewModel: MainActivityViewModel by viewModels()

    private lateinit var settingsModel: SettingsViewModel
    private lateinit var teamModel: TeamViewModel
    private lateinit var invModel: InvestigationsViewModel
    private lateinit var newOrderModel: NewItemViewModel
    private lateinit var employeeModel: EmployeeViewModel
    private lateinit var userModel: UserViewModel

    private lateinit var navController: NavHostController

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
            QMAppTheme {
                navController = rememberNavController()
                val backStackEntry = navController.currentBackStackEntryAsState()

                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val selectedDrawerMenuItemId by viewModel.selectedDrawerMenuItemId.collectAsStateWithLifecycle()
                BackHandler(enabled = drawerState.isOpen, onBack = { scope.launch { drawerState.close() } })

                val selectedContextMenuItemId = rememberSaveable { mutableStateOf(MenuItem.getStartingActionsFilterMenuItem().id) }

                val observerLoadingProcess by viewModel.isLoadingInProgress.collectAsStateWithLifecycle()
                val observerIsNetworkError by viewModel.isErrorMessage.collectAsStateWithLifecycle()
                val fabPosition by viewModel.fabPosition.collectAsStateWithLifecycle()

                val searchBarState = rememberSaveable { mutableStateOf(false) }
                BackHandler(enabled = searchBarState.value, onBack = { searchBarState.value = false })

                BackHandler(enabled = !drawerState.isOpen && !searchBarState.value) {
                    this@MainActivity.moveTaskToBack(true)
                }

                val addEditMode by viewModel.addEditMode.collectAsStateWithLifecycle()

                val topBadgeCounts by viewModel.topBadgeCounts.collectAsStateWithLifecycle()
                var selectedTabIndex by rememberSaveable { mutableIntStateOf(ZeroValue.num) }
                val onTabSelectedLambda = remember<(SelectedNumber, Int) -> Unit> {
                    { tabId, tabIndex ->
                        when (backStackEntry.value?.destination?.parent?.route) {
                            null -> {
                                when (backStackEntry.value?.destination?.route) {
                                    Screen.Main.Inv.routeWithArgKeys() -> invModel.setCurrentOrdersFilter(status = tabId)
                                    Screen.Main.ProcessControl.routeWithArgKeys() -> invModel.setCurrentSubOrdersFilter(status = tabId)
                                }
                            }

                            Screen.Main.Team.route -> {
                                if (tabId == FirstTabId) {
                                    if (backStackEntry.value?.destination?.route != Screen.Main.Team.Employees.routeWithArgKeys())
                                        navController.popBackStack()
                                } else if (tabId == SecondTabId) {
                                    if (backStackEntry.value?.destination?.route != Screen.Main.Team.Users.routeWithArgKeys())
                                        navController.navigate(Screen.Main.Team.Users.withArgs(NoRecordStr.str)) { popUpTo(Screen.Main.Team.Employees.routeWithArgKeys()) }
                                    teamModel.setUsersFilter(newUsers = false)
                                } else if (tabId == ThirdTabId) {
                                    if (backStackEntry.value?.destination?.route != Screen.Main.Team.Users.routeWithArgKeys())
                                        navController.navigate(Screen.Main.Team.Users.withArgs(NoRecordStr.str)) { popUpTo(Screen.Main.Team.Employees.routeWithArgKeys()) }
                                    teamModel.setUsersFilter(newUsers = true)
                                }
                            }
                        }
                        selectedTabIndex = tabIndex
                    }
                }

                fun onDrawerItemClick(id: String) {
                    scope.launch { drawerState.close() }
                    if (id != selectedDrawerMenuItemId) {
                        viewModel.setDrawerMenuItemId(id)
                        when (id) {
                            Screen.Main.Team.route -> navController.navigate(id) { popUpTo(0) }
                            Screen.Main.Inv.withArgs(NoRecordStr.str, NoRecordStr.str) -> navController.navigate(id) { popUpTo(0) }
                            Screen.Main.ProcessControl.withArgs(NoRecordStr.str, NoRecordStr.str) -> navController.navigate(id) { popUpTo(0) }
                            Screen.Main.Settings.route -> navController.navigate(id) { popUpTo(0) }
                            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                        }
                        selectedTabIndex = ZeroValue.num
                        viewModel.resetTopBadgesCount()
                    }
                }

                fun onActionsMenuItemClick(filterOnly: String, action: String) {
                    selectedContextMenuItemId.value = filterOnly
                    if (filterOnly != action) {
                        when (action) {
                            MenuItem.Actions.UPLOAD_MD.action -> viewModel.refreshMasterDataFromRepository()
                            MenuItem.Actions.SYNC_INV.action -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                            MenuItem.Actions.CUSTOM_FILTER.action -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                fun onSearchBarSearch(searchValues: String) {
                    when (backStackEntry.value?.destination?.route) {
                        Screen.Main.Inv.routeWithArgKeys() -> invModel.setCurrentOrdersFilter(number = SelectedString(searchValues))
                        Screen.Main.ProcessControl.routeWithArgKeys() -> invModel.setCurrentSubOrdersFilter(number = SelectedString(searchValues))
                        else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                    }
                }

                LaunchedEffect(backStackEntry.value?.destination?.route) {
                    if (backStackEntry.value?.destination?.route == Screen.Main.Team.Employees.routeWithArgKeys()) {
                        selectedTabIndex = ZeroValue.num
                    }
                }

                ModalNavigationDrawer(
                    gesturesEnabled = addEditMode == AddEditMode.NO_MODE.ordinal,
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                        ) {
                            DrawerHeader(userInfo = viewModel.userInfo)
                            DrawerBody(
                                selectedItemId = selectedDrawerMenuItemId,
                                onDrawerItemClick = { onDrawerItemClick(it) }
                            )
                        }
                    },
                    content = {
                        Scaffold(
                            topBar = {
                                AppBar(
                                    screen = MenuItem.getItemById(selectedDrawerMenuItemId),
                                    destination = backStackEntry.value?.destination,

                                    onDrawerMenuClick = { scope.launch { drawerState.open() } },
                                    drawerState = drawerState,

                                    selectedActionsMenuItemId = selectedContextMenuItemId,
                                    onActionsMenuItemClick = { filterOnly, action -> onActionsMenuItemClick(filterOnly, action) },

                                    searchBarState = searchBarState,
                                    onSearchBarSearch = { onSearchBarSearch(it) },

                                    addEditMode = addEditMode,
                                    onBackFromAddEditModeClick = {
                                        when (addEditMode) {
                                            AddEditMode.ACCOUNT_EDIT.ordinal -> navController.popBackStack(
                                                Screen.Main.Settings.UserDetails.route,
                                                inclusive = false
                                            )

                                            else -> navController.popBackStack()
                                        }
                                        viewModel.setAddEditMode(AddEditMode.NO_MODE)
                                    }
                                )
                            },
                            floatingActionButton = {
                                if (
                                    (backStackEntry.value?.destination?.route != Screen.Main.Settings.UserDetails.route || addEditMode == AddEditMode.ACCOUNT_EDIT.ordinal)
                                    &&
                                    (backStackEntry.value?.destination?.route != Screen.Main.Team.Users.routeWithArgKeys() || addEditMode == AddEditMode.AUTHORIZE_USER.ordinal)
                                )
                                    FloatingActionButton(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        onClick = {
                                            if (addEditMode == AddEditMode.NO_MODE.ordinal)
                                                when (backStackEntry.value?.destination?.route) {
                                                    Screen.Main.Team.Employees.routeWithArgKeys() -> {
                                                        navController.navigate(Screen.Main.Team.EmployeeAddEdit.withArgs(NoRecordStr.str))
                                                        viewModel.setAddEditMode(AddEditMode.ADD_EMPLOYEE)
                                                    }

                                                    Screen.Main.Inv.routeWithArgKeys() -> {
                                                        navController.navigate(Screen.Main.OrderAddEdit.withArgs(NoRecordStr.str))
                                                        viewModel.setAddEditMode(AddEditMode.ADD_ORDER)
                                                    }

                                                    Screen.Main.ProcessControl.routeWithArgKeys() -> {
                                                        navController.navigate(
                                                            Screen.Main.SubOrderAddEdit.withArgs(NoRecordStr.str, NoRecordStr.str, TrueStr.str)
                                                        )
                                                        viewModel.setAddEditMode(AddEditMode.ADD_SUB_ORDER_STAND_ALONE)
                                                    }

                                                    else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                                                }
                                            else {
                                                when (AddEditMode.values()[addEditMode]) {
                                                    AddEditMode.ADD_ORDER -> newOrderModel.makeOrder(newRecord = true)
                                                    AddEditMode.EDIT_ORDER -> newOrderModel.makeOrder(newRecord = false)
                                                    AddEditMode.ADD_SUB_ORDER -> newOrderModel.makeSubOrder(FalseStr.str, true)
                                                    AddEditMode.EDIT_SUB_ORDER -> newOrderModel.makeSubOrder(FalseStr.str, false)
                                                    AddEditMode.ADD_SUB_ORDER_STAND_ALONE -> newOrderModel.makeNewOrderWithSubOrder(newRecord = true)
                                                    AddEditMode.EDIT_SUB_ORDER_STAND_ALONE -> newOrderModel.makeNewOrderWithSubOrder(newRecord = false)
                                                    AddEditMode.ACCOUNT_EDIT -> settingsModel.validateUserData()
                                                    AddEditMode.ADD_EMPLOYEE -> employeeModel.validateInput()
                                                    AddEditMode.EDIT_EMPLOYEE -> employeeModel.validateInput()
                                                    AddEditMode.AUTHORIZE_USER -> userModel.validateInput()
                                                    AddEditMode.EDIT_USER -> userModel.validateInput()
                                                    else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        },
                                        content = {
                                            if (addEditMode == AddEditMode.NO_MODE.ordinal) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add button",
                                                    tint = MaterialTheme.colorScheme.onTertiary
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Save,
                                                    contentDescription = "Save button",
                                                    tint = MaterialTheme.colorScheme.onTertiary
                                                )
                                            }
                                        }
                                    )
                            },
                            floatingActionButtonPosition = fabPosition
                        ) {
                            println("Scaffold has been rebuild")
                            val pullRefreshState = rememberPullRefreshState(
                                refreshing = observerLoadingProcess,
                                onRefresh = {
                                    when (backStackEntry.value?.destination?.route) {
                                        Screen.Main.Team.Employees.routeWithArgKeys() -> teamModel.updateEmployeesData()
                                        Screen.Main.Team.Users.routeWithArgKeys() -> teamModel.updateEmployeesData()
                                        Screen.Main.Inv.routeWithArgKeys() -> invModel.uploadNewInvestigations()
                                        Screen.Main.ProcessControl.routeWithArgKeys() -> invModel.uploadNewInvestigations()
                                        Screen.Main.Settings.UserDetails.route -> settingsModel.updateUserData()
                                        Screen.Main.Settings.EditUserDetails.routeWithArgKeys() -> settingsModel.updateUserData()
                                        else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                                    }
                                }
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
                                        when (backStackEntry.value?.destination?.route) {
                                            Screen.Main.Inv.routeWithArgKeys(), Screen.Main.ProcessControl.routeWithArgKeys() -> ProgressTabs.toListOfTriples()
                                            Screen.Main.Team.Employees.routeWithArgKeys(), Screen.Main.Team.Users.routeWithArgKeys() -> UsersTabs.toListOfTriples()
                                            else -> emptyList()
                                        },
                                        selectedTabIndex,
                                        topBadgeCounts,
                                        onTabSelectedLambda
                                    )
                                    Navigation(
                                        Modifier.pullRefresh(pullRefreshState),
                                        initialRoute,
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

    fun initSettingsModel(model: SettingsViewModel) {
        this.settingsModel = model
        this.settingsModel.initMainActivityViewModel(this.viewModel)
    }

    fun initInvModel(model: InvestigationsViewModel) {
        this.invModel = model
        this.invModel.initMainActivityViewModel(this.viewModel)
        this.invModel.initNavController(this.navController)
    }

    fun initTeamModel(model: TeamViewModel) {
        this.teamModel = model
        this.teamModel.initMainActivityViewModel(this.viewModel)
        this.teamModel.initNavController(this.navController)
    }

    fun initNewOrderModel(model: NewItemViewModel) {
        this.newOrderModel = model
        this.newOrderModel.initMainActivityViewModel(this.viewModel)
        this.newOrderModel.initNavController(this.navController)
    }

    fun initEmployeeModel(employeeModel: EmployeeViewModel) {
        this.employeeModel = employeeModel
        this.employeeModel.initMainActivityViewModel(this.viewModel)
        this.employeeModel.initNavController(this.navController)
    }

    fun initUserModel(model: UserViewModel) {
        this.userModel = model
        this.userModel.initMainActivityViewModel(this.viewModel)
        this.userModel.initNavController(this.navController)
    }
}

data class CreatedRecord(
    val orderId: Int = NoRecord.num,
    val subOrderId: Int = NoRecord.num
)