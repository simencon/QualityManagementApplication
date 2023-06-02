package com.simenko.qmapp.ui.main.manufacturing

import androidx.lifecycle.*
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ManufacturingViewModel @Inject constructor(
    private val manufacturingRepository: ManufacturingRepository
) : ViewModel() {
    /**
     * Gets data from Repositories - which is live data with list
     */

    private val isLoadingInProgress = MutableLiveData(false)
    private val isNetworkError = MutableLiveData(false)

    val departments = manufacturingRepository.departments
    val departmentsDetailed = manufacturingRepository.departmentsDetailed
    val subDepartments = manufacturingRepository.subDepartments
    val channels = manufacturingRepository.channels
    val lines = manufacturingRepository.lines
    val operations = manufacturingRepository.operations


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

                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }
}