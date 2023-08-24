package com.simenko.qmapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
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
                val selectedItemId = rememberSaveable { mutableStateOf(MenuItem.getStartingDrawerMenuItem().id) }

                val actionsMenuState = rememberSaveable { mutableStateOf(false) }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                        ) {
                            DrawerHeader()
                            DrawerBody(scope = scope, selectedItemId = selectedItemId, drawerState = drawerState)
                        }
                    },
                    content = {
                        Scaffold(
                            topBar = {
                                AppBar(
                                    onNavigationMenuClick = { scope.launch { drawerState.open() } },
                                    drawerState = drawerState,
                                    isActionsMenuVisible = actionsMenuState.value,
                                    onActionsMenuClick = { scope.launch { actionsMenuState.value = true } },
                                    onDismissRequest = { scope.launch { actionsMenuState.value = false } }
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