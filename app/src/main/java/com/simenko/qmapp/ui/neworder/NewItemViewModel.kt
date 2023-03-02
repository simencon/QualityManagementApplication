package com.simenko.qmapp.ui.neworder

import android.content.Context
import androidx.lifecycle.*
import androidx.room.Index
import com.simenko.qmapp.di.neworder.NewItemScope
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.retrofit.entities.NetworkOrder
import com.simenko.qmapp.retrofit.entities.toNetworkOrder
import com.simenko.qmapp.retrofit.implementation.QualityManagementNetwork
import com.simenko.qmapp.room.entities.toDatabaseOrder
import com.simenko.qmapp.room.implementation.getDatabase
import com.simenko.qmapp.ui.main.launchMainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
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

    fun <T : DomainModel> selectSingleRecord(d: MutableLiveData<MutableList<T>>, record: T?) {
        d.value?.forEach {
            it.setIsChecked(false)
        }
        d.value?.find { it.getRecordId() == record?.getRecordId() }?.setIsChecked(true)
        pairedTrigger.value = !(pairedTrigger.value as Boolean)
    }

    fun <T : DomainModel> filterWithOneParent(
        d: MutableLiveData<MutableList<T>>,
        s: LiveData<List<T>>,
        pId: Int
    ) {

        when (pId) {
//            Add all without deselection
            -2 -> {
                d.value?.clear()
                s.value?.let { d.value?.addAll(it.toList()) }
            }
//            Add all
            -1 -> {
                selectSingleRecord(
                    d,
                    null
                ) //Is made because previously selected/filtered/unfiltered item again selected
                d.value?.clear()
                s.value?.let { d.value?.addAll(it.toList()) }
            }
//            Clear all
            0 -> {
                selectSingleRecord(
                    d,
                    null
                ) //Is made because previously selected/filtered/unfiltered item again selected
                d.value?.clear()
            }
//            Add filtered by one parent id
            else -> {
                selectSingleRecord(
                    d,
                    null
                ) //Is made because previously selected/filtered/unfiltered item again selected
                d.value?.clear()
                s.apply {
                    this.value?.filter { it.getRecordId() > pId }?.forEach { input ->
                        if (d.value?.find { it.getRecordId() == input.getRecordId() } == null) {
                            d.value?.add(input)
                        }
                    }
                }
            }
        }

        pairedTrigger.value = !(pairedTrigger.value as Boolean)
    }

    private val pairedTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

    val investigationTypes = qualityManagementInvestigationsRepository.investigationTypes
    val investigationTypesMutable = MutableLiveData<MutableList<DomainOrdersType>>(mutableListOf())
    val investigationTypesMediator: MediatorLiveData<Pair<MutableList<DomainOrdersType>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainOrdersType>?, Boolean?>>().apply {
            addSource(investigationTypesMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(investigationTypesMutable.value, it) }
        }

    val investigationReasons = qualityManagementInvestigationsRepository.investigationReasons
    val investigationReasonsMutable =
        MutableLiveData<MutableList<DomainMeasurementReason>>(mutableListOf())
    val investigationReasonsMediator: MediatorLiveData<Pair<MutableList<DomainMeasurementReason>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainMeasurementReason>?, Boolean?>>().apply {
            addSource(investigationReasonsMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(investigationReasonsMutable.value, it) }
        }

    val customers = qualityManagementManufacturingRepository.departments
    val customersMutable = MutableLiveData<MutableList<DomainDepartment>>(mutableListOf())
    val customersMediator: MediatorLiveData<Pair<MutableList<DomainDepartment>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainDepartment>?, Boolean?>>().apply {
            addSource(customersMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(customersMutable.value, it) }
        }

    val teamMembers = qualityManagementManufacturingRepository.teamMembers
    val teamMembersMutable = MutableLiveData<MutableList<DomainTeamMember>>(mutableListOf())
    val teamMembersMediator: MediatorLiveData<Pair<MutableList<DomainTeamMember>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainTeamMember>?, Boolean?>>().apply {
            addSource(teamMembersMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(teamMembersMutable.value, it) }
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

    fun postNewOrder(activity: NewItemActivity, order: DomainOrder) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val channel = getCreatedRecord<DomainOrder>(order)
                channel.consumeEach {
                    launchMainActivity(activity, it.id)
                    activity.finish()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> CoroutineScope.getCreatedRecord(record: DomainOrder) = produce {

        val newOrder =
            QualityManagementNetwork.serviceholderInvestigations.createOrder(record.toNetworkOrder())
                .toDatabaseOrder()

        roomDatabase.qualityManagementInvestigationsDao.insertOrder(newOrder)

        send(newOrder.toDomainOrder()) //cold send
//            this.trySend(l).isSuccess //hot send
    }
}