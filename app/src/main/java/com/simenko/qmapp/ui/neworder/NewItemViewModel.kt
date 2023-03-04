package com.simenko.qmapp.ui.neworder

import android.content.Context
import android.util.Log
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


private const val TAG = "NewItemViewModel"

@NewItemScope
class NewItemViewModel @Inject constructor(
    context: Context
) : ViewModel() {

    private val roomDatabase = getDatabase(context)

    private val qualityManagementInvestigationsRepository =
        QualityManagementInvestigationsRepository(roomDatabase)
    private val qualityManagementManufacturingRepository =
        QualityManagementManufacturingRepository(roomDatabase)
    val isLoadingInProgress = MutableLiveData<Boolean>(false)
    val isNetworkError = MutableLiveData<Boolean>(false)

    init {
//        ToDo decide when to update all SQLData (but not every time when MainActivity Created!)
//        refreshDataFromRepository()
    }

    fun <T : DomainModel> selectSingleRecord(d: MutableLiveData<MutableList<T>>, selectedId: Int) {
        d.value?.forEach {
            it.setIsChecked(false)
        }
        d.value?.find { it.getRecordId() == selectedId }?.setIsChecked(true)
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
                    0
                ) //Is made because previously selected/filtered/unfiltered item again selected
                d.value?.clear()
                s.value?.let { d.value?.addAll(it.toList()) }
            }
//            Clear all
            0 -> {
                selectSingleRecord(
                    d,
                    0
                ) //Is made because previously selected/filtered/unfiltered item again selected
                d.value?.clear()
            }
//            Add filtered by one parent id
            else -> {
                selectSingleRecord(
                    d,
                    0
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

    val investigationOrders = qualityManagementInvestigationsRepository.orders

    //
    val currentOrder =
        MutableLiveData(DomainOrder(0, 0, 0, null, 0, 0, 1, "2022-01-30T15:30:00", null))

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
    fun onNetworkErrorShown() {
        isLoadingInProgress.value = false
        isNetworkError.value = false
    }

    /**
     *
     */

    fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                qualityManagementInvestigationsRepository.refreshInvestigationTypes()
                qualityManagementInvestigationsRepository.refreshInvestigationReasons()
                qualityManagementInvestigationsRepository.refreshInputForOrder()
                qualityManagementManufacturingRepository.refreshDepartments()
                qualityManagementManufacturingRepository.refreshTeamMembers()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun postNewOrder(activity: NewItemActivity, order: DomainOrder) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    val channel = getCreatedRecord<DomainOrder>(order)
                    channel.consumeEach {
                        launchMainActivity(activity, it.id)
                        activity.finish()
                    }
                }
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun editOrder(activity: NewItemActivity, order: DomainOrder) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    val channel = updateRecord<DomainOrder>(order)
                    channel.consumeEach {
                        launchMainActivity(activity, it.id)
                        activity.finish()
                    }
                }
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> CoroutineScope.getCreatedRecord(record: DomainOrder) = produce {

        val newOrder = QualityManagementNetwork.serviceholderInvestigations.createOrder(
            record.toNetworkOrder()
        ).toDatabaseOrder()

        roomDatabase.qualityManagementInvestigationsDao.insertOrder(newOrder)

        send(newOrder.toDomainOrder()) //cold send, can be this.trySend(l).isSuccess //hot send
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> CoroutineScope.updateRecord(record: DomainOrder) = produce {

        QualityManagementNetwork.serviceholderInvestigations.editOrder(
            record.id,
            record.toNetworkOrder()
        )

        roomDatabase.qualityManagementInvestigationsDao.insertOrder(record.toDatabaseOrder())

        send(record)
    }
}