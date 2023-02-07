package com.simenko.qmapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.repository.QualityManagementProductsRepository
import com.simenko.qmapp.room_implementation.getDatabase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.io.IOException

class QualityManagementViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Gets data from Repositories - which is live data with list
     */
    private val qualityManagementManufacturingRepository =
        QualityManagementManufacturingRepository(getDatabase(application))
    private val qualityManagementProductsRepository =
        QualityManagementProductsRepository(getDatabase(application))
    private val qualityManagementInvestigationsRepository =
        QualityManagementInvestigationsRepository(getDatabase(application))

    val departments = qualityManagementManufacturingRepository.departments
    val teamMembers = qualityManagementManufacturingRepository.teamMembers
    val departmentsDetailed = qualityManagementManufacturingRepository.departmentsDetailed
    val inputForOrder = qualityManagementInvestigationsRepository.inputForOrder
    val measurementReasons = qualityManagementInvestigationsRepository.measurementReasons

    val completeOrders = qualityManagementInvestigationsRepository.completeOrders
    val completeSubOrders = qualityManagementInvestigationsRepository.completeSubOrders

    /**
     *
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     *
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    /**
     *
     */
    init {
        refreshDataFromRepository()
    }

    /**
     * Factory for constructing ViewModel with into specific application
     * Used here [com.simenko.qmapp.fragments.Fragment_____Structure.viewModel]
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QualityManagementViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QualityManagementViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    /**
     * Used in [com.simenko.qmapp.fragments.Fragment_____Structure.onNetworkError] to change trigger value
     */

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * Runs every time when ViewModel in initializing process
     */
    companion object {
        fun refreshSubOrderList(parentId: Int) {

        }
    }

    private fun refreshDataFromRepository() {
        val lock = Mutex()
        var job: Job? = null
        viewModelScope.launch {
            try {
//                runBlocking {
                qualityManagementManufacturingRepository.refreshPositionLevels()
                qualityManagementManufacturingRepository.refreshTeamMembers()
                qualityManagementManufacturingRepository.refreshCompanies()
                qualityManagementManufacturingRepository.refreshDepartments()
                qualityManagementManufacturingRepository.refreshSubDepartments()
                qualityManagementManufacturingRepository.refreshManufacturingChannels()
                qualityManagementManufacturingRepository.refreshManufacturingLines()
                qualityManagementManufacturingRepository.refreshManufacturingOperations()

                qualityManagementProductsRepository.refreshElementIshModels()
                qualityManagementProductsRepository.refreshIshSubCharacteristics()
                qualityManagementProductsRepository.refreshManufacturingProjects()
                qualityManagementProductsRepository.refreshCharacteristics()
                qualityManagementProductsRepository.refreshMetrixes()

                qualityManagementInvestigationsRepository.refreshInputForOrder()
                qualityManagementInvestigationsRepository.refreshOrdersStatuses()
                qualityManagementInvestigationsRepository.refreshMeasurementReasons()
                qualityManagementInvestigationsRepository.refreshOrdersTypes()
                qualityManagementInvestigationsRepository.refreshOrders()
                qualityManagementInvestigationsRepository.refreshSubOrders()
                qualityManagementInvestigationsRepository.refreshSubOrderTasks()
                qualityManagementInvestigationsRepository.refreshSamples()
                qualityManagementInvestigationsRepository.refreshResultsDecryptions()
                qualityManagementInvestigationsRepository.refreshResults()
//                }
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if (departments.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

}