package com.simenko.qmapp.ui.neworder

import android.content.Context
import androidx.lifecycle.*
import com.simenko.qmapp.di.neworder.NewItemScope
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.room.implementation.getDatabase
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@NewItemScope
class NewItemViewModel @Inject constructor(
    context: Context
) : ViewModel() {

    private val roomDatabase = getDatabase(context)

    private val qualityManagementInvestigationsRepository =
        QualityManagementInvestigationsRepository(roomDatabase)
    private val qualityManagementManufacturingRepository =
        QualityManagementManufacturingRepository(roomDatabase)

    init {
        refreshDataFromRepository()
    }

    private val pairedTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

    inner class ChangeState<T : DomainModel>(private val destination: MutableList<T>) {
        lateinit var source: List<T>

        constructor(destination: MutableList<T>, source: List<T>) : this(destination) {
            this.source = source
        }

        fun selectSingleRecord(record: T) {
            destination.forEach {
                it.setIsChecked(false)
            }
            destination.find { it.getRecordId() == record.getRecordId() }?.setIsChecked(true)
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
        }

        fun updateMutableList(parentId: Int) {
            destination.clear()
            source.apply {
                this.filter { it.getRecordId() > parentId }.forEach { input ->
                    if (destination.find { it.getRecordId() == input.getRecordId() } == null) {
                        destination.add(input)
                    }
                }
            }
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
        }
    }

    val investigationTypes = qualityManagementInvestigationsRepository.investigationTypes
    private val orderTypes = MutableLiveData<MutableList<DomainOrdersType>>(mutableListOf())
    val investigationTypesMediator: MediatorLiveData<Pair<MutableList<DomainOrdersType>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainOrdersType>?, Boolean?>>().apply {
            addSource(orderTypes) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(orderTypes.value, it) }
        }

    private val investigationReasons =
        qualityManagementInvestigationsRepository.investigationReasons
    val investigationReasonsMediator: MediatorLiveData<Pair<List<DomainMeasurementReason>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainMeasurementReason>?, Boolean?>>().apply {
            addSource(investigationReasons) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(investigationReasons.value, it) }
        }

    private val customers = qualityManagementManufacturingRepository.departments
    val customersMediator: MediatorLiveData<Pair<List<DomainDepartment>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainDepartment>?, Boolean?>>().apply {
            addSource(customers) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(customers.value, it) }
        }

    private val teamMembers = qualityManagementManufacturingRepository.teamMembers
    val teamMembersMediator: MediatorLiveData<Pair<List<DomainTeamMember>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainTeamMember>?, Boolean?>>().apply {
            addSource(teamMembers) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(teamMembers.value, it) }
        }

    private val inputForOrder = qualityManagementInvestigationsRepository.inputForOrder
    val inputForOrderMediator: MediatorLiveData<Pair<List<DomainInputForOrder>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainInputForOrder>?, Boolean?>>().apply {
            addSource(inputForOrder) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(inputForOrder.value, it) }
        }

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
                qualityManagementInvestigationsRepository.refreshInvestigationTypes()
                qualityManagementInvestigationsRepository.refreshInvestigationReasons()
                qualityManagementInvestigationsRepository.refreshInputForOrder()
                qualityManagementManufacturingRepository.refreshDepartments()
                qualityManagementManufacturingRepository.refreshTeamMembers()
            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if (inputForOrder.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

}