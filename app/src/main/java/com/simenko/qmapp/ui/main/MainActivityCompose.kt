package com.simenko.qmapp.ui.main

import android.content.Intent
import android.os.Bundle
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
import com.simenko.qmapp.R
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint
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
                                onDrawerItemClick = {
                                    scope.launch { drawerState.close() }
                                    selectedDrawerMenuItemId.value = it
                                }
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
                                    onSearchBarSearch = {
                                        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        ) {
                            Navigation(Modifier.padding(it), selectedDrawerMenuItemId.value)
                        }
                    }
                )
            }
        }
    }
}