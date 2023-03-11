package com.simenko.qmapp.ui.neworder

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.simenko.qmapp.di.neworder.NewItemScope
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.repository.QualityManagementProductsRepository
import com.simenko.qmapp.retrofit.entities.toNetworkOrderWithId
import com.simenko.qmapp.retrofit.entities.toNetworkOrderWithoutId
import com.simenko.qmapp.retrofit.implementation.QualityManagementNetwork
import com.simenko.qmapp.room.entities.toDatabaseOrder
import com.simenko.qmapp.room.implementation.getDatabase
import com.simenko.qmapp.ui.main.launchMainActivity
import com.simenko.qmapp.utils.StringUtils
import kotlinx.coroutines.*
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

    private val qualityManagementManufacturingRepository =
        QualityManagementManufacturingRepository(roomDatabase)
    private val qualityManagementProductsRepository =
        QualityManagementProductsRepository(roomDatabase)
    private val qualityManagementInvestigationsRepository =
        QualityManagementInvestigationsRepository(roomDatabase)

    val isLoadingInProgress = MutableLiveData<Boolean>(false)
    val isNetworkError = MutableLiveData<Boolean>(false)

    init {
//        ToDo decide when to update all SQLData (but not every time when MainActivity Created!)
//        refreshDataFromRepository()
    }

    val pairedTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

    val investigationOrders = qualityManagementInvestigationsRepository.orders

    val currentOrder = MutableLiveData(getEmptyOrder())

    val currentSubOrder = MutableLiveData(getEmptySubOrder())
    val currentSubOrderMediator: MediatorLiveData<Pair<DomainSubOrder?, Boolean?>> =
        MediatorLiveData<Pair<DomainSubOrder?, Boolean?>>().apply {
            addSource(currentSubOrder) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(currentSubOrder.value, it) }
        }

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
    val orderPlacersMutable = MutableLiveData<MutableList<DomainTeamMember>>(mutableListOf())
    val teamMembersMediator: MediatorLiveData<Pair<MutableList<DomainTeamMember>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainTeamMember>?, Boolean?>>().apply {
            addSource(orderPlacersMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(orderPlacersMutable.value, it) }
        }

    val inputForOrder = qualityManagementInvestigationsRepository.inputForOrder

    val departments = qualityManagementManufacturingRepository.departments
    val departmentsMutable = MutableLiveData<MutableList<DomainDepartment>>(mutableListOf())
    val departmentsMediator: MediatorLiveData<Pair<MutableList<DomainDepartment>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainDepartment>?, Boolean?>>().apply {
            addSource(departmentsMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(departmentsMutable.value, it) }
        }

    val subDepartments = qualityManagementManufacturingRepository.subDepartments
    val subDepartmentsMutable = MutableLiveData<MutableList<DomainSubDepartment>>(mutableListOf())
    val subDepartmentsMediator: MediatorLiveData<Pair<MutableList<DomainSubDepartment>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainSubDepartment>?, Boolean?>>().apply {
            addSource(subDepartmentsMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(subDepartmentsMutable.value, it) }
        }

    val subOrderPlacersMutable = MutableLiveData<MutableList<DomainTeamMember>>(mutableListOf())
    val subOrderPlacersMediator: MediatorLiveData<Pair<MutableList<DomainTeamMember>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainTeamMember>?, Boolean?>>().apply {
            addSource(subOrderPlacersMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(subOrderPlacersMutable.value, it) }
        }

    val channels = qualityManagementManufacturingRepository.channels
    val channelsMutable = MutableLiveData<MutableList<DomainManufacturingChannel>>(mutableListOf())
    val channelsMediator: MediatorLiveData<Pair<MutableList<DomainManufacturingChannel>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainManufacturingChannel>?, Boolean?>>().apply {
            addSource(channelsMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(channelsMutable.value, it) }
        }

    val lines = qualityManagementManufacturingRepository.lines
    val linesMutable = MutableLiveData<MutableList<DomainManufacturingLine>>(mutableListOf())
    val linesMediator: MediatorLiveData<Pair<MutableList<DomainManufacturingLine>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainManufacturingLine>?, Boolean?>>().apply {
            addSource(linesMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(linesMutable.value, it) }
        }

    val itemVersionsCompleteP = qualityManagementProductsRepository.itemsVersionsCompleteP
    val itemVersionsCompleteC = qualityManagementProductsRepository.itemsVersionsCompleteC
    val itemVersionsCompleteS = qualityManagementProductsRepository.itemsVersionsCompleteS

    val itemVersionsCompleteMutable = MutableLiveData<MutableList<DomainItemVersionComplete>>(mutableListOf())
    val itemVersionsMediator: MediatorLiveData<Pair<MutableList<DomainItemVersionComplete>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainItemVersionComplete>?, Boolean?>>().apply {
            addSource(itemVersionsCompleteMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(itemVersionsCompleteMutable.value, it) }
        }

    val operations = qualityManagementManufacturingRepository.operations
    val operationsMutable = MutableLiveData<MutableList<DomainManufacturingOperation>>(mutableListOf())
    val operationsMediator: MediatorLiveData<Pair<MutableList<DomainManufacturingOperation>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainManufacturingOperation>?, Boolean?>>().apply {
            addSource(operationsMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(operationsMutable.value, it) }
        }

    val operationsFlows = qualityManagementManufacturingRepository.operationsFlows

    val characteristics = qualityManagementProductsRepository.characteristics
    val characteristicsMutable = MutableLiveData<MutableList<DomainCharacteristic>>(mutableListOf())
    val characteristicsMediator: MediatorLiveData<Pair<MutableList<DomainCharacteristic>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainCharacteristic>?, Boolean?>>().apply {
            addSource(characteristicsMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(characteristicsMutable.value, it) }
        }


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

                qualityManagementProductsRepository.refreshKeys()
                qualityManagementProductsRepository.refreshComponents()
                qualityManagementProductsRepository.refreshVersionStatuses()
                qualityManagementProductsRepository.refreshComponentVersions()

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
            } catch (e: Error) {
                Log.d(TAG, "editOrder: $e")
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> CoroutineScope.getCreatedRecord(record: DomainOrder) = produce {

        val newOrder = QualityManagementNetwork.serviceholderInvestigations.createOrder(
            record.toNetworkOrderWithoutId()
        ).toDatabaseOrder()

        roomDatabase.qualityManagementInvestigationsDao.insertOrder(newOrder)

        send(newOrder.toDomainOrder()) //cold send, can be this.trySend(l).isSuccess //hot send
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> CoroutineScope.updateRecord(record: DomainOrder) = produce {

        val nOrder = record.toNetworkOrderWithId()

        QualityManagementNetwork.serviceholderInvestigations.editOrder(
            record.id,
            nOrder
        )

        roomDatabase.qualityManagementInvestigationsDao.insertOrder(record.toDatabaseOrder())

        send(record)
    }
}

enum class FilteringMode {
    ADD_ALL,
    REMOVE_ALL,
    ADD_BY_PARENT_ID,
    ADD_ALL_FROM_META_TABLE,
    ADD_BY_PARENT_ID_FROM_META_TABLE
}

enum class FilteringStep {
    NOT_FROM_META_TABLE,
    SUB_DEPARTMENTS,
    CHANNELS,
    LINES,
    ITEM_VERSIONS,
    OPERATIONS,
    CHARACTERISTICS
}

fun <T : DomainModel> selectSingleRecord(
    d: MutableLiveData<MutableList<T>>,
    pairedTrigger: MutableLiveData<Boolean>,
    selectedId: Any = 0,
) {
    d.value?.forEach {
        it.setIsChecked(false)
    }
    d.value?.find { it.getRecordId() == selectedId }?.setIsChecked(true)
    pairedTrigger.value = !(pairedTrigger.value as Boolean)
}

fun isOperationInFlow(operationId: Int, selectedOperationId: Int,  operationsFlow: List<DomainOperationsFlow>): Boolean {
    var result = false
    val previousIds = getPreviousIds(selectedOperationId, operationsFlow)
    previousIds.forEach byBlock@{
        if(it == operationId) {
            result = true
            return@byBlock
        }
    }
    return result
}

fun getPreviousIds(currentId: Int, pairs: List<DomainOperationsFlow>): List<Int> {
    val previousIds = mutableListOf<Int>()
    for (pair in pairs) {
        if (pair.currentOperationId == currentId) {
            previousIds.add(pair.previousOperationId)
            previousIds.addAll(getPreviousIds(pair.previousOperationId, pairs))
        }
    }
    return previousIds
}

fun <T : DomainModel> MutableLiveData<MutableList<T>>.performFiltration(
    s: LiveData<List<T>>? = null,
    action: FilteringMode,
    trigger: MutableLiveData<Boolean>,
    p1Id: Int = 0,
    p2Id: Any = 0,
    p3Id: Int = 0,
    pFlow: List<DomainOperationsFlow>? = null,
    m: LiveData<List<DomainInputForOrder>>? = null,
    step: FilteringStep = FilteringStep.NOT_FROM_META_TABLE
) {
    val d = this
    when (action) {
        FilteringMode.ADD_ALL -> {
            //Is made because previously selected/filtered/unfiltered item again selected
            selectSingleRecord(d, trigger)
            d.value?.clear()
            s?.value?.let { d.value?.addAll(it.toList()) }
        }
        FilteringMode.REMOVE_ALL -> {
            //Is made because previously selected/filtered/unfiltered item again selected
            selectSingleRecord(d, trigger)
            d.value?.clear()
        }
        FilteringMode.ADD_BY_PARENT_ID -> {
            //Is made because previously selected/filtered/unfiltered item again selected
            selectSingleRecord(d, trigger)
            d.value?.clear()
            s.apply {
                this?.value?.filter { it.getParentOneId() == p1Id }?.forEach { input ->
                    if (d.value?.find { it.getRecordId() == input.getRecordId() } == null) {
                        d.value?.add(input)
                    }
                }
            }
        }
        FilteringMode.ADD_ALL_FROM_META_TABLE -> {
            if (m != null && m.value != null) {
                m.value!!.forEach { mIt ->
                    val item = s?.value?.find { it.getRecordId() == mIt.id }
                    if (d.value?.find { it.getRecordId() == item?.getRecordId() } == null) {
                        d.value?.add(item!!)
                    }
                }
            }
        }
        FilteringMode.ADD_BY_PARENT_ID_FROM_META_TABLE -> {
            selectSingleRecord(d, trigger)
            d.value?.clear()

            if (m != null && m.value != null) {
                val mSorted = m.value!!.sortedBy {
                    when (step) {
                        FilteringStep.NOT_FROM_META_TABLE -> it.depOrder
                        FilteringStep.SUB_DEPARTMENTS -> it.subDepOrder
                        FilteringStep.CHANNELS -> it.channelOrder
                        FilteringStep.LINES -> it.lineOrder
                        FilteringStep.ITEM_VERSIONS -> it.itemId
                        FilteringStep.OPERATIONS -> it.operationOrder
                        FilteringStep.CHARACTERISTICS -> it.operationOrder
                    }
                }
                mSorted.forEach { mIt ->
                    val item = s?.value?.find {
                        (it.getParentOneId() == p1Id || it.hasParentOneId(p1Id)) &&
                                when (step) {
                                    FilteringStep.NOT_FROM_META_TABLE -> {
                                        it.getRecordId() == 0
                                    }
                                    FilteringStep.SUB_DEPARTMENTS -> {
                                        it.getRecordId() == mIt.subDepId
                                    }
                                    FilteringStep.CHANNELS -> {
                                        it.getRecordId() == mIt.chId
                                    }
                                    FilteringStep.LINES -> {
                                        it.getRecordId() == mIt.lineId
                                    }
                                    FilteringStep.ITEM_VERSIONS -> {
                                        it.getRecordId() == StringUtils.concatTwoStrings4(
                                            mIt.itemPrefix,
                                            mIt.itemVersionId.toString()
                                        )
                                    }
                                    FilteringStep.OPERATIONS -> {
                                        it.getRecordId() == mIt.operationId &&
                                        p2Id == StringUtils.concatTwoStrings4(
                                            mIt.itemPrefix,
                                            mIt.itemVersionId.toString())
                                    }
                                    FilteringStep.CHARACTERISTICS -> {
                                        it.getRecordId() == mIt.charId &&
                                        p2Id == StringUtils.concatTwoStrings4(
                                            mIt.itemPrefix,
                                            mIt.itemVersionId.toString())
                                                &&
                                                (p3Id == mIt.operationId || isOperationInFlow(mIt.operationId,p3Id, pFlow!!))
                                    }

                                }
                    }
                    if (item != null)
                        if (d.value?.find { it.getRecordId() == item.getRecordId() } == null) {
                            d.value?.add(item)
                        }
                }
            }
        }
    }

    trigger.value = !(trigger.value as Boolean)
}

fun getEmptyOrder() = DomainOrder(
    id = 0, orderTypeId = 0, reasonId = 0,
    orderNumber = null,
    customerId = 0,
    orderedById = 0,
    statusId = 1,
    createdDate = "2022-01-30T15:30:00",
    completedDate = null
)

fun getEmptySubOrder() = DomainSubOrder(
    id = 0,
    orderId = 0,//maybe currentOrder.id?
    subOrderNumber = 0,
    orderedById = 0,
    completedById = null,
    statusId = 1,
    createdDate = "2022-01-30T15:30:00",
    completedDate = null,
    departmentId = 0,
    subDepartmentId = 0,
    channelId = 0,
    lineId = 0,
    operationId = 0,
    itemPreffix = "",
    itemTypeId = 0,
    itemVersionId = 0,
    samplesCount = null
)