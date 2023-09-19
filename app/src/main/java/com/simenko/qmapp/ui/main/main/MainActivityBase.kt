package com.simenko.qmapp.ui.main.main

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.State
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.simenko.qmapp.domain.FalseStr
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.ui.Screen
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
    fun onDrawerItemClick(id: String) {
        when (id) {
            Screen.Main.Team.route -> navController.navigate(id) { popUpTo(0) }
            Screen.Main.Inv.withArgs(NoRecordStr.str, NoRecordStr.str) -> navController.navigate(id) { popUpTo(0) }
            Screen.Main.ProcessControl.withArgs(NoRecordStr.str, NoRecordStr.str) -> navController.navigate(id) { popUpTo(0) }
            Screen.Main.Settings.route -> navController.navigate(id) { popUpTo(0) }
            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Action menu -----------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onActionsMenuItemClick(filterOnly: String, action: String) {
        if (filterOnly != action) {
            when (action) {
                MenuItem.Actions.UPLOAD_MD.action -> viewModel.refreshMasterDataFromRepository()
                MenuItem.Actions.SYNC_INV.action -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
                MenuItem.Actions.CUSTOM_FILTER.action -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Search bar ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onSearchBarSearch(backStackEntry: State<NavBackStackEntry?>, searchValues: String) {
        when (backStackEntry.value?.destination?.route) {
            Screen.Main.Inv.routeWithArgKeys() -> invModel.setCurrentOrdersFilter(number = SelectedString(searchValues))
            Screen.Main.ProcessControl.routeWithArgKeys() -> invModel.setCurrentSubOrdersFilter(number = SelectedString(searchValues))
            else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Top tabs --------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun getTopTabsContent(backStackEntry: State<NavBackStackEntry?>): List<Triple<String, Int, SelectedNumber>> {
        return when (backStackEntry.value?.destination?.route) {
            Screen.Main.Inv.routeWithArgKeys(), Screen.Main.ProcessControl.routeWithArgKeys() -> ProgressTabs.toListOfTriples()
            Screen.Main.Team.Employees.routeWithArgKeys(), Screen.Main.Team.Users.routeWithArgKeys(), Screen.Main.Team.Requests.routeWithArgKeys() -> TeamTabs.toListOfTriples()
            else -> emptyList()
        }
    }

    val onTabSelectedLambda: (State<NavBackStackEntry?>, SelectedNumber, Int) -> Int = { backStackEntry, tabId, tabIndex ->
        when (backStackEntry.value?.destination?.parent?.route) {
            null -> {
                when (backStackEntry.value?.destination?.route) {
                    Screen.Main.Inv.routeWithArgKeys() -> invModel.setCurrentOrdersFilter(status = tabId)
                    Screen.Main.ProcessControl.routeWithArgKeys() -> invModel.setCurrentSubOrdersFilter(status = tabId)
                }
            }

            Screen.Main.Team.route -> {
                if (tabIndex == TeamTabs.EMPLOYEES.ordinal) {
                    if (backStackEntry.value?.destination?.route != Screen.Main.Team.Employees.routeWithArgKeys())
                        navController.popBackStack()
                } else if (tabIndex == TeamTabs.USERS.ordinal) {
                    if (backStackEntry.value?.destination?.route != Screen.Main.Team.Users.routeWithArgKeys())
                        navController.navigate(Screen.Main.Team.Users.withArgs(NoRecordStr.str)) { popUpTo(Screen.Main.Team.Employees.routeWithArgKeys()) }
                    teamModel.setUsersFilter(newUsers = false)
                } else if (tabIndex == TeamTabs.REQUESTS.ordinal) {
                    if (backStackEntry.value?.destination?.route != Screen.Main.Team.Requests.routeWithArgKeys())
                        navController.navigate(Screen.Main.Team.Requests.withArgs(NoRecordStr.str)) { popUpTo(Screen.Main.Team.Employees.routeWithArgKeys()) }
                    teamModel.setUsersFilter(newUsers = true)
                }
            }
        }
        tabIndex
    }
    fun selectProperTab(backStackEntry: State<NavBackStackEntry?>): Int? {
        return when (backStackEntry.value?.destination?.route) {
            Screen.Main.Team.Employees.routeWithArgKeys() -> TeamTabs.EMPLOYEES.ordinal
            Screen.Main.Team.Users.routeWithArgKeys() -> TeamTabs.USERS.ordinal
            Screen.Main.Team.Requests.routeWithArgKeys() -> TeamTabs.REQUESTS.ordinal
            else -> null
        }
    }

    /**
     * Main floating action button -------------------------------------------------------------------------------------------------------------------
     * */
    fun showFab(backStackEntry: State<NavBackStackEntry?>, addEditMode: Int): Boolean {
        return ((backStackEntry.value?.destination?.route != Screen.Main.Settings.UserDetails.route || addEditMode == AddEditMode.ACCOUNT_EDIT.ordinal) &&
                (backStackEntry.value?.destination?.route != Screen.Main.Team.Users.routeWithArgKeys() || addEditMode == AddEditMode.AUTHORIZE_USER.ordinal) &&
                (backStackEntry.value?.destination?.route != Screen.Main.Team.Requests.routeWithArgKeys() || addEditMode == AddEditMode.AUTHORIZE_USER.ordinal))
    }
    fun onFabClick(backStackEntry: State<NavBackStackEntry?>, addEditMode: Int) {
        if (addEditMode == AddEditMode.NO_MODE.ordinal)
            when (backStackEntry.value?.destination?.route) {
                Screen.Main.Team.Employees.routeWithArgKeys() -> {
                    navController.navigate(Screen.Main.Team.EmployeeAddEdit.withArgs(NoRecordStr.str))
                    viewModel.setAddEditMode(AddEditMode.ADD_EMPLOYEE)
                }

                Screen.Main.Inv.routeWithArgKeys() -> {
                    navController.navigate(Screen.Main.OrderAddEdit.withArgs(NoRecordStr.str))
                    viewModel.setAddEditMode(AddEditMode.ADD_ORDER)
                }

                Screen.Main.ProcessControl.routeWithArgKeys() -> {
                    navController.navigate(
                        Screen.Main.SubOrderAddEdit.withArgs(NoRecordStr.str, NoRecordStr.str, TrueStr.str)
                    )
                    viewModel.setAddEditMode(AddEditMode.ADD_SUB_ORDER_STAND_ALONE)
                }

                else -> Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG).show()
            }
        else {
            when (AddEditMode.values()[addEditMode]) {
                AddEditMode.ADD_ORDER -> newOrderModel.makeOrder(newRecord = true)
                AddEditMode.EDIT_ORDER -> newOrderModel.makeOrder(newRecord = false)
                AddEditMode.ADD_SUB_ORDER -> newOrderModel.makeSubOrder(FalseStr.str, true)
                AddEditMode.EDIT_SUB_ORDER -> newOrderModel.makeSubOrder(FalseStr.str, false)
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

    /**
     * Pull refresh ----------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onPullRefresh(backStackEntry: State<NavBackStackEntry?>) {
        when (backStackEntry.value?.destination?.route) {
            Screen.Main.Team.Employees.routeWithArgKeys() -> teamModel.updateEmployeesData()
            Screen.Main.Team.Users.routeWithArgKeys() -> teamModel.updateEmployeesData()
            Screen.Main.Team.Requests.routeWithArgKeys() -> teamModel.updateEmployeesData()
            Screen.Main.Inv.routeWithArgKeys() -> invModel.uploadNewInvestigations()
            Screen.Main.ProcessControl.routeWithArgKeys() -> invModel.uploadNewInvestigations()
            Screen.Main.Settings.UserDetails.route -> settingsModel.updateUserData()
            Screen.Main.Settings.EditUserDetails.routeWithArgKeys() -> settingsModel.updateUserData()
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