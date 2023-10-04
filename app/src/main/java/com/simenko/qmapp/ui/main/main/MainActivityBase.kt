package com.simenko.qmapp.ui.main.main

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.main.main.page.MenuItem
import com.simenko.qmapp.ui.main.main.page.components.TopBarSetup

abstract class MainActivityBase : ComponentActivity() {
    val viewModel: MainActivityViewModel by viewModels()
    protected lateinit var navController: NavHostController

    /**
     * Drawer menu -----------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDrawerItemClick(currentId: String, id: String, topBarSetup: TopBarSetup) {
        if (id != currentId) {
            topBarSetup.setDrawerMenuItemId(id)
            when (id) {
                Route.Main.Team.link -> viewModel.onDrawerMenuTeamSelected()
                Route.Main.Inv.link -> viewModel.onDrawerMenuInvSelected()
                Route.Main.ProcessControl.link -> viewModel.onDrawerMenuProcessControlSelected()
                Route.Main.Settings.link -> viewModel.onDrawerMenuSettingsSelected()
                else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Action menu -----------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onActionsMenuItemClick(filterOnly: String, action: String): String {
        if (filterOnly != action) {
            when (action) {
                MenuItem.Actions.UPLOAD_MD.action -> viewModel.refreshMasterDataFromRepository()
                MenuItem.Actions.SYNC_INV.action -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                MenuItem.Actions.CUSTOM_FILTER.action -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
            }
        }
        return filterOnly
    }
}