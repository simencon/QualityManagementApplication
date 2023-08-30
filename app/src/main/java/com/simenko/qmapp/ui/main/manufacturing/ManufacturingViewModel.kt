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

    val departmentsDetailed = manufacturingRepository.departmentsDetailed
    val lines = manufacturingRepository.lines.asLiveData()
    val operations = manufacturingRepository.operations.asLiveData()


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

                manufacturingRepository.syncTeamMembers()
                manufacturingRepository.syncCompanies()
                manufacturingRepository.syncDepartments()
                manufacturingRepository.syncSubDepartments()
                manufacturingRepository.syncChannels()
                manufacturingRepository.syncLines()
                manufacturingRepository.syncOperations()
                manufacturingRepository.syncOperationsFlows()

                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }
}