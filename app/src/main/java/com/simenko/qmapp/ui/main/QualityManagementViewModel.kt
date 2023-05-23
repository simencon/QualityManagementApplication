package com.simenko.qmapp.ui.main

import androidx.lifecycle.*
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject

private const val TAG = "QualityManagementViewMo"

@HiltViewModel
class QualityManagementViewModel @Inject constructor(
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val investigationsRepository: InvestigationsRepository
) : ViewModel() {
    /**
     * Gets data from Repositories - which is live data with list
     */

    val isLoadingInProgress = MutableLiveData(false)
    val isNetworkError = MutableLiveData(false)

    val departments = manufacturingRepository.departments
    val departmentsDetailed = manufacturingRepository.departmentsDetailed
    val subDepartments = manufacturingRepository.subDepartments
    val channels = manufacturingRepository.channels
    val lines = manufacturingRepository.lines
    val operations = manufacturingRepository.operations

    val inputForOrder = investigationsRepository.inputForOrder
    val investigationReasons = investigationsRepository.investigationReasons

    var currentTitle = MutableLiveData("")

    fun onNetworkErrorShown() {
        isLoadingInProgress.value = false
        isNetworkError.value = false
    }
    /**
     *
     */
    fun refreshMasterDataFromRepository() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

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

                investigationsRepository.refreshInputForOrder()
                investigationsRepository.refreshOrdersStatuses()
                investigationsRepository.refreshInvestigationReasons()
                investigationsRepository.refreshInvestigationTypes()
                investigationsRepository.refreshOrders()
                investigationsRepository.refreshSubOrders()
                investigationsRepository.refreshSubOrderTasks()
                investigationsRepository.refreshSamples()
                investigationsRepository.refreshResultsDecryptions()
                investigationsRepository.refreshResults()
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }
}