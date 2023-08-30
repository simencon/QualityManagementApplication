package com.simenko.qmapp.ui.neworder

import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.setMainActivityResult
import com.simenko.qmapp.utils.InvStatuses
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import javax.inject.Inject

private const val TAG = "NewItemViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewItemViewModel @Inject constructor(
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository
) : ViewModel() {

    val isLoadingInProgress = MutableLiveData(false)
    val isNetworkError = MutableLiveData(false)

    val pairedTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

    val investigationOrders = repository.orders

    val currentOrder = MutableLiveData(
        DomainOrder().copy(statusId = InvStatuses.TO_DO.statusId)
    )
    val currentSubOrder = MutableLiveData(
        DomainSubOrderShort(DomainSubOrder().copy(statusId = InvStatuses.TO_DO.statusId), DomainOrder().copy(statusId = InvStatuses.TO_DO.statusId))
    )


    private val _currentOrder: MutableStateFlow<DomainOrder> = MutableStateFlow(
        DomainOrder().copy(statusId = InvStatuses.TO_DO.statusId)
    )
    val currentOrderSF: StateFlow<DomainOrder> = _currentOrder.flatMapLatest { order ->
        flow { emit(order) }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _currentOrder.value)

    fun loadCurrentOrder(id: Int) {
        _currentOrder.value = repository.getOrderById(id)
        _currentSubOrder.value = _currentSubOrder.value.copy(order = _currentOrder.value)
    }

    private val _currentSubOrder: MutableStateFlow<DomainSubOrderShort> = MutableStateFlow(
        DomainSubOrderShort(DomainSubOrder().copy(statusId = InvStatuses.TO_DO.statusId), DomainOrder().copy(statusId = InvStatuses.TO_DO.statusId))
    )
    val currentSubOrderSF: StateFlow<DomainSubOrderShort> = _currentSubOrder.flatMapLatest { order ->
        flow { emit(order) }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _currentSubOrder.value)

    private val _orderTypes = repository.getOrderTypes
    val orderTypes: StateFlow<List<DomainOrdersType>> = _orderTypes.flatMapLatest { types ->
        _currentOrder.flatMapLatest { currentOrder ->
            val cpy = mutableListOf<DomainOrdersType>()
            types.forEach { cpy.add(it.copy(isSelected = it.id == currentOrder.orderTypeId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun selectOrderType(id: Int) {
        _currentOrder.value =
            _currentOrder.value.copy(orderTypeId = id, reasonId = NoRecord.num, customerId = NoRecord.num, orderedById = NoRecord.num)
        _currentSubOrder.value = _currentSubOrder.value.copy(order = _currentOrder.value)
    }

    private val _orderReasons: Flow<List<DomainReason>> = repository.getOrderReasons
    val orderReasons: StateFlow<List<DomainReason>> = _orderReasons.flatMapLatest { reasons ->
        _currentOrder.flatMapLatest { currentOrder ->
            if (currentOrder.orderTypeId != NoRecord.num) {
                val cpy = mutableListOf<DomainReason>()
                reasons.forEach { cpy.add(it.copy(isSelected = it.id == currentOrder.reasonId)) }
                flow { emit(cpy) }
            } else {
                flow { emit(emptyList<DomainReason>()) }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectOrderReason(id: Int) {
        _currentOrder.value = _currentOrder.value.copy(reasonId = id, customerId = NoRecord.num, orderedById = NoRecord.num)
        _currentSubOrder.value = _currentSubOrder.value.copy(order = _currentOrder.value)
    }

    private val _orderCustomers: Flow<List<DomainDepartment>> = manufacturingRepository.getDepartments
    val orderCustomers: StateFlow<List<DomainDepartment>> = _orderCustomers.flatMapLatest { reasons ->
        _currentOrder.flatMapLatest { currentOrder ->
            if (currentOrder.reasonId != NoRecord.num) {
                val cpy = mutableListOf<DomainDepartment>()
                reasons.forEach { cpy.add(it.copy(isSelected = it.id == currentOrder.customerId)) }
                flow { emit(cpy) }
            } else {
                flow { emit(emptyList<DomainDepartment>()) }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectOrderCustomer(id: Int) {
        _currentOrder.value = _currentOrder.value.copy(customerId = id, orderedById = NoRecord.num)
        _currentSubOrder.value = _currentSubOrder.value.copy(order = _currentOrder.value)
    }

    private val _orderPlacers: Flow<List<DomainTeamMember>> = manufacturingRepository.getTeamMembers
    val orderPlacers: StateFlow<List<DomainTeamMember>> = _orderPlacers.flatMapLatest { reasons ->
        _currentOrder.flatMapLatest { currentOrder ->
            if (currentOrder.customerId != NoRecord.num) {
                val cpy = mutableListOf<DomainTeamMember>()
                reasons.forEach { cpy.add(it.copy(isSelected = it.id == currentOrder.orderedById)) }
                flow { emit(cpy) }
            } else {
                flow { emit(emptyList<DomainTeamMember>()) }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectOrderPlacer(id: Int) {
        _currentOrder.value = _currentOrder.value.copy(orderedById = id)
        _currentSubOrder.value = _currentSubOrder.value.copy(order = _currentOrder.value)
    }

    val inputForOrder = repository.inputForOrder

    val departmentsMutable = MutableLiveData<MutableList<DomainDepartment>>(mutableListOf())
    val departmentsMediator: MediatorLiveData<Pair<MutableList<DomainDepartment>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainDepartment>?, Boolean?>>().apply {
            addSource(departmentsMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(departmentsMutable.value, it) }
        }

    val subDepartments = manufacturingRepository.subDepartments
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

    val channels = manufacturingRepository.channels
    val channelsMutable = MutableLiveData<MutableList<DomainManufacturingChannel>>(mutableListOf())
    val channelsMediator: MediatorLiveData<Pair<MutableList<DomainManufacturingChannel>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainManufacturingChannel>?, Boolean?>>().apply {
            addSource(channelsMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(channelsMutable.value, it) }
        }

    val lines = manufacturingRepository.lines
    val linesMutable = MutableLiveData<MutableList<DomainManufacturingLine>>(mutableListOf())
    val linesMediator: MediatorLiveData<Pair<MutableList<DomainManufacturingLine>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainManufacturingLine>?, Boolean?>>().apply {
            addSource(linesMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(linesMutable.value, it) }
        }

    val itemVersionsComplete = productsRepository.itemVersionsComplete

    val itemVersionsCompleteMutable =
        MutableLiveData<MutableList<DomainItemVersionComplete>>(mutableListOf())
    val itemVersionsMediator: MediatorLiveData<Pair<MutableList<DomainItemVersionComplete>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainItemVersionComplete>?, Boolean?>>().apply {
            addSource(itemVersionsCompleteMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(itemVersionsCompleteMutable.value, it) }
        }

    val operations = manufacturingRepository.operations
    val operationsMutable =
        MutableLiveData<MutableList<DomainManufacturingOperation>>(mutableListOf())
    val operationsMediator: MediatorLiveData<Pair<MutableList<DomainManufacturingOperation>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainManufacturingOperation>?, Boolean?>>().apply {
            addSource(operationsMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(operationsMutable.value, it) }
        }

    val operationsFlows = manufacturingRepository.operationsFlows

    val characteristics = productsRepository.characteristics
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

                repository.syncInvestigationTypes()
                repository.syncInvestigationReasons()
                repository.syncInputForOrder()
                manufacturingRepository.syncDepartments()
                manufacturingRepository.syncTeamMembers()

                productsRepository.refreshKeys()
                productsRepository.refreshComponents()
                productsRepository.refreshVersionStatuses()
                productsRepository.refreshComponentVersions()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    private suspend fun postDeleteSamples(subOrderId: Int, subOrder: DomainSubOrderShort) {
        withContext(Dispatchers.IO) {
            subOrder.samples.forEach {
                if (it.toBeDeleted) {
                    withContext(Dispatchers.IO) {
                        repository.run {
                            deleteSample(it.id).consumeEach { event ->
                                event.getContentIfNotHandled()?.let { resource ->
                                    when (resource.status) {
                                        Status.LOADING -> {}
                                        Status.SUCCESS -> {}
                                        Status.ERROR -> {}
                                    }
                                }
                            }
                        }
                    }
                } else if (it.isNewRecord) {
                    it.subOrderId = subOrderId
                    repository.run { insertSample(it) }.consumeEach { }
                }
            }
        }
    }

    private suspend fun postDeleteSubOrderTasks(
        subOrderId: Int,
        subOrder: DomainSubOrderShort
    ) {
        withContext(Dispatchers.IO) {
            subOrder.subOrderTasks.forEach {
                if (it.toBeDeleted) {
                    withContext(Dispatchers.IO) {
                        repository.run {
                            deleteSubOrderTask(it.id).consumeEach { event ->
                                event.getContentIfNotHandled()?.let { resource ->
                                    when (resource.status) {
                                        Status.LOADING -> {}
                                        Status.SUCCESS -> {}
                                        Status.ERROR -> {}
                                    }
                                }
                            }
                        }
                    }
                } else if (it.isNewRecord) {
                    it.subOrderId = subOrderId
                    it.orderedById = subOrder.subOrder.orderedById
                    repository.run { insertTask(it) }.consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> {}
                                Status.SUCCESS -> {}
                                Status.ERROR -> {}
                            }
                        }
                    }
                }
            }
        }
    }

    fun postNewSubOrder(activity: NewItemActivity, subOrder: DomainSubOrderShort) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run { insertSubOrder(subOrder.subOrder) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> {
                            withContext(Dispatchers.Main) { isLoadingInProgress.value = true }
                        }

                        Status.SUCCESS -> {
                            postDeleteSubOrderTasks(resource.data!!.id, subOrder)
                            postDeleteSamples(resource.data.id, subOrder)
                            withContext(Dispatchers.Main) { isLoadingInProgress.value = false }
                            setMainActivityResult(activity, activity.addEditModeEnum, resource.data.orderId, resource.data.id)
                            activity.finish()
                        }

                        Status.ERROR -> {
                            withContext(Dispatchers.Main) {
                                isLoadingInProgress.value = false
                                isNetworkError.value = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun postNewOrder(activity: NewItemActivity, order: DomainOrder) {
        viewModelScope.launch(Dispatchers.IO) {
            with(repository) { insertOrder(order) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> {
                            withContext(Dispatchers.Main) { isLoadingInProgress.value = true }
                        }

                        Status.SUCCESS -> {
                            withContext(Dispatchers.Main) { isLoadingInProgress.value = false }
                            setMainActivityResult(activity, activity.addEditModeEnum, resource.data!!.id)
                            activity.finish()
                        }

                        Status.ERROR -> {
                            withContext(Dispatchers.Main) {
                                isLoadingInProgress.value = false
                                isNetworkError.value = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun postNewOrderWithSubOrder(activity: NewItemActivity, subOrder: DomainSubOrderShort) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run { insertOrder(subOrder.order) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> {
                            withContext(Dispatchers.Main) { isLoadingInProgress.value = true }
                        }

                        Status.SUCCESS -> {
                            subOrder.subOrder.orderId = resource.data!!.id
                            postNewSubOrder(activity, subOrder)
                        }

                        Status.ERROR -> {
                            withContext(Dispatchers.Main) {
                                isLoadingInProgress.value = false
                                isNetworkError.value = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun editSubOrder(activity: NewItemActivity, subOrder: DomainSubOrderShort) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run { updateSubOrder(subOrder.subOrder) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> {
                            withContext(Dispatchers.Main) { isLoadingInProgress.value = true }
                        }

                        Status.SUCCESS -> {
                            postDeleteSubOrderTasks(resource.data!!.id, subOrder)
                            postDeleteSamples(resource.data.id, subOrder)
                            withContext(Dispatchers.Main) { isLoadingInProgress.value = false }
                            setMainActivityResult(activity, activity.addEditModeEnum, resource.data.orderId, resource.data.id)
                            activity.finish()
                        }

                        Status.ERROR -> {
                            withContext(Dispatchers.Main) { isNetworkError.value = true }
                        }
                    }
                }
            }

            syncTasks()
            syncSamples()
            syncResults()
        }
    }

    fun editOrder(activity: NewItemActivity, order: DomainOrder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run { updateOrder(order) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> {
                            withContext(Dispatchers.Main) { isLoadingInProgress.value = true }
                        }

                        Status.SUCCESS -> {
                            setMainActivityResult(activity, activity.addEditModeEnum, resource.data!!.id)
                            activity.finish()
                        }

                        Status.ERROR -> {
                            withContext(Dispatchers.Main) { isLoadingInProgress.value = false }
                            isNetworkError.value = false
                        }
                    }
                }
            }
        }
    }

    fun syncTasks() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.syncSubOrderTasks(Pair(currentOrder.value!!.createdDate, currentOrder.value!!.createdDate))
                repository.syncResults(Pair(currentOrder.value!!.createdDate, currentOrder.value!!.createdDate))

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun syncSamples() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.syncSamples(Pair(currentOrder.value!!.createdDate, currentOrder.value!!.createdDate))
                repository.syncResults(Pair(currentOrder.value!!.createdDate, currentOrder.value!!.createdDate))

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun syncResults() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.syncResults(Pair(currentOrder.value!!.createdDate, currentOrder.value!!.createdDate))

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
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

fun <D, T : DomainBaseModel<D>> changeRecordSelection(
    d: MutableLiveData<MutableList<T>>,
    pairedTrigger: MutableLiveData<Boolean>,
    selectedId: Any = NoRecord.num,
): Boolean {
    val result = d.value?.find { it.getRecordId() == selectedId }?.changeCheckedState()
    pairedTrigger.value = !(pairedTrigger.value as Boolean)
    return result ?: false
}

fun <D, T : DomainBaseModel<D>> selectSingleRecord(
    d: MutableLiveData<MutableList<T>>,
    pairedTrigger: MutableLiveData<Boolean>,
    selectedId: Any = NoRecord.num,
) {
    d.value?.forEach {
        it.setIsSelected(false)
    }
    d.value?.find { it.getRecordId() == selectedId }?.setIsSelected(true)
    pairedTrigger.value = !(pairedTrigger.value as Boolean)
}

fun isOperationInFlow(
    operationId: Int,
    selectedOperationId: Int,
    operationsFlow: List<DomainOperationsFlow>
): Boolean {
    var result = false
    val previousIds = getPreviousIds(selectedOperationId, operationsFlow)
    previousIds.forEach byBlock@{
        if (it == operationId) {
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

fun <D, T : DomainBaseModel<D>> MutableLiveData<MutableList<T>>.performFiltration(
    s: LiveData<List<T>>? = null,
    action: FilteringMode,
    trigger: MutableLiveData<Boolean>,
    p1Id: Int = NoRecord.num,
    p2Id: Any = NoRecord.num,
    p3Id: Int = NoRecord.num,
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
                this?.value?.filter { it.getParentId() == p1Id }?.forEach { input ->
                    if (d.value?.find { it.getRecordId() == input.getRecordId() } == null) {
                        d.value?.add(input)
                    }
                }
            }
        }

        FilteringMode.ADD_ALL_FROM_META_TABLE -> {
            if ((m != null) && (m.value != null)) {
                m.value!!.forEach { mIt ->
                    val item = s?.value?.find { it.getRecordId() == mIt.depId }
                    if (d.value?.find { it.getRecordId() == item?.getRecordId() } == null) {
                        d.value?.add(item!!)
                    }
                }
            }
        }

        FilteringMode.ADD_BY_PARENT_ID_FROM_META_TABLE -> {
            selectSingleRecord(d, trigger)
            d.value?.clear()

            if ((m != null) && (m.value != null)) {
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
                        (it.getParentId() == p1Id || it.hasParentId(p1Id)) &&
                                when (step) {
                                    FilteringStep.NOT_FROM_META_TABLE -> {
                                        it.getRecordId() == NoRecord.num
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
                                        it.getRecordId() == mIt.itemPrefix + mIt.itemVersionId.toString()
                                    }

                                    FilteringStep.OPERATIONS -> {
                                        it.getRecordId() == mIt.operationId && p2Id == mIt.itemPrefix + mIt.itemVersionId.toString()
                                    }

                                    FilteringStep.CHARACTERISTICS -> {
                                        it.getRecordId() == mIt.charId && p2Id == mIt.itemPrefix + mIt.itemVersionId.toString()
                                                &&
                                                (p3Id == mIt.operationId || isOperationInFlow(mIt.operationId, p3Id, pFlow!!))
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