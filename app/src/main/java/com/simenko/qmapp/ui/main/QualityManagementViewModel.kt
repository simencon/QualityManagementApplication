package com.simenko.qmapp.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.simenko.qmapp.domain.DomainSubOrderComplete
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.repository.QualityManagementProductsRepository
import com.simenko.qmapp.room.implementation.getDatabase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.io.IOException
import javax.inject.Inject

class QualityManagementViewModel @Inject constructor (application: Application) : AndroidViewModel(application) {
    /**
     * Gets data from Repositories - which is live data with list
     */
    private val qualityManagementManufacturingRepository =
        QualityManagementManufacturingRepository(getDatabase(application))
    private val qualityManagementProductsRepository =
        QualityManagementProductsRepository(getDatabase(application))
    private val qualityManagementInvestigationsRepository =
        QualityManagementInvestigationsRepository(getDatabase(application))

    val teamMembers = qualityManagementManufacturingRepository.teamMembers
    val departments = qualityManagementManufacturingRepository.departments
    val departmentsDetailed = qualityManagementManufacturingRepository.departmentsDetailed
    val subDepartments = qualityManagementManufacturingRepository.subDepartments
    val channels = qualityManagementManufacturingRepository.channels
    val lines = qualityManagementManufacturingRepository.lines
    val operations = qualityManagementManufacturingRepository.operations

    val inputForOrder = qualityManagementInvestigationsRepository.inputForOrder
    val measurementReasons = qualityManagementInvestigationsRepository.measurementReasons

    val completeOrders = qualityManagementInvestigationsRepository.completeOrders

    val completeSubOrders = qualityManagementInvestigationsRepository.completeSubOrders
    val subOrderParentId: MutableLiveData<Int> = MutableLiveData(-1)

    val subOrderLiveData: MediatorLiveData<Pair<List<DomainSubOrderComplete>?, Int?>> =
        MediatorLiveData<Pair<List<DomainSubOrderComplete>?, Int?>>().apply {
            addSource(completeSubOrders) { value = Pair(it, subOrderParentId.value) }
            addSource(subOrderParentId) { value = Pair(completeSubOrders.value, it) }
        }

    val completeSubOrderTasks = qualityManagementInvestigationsRepository.completeSubOrderTasks

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

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QualityManagementViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QualityManagementViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }


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