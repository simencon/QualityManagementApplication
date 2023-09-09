package com.simenko.qmapp.ui.main

import androidx.compose.material3.FabPosition
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val systemRepository: SystemRepository,
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository
) : ViewModel() {
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

    fun onListEnd(position: FabPosition) {
        _fabPosition.value = position
    }

    private val _selectedDrawerMenuItemId: MutableStateFlow<String> = MutableStateFlow(MenuItem.getStartingDrawerMenuItem().id)
    val selectedDrawerMenuItemId: StateFlow<String> get() = _selectedDrawerMenuItemId
    fun setDrawerMenuItemId(id: String) {
        this._selectedDrawerMenuItemId.value = id
    }

    private val _addEditMode: MutableStateFlow<Int> = MutableStateFlow(AddEditMode.NO_MODE.ordinal)
    val addEditMode: StateFlow<Int> get() = _addEditMode
    fun setAddEditMode(mode: AddEditMode) {
        this._addEditMode.value = mode.ordinal
    }

    private val _topBadgeCounts: MutableStateFlow<MutableList<Int>> = MutableStateFlow(mutableListOf(0, 0, 0, 0))
    val topBadgeCounts: StateFlow<List<Int>> get() = _topBadgeCounts
    fun setTopBadgesCount(index: Int, badgeCount: Int) {
        if (index < 4) _topBadgeCounts.value[index] = badgeCount
    }

    fun resetTopBadgesCount() {
        _topBadgeCounts.value = mutableListOf(0, 0, 0, 0)
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