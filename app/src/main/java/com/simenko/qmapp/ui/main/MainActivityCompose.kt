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
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.other.RandomTeamMembers.getAnyTeamMember
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.team.TeamViewModel
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

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QMAppTheme {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val selectedDrawerMenuItemId = rememberSaveable { mutableStateOf(MenuItem.getStartingDrawerMenuItem().id) }
                BackHandler(enabled = drawerState.isOpen, onBack = { scope.launch { drawerState.close() } })

                val selectedContextMenuItemId = rememberSaveable { mutableStateOf(MenuItem.getStartingActionsFilterMenuItem().id) }

                val observerLoadingProcess by viewModel.isLoadingInProgress.collectAsStateWithLifecycle()
                val observerIsNetworkError by viewModel.isErrorMessage.collectAsStateWithLifecycle()
                val fabPosition by viewModel.fabPosition.collectAsStateWithLifecycle()

                val searchBarState = rememberSaveable { mutableStateOf(false) }
                BackHandler(enabled = searchBarState.value, onBack = { searchBarState.value = false })

                val navController = rememberNavController()

                fun onDrawerItemClick(id: String) {
                    scope.launch { drawerState.close() }

                    if (id != selectedDrawerMenuItemId.value) {
                        selectedDrawerMenuItemId.value = id
                        when (id) {
                            Screen.Main.Employees.route -> navController.navigate(Screen.Main.Employees.route) { popUpTo(0) }
                            Screen.Main.AllInvestigations.route -> navController.navigate(Screen.Main.AllInvestigations.withArgs("false")) { popUpTo(0) }
                            Screen.Main.ProcessControl.route -> navController.navigate(Screen.Main.AllInvestigations.withArgs("true")) { popUpTo(0) }
                            Screen.Main.Settings.route -> navController.navigate(Screen.Main.Settings.route) { popUpTo(0) }
                            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                fun onActionsMenuItemClick(filterOnly: String, action: String) {
                    selectedContextMenuItemId.value = filterOnly
                    if (filterOnly != action) {
                        when (action) {
                            MenuItem.Actions.UPLOAD_MASTER_DATA.action -> viewModel.refreshMasterDataFromRepository()
                            MenuItem.Actions.SYNC_INVESTIGATIONS.action -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                            MenuItem.Actions.CUSTOM_FILTER.action -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                fun onSearchBarSearch(searchValues: String) {
                    when (selectedDrawerMenuItemId.value) {
                        Screen.Main.AllInvestigations.route -> invModel.setCurrentOrdersFilter(number = SelectedString(searchValues))
                        Screen.Main.ProcessControl.route -> invModel.setCurrentSubOrdersFilter(number = SelectedString(searchValues))
                        else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                    }
                }

                ModalNavigationDrawer(
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
                                    screen = MenuItem.getItemById(selectedDrawerMenuItemId.value) ?: MenuItem.getStartingDrawerMenuItem(),

                                    onDrawerMenuClick = { scope.launch { drawerState.open() } },
                                    drawerState = drawerState,

                                    selectedActionsMenuItemId = selectedContextMenuItemId,
                                    onActionsMenuItemClick = { filterOnly, action -> onActionsMenuItemClick(filterOnly, action) },

                                    searchBarState = searchBarState,
                                    onSearchBarSearch = { onSearchBarSearch(it) }
                                )
                            },
                            floatingActionButton = {
                                if (selectedDrawerMenuItemId.value != Screen.Main.Settings.route)
                                    FloatingActionButton(
                                        onClick = {
                                            when (selectedDrawerMenuItemId.value) {
                                                Screen.Main.Employees.route -> teamModel.insertRecord(getAnyTeamMember[(getAnyTeamMember.indices).random()])
                                                else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        content = {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Add button"
                                            )
                                        }
                                    )
                            },
                            floatingActionButtonPosition = fabPosition
                        ) {
                            val pullRefreshState = rememberPullRefreshState(
                                refreshing = observerLoadingProcess!!,
                                onRefresh = {
                                    when (selectedDrawerMenuItemId.value) {
                                        Screen.Main.Employees.route -> teamModel.updateEmployeesData()
                                        Screen.Main.AllInvestigations.route -> invModel.uploadNewInvestigations()
                                        Screen.Main.ProcessControl.route -> invModel.uploadNewInvestigations()
                                        
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
                                    MenuItem.getStartingDrawerMenuItem().id,
                                    navController
                                )
                                PullRefreshIndicator(
                                    refreshing = observerLoadingProcess!!,
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
    }

    fun initTeamModel(model: TeamViewModel) {
        this.teamModel = model
        this.teamModel.initMainActivityViewModel(this.viewModel)
    }
}