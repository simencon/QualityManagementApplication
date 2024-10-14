package com.simenko.qmapp.presentation.ui.main

import androidx.compose.material3.FabPosition
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.usecase.products.SyncProductsUseCase
import com.simenko.qmapp.data.repository.InvestigationsRepository
import com.simenko.qmapp.data.repository.ManufacturingRepository
import com.simenko.qmapp.data.repository.SystemRepository
import com.simenko.qmapp.data.repository.UserRepository
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.TopScreenIntent
import com.simenko.qmapp.presentation.ui.main.main.setup.FabSetup
import com.simenko.qmapp.presentation.ui.main.main.setup.PullRefreshSetup
import com.simenko.qmapp.presentation.ui.main.main.setup.TopBarSetup
import com.simenko.qmapp.presentation.ui.main.main.setup.TopTabsSetup
import com.simenko.qmapp.presentation.ui.main.main.content.Common
import com.simenko.qmapp.presentation.ui.main.main.content.MenuItem
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val userRepository: UserRepository,
    private val systemRepository: SystemRepository,
    private val manufacturingRepository: ManufacturingRepository,
    private val syncProductsUseCase: SyncProductsUseCase,
    private val repository: InvestigationsRepository,
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
    }

    private fun subscribeMainScreenSetupEvents(intents: Flow<TopScreenIntent>) {
        viewModelScope.launch(Dispatchers.Default) {
            intents.collect {
                handleEvent(it, _topTabsSetup.value, _pullRefreshSetup.value)
            }
        }
    }

    private fun handleEvent(intent: TopScreenIntent, topTabsSetup: TopTabsSetup, pullRefreshSetup: PullRefreshSetup) {
        when (intent) {
            is TopScreenIntent.MainPageSetup -> setupMainPage(intent.topBarSetup, intent.topTabsSetup, intent.fabSetup, intent.pullRefreshSetup)
            is TopScreenIntent.TabBadgesState -> topTabsSetup.updateBadgeContent(intent.state)
            is TopScreenIntent.SelectedTabState -> topTabsSetup.setSelectedTab(intent.state)
            is TopScreenIntent.FabVisibilityState -> _fabSetup.value = _fabSetup.value.copy(isFabVisible = intent.state)
            is TopScreenIntent.FabIconState -> _fabSetup.value = _fabSetup.value.copy(fabIcon = intent.state)
            is TopScreenIntent.EndOfListState -> _fabSetup.value = _fabSetup.value.copy(fabPosition = if (intent.state) FabPosition.Center else FabPosition.End)
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
        appNavigator.tryNavigateTo(route = Route.Main.Team, popUpToRoute = Route.Main, inclusive = true)
    }

    fun onDrawerMenuCompanyStructureSelected() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
//                todo-me - companyId should be initially stored under user
                (manufacturingRepository.companyByName(userRepository.user.company)?.id ?: NoRecord.num).let {
                    appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.StructureView(companyId = it), popUpToRoute = Route.Main, inclusive = true)
                }
            }
        }
    }

    fun onDrawerMenuProductsSelected() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
//                todo-me - companyId should be initially stored under user
                (manufacturingRepository.companyByName(userRepository.user.company)?.id ?: NoRecord.num).let {
                    appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductLinesList(companyId = it), popUpToRoute = Route.Main, inclusive = true)
                }
            }
        }
    }

    fun onDrawerMenuInvSelected() {
        appNavigator.tryNavigateTo(route = Route.Main.AllInvestigations, popUpToRoute = Route.Main, inclusive = true)
    }

    fun onDrawerMenuProcessControlSelected() {
        appNavigator.tryNavigateTo(route = Route.Main.ProcessControl, popUpToRoute = Route.Main, inclusive = true)
    }

    fun onDrawerMenuSettingsSelected() {
        appNavigator.tryNavigateTo(route = Route.Main.Settings, popUpToRoute = Route.Main.Settings, inclusive = true)
    }

    private fun refreshMasterDataFromRepository() = viewModelScope.launch {
        try {
            pullRefreshSetup.value.updateLoadingState(Triple(true, false, null))

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

            syncProductsUseCase.execute()

            repository.syncInputForOrder()
            repository.syncOrdersStatuses()
            repository.syncInvestigationReasons()
            repository.syncInvestigationTypes()
            repository.syncResultsDecryptions()

            pullRefreshSetup.value.updateLoadingState(Triple(false, false, null))
        } catch (e: Exception) {
            pullRefreshSetup.value.updateLoadingState(Triple(false, false, e.message))
        }
    }
}