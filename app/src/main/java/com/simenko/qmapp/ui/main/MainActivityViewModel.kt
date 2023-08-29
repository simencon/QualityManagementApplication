package com.simenko.qmapp.ui.main

import androidx.compose.material3.FabPosition
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository
) : ViewModel() {
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

    fun refreshMasterDataFromRepository() = viewModelScope.launch {
        try {
            updateLoadingState(Pair(true, null))

            manufacturingRepository.refreshPositionLevels()
            manufacturingRepository.syncTeamMembers()
            manufacturingRepository.refreshCompanies()
            manufacturingRepository.syncDepartments()
            manufacturingRepository.refreshSubDepartments()
            manufacturingRepository.refreshManufacturingChannels()
            manufacturingRepository.refreshManufacturingLines()
            manufacturingRepository.refreshManufacturingOperations()
            manufacturingRepository.refreshOperationsFlows()

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