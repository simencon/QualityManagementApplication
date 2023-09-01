package com.simenko.qmapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.FalseStr
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.other.RandomTeamMembers.getAnyTeamMember
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.neworder.NewItemViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

fun launchMainActivityCompose(
    activity: MainActivity,
    actionType: Int
) {
    activity.startActivityForResult(
        Intent(activity, MainActivityCompose::class.java),
        actionType
    )
}

@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    @Inject
    lateinit var userRepository: UserRepository
    val viewModel: MainActivityViewModel by viewModels()

    private lateinit var teamModel: TeamViewModel
    private lateinit var invModel: InvestigationsViewModel
    private lateinit var newOrderModel: NewItemViewModel

    private lateinit var navController: NavHostController

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QMAppTheme {
                navController = rememberNavController()

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

                val addEditMode by viewModel.addEditMode.collectAsStateWithLifecycle()

                fun onDrawerItemClick(id: String) {
                    scope.launch { drawerState.close() }
                    if (id != selectedDrawerMenuItemId) {
                        viewModel.setDrawerMenuItemId(id)
                        when (id) {
                            Screen.Main.Employees.route -> navController.navigate(id) { popUpTo(0) }
                            Screen.Main.Inv.withArgs(FalseStr.str, NoRecordStr.str, NoRecordStr.str) -> navController.navigate(id) { popUpTo(0) }
                            Screen.Main.Inv.withArgs(TrueStr.str, NoRecordStr.str, NoRecordStr.str) -> navController.navigate(id) { popUpTo(0) }
                            Screen.Main.Settings.route -> navController.navigate(id) { popUpTo(0) }
                            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                        }
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
                    when (selectedDrawerMenuItemId) {
                        Screen.Main.Inv.withArgs(FalseStr.str, NoRecordStr.str, NoRecordStr.str) -> invModel.setCurrentOrdersFilter(number = SelectedString(searchValues))
                        Screen.Main.Inv.withArgs(TrueStr.str, NoRecordStr.str, NoRecordStr.str) -> invModel.setCurrentSubOrdersFilter(number = SelectedString(searchValues))
                        else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
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
                            DrawerHeader(
                                logo = painterResource(id = R.drawable.ic_launcher_round),
                                userInfo = userRepository.user
                            )
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
                                    screen = MenuItem.getItemById(selectedDrawerMenuItemId) ?: MenuItem.getStartingDrawerMenuItem(),

                                    onDrawerMenuClick = { scope.launch { drawerState.open() } },
                                    drawerState = drawerState,

                                    selectedActionsMenuItemId = selectedContextMenuItemId,
                                    onActionsMenuItemClick = { filterOnly, action -> onActionsMenuItemClick(filterOnly, action) },

                                    searchBarState = searchBarState,
                                    onSearchBarSearch = { onSearchBarSearch(it) },

                                    addEditMode = addEditMode,
                                    onBackFromAddEditModeClick = {
                                        viewModel.setAddEditMode(AddEditMode.NO_MODE)
                                        navController.popBackStack()
                                    }
                                )
                            },
                            floatingActionButton = {
                                if (selectedDrawerMenuItemId != Screen.Main.Settings.route)
                                    FloatingActionButton(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        onClick = {
                                            if (addEditMode == AddEditMode.NO_MODE.ordinal)
                                                when (selectedDrawerMenuItemId) {
                                                    Screen.Main.Employees.route -> teamModel.insertRecord(getAnyTeamMember[(getAnyTeamMember.indices).random()])
                                                    Screen.Main.Inv.withArgs(FalseStr.str, NoRecordStr.str, NoRecordStr.str) -> {
                                                        navController.navigate(Screen.Main.OrderAddEdit.withArgs(NoRecordStr.str))
                                                        viewModel.setAddEditMode(AddEditMode.ADD_ORDER)
                                                    }
                                                    Screen.Main.Inv.withArgs(TrueStr.str, NoRecordStr.str, NoRecordStr.str) -> {
                                                        navController.navigate(Screen.Main.SubOrderAddEdit
                                                            .withArgs(NoRecordStr.str, NoRecordStr.str, TrueStr.str))
                                                        viewModel.setAddEditMode(AddEditMode.ADD_SUB_ORDER_STAND_ALONE)
                                                    }
                                                    else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                                                }
                                            else {
                                                when (AddEditMode.values()[addEditMode]) {
                                                    AddEditMode.ADD_ORDER -> newOrderModel.postOrder()
                                                    AddEditMode.EDIT_ORDER -> newOrderModel.editOrder()
                                                    AddEditMode.ADD_SUB_ORDER -> newOrderModel.postSubOrder(FalseStr.str)
                                                    AddEditMode.ADD_SUB_ORDER_STAND_ALONE -> newOrderModel.postNewOrderWithSubOrder()
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
                            val pullRefreshState = rememberPullRefreshState(
                                refreshing = observerLoadingProcess,
                                onRefresh = {
                                    when (selectedDrawerMenuItemId) {
                                        Screen.Main.Employees.route -> teamModel.updateEmployeesData()
                                        Screen.Main.Inv.withArgs(FalseStr.str, NoRecordStr.str, NoRecordStr.str) -> invModel.uploadNewInvestigations()
                                        Screen.Main.Inv.withArgs(TrueStr.str, NoRecordStr.str, NoRecordStr.str) -> invModel.uploadNewInvestigations()

                                        Screen.Main.Settings.route -> scope.launch {
                                            viewModel.updateLoadingState(Pair(true, null))
                                            delay(3000)
                                            viewModel.updateLoadingState(Pair(false, "Some test error!"))
                                        }

                                        else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(all = 0.dp)
                            ) {
                                Navigation(
                                    Modifier
                                        .padding(it)
                                        .pullRefresh(pullRefreshState),
                                    it,
                                    MenuItem.getStartingDrawerMenuItem().id,
                                    navController
                                )
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

    fun initInvModel(model: InvestigationsViewModel) {
        this.invModel = model
        this.invModel.initMainActivityViewModel(this.viewModel)
        this.invModel.initNavController(this.navController)
    }

    fun initTeamModel(model: TeamViewModel) {
        this.teamModel = model
        this.teamModel.initMainActivityViewModel(this.viewModel)
    }

    fun initNewOrderModel(newOrderModel: NewItemViewModel) {
        this.newOrderModel = newOrderModel
        this.newOrderModel.initMainActivityViewModel(this.viewModel)
        this.newOrderModel.initNavController(this.navController)
    }
}