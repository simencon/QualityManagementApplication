package com.simenko.qmapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository
) : ViewModel() {
    private val _isLoadingInProgress = MutableStateFlow(false)
    val isLoadingInProgress: LiveData<Boolean> = _isLoadingInProgress.asLiveData()
    private val _isErrorMessage = MutableStateFlow<String?>(null)
    val isErrorMessage: LiveData<String?> = _isErrorMessage.asLiveData()

    fun updateLoadingState(state: Pair<Boolean, String?>) {
        _isLoadingInProgress.value = state.first
        _isErrorMessage.value = state.second
    }

    fun onNetworkErrorShown() {
        _isLoadingInProgress.value = false
        _isErrorMessage.value = null
    }

    fun refreshMasterDataFromRepository() = flow {
        try {
            emit(Pair(true, null))
            _isLoadingInProgress.value = true

            manufacturingRepository.refreshPositionLevels()
            manufacturingRepository.refreshTeamMembers()
            manufacturingRepository.refreshCompanies()
            manufacturingRepository.refreshDepartments()
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

            emit(Pair(false, null))

        } catch (e: Exception) {
            emit(Pair(false, e.message))
        }
    }
}