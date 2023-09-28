package com.simenko.qmapp.ui.main

import android.util.Log
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.study.TestDiClassActivityRetainedScope
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.common.FabSetup
import com.simenko.qmapp.ui.common.MainPage
import com.simenko.qmapp.ui.common.TopBarSetup
import com.simenko.qmapp.ui.common.TopScreenState
import com.simenko.qmapp.ui.common.TopTabsContent
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.ui.main.main.MenuItem
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

private const val TAG = "MainActivityViewModel"

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

    fun logWhenInstantiated() {
        Log.d(TAG, "logWhenInstantiated: ${testDiScope.getOwnerName()}")
    }

    val userInfo get() = userRepository.user

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

    private val _fabPosition: MutableStateFlow<FabPosition> = MutableStateFlow(FabPosition.End)
    val fabPosition: StateFlow<FabPosition> get() = _fabPosition


    fun onEndOfList(position: Boolean) {
        _fabPosition.value = if (position) FabPosition.Center else FabPosition.End
    }

    private val _selectedDrawerMenuItemId: MutableStateFlow<String> = MutableStateFlow(MenuItem.getStartingDrawerMenuItem().id)
    val selectedDrawerMenuItemId: StateFlow<String> get() = _selectedDrawerMenuItemId
    fun setDrawerMenuItemId(id: String) {
        this._selectedDrawerMenuItemId.value = id
    }

    private val _addEditMode: MutableStateFlow<Int> = MutableStateFlow(AddEditMode.NO_MODE.ordinal)
    val addEditMode get() = _addEditMode.asStateFlow()
    private val _addEditAction: MutableStateFlow<() -> Unit> = MutableStateFlow {}
    val addEditAction get() = _addEditAction.asStateFlow()
    private val _refreshAction: MutableStateFlow<() -> Unit> = MutableStateFlow {}
    val refreshAction get() = _refreshAction.asStateFlow()
    private val _filterAction: MutableStateFlow<(BaseFilter) -> Unit> = MutableStateFlow {}
    val filterAction get() = _filterAction.asStateFlow()
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

    private val _topBarSetup = MutableStateFlow(TopBarSetup())
    val topBarSetup get() = _topBarSetup.asStateFlow()

    private val _topTabsSetup = MutableStateFlow(TopTabsContent())
    val topTabsSetup get() = _topTabsSetup.asStateFlow()

    fun setupTopScreenDev(
        titleSetup: TopBarSetup,
        topTabsSetup: TopTabsContent,
        fabSetup: FabSetup,
        refreshAction: () -> Unit,
        filterAction: (BaseFilter) -> Unit
    ) {
        this._topBarSetup.value = titleSetup
        this._topBarSetup.value.onNavBtnClick = { if (it) _drawerMenuState.value.open() else _drawerMenuState.value.close() }
        this._topBarSetup.value.onSearchBtnClick = { setSearchBarState(it) }
        this._topBarSetup.value.onActionBtnClick = { setActionMenuState(it) }

        this._topTabsSetup.value = topTabsSetup
        this._topTabsSetup.value.onTabClickAction = { setSelectedTabIndex(it) }
    }

    private val _badgeItem = Triple(0, Color.Red, Color.White)
    private val _topBadgeCounts: MutableStateFlow<List<Triple<Int, Color, Color>>> =
        MutableStateFlow(listOf(_badgeItem, _badgeItem, _badgeItem, _badgeItem))
    val topBadgeCounts: StateFlow<List<Triple<Int, Color, Color>>> get() = _topBadgeCounts
    fun setTopBadgesCount(index: Int, badgeCount: Int, bg: Color, cnt: Color) {
        if (index < 4) {
            var i = 0
            _topBadgeCounts.value = _topBadgeCounts.value.map { if (index == i++) Triple(badgeCount, bg, cnt) else it }.toList()
        }
    }

    fun resetTopBadgesCount() {
        _topBadgeCounts.value = listOf(_badgeItem, _badgeItem, _badgeItem, _badgeItem)
    }

    fun onBackFromAddEditMode() {
        appNavigator.tryNavigateBack()
    }

    private val _drawerMenuState = MutableStateFlow(DrawerState(DrawerValue.Closed))
    val drawerMenuState = _drawerMenuState.asStateFlow()
    suspend fun setDrawerMenuState(value: Boolean) {
        if (value) _drawerMenuState.value.open() else _drawerMenuState.value.close()
    }

    private val _searchBarState = MutableStateFlow(false)
    val searchBarState = _searchBarState.asStateFlow()
    fun setSearchBarState(value: Boolean) {
        _searchBarState.value = value
    }

    private val _actionsMenuState = MutableStateFlow(false)
    val actionsMenuState = _actionsMenuState.asStateFlow()
    fun setActionMenuState(value: Boolean) {
        _searchBarState.value = value
    }

    private val _selectedTabIndex = MutableStateFlow(ZeroValue.num)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()
    fun setSelectedTabIndex(value: Int) {
        _selectedTabIndex.value = value
    }

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