package com.simenko.qmapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.TopScreenIntent
import com.simenko.qmapp.ui.main.main.setup.FabSetup
import com.simenko.qmapp.ui.main.main.setup.PullRefreshSetup
import com.simenko.qmapp.ui.main.main.setup.TopBarSetup
import com.simenko.qmapp.ui.main.main.setup.TopTabsSetup
import com.simenko.qmapp.ui.main.main.content.Common
import com.simenko.qmapp.ui.main.main.content.MenuItem
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.subscribeNavigationEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val userRepository: UserRepository,
    private val systemRepository: SystemRepository,
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository,
    val navHostController: NavHostController
) : ViewModel() {
    val userInfo get() = userRepository.user

    /**
     * Top bar state holders -------------------------------------------------------------------------------------------------------------------------
     * */
    private val _topBarSetup = MutableStateFlow(TopBarSetup())
    val topBarSetup get() = _topBarSetup.asStateFlow()

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

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    init {
        subscribeMainScreenSetupEvents(mainPageState.topScreenChannel.receiveAsFlow())
        appNavigator.navigationChannel.receiveAsFlow().subscribeNavigationEvents(viewModelScope, navHostController)
    }

    private fun subscribeMainScreenSetupEvents(intents: Flow<TopScreenIntent>) {
        viewModelScope.launch {
            combine(intents, _topTabsSetup, _fabSetup, _pullRefreshSetup) { intent, topTabsSetup, fabSetup, pullRefreshSetup ->
                handleEvent(intent, topTabsSetup, fabSetup, pullRefreshSetup)
            }.collect()
        }
    }

    private fun handleEvent(intent: TopScreenIntent, topTabsSetup: TopTabsSetup, fabSetup: FabSetup, pullRefreshSetup: PullRefreshSetup) {
        when (intent) {
            is TopScreenIntent.MainPageSetup -> setupMainPage(intent.topBarSetup, intent.topTabsSetup, intent.fabSetup, intent.pullRefreshSetup)
            is TopScreenIntent.TabBadgesState -> topTabsSetup.updateBadgeContent(intent.state)
            is TopScreenIntent.SelectedTabState -> topTabsSetup.setSelectedTab(intent.state)
            is TopScreenIntent.FabVisibilityState -> fabSetup.setFabVisibility(intent.state)
            is TopScreenIntent.EndOfListState -> fabSetup.onEndOfList(intent.state)
            is TopScreenIntent.LoadingState -> pullRefreshSetup.updateLoadingState(intent.state)
        }
    }

    private fun setupMainPage(topBarSetup: TopBarSetup, topTabsSetup: TopTabsSetup, fabSetup: FabSetup, pullRefreshSetup: PullRefreshSetup) {
        this._topBarSetup.value = topBarSetup
        val currentOnActionItemClick: ((MenuItem) -> Unit)? = topBarSetup.onActionItemClick
        topBarSetup.onActionItemClick = {
            currentOnActionItemClick?.invoke(it)
            if (it == Common.UPLOAD_MASTER_DATA) refreshMasterDataFromRepository()
        }
        this._topTabsSetup.value = topTabsSetup
        this._fabSetup.value = fabSetup
        this._pullRefreshSetup.value = pullRefreshSetup
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDrawerMenuTeamSelected() {
        appNavigator.tryNavigateTo(route = Route.Main.Team.link, popUpToRoute = Route.Main.Team.route, inclusive = true)
    }

    fun onDrawerMenuCompanyStructureSelected() {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.withOpts(), popUpToRoute = Route.Main.CompanyStructure.route, inclusive = true)
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

    private fun refreshMasterDataFromRepository() = viewModelScope.launch {
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