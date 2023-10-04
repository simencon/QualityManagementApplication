package com.simenko.qmapp.ui.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
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
import com.simenko.qmapp.ui.main.main.MenuItem
import com.simenko.qmapp.ui.main.main.page.components.FabSetup
import com.simenko.qmapp.ui.main.main.page.components.PullRefreshSetup
import com.simenko.qmapp.ui.main.main.page.components.TopTabsSetup
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
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
    fun setupTopScreen(
        topBarSetup: TopBarSetup,
        topTabsSetup: TopTabsSetup
    ) {
        this._topBarSetup.value = topBarSetup
        this._topBarSetup.value.onNavBtnClick = topBarSetup.onNavBtnClick ?: { setDrawerMenuState(it) }
        this._topBarSetup.value.onSearchBtnClick = topBarSetup.onSearchBtnClick ?: { setSearchBarState(it) }
        this._topBarSetup.value.onActionBtnClick = topBarSetup.onActionBtnClick ?: { setActionMenuState(it) }

        this._topTabsSetup.value = topTabsSetup
    }

    fun setupTopScreenFab(fabSetup: FabSetup) {
        println("setupTopScreenFab - $fabSetup")
        this._fabSetup.value = fabSetup
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
    private val _pullRefreshSetup = MutableStateFlow(PullRefreshSetup())
    val pullRefreshSetup get() = _pullRefreshSetup.asStateFlow()

    fun setupTopScreenPullRefresh(pullRefreshSetup: PullRefreshSetup) {
        _pullRefreshSetup.value = pullRefreshSetup
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

    fun onAddInvClick() {
        appNavigator.tryNavigateTo(route = Route.Main.OrderAddEdit.withArgs(NoRecordStr.str))
    }

    fun onAddProcessControlClick() {
        appNavigator.tryNavigateTo(route = Route.Main.SubOrderAddEdit.withArgs(NoRecordStr.str, NoRecordStr.str, TrueStr.str))
    }


    fun refreshMasterDataFromRepository() = viewModelScope.launch {
        try {
            pullRefreshSetup.value.updateLoadingState(Pair(true, null))

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

            pullRefreshSetup.value.updateLoadingState(Pair(false, null))
        } catch (e: Exception) {
            pullRefreshSetup.value.updateLoadingState(Pair(false, e.message))
        }
    }
}