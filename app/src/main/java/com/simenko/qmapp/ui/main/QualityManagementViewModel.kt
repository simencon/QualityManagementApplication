package com.simenko.qmapp.ui.main

import android.content.Context
import androidx.lifecycle.*
import com.simenko.qmapp.di.main.MainScope
import com.simenko.qmapp.domain.DomainOrderComplete
import com.simenko.qmapp.domain.DomainSubOrderComplete
import com.simenko.qmapp.domain.DomainTeamMember
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.repository.QualityManagementProductsRepository
import com.simenko.qmapp.room.implementation.getDatabase
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@MainScope
class QualityManagementViewModel @Inject constructor(
    context: Context
) : ViewModel() {
    /**
     * Gets data from Repositories - which is live data with list
     */

    private val roomDatabase = getDatabase(context)

    private val qualityManagementManufacturingRepository =
        QualityManagementManufacturingRepository(roomDatabase)
    private val qualityManagementProductsRepository =
        QualityManagementProductsRepository(roomDatabase)
    private val qualityManagementInvestigationsRepository =
        QualityManagementInvestigationsRepository(roomDatabase)

    init {
        refreshDataFromRepository()
    }

    private val pairedTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

    private val teamMembers = qualityManagementManufacturingRepository.teamMembers
    val teamMembersMediator: MediatorLiveData<Pair<List<DomainTeamMember>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainTeamMember>?, Boolean?>>().apply {
            addSource(teamMembers) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(teamMembers.value, it) }
        }

    fun changeTeamMembersDetailsVisibility(item: DomainTeamMember): Unit {
        teamMembers.value?.find { it.id == item.id }?.let { member ->
            member.detailsVisibility = !member.detailsVisibility
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
        }
    }

    val departments = qualityManagementManufacturingRepository.departments
    val departmentsDetailed = qualityManagementManufacturingRepository.departmentsDetailed
    val subDepartments = qualityManagementManufacturingRepository.subDepartments
    val channels = qualityManagementManufacturingRepository.channels
    val lines = qualityManagementManufacturingRepository.lines
    val operations = qualityManagementManufacturingRepository.operations

    val inputForOrder = qualityManagementInvestigationsRepository.inputForOrder
    val measurementReasons = qualityManagementInvestigationsRepository.measurementReasons

    val completeOrders = qualityManagementInvestigationsRepository.completeOrders
    val completeOrdersMediator: MediatorLiveData<Pair<List<DomainOrderComplete>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainOrderComplete>?, Boolean?>>().apply {
            addSource(completeOrders) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(completeOrders.value, it) }
        }
    fun changeCompleteOrdersDetailsVisibility(item: DomainOrderComplete): Unit {
        completeOrders.value?.find { it.order.id == item.order.id }?.let { order ->
            order.detailsVisibility = !order.detailsVisibility
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
        }
    }

    val completeSubOrders = qualityManagementInvestigationsRepository.completeSubOrders
    val completeSubOrdersMediator: MediatorLiveData<Pair<List<DomainSubOrderComplete>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainSubOrderComplete>?, Boolean?>>().apply {
            addSource(completeSubOrders) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(completeSubOrders.value, it) }
        }
    fun changeCompleteSubOrdersDetailsVisibility(item: DomainSubOrderComplete): Unit {
        completeSubOrders.value?.find { it.subOrder.id == item.subOrder.id }?.let { subOrder ->
            subOrder.detailsVisibility = !subOrder.detailsVisibility
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
        }
    }

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

    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * Runs every time when ViewModel in initializing process
     */

    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
//                for (i in 1..30) {
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

                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

//                    delay(5000)
//                }

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if (departments.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

}