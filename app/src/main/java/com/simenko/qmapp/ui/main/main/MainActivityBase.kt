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
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.ui.navigation.NavArguments
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.settings.SettingsViewModel
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.main.team.forms.employee.EmployeeViewModel
import com.simenko.qmapp.ui.main.team.forms.user.UserViewModel

abstract class MainActivityBase : ComponentActivity() {
    val viewModel: MainActivityViewModel by viewModels()

    private lateinit var settingsModel: SettingsViewModel
    private lateinit var teamModel: TeamViewModel
    private lateinit var invModel: InvestigationsViewModel
    private lateinit var newOrderModel: NewItemViewModel
    private lateinit var employeeModel: EmployeeViewModel
    private lateinit var userModel: UserViewModel

    protected lateinit var navController: NavHostController

    /**
     * Drawer menu -----------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDrawerItemClick(currentId: String, id: String): Int? {
        return if (id != currentId) {
            viewModel.setDrawerMenuItemId(id)
            when (id) {
                Route.Main.Team.link -> viewModel.onDrawerMenuTeamSelected()
                Route.Main.Inv.link -> viewModel.onDrawerMenuInvSelected()
                Route.Main.ProcessControl.link -> viewModel.onDrawerMenuProcessControlSelected()
                Route.Main.Settings.link -> viewModel.onDrawerMenuSettingsSelected()
                else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
            }
            viewModel.resetTopBadgesCount()
            ZeroValue.num
        } else {
            null
        }
    }

    /**
     * Action bar ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProperAddEditMode(backStackEntry: State<NavBackStackEntry?>) {
        when (backStackEntry.value?.destination?.route) {
            Route.Main.Team.EmployeeAddEdit.link -> {
                if (backStackEntry.value?.arguments?.getInt(NavArguments.employeeId) == NoRecord.num) {
                    viewModel.setAddEditMode(AddEditMode.ADD_EMPLOYEE)
                } else {
                    viewModel.setAddEditMode(AddEditMode.EDIT_EMPLOYEE)
                }
            }

            Route.Main.OrderAddEdit.link -> {
                if (backStackEntry.value?.arguments?.getInt(NavArguments.orderId) == NoRecord.num) {
                    viewModel.setAddEditMode(AddEditMode.ADD_ORDER)
                } else {
                    viewModel.setAddEditMode(AddEditMode.EDIT_ORDER)
                }
            }

            Route.Main.SubOrderAddEdit.link -> {
                if (backStackEntry.value?.arguments?.getInt(NavArguments.subOrderId) == NoRecord.num &&
                    backStackEntry.value?.arguments?.getBoolean(NavArguments.subOrderAddEditMode) == false
                ) {
                    viewModel.setAddEditMode(AddEditMode.ADD_SUB_ORDER)
                } else if (backStackEntry.value?.arguments?.getInt(NavArguments.subOrderId) == NoRecord.num &&
                    backStackEntry.value?.arguments?.getBoolean(NavArguments.subOrderAddEditMode) == true
                ) {
                    viewModel.setAddEditMode(AddEditMode.ADD_SUB_ORDER_STAND_ALONE)
                } else if (backStackEntry.value?.arguments?.getInt(NavArguments.subOrderId) != NoRecord.num &&
                    backStackEntry.value?.arguments?.getBoolean(NavArguments.subOrderAddEditMode) == false
                ) {
                    viewModel.setAddEditMode(AddEditMode.EDIT_SUB_ORDER)
                } else if (backStackEntry.value?.arguments?.getInt(NavArguments.subOrderId) != NoRecord.num &&
                    backStackEntry.value?.arguments?.getBoolean(NavArguments.subOrderAddEditMode) == true
                ) {
                    viewModel.setAddEditMode(AddEditMode.EDIT_SUB_ORDER_STAND_ALONE)
                }
            }

            Route.Main.Settings.EditUserDetails.link -> {
                viewModel.setAddEditMode(AddEditMode.ACCOUNT_EDIT)
            }

            Route.Main.Team.AuthorizeUser.link -> {
                viewModel.setAddEditMode(AddEditMode.AUTHORIZE_USER)
            }

            Route.Main.Team.EditUser.link -> {
                viewModel.setAddEditMode(AddEditMode.EDIT_USER)
            }

            else -> viewModel.setAddEditMode(AddEditMode.NO_MODE)
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
     * Search bar ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onSearchBarSearch(backStackEntry: State<NavBackStackEntry?>, searchValues: String) {
        when (backStackEntry.value?.destination?.route) {
            Route.Main.Inv.link -> invModel.setCurrentOrdersFilter(number = SelectedString(searchValues))
            Route.Main.ProcessControl.link -> invModel.setCurrentSubOrdersFilter(number = SelectedString(searchValues))
            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Top tabs --------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun getTopTabsContent(backStackEntry: State<NavBackStackEntry?>): List<Triple<String, Int, SelectedNumber>> {
        return when (backStackEntry.value?.destination?.route) {
            Route.Main.Inv.link, Route.Main.ProcessControl.link -> ProgressTabs.toListOfTriples()
            Route.Main.Team.Employees.link, Route.Main.Team.Users.link, Route.Main.Team.Requests.link -> TeamTabs.toListOfTriples()
            else -> emptyList()
        }
    }

    val onTabSelectedLambda: (State<NavBackStackEntry?>, SelectedNumber, Int) -> Int = { backStackEntry, tabId, tabIndex ->
        when (backStackEntry.value?.destination?.parent?.route) {
            null -> {
                when (backStackEntry.value?.destination?.route) {
                    Route.Main.Inv.link -> invModel.setCurrentOrdersFilter(status = tabId)
                    Route.Main.ProcessControl.link -> invModel.setCurrentSubOrdersFilter(status = tabId)
                }
            }

            Route.Main.Team.link -> {
                if (tabIndex == TeamTabs.EMPLOYEES.ordinal) {
                    if (backStackEntry.value?.destination?.route != Route.Main.Team.Employees.link)
                        viewModel.onTopTabsEmployeesClick()
                } else if (tabIndex == TeamTabs.USERS.ordinal) {
                    if (backStackEntry.value?.destination?.route != Route.Main.Team.Users.link)
                        viewModel.onTopTabsUsersClick()
                } else if (tabIndex == TeamTabs.REQUESTS.ordinal) {
                    if (backStackEntry.value?.destination?.route != Route.Main.Team.Requests.link)
                        viewModel.onTopTabsRequestsClick()
                }
            }
        }
        tabIndex
    }

    fun selectProperTab(backStackEntry: State<NavBackStackEntry?>): Int? {
        return when (backStackEntry.value?.destination?.route) {
            Route.Main.Team.Employees.link -> TeamTabs.EMPLOYEES.ordinal
            Route.Main.Team.Users.link -> TeamTabs.USERS.ordinal
            Route.Main.Team.Requests.link -> TeamTabs.REQUESTS.ordinal
            else -> null
        }
    }

    /**
     * Main floating action button -------------------------------------------------------------------------------------------------------------------
     * */
    fun showFab(backStackEntry: State<NavBackStackEntry?>, addEditMode: Int): Boolean {
        return ((backStackEntry.value?.destination?.route != Route.Main.Settings.UserDetails.link || addEditMode == AddEditMode.ACCOUNT_EDIT.ordinal) &&
                (backStackEntry.value?.destination?.route != Route.Main.Team.Users.link || addEditMode == AddEditMode.AUTHORIZE_USER.ordinal) &&
                (backStackEntry.value?.destination?.route != Route.Main.Team.Requests.link || addEditMode == AddEditMode.AUTHORIZE_USER.ordinal))
    }

    fun onFabClick(backStackEntry: State<NavBackStackEntry?>, addEditMode: Int) {
        if (addEditMode == AddEditMode.NO_MODE.ordinal)
            when (backStackEntry.value?.destination?.route) {
                Route.Main.Team.Employees.link -> viewModel.onAddEmployeeClick()
                Route.Main.Inv.link -> viewModel.onAddInvClick()
                Route.Main.ProcessControl.link -> viewModel.onAddProcessControlClick()

                else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
            }
        else {
            when (AddEditMode.values()[addEditMode]) {
                AddEditMode.ADD_ORDER -> newOrderModel.makeOrder(newRecord = true)
                AddEditMode.EDIT_ORDER -> newOrderModel.makeOrder(newRecord = false)
                AddEditMode.ADD_SUB_ORDER -> newOrderModel.makeSubOrder(newRecord = true)
                AddEditMode.EDIT_SUB_ORDER -> newOrderModel.makeSubOrder(newRecord = false)
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
    fun onPullRefresh(backStackEntry: State<NavBackStackEntry?>) {
        when (backStackEntry.value?.destination?.route) {
            Route.Main.Team.Employees.link -> teamModel.updateEmployeesData()
            Route.Main.Team.Users.link -> teamModel.updateEmployeesData()
            Route.Main.Team.Requests.link -> teamModel.updateEmployeesData()
            Route.Main.Inv.link -> invModel.uploadNewInvestigations()
            Route.Main.ProcessControl.link -> invModel.uploadNewInvestigations()
            Route.Main.Settings.UserDetails.link -> settingsModel.updateUserData()
            Route.Main.Settings.EditUserDetails.link -> settingsModel.updateUserData()
            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * View models consistency -----------------------------------------------------------------------------------------------------------------------
     * */
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