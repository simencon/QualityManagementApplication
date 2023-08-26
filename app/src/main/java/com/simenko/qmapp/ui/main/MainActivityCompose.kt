package com.simenko.qmapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.OrderTypeProcessOnly
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.viewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
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

private const val TAG = "MainActivityCompose"

@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    @Inject
    lateinit var userRepository: UserRepository
    private lateinit var invModel: InvestigationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QMAppTheme {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val selectedDrawerMenuItemId = rememberSaveable { mutableStateOf(MenuItem.getStartingDrawerMenuItem().id) }
                BackHandler(enabled = drawerState.isOpen, onBack = { scope.launch { drawerState.close() } })

                val selectedContextMenuItemId = rememberSaveable { mutableStateOf(MenuItem.getStartingActionsFilterMenuItem().id) }

                val searchBarState = rememberSaveable { mutableStateOf(false) }
                BackHandler(enabled = searchBarState.value, onBack = { searchBarState.value = false })

                val navController = rememberNavController()

                fun onDrawerItemClick(id: String) {
                    scope.launch { drawerState.close() }

                    if(id!=selectedDrawerMenuItemId.value) {
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
                                    onActionsMenuItemClick = { selectedContextMenuItemId.value = it },

                                    searchBarState = searchBarState,
                                    onSearchBarSearch = { onSearchBarSearch(it) }
                                )
                            }
                        ) {
                            Navigation(
                                Modifier.padding(it),
                                MenuItem.getStartingDrawerMenuItem().id,
                                navController
                            )
                        }
                    }
                )
            }
        }
    }

    fun initInvModel(model: InvestigationsViewModel) {
        this.invModel = model
    }
}