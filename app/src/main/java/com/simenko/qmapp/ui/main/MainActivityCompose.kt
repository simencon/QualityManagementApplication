package com.simenko.qmapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

private const val TAG = "MainActivityCompose"

class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QMAppTheme {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val selectedItem = remember { mutableStateOf(MenuItem.getStartingMenuItem()) }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            DrawerHeader()
                            DrawerBody(scope = scope, selectedItem = selectedItem, drawerState = drawerState)
                        }
                    },
                    content = {
                        Scaffold(
                            topBar = {
                                AppBar(
                                    onNavigationItemClick = { scope.launch { drawerState.open() } },
                                    drawerState = drawerState
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