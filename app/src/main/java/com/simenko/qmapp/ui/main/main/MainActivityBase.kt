package com.simenko.qmapp.ui.main.main

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.ui.navigation.NavArguments
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.main.MainActivityViewModel

abstract class MainActivityBase : ComponentActivity() {
    val viewModel: MainActivityViewModel by viewModels()
    protected lateinit var navController: NavHostController

    /**
     * Drawer menu -----------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDrawerItemClick(currentId: String, id: String) {
        if (id != currentId) {
            viewModel.setDrawerMenuItemId(id)
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
     * Action bar ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProperAddEditMode(backStackEntry: State<NavBackStackEntry?>) {
        when (backStackEntry.value?.destination?.route) {
            Route.Main.Team.EmployeeAddEdit.link -> {
                if (backStackEntry.value?.arguments?.getInt(NavArguments.employeeId) == NoRecord.num) {
                    viewModel.setupTopScreen(AddEditMode.ADD_EMPLOYEE)
                } else {
                    viewModel.setupTopScreen(AddEditMode.EDIT_EMPLOYEE)
                }
            }

            Route.Main.OrderAddEdit.link -> {
                if (backStackEntry.value?.arguments?.getInt(NavArguments.orderId) == NoRecord.num) {
                    viewModel.setupTopScreen(AddEditMode.ADD_ORDER)
                } else {
                    viewModel.setupTopScreen(AddEditMode.EDIT_ORDER)
                }
            }

            Route.Main.SubOrderAddEdit.link -> {
                if (backStackEntry.value?.arguments?.getInt(NavArguments.subOrderId) == NoRecord.num &&
                    backStackEntry.value?.arguments?.getBoolean(NavArguments.subOrderAddEditMode) == false
                ) {
                    viewModel.setupTopScreen(AddEditMode.ADD_SUB_ORDER)
                } else if (backStackEntry.value?.arguments?.getInt(NavArguments.subOrderId) == NoRecord.num &&
                    backStackEntry.value?.arguments?.getBoolean(NavArguments.subOrderAddEditMode) == true
                ) {
                    viewModel.setupTopScreen(AddEditMode.ADD_SUB_ORDER_STAND_ALONE)
                } else if (backStackEntry.value?.arguments?.getInt(NavArguments.subOrderId) != NoRecord.num &&
                    backStackEntry.value?.arguments?.getBoolean(NavArguments.subOrderAddEditMode) == false
                ) {
                    viewModel.setupTopScreen(AddEditMode.EDIT_SUB_ORDER)
                } else if (backStackEntry.value?.arguments?.getInt(NavArguments.subOrderId) != NoRecord.num &&
                    backStackEntry.value?.arguments?.getBoolean(NavArguments.subOrderAddEditMode) == true
                ) {
                    viewModel.setupTopScreen(AddEditMode.EDIT_SUB_ORDER_STAND_ALONE)
                }
            }

//            Route.Main.Settings.EditUserDetails.link -> { viewModel.setAddEditMode(AddEditMode.ACCOUNT_EDIT) }

            Route.Main.Team.AuthorizeUser.link -> {
                viewModel.setupTopScreen(AddEditMode.AUTHORIZE_USER)
            }

            Route.Main.Team.EditUser.link -> {
                viewModel.setupTopScreen(AddEditMode.EDIT_USER)
            }

            else -> viewModel.setupTopScreen(AddEditMode.NO_MODE)
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

    /**
     * Main floating action button -------------------------------------------------------------------------------------------------------------------
     * */
    fun showFab(backStackEntry: State<NavBackStackEntry?>, addEditMode: Int): Boolean {
        return ((backStackEntry.value?.destination?.route != Route.Main.Settings.UserDetails.link || addEditMode == AddEditMode.ACCOUNT_EDIT.ordinal) &&
                (backStackEntry.value?.destination?.route != Route.Main.Team.Users.link || addEditMode == AddEditMode.AUTHORIZE_USER.ordinal) &&
                (backStackEntry.value?.destination?.route != Route.Main.Team.Requests.link || addEditMode == AddEditMode.AUTHORIZE_USER.ordinal))
    }

    fun onFabClick(backStackEntry: State<NavBackStackEntry?>, addEditMode: Int, addEditAction: () -> Unit) {
        if (addEditMode == AddEditMode.NO_MODE.ordinal)
            when (backStackEntry.value?.destination?.route) {
                Route.Main.Team.Employees.link -> viewModel.onAddEmployeeClick()
                Route.Main.Inv.link -> viewModel.onAddInvClick()
                Route.Main.ProcessControl.link -> viewModel.onAddProcessControlClick()
                else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
            }
        else {
            when (AddEditMode.values()[addEditMode]) {
                AddEditMode.ADD_ORDER -> addEditAction()
                AddEditMode.EDIT_ORDER -> addEditAction()
                AddEditMode.ADD_SUB_ORDER -> addEditAction()
                AddEditMode.EDIT_SUB_ORDER -> addEditAction()
                AddEditMode.ADD_SUB_ORDER_STAND_ALONE -> addEditAction()
                AddEditMode.EDIT_SUB_ORDER_STAND_ALONE -> addEditAction()
                AddEditMode.ADD_EMPLOYEE -> addEditAction()
                AddEditMode.EDIT_EMPLOYEE -> addEditAction()
                AddEditMode.AUTHORIZE_USER -> addEditAction()
                AddEditMode.EDIT_USER -> addEditAction()
                AddEditMode.ACCOUNT_EDIT -> addEditAction()
                else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
            }
        }
    }

    @Composable
    fun FabContent(addEditMode: Int) {
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

    /**
     * Pull refresh ----------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onPullRefresh(backStackEntry: State<NavBackStackEntry?>, refreshAction: () -> Unit) {
        when (backStackEntry.value?.destination?.route) {
            Route.Main.Team.Employees.link -> refreshAction()
            Route.Main.Team.Users.link -> refreshAction()
            Route.Main.Team.Requests.link -> refreshAction()
            Route.Main.Inv.link -> refreshAction()
            Route.Main.ProcessControl.link -> refreshAction()
            Route.Main.Settings.UserDetails.link -> refreshAction()
            Route.Main.Settings.EditUserDetails.link -> refreshAction()
            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
        }
    }
}