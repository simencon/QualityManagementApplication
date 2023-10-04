package com.simenko.qmapp.ui.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.study.TestDiClassActivityRetainedScope
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.main.main.page.TopBarSetup
import com.simenko.qmapp.ui.main.main.page.TopScreenState
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.ui.main.main.MenuItem
import com.simenko.qmapp.ui.main.main.page.components.FabSetup
import com.simenko.qmapp.ui.main.main.page.components.TopTabsSetup
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.BaseFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val topScreenState: TopScreenState,
    private val userRepository: UserRepository,
    private val systemRepository: SystemRepository,
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository,
    private val testDiScope: TestDiClassActivityRetainedScope
) : ViewModel() {
    val navigationChannel = appNavigator.navigationChannel
    val topScreenChannel = topScreenState.topScreenChannel
    val userInfo get() = userRepository.user

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setupTopScreenDev(
        topBarSetup: TopBarSetup,
        topTabsSetup: TopTabsSetup,
        fabSetup: FabSetup,
        refreshAction: () -> Unit
    ) {
        this._topBarSetup.value = topBarSetup
        this._topBarSetup.value.onNavBtnClick = topBarSetup.onNavBtnClick ?: { setDrawerMenuState(it) }
        this._topBarSetup.value.onSearchBtnClick = topBarSetup.onSearchBtnClick ?: { setSearchBarState(it) }
        this._topBarSetup.value.onActionBtnClick = topBarSetup.onActionBtnClick ?: { setActionMenuState(it) }

        this._topTabsSetup.value = topTabsSetup
    }

    /**
     * Top bar state holders -------------------------------------------------------------------------------------------------------------------------
     * */
    private val _topBarSetup = MutableStateFlow(TopBarSetup())
    val topBarSetup get() = _topBarSetup.asStateFlow()

    private val _selectedDrawerMenuItemId: MutableStateFlow<String> = MutableStateFlow(MenuItem.getStartingDrawerMenuItem().id)
    val selectedDrawerMenuItemId: StateFlow<String> get() = _selectedDrawerMenuItemId
    fun setDrawerMenuItemId(id: String) {
        this._selectedDrawerMenuItemId.value = id
    }

    private val _drawerMenuState = MutableStateFlow(DrawerState(DrawerValue.Closed))
    val drawerMenuState = _drawerMenuState.asStateFlow()
    private suspend fun setDrawerMenuState(value: Boolean) {
        if (value) _drawerMenuState.value.open() else _drawerMenuState.value.close()
    }

    private val _searchBarState = MutableStateFlow(false)
    val searchBarState = _searchBarState.asStateFlow()
    private fun setSearchBarState(value: Boolean) {
        _searchBarState.value = value
    }

    private val _actionsMenuState = MutableStateFlow(false)
    val actionsMenuState = _actionsMenuState.asStateFlow()
    private fun setActionMenuState(value: Boolean) {
        _actionsMenuState.value = value
    }

    /**
     * Top tabs state holders ------------------------------------------------------------------------------------------------------------------------
     * */
    private val _topTabsSetup = MutableStateFlow(TopTabsSetup())
    val topTabsSetup get() = _topTabsSetup.asStateFlow()

    /**
     * FAB state holders -----------------------------------------------------------------------------------------------------------------------------
     * */
    private val _fabSetup = MutableStateFlow(FabSetup())
    val fabSetup get() = _fabSetup.asStateFlow()

    /**
     * Full refresh holders --------------------------------------------------------------------------------------------------------------------------
     * */
    private val _isLoadingInProgress = MutableStateFlow(false)
    val isLoadingInProgress: StateFlow<Boolean> get() = _isLoadingInProgress
    private val _isErrorMessage = MutableStateFlow<String?>(null)
    val isErrorMessage: StateFlow<String?> get() = _isErrorMessage

    fun updateLoadingState(state: Pair<Boolean, String?>) {
        _isLoadingInProgress.value = state.first
        _isErrorMessage.value = state.second
    }

    fun onNetworkErrorShown() {
        _isLoadingInProgress.value = false
        _isErrorMessage.value = null
    }

//    TODO: To be deleted

    private val _addEditMode: MutableStateFlow<Int> = MutableStateFlow(AddEditMode.NO_MODE.ordinal)
    val addEditMode get() = _addEditMode.asStateFlow()
    private val _addEditAction: MutableStateFlow<() -> Unit> = MutableStateFlow {}
    val addEditAction get() = _addEditAction.asStateFlow()
    private val _refreshAction: MutableStateFlow<() -> Unit> = MutableStateFlow {}
    val refreshAction get() = _refreshAction.asStateFlow()
    private val _filterAction: MutableStateFlow<(BaseFilter) -> Unit> = MutableStateFlow {}

    fun setupTopScreen(
        mode: AddEditMode,
        addEditAction: () -> Unit = {},
        refreshAction: () -> Unit = {},
        searchAction: (BaseFilter) -> Unit = {}
    ) {
        this._addEditMode.value = mode.ordinal
        this._addEditAction.value = addEditAction
        this._refreshAction.value = refreshAction
        this._filterAction.value = searchAction
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    fun onDrawerMenuTeamSelected() {
        appNavigator.tryNavigateTo(route = Route.Main.Team.link, popUpToRoute = Route.Main.Team.route, inclusive = true)
    }

    fun onDrawerMenuInvSelected() {
        appNavigator.tryNavigateTo(route = Route.Main.Inv.withOpts(), popUpToRoute = Route.Main.Inv.route, inclusive = true)
    }

    fun onDrawerMenuProcessControlSelected() {
        appNavigator.tryNavigateTo(route = Route.Main.ProcessControl.withOpts(), popUpToRoute = Route.Main.ProcessControl.route, inclusive = true)
    }

    fun onDrawerMenuSettingsSelected() {
        appNavigator.tryNavigateTo(route = Route.Main.Settings.link, popUpToRoute = Route.Main.Settings.route, inclusive = true)
    }

    fun onTopTabsEmployeesClick() {
        appNavigator.tryNavigateBack()
    }

    fun onTopTabsUsersClick() {
        appNavigator.tryNavigateTo(route = Route.Main.Team.Users.withArgs(NoRecordStr.str), popUpToRoute = Route.Main.Team.Employees.link)
    }

    fun onTopTabsRequestsClick() {
        appNavigator.tryNavigateTo(route = Route.Main.Team.Requests.withArgs(NoRecordStr.str), popUpToRoute = Route.Main.Team.Employees.link)
    }

    fun onAddEmployeeClick() {
        appNavigator.tryNavigateTo(route = Route.Main.Team.EmployeeAddEdit.withArgs(NoRecordStr.str))
    }

    fun onAddInvClick() {
        appNavigator.tryNavigateTo(route = Route.Main.OrderAddEdit.withArgs(NoRecordStr.str))
    }

    fun onAddProcessControlClick() {
        appNavigator.tryNavigateTo(route = Route.Main.SubOrderAddEdit.withArgs(NoRecordStr.str, NoRecordStr.str, TrueStr.str))
    }


    fun refreshMasterDataFromRepository() = viewModelScope.launch {
        try {
            updateLoadingState(Pair(true, null))

            systemRepository.syncUserRoles()
            systemRepository.syncUsers()

            manufacturingRepository.syncTeamMembers()
            manufacturingRepository.syncCompanies()
            manufacturingRepository.syncJobRoles()
            manufacturingRepository.syncDepartments()
            manufacturingRepository.syncSubDepartments()
            manufacturingRepository.syncChannels()
            manufacturingRepository.syncLines()
            manufacturingRepository.syncOperations()
            manufacturingRepository.syncOperationsFlows()

            productsRepository.refreshElementIshModels()
            productsRepository.refreshIshSubCharacteristics()
            productsRepository.refreshManufacturingProjects()
            productsRepository.refreshCharacteristics()
            productsRepository.refreshMetrixes()
            productsRepository.refreshKeys()
            productsRepository.refreshProductBases()
            productsRepository.refreshProducts()
            productsRepository.refreshComponents()
            productsRepository.refreshComponentInStages()
            productsRepository.refreshVersionStatuses()
            productsRepository.refreshProductVersions()
            productsRepository.refreshComponentVersions()
            productsRepository.refreshComponentInStageVersions()
            productsRepository.refreshProductTolerances()
            productsRepository.refreshComponentTolerances()
            productsRepository.refreshComponentInStageTolerances()
            productsRepository.refreshProductsToLines()
            productsRepository.refreshComponentsToLines()
            productsRepository.refreshComponentInStagesToLines()

            repository.syncInputForOrder()
            repository.syncOrdersStatuses()
            repository.syncInvestigationReasons()
            repository.syncInvestigationTypes()
            repository.syncResultsDecryptions()

            updateLoadingState(Pair(false, null))
        } catch (e: Exception) {
            updateLoadingState(Pair(false, e.message))
        }
    }
}