package com.simenko.qmapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.simenko.qmapp.ui.theme.QMAppTheme
import kotlinx.coroutines.launch

fun launchMainActivityCompose(
    activity: MainActivity,
    actionType: Int
) {
    activity.startActivityForResult(
        Intent(activity, MainActivityCompose::class.java),
        actionType
    )
}

class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QMAppTheme {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val selectedDrawerMenuItemId = rememberSaveable { mutableStateOf(MenuItem.getStartingDrawerMenuItem().id) }
                BackHandler(enabled = drawerState.isOpen, onBack = { scope.launch { drawerState.close() } })

                val actionsTopMenuState = rememberSaveable { mutableStateOf(false) }

                val actionsContextMenuState = rememberSaveable { mutableStateOf(false) }
                val selectedActionTopMenuGroup = rememberSaveable { mutableStateOf(MenuItem.MenuGroup.ACTIONS) }
                val selectedContextMenuItemId = rememberSaveable { mutableStateOf(MenuItem.getStartingActionsFilterMenuItem().id) }

                val isSearchModeActivated = rememberSaveable { mutableStateOf(false) }
                BackHandler(enabled = isSearchModeActivated.value, onBack = { scope.launch { isSearchModeActivated.value = false } })

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                        ) {
                            DrawerHeader()
                            DrawerBody(scope = scope, selectedItemId = selectedDrawerMenuItemId, drawerState = drawerState)
                        }
                    },
                    content = {
                        Scaffold(
                            topBar = {
                                AppBar(
                                    screen = MenuItem.getItemById(selectedDrawerMenuItemId.value) ?: MenuItem.getStartingDrawerMenuItem(),

                                    onNavigationMenuClick = { scope.launch { drawerState.open() } },
                                    drawerState = drawerState,

                                    onActionsMenuClick = { scope.launch { actionsTopMenuState.value = true } },

                                    isTopMenuVisible = actionsTopMenuState.value,
                                    onDismissTopMenu = { scope.launch { actionsTopMenuState.value = false } },
                                    onTopMenuItemClick = {
                                        scope.launch {
                                            selectedActionTopMenuGroup.value = it
                                            actionsTopMenuState.value = false
                                            actionsContextMenuState.value = true
                                        }
                                    },
                                    isContextMenuVisible = actionsContextMenuState.value,
                                    actionsGroup = selectedActionTopMenuGroup.value,
                                    onDismissContextMenu = { scope.launch { actionsContextMenuState.value = false } },
                                    selectedItemId = selectedContextMenuItemId,
                                    onClickBack = {
                                        scope.launch {
                                            scope.launch {
                                                actionsTopMenuState.value = true
                                                actionsContextMenuState.value = false
                                            }
                                        }
                                    },
                                    onContextMenuItemClick = { scope.launch { selectedContextMenuItemId.value = it } },

                                    onSearchMenuClick = {scope.launch { isSearchModeActivated.value = true }},
                                    isSearchModeActivated = isSearchModeActivated.value,
                                    hideSearchBar = { isSearchModeActivated.value = false }
                                )
                            }
                        ) {

                        }
                    }
                )
            }
        }
    }
}