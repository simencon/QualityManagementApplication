package com.simenko.qmapp.ui.main.investigations.forms

import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.TopPageState
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.AppNavigator
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewItemViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val topPageState: TopPageState,
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository
) : ViewModel() {
    suspend fun setupTopScreen(addEditMode: AddEditMode, record: Pair<Int, Int>) {
        var makeAction: () -> Unit = {}
        var recordSetUpAction: () -> Unit = {}

        when (addEditMode) {
            AddEditMode.ADD_ORDER -> {
                makeAction = { makeOrder(true) }
            }

            AddEditMode.EDIT_ORDER -> {
                makeAction = { makeOrder(false) }
                recordSetUpAction = { loadOrder(record.first) }
            }

            AddEditMode.ADD_SUB_ORDER -> {
                makeAction = { makeSubOrder(true) }
                recordSetUpAction = { loadOrder(record.first) }
            }

            AddEditMode.EDIT_SUB_ORDER -> {
                makeAction = { makeSubOrder(true) }
                recordSetUpAction = {
                    loadOrder(record.first)
                    loadSubOrder(record.second)
                }
            }

            AddEditMode.ADD_SUB_ORDER_STAND_ALONE -> {
                makeAction = { makeNewOrderWithSubOrder(true) }
                recordSetUpAction = { setNewOrderForProcessControl() }
            }

            AddEditMode.EDIT_SUB_ORDER_STAND_ALONE -> {
                makeAction = { makeNewOrderWithSubOrder(false) }
                recordSetUpAction = {
                    loadOrder(record.first)
                    loadSubOrder(record.second)
                }
            }

            else -> {}
        }

//        todo-me: ToDo make in proper way later
        /*topScreenState.trySendTopScreenSetup(
            addEditMode = Pair(addEditMode) { makeAction() },
            refreshAction = {},
            filterAction = {}
        )*/
        withContext(Dispatchers.Default) { recordSetUpAction() }
    }

    private fun updateLoadingState(state: Pair<Boolean, String?>) {
        topPageState.trySendLoadingState(state)
    }

    /**
     * Order logic -----------------------------------------------------------------------------------------------------------------------------------
     * */
    private val _subOrderStandAlone: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val subOrderStandAlone: StateFlow<Boolean> get() = _subOrderStandAlone
    fun setSubOrderStandAlone(value: Boolean) {
        _subOrderStandAlone.value = value
    }


    private val _order: MutableStateFlow<DomainOrder> = MutableStateFlow(
        DomainOrder().copy(statusId = InvStatuses.TO_DO.statusId)
    )
    val order: StateFlow<DomainOrder> get() = _order

    fun loadOrder(id: Int) {
        _order.value = repository.getOrderById(id)
    }

    fun setNewOrderForProcessControl() {
        _order.value = _order.value.copy(orderTypeId = 3/*Process Control*/, customerId = 4/*УЯк*/, orderedById = 18/*Роман Семенишин*/)
    }

    // Order Type ------------------------------------------------------------------------------------------------------------------------------------
    private val _orderTypes = repository.getOrderTypes
    val orderTypes: StateFlow<List<DomainOrdersType>> = _orderTypes.flatMapLatest { types ->
        _order.flatMapLatest { currentOrder ->
            val cpy = mutableListOf<DomainOrdersType>()
            types.forEach { cpy.add(it.copy(isSelected = it.id == currentOrder.orderTypeId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun selectOrderType(id: Int) {
        if (_order.value.orderTypeId != id)
            _order.value = _order.value.copy(orderTypeId = id, reasonId = NoRecord.num, customerId = NoRecord.num, orderedById = NoRecord.num)
    }

    // Order Reason ----------------------------------------------------------------------------------------------------------------------------------
    private val _orderReasons: Flow<List<DomainReason>> = repository.getOrderReasons
    val orderReasons: StateFlow<List<DomainReason>> = _orderReasons.flatMapLatest { reasons ->
        _order.flatMapLatest { currentOrder ->
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
        if (_order.value.reasonId != id && !_subOrderStandAlone.value)
            _order.value = _order.value.copy(reasonId = id, customerId = NoRecord.num, orderedById = NoRecord.num)
        else if (_order.value.reasonId != id)
            _order.value = _order.value.copy(reasonId = id)
    }

    // Order Customer --------------------------------------------------------------------------------------------------------------------------------
    private val _orderCustomers: Flow<List<DomainDepartment>> = manufacturingRepository.departments
    val orderCustomers: StateFlow<List<DomainDepartment>> = _orderCustomers.flatMapLatest { reasons ->
        _order.flatMapLatest { currentOrder ->
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
        if (_order.value.customerId != id)
            _order.value = _order.value.copy(customerId = id, orderedById = NoRecord.num)
    }

    // Order Initiator -------------------------------------------------------------------------------------------------------------------------------
    private val _orderInitiators: Flow<List<DomainEmployee>> = manufacturingRepository.employees
    val orderInitiators: StateFlow<List<DomainEmployee>> = _orderInitiators.flatMapLatest { reasons ->
        _order.flatMapLatest { currentOrder ->
            if (currentOrder.customerId != NoRecord.num) {
                val cpy = mutableListOf<DomainEmployee>()
                reasons.forEach { cpy.add(it.copy(isSelected = it.id == currentOrder.orderedById)) }
                flow { emit(cpy) }
            } else {
                flow { emit(emptyList<DomainEmployee>()) }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectOrderInitiator(id: Int) {
        if (_order.value.orderedById != id)
            _order.value = _order.value.copy(orderedById = id)
    }

    /**
     * Sub Order logic -------------------------------------------------------------------------------------------------------------------------------
     * */
    private val _subOrder: MutableStateFlow<DomainSubOrderShort> = MutableStateFlow(
        DomainSubOrderShort(DomainSubOrder().copy(statusId = InvStatuses.TO_DO.statusId), DomainOrder().copy(statusId = InvStatuses.TO_DO.statusId))
    )
    val subOrder: StateFlow<DomainSubOrderShort> = _order.flatMapLatest { order ->
        _subOrder.flatMapLatest { subOrder ->
            _subOrder.value = subOrder.copy(order = order, subOrder = _subOrder.value.subOrder.copy(orderId = order.id))
            flow { emit(_subOrder.value) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _subOrder.value)

    fun loadSubOrder(id: Int) {
        val subOrder = repository.getSubOrderById(id)
        val samples = repository.getSamplesBySubOrderId(id)
        val tasks = repository.getTasksBySubOrderId(id)
        _subOrder.value = _subOrder.value.copy(subOrder = subOrder, samples = samples.toMutableList(), subOrderTasks = tasks.toMutableList())
    }

    // Master List -----------------------------------------------------------------------------------------------------------------------------------
    private val _inputForOrder: Flow<List<DomainInputForOrder>> = repository.inputForOrder

    // Sub Order Department --------------------------------------------------------------------------------------------------------------------------
    private val _subOrderDepartments: Flow<List<DomainDepartment>> = manufacturingRepository.departments
    val subOrderDepartments: StateFlow<List<DomainDepartment>> = _subOrderDepartments.flatMapLatest { departments ->
        _subOrder.flatMapLatest { so ->
            _order.flatMapLatest { o ->
                _inputForOrder.flatMapLatest { master ->
                    if (o.reasonId != NoRecord.num) {
                        val ids = master.sortedBy { it.depOrder }.map { it.depId }.toSet()
                        val cpy = mutableListOf<DomainDepartment>()
                        ids.forEach { id ->
                            departments.findLast { it.id == id }?.let {
                                cpy.add(it.copy(isSelected = it.id == so.subOrder.departmentId))
                            }
                        }
                        flow { emit(cpy) }
                    } else {
                        flow { emit(emptyList<DomainDepartment>()) }
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectSubOrderDepartment(id: Int) {
        if (_subOrder.value.subOrder.departmentId != id) {
            _subOrder.value = _subOrder.value.copy(
                subOrder = _subOrder.value.subOrder.copy(
                    departmentId = id,
                    subDepartmentId = NoRecord.num,
                    orderedById = NoRecord.num,
                    channelId = NoRecord.num,
                    lineId = NoRecord.num,
                    itemPreffix = NoString.str,
                    itemTypeId = NoRecord.num,
                    itemVersionId = NoRecord.num,
                    operationId = NoRecord.num
                )
            )
            selectSubOrderItemsCount(ZeroValue.num)
            selectSubOrderCharacteristic(NoRecord.num)
        }
    }

    // Sub Order Sub Department ----------------------------------------------------------------------------------------------------------------------
    private val _subOrderSubDepartments: Flow<List<DomainSubDepartment>> = manufacturingRepository.subDepartments
    val subOrderSubDepartments: StateFlow<List<DomainSubDepartment>> = _subOrderSubDepartments.flatMapLatest { subDepartments ->
        _subOrder.flatMapLatest { so ->
            _inputForOrder.flatMapLatest { master ->
                if (so.subOrder.departmentId != NoRecord.num) {
                    val ids = master.filter { it.depId == so.subOrder.departmentId }.sortedBy { it.subDepOrder }.map { it.subDepId }.toSet()
                    val cpy = mutableListOf<DomainSubDepartment>()
                    ids.forEach { id ->
                        subDepartments.findLast { it.id == id }?.let {
                            cpy.add(it.copy(isSelected = it.id == so.subOrder.subDepartmentId))
                        }
                    }
                    flow { emit(cpy) }
                } else {
                    flow { emit(emptyList<DomainSubDepartment>()) }
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectSubOrderSubDepartment(id: Int) {
        if (_subOrder.value.subOrder.subDepartmentId != id) {
            _subOrder.value = _subOrder.value.copy(
                subOrder = _subOrder.value.subOrder.copy(
                    subDepartmentId = id,
                    orderedById = NoRecord.num,
                    channelId = NoRecord.num,
                    lineId = NoRecord.num,
                    itemPreffix = NoString.str,
                    itemTypeId = NoRecord.num,
                    itemVersionId = NoRecord.num,
                    operationId = NoRecord.num
                )
            )
            selectSubOrderItemsCount(ZeroValue.num)
            selectSubOrderCharacteristic(NoRecord.num)
        }
    }

    // Sub Order Placer ------------------------------------------------------------------------------------------------------------------------------
    private val _subOrderPlacers: Flow<List<DomainEmployee>> = manufacturingRepository.employees
    val subOrderPlacers: StateFlow<List<DomainEmployee>> = _subOrderPlacers.flatMapLatest { placers ->
        _subOrder.flatMapLatest { so ->
            if (so.subOrder.subDepartmentId != NoRecord.num) {
                val cpy = mutableListOf<DomainEmployee>()
                placers.filter { it.departmentId == so.subOrder.departmentId }
                    .forEach { cpy.add(it.copy(isSelected = it.id == so.subOrder.orderedById)) }
                flow { emit(cpy) }
            } else {
                flow { emit(emptyList<DomainEmployee>()) }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectSubOrderPlacer(id: Int) {
        if (_subOrder.value.subOrder.orderedById != id) {
            _subOrder.value = _subOrder.value.copy(
                subOrder = _subOrder.value.subOrder.copy(
                    orderedById = id,
                    channelId = NoRecord.num,
                    lineId = NoRecord.num,
                    itemPreffix = NoString.str,
                    itemTypeId = NoRecord.num,
                    itemVersionId = NoRecord.num,
                    operationId = NoRecord.num
                )
            )
            selectSubOrderItemsCount(ZeroValue.num)
            selectSubOrderCharacteristic(NoRecord.num)
        }
    }

    // Sub Order Channel -----------------------------------------------------------------------------------------------------------------------------
    private val _subOrderChannels: Flow<List<DomainManufacturingChannel>> = manufacturingRepository.channels
    val subOrderChannels: StateFlow<List<DomainManufacturingChannel>> = _subOrderChannels.flatMapLatest { channels ->
        _subOrder.flatMapLatest { so ->
            _inputForOrder.flatMapLatest { master ->
                if (so.subOrder.orderedById != NoRecord.num) {
                    val ids = master.filter { it.subDepId == so.subOrder.subDepartmentId }.sortedBy { it.channelOrder }.map { it.chId }.toSet()
                    val cpy = mutableListOf<DomainManufacturingChannel>()
                    ids.forEach { id ->
                        channels.findLast { it.id == id }?.let {
                            cpy.add(it.copy(isSelected = it.id == so.subOrder.channelId))
                        }
                    }
                    flow { emit(cpy) }
                } else {
                    flow { emit(emptyList<DomainManufacturingChannel>()) }
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectSubOrderChannel(id: Int) {
        if (_subOrder.value.subOrder.channelId != id) {
            _subOrder.value = _subOrder.value.copy(
                subOrder = _subOrder.value.subOrder.copy(
                    channelId = id,
                    lineId = NoRecord.num,
                    itemPreffix = NoString.str,
                    itemTypeId = NoRecord.num,
                    itemVersionId = NoRecord.num,
                    operationId = NoRecord.num
                )
            )
            selectSubOrderItemsCount(ZeroValue.num)
            selectSubOrderCharacteristic(NoRecord.num)
        }
    }

    // Sub Order Line --------------------------------------------------------------------------------------------------------------------------------
    private val _subOrderLines: Flow<List<DomainManufacturingLine>> = manufacturingRepository.lines
    val subOrderLines: StateFlow<List<DomainManufacturingLine>> = _subOrderLines.flatMapLatest { lines ->
        _subOrder.flatMapLatest { so ->
            _inputForOrder.flatMapLatest { master ->
                if (so.subOrder.channelId != NoRecord.num) {
                    val ids = master.filter { it.chId == so.subOrder.channelId }.sortedBy { it.lineOrder }.map { it.lineId }.toSet()
                    val cpy = mutableListOf<DomainManufacturingLine>()
                    ids.forEach { id ->
                        lines.findLast { it.id == id }?.let {
                            cpy.add(it.copy(isSelected = it.id == so.subOrder.lineId))
                        }
                    }
                    flow { emit(cpy) }
                } else {
                    flow { emit(emptyList<DomainManufacturingLine>()) }
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectSubOrderLine(id: Int) {
        if (_subOrder.value.subOrder.lineId != id) {
            _subOrder.value = _subOrder.value.copy(
                subOrder = _subOrder.value.subOrder.copy(
                    lineId = id,
                    itemPreffix = NoString.str,
                    itemTypeId = NoRecord.num,
                    itemVersionId = NoRecord.num,
                    operationId = NoRecord.num
                )
            )
            selectSubOrderItemsCount(ZeroValue.num)
            selectSubOrderCharacteristic(NoRecord.num)
        }
    }

    // Sub Order Item --------------------------------------------------------------------------------------------------------------------------------
    private val _subOrderItemVersions: Flow<List<DomainItemVersionComplete>> = productsRepository.itemVersionsComplete
    val subOrderItemVersions: StateFlow<List<DomainItemVersionComplete>> = _subOrderItemVersions.flatMapLatest { itemVersions ->
        _subOrder.flatMapLatest { so ->
            _inputForOrder.flatMapLatest { master ->
                if (so.subOrder.lineId != NoRecord.num) {
                    val ids = master.filter { it.lineId == so.subOrder.lineId }.sortedBy { it.itemDesignation }.map { it.getItemVersionPid() }.toSet()
                    val cpy = mutableListOf<DomainItemVersionComplete>()
                    ids.forEach { id ->
                        itemVersions.findLast { it.itemVersion.fId == id }?.let {
                            cpy.add(it.copy(isSelected = it.itemVersion.fId == (so.subOrder.getItemIds().first + so.subOrder.itemVersionId)))
                        }
                    }
                    flow { emit(cpy) }
                } else {
                    flow { emit(emptyList<DomainItemVersionComplete>()) }
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectSubOrderItemVersion(id: Triple<String, Int, Int>) {
        if (_subOrder.value.subOrder.getItemIds() != id) {
            _subOrder.value = _subOrder.value.copy(
                subOrder = _subOrder.value.subOrder.copy(
                    itemPreffix = id.first + id.third,
                    itemTypeId = id.second,
                    itemVersionId = id.third,
                    operationId = NoRecord.num
                )
            )
            selectSubOrderItemsCount(ZeroValue.num)
            selectSubOrderCharacteristic(NoRecord.num)
        }
    }

    // Sub Order Operation ---------------------------------------------------------------------------------------------------------------------------
    private val _subOrderOperations: Flow<List<DomainManufacturingOperation>> = manufacturingRepository.operations
    val subOrderOperations: StateFlow<List<DomainManufacturingOperation>> = _subOrderOperations.flatMapLatest { operations ->
        _subOrder.flatMapLatest { so ->
            _inputForOrder.flatMapLatest { master ->
                if (so.subOrder.itemVersionId != NoRecord.num) {
                    val ids = master.filter { it.getItemVersionPid() == so.subOrder.getItemVersionPid() && it.lineId == so.subOrder.lineId }
                        .sortedBy { it.operationOrder }.map { it.operationId }.toSet()
                    val cpy = mutableListOf<DomainManufacturingOperation>()
                    ids.forEach { id ->
                        operations.findLast { it.id == id }?.let {
                            cpy.add(it.copy(isSelected = it.id == so.subOrder.operationId))
                        }
                    }
                    flow { emit(cpy) }
                } else {
                    flow { emit(emptyList<DomainManufacturingOperation>()) }
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectSubOrderOperation(id: Int) {
        if (_subOrder.value.subOrder.operationId != id) {
            _subOrder.value = _subOrder.value.copy(subOrder = _subOrder.value.subOrder.copy(operationId = id))
            selectSubOrderItemsCount(ZeroValue.num)
            selectSubOrderCharacteristic(NoRecord.num)
        }
    }

    // Sub Order Items Count -------------------------------------------------------------------------------------------------------------------------
    fun selectSubOrderItemsCount(count: Int) {
        val cpy = _subOrder.value.copy()

        cpy.subOrder.samplesCount = count

        val subOrderId = cpy.subOrder.id
        var currentSize = ZeroValue.num
        cpy.samples.forEach {
            if (it.isNewRecord || !it.toBeDeleted)
                currentSize++
        }

        if (count > currentSize) {
            for (q in (currentSize + 1)..count)
                if ((cpy.samples.size) >= q && !cpy.samples[q - 1].isNewRecord)
                    cpy.samples[q - 1].toBeDeleted = false
                else
                    cpy.samples.add(DomainSample().copy(subOrderId = subOrderId, sampleNumber = q, isNewRecord = true))
        } else if (count < currentSize) {
            for (q in currentSize downTo count + 1)
                if (cpy.samples[q - 1].isNewRecord)
                    cpy.samples.removeAt(q - 1)
                else
                    cpy.samples[q - 1].toBeDeleted = true
        }

        _subOrder.value = cpy
    }

    // Sub Order Characteristics ---------------------------------------------------------------------------------------------------------------------
    private val _subOrderCharacteristics: Flow<List<DomainCharacteristic>> = productsRepository.characteristics
    private val _subOrderOperationsFlows: Flow<List<DomainOperationsFlow>> = manufacturingRepository.operationsFlows
    val subOrderCharacteristics: StateFlow<List<DomainCharacteristic>> = _subOrderCharacteristics.flatMapLatest { characteristics ->
        _subOrderOperationsFlows.flatMapLatest { opf ->
            _subOrder.flatMapLatest { so ->
                _inputForOrder.flatMapLatest { master ->
                    if (so.subOrder.samplesCount != NoRecord.num) {
                        val ids = master.filter {
                            it.getItemVersionPid() == so.subOrder.getItemVersionPid() &&
                                    (it.operationId == so.subOrder.operationId || isOperationInFlow(it.operationId, so.subOrder.operationId, opf))
                        }.sortedBy { it.charOrder }.map { it.charId }.toSet()

                        val cpy = mutableListOf<DomainCharacteristic>()
                        ids.forEach { id ->
                            characteristics.findLast { it.id == id }?.let { ch ->
                                var isSelected = false
                                so.subOrderTasks.findLast { it.charId == id }?.let { isSelected = !it.toBeDeleted }
                                cpy.add(ch.copy(isSelected = isSelected))
                            }
                        }
                        flow { emit(cpy) }
                    } else {
                        flow { emit(emptyList<DomainCharacteristic>()) }
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectSubOrderCharacteristic(id: Int) {
        if (id == NoRecord.num) {
            _subOrder.value.subOrderTasks.removeIf { it.isNewRecord }
            _subOrder.value.subOrderTasks.forEach { it.toBeDeleted = true }
        } else {
            _subOrder.value.subOrderTasks.findLast { it.charId == id }.let {
                if (it == null) {
                    _subOrder.value.subOrderTasks.add(
                        DomainSubOrderTask().copy(
                            statusId = InvStatuses.TO_DO.statusId,
                            subOrderId = _subOrder.value.subOrder.id,
                            orderedById = _subOrder.value.subOrder.orderedById,
                            charId = id,
                            isNewRecord = true
                        )
                    )
                } else {
                    if (it.isNewRecord) {
                        _subOrder.value.subOrderTasks.remove(it)
                    } else {
                        val index = _subOrder.value.subOrderTasks.indexOf(it)
                        _subOrder.value.subOrderTasks[index].toBeDeleted = !_subOrder.value.subOrderTasks[index].toBeDeleted
                    }
                }
            }
        }
        _subOrder.value = _subOrder.value.copy(extraTrigger = !_subOrder.value.extraTrigger)
    }

    /**
     * Data Base/REST API Operations --------------------------------------------------------------------------------------------------------------------------
     * */
    private fun makeOrder(newRecord: Boolean = true) {
        if (checkIfPossibleToSave(_order.value))
            viewModelScope.launch(Dispatchers.IO) {
                with(repository) { if (newRecord) insertOrder(_order.value) else updateOrder(_order.value) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> {
                                updateLoadingState(Pair(false, null))
                                withContext(Dispatchers.Main) {
                                    appNavigator.tryNavigateTo(
                                        route = Route.Main.Inv.withOpts(resource.data?.id.toString(), NoRecordStr.str),
                                        popUpToRoute = Route.Main.Inv.route,
                                        inclusive = true
                                    )
                                }
                            }

                            Status.ERROR -> updateLoadingState(Pair(false, resource.message))
                        }
                    }
                }
            }
        else
            updateLoadingState(Pair(false, "Fill in all field before save!"))
    }

    private fun makeNewOrderWithSubOrder(newRecord: Boolean = true) {
        if (checkIfPossibleToSave(Triple(_order.value, _subOrder.value.subOrder, _subOrder.value.subOrderTasks.filter { !it.toBeDeleted }.size)))
            viewModelScope.launch(Dispatchers.IO) {
                repository.run { if (newRecord) insertOrder(_order.value) else updateOrder(_order.value) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> updateLoadingState(Pair(true, null))

                            Status.SUCCESS -> {
                                resource.data?.let {
                                    _order.value = it
                                    _subOrder.value.order = it
                                    _subOrder.value.subOrder.orderId = it.id
                                    makeSubOrder(newRecord, true)
                                }
                            }

                            Status.ERROR -> updateLoadingState(Pair(false, resource.message))
                        }
                    }
                }
            }
        else
            updateLoadingState(Pair(false, "Fill in all field before save!"))
    }

    private fun makeSubOrder(newRecord: Boolean = true, pcOnly: Boolean = false) {
        if (checkIfPossibleToSave(Triple(_order.value, _subOrder.value.subOrder, _subOrder.value.subOrderTasks.filter { !it.toBeDeleted }.size)))
            viewModelScope.launch(Dispatchers.IO) {
                repository.run { if (newRecord) insertSubOrder(_subOrder.value.subOrder) else updateSubOrder(subOrder.value.subOrder) }
                    .consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> updateLoadingState(Pair(true, null))
                                Status.SUCCESS -> {
                                    resource.data?.let {
                                        postDeleteSubOrderTasks(it.id)
                                        postDeleteSamples(it.id)
                                    }
                                    updateLoadingState(Pair(false, null))
                                    withContext(Dispatchers.Main) {
                                        if (pcOnly)
                                            appNavigator.tryNavigateTo(
                                                route = Route.Main.ProcessControl.withOpts(
                                                    resource.data?.orderId.toString(),
                                                    resource.data?.id.toString()
                                                ),
                                                popUpToRoute = Route.Main.ProcessControl.route,
                                                inclusive = true
                                            )
                                        else
                                            appNavigator.tryNavigateTo(
                                                route = Route.Main.Inv.withOpts(resource.data?.orderId.toString(), resource.data?.id.toString()),
                                                popUpToRoute = Route.Main.Inv.route,
                                                inclusive = true
                                            )
                                    }
                                }

                                Status.ERROR -> updateLoadingState(Pair(false, resource.message))
                            }
                        }
                    }
            }
        else
            updateLoadingState(Pair(false, "Fill in all field before save!"))
    }

    private suspend fun postDeleteSamples(subOrderId: Int) {
        withContext(Dispatchers.IO) {
            repository.run {
                _subOrder.value.samples.map {
                    it.subOrderId = subOrderId
                    it
                }.let { it ->
                    it.filter { it.toBeDeleted }.let {
                        if (it.isNotEmpty()) deleteSamples(it).consumeEach { event ->
                            event.getContentIfNotHandled()?.let { resource ->
                                when (resource.status) {
                                    Status.LOADING -> updateLoadingState(Pair(true, null))
                                    Status.SUCCESS -> updateLoadingState(Pair(false, null))
                                    Status.ERROR -> updateLoadingState(Pair(false, resource.message))
                                }
                            }
                        }
                    }
                    it.filter { it.isNewRecord }.let {
                        if (it.isNotEmpty()) insertSamples(it).consumeEach { event ->
                            event.getContentIfNotHandled()?.let { resource ->
                                when (resource.status) {
                                    Status.LOADING -> updateLoadingState(Pair(true, null))
                                    Status.SUCCESS -> updateLoadingState(Pair(false, null))
                                    Status.ERROR -> updateLoadingState(Pair(false, resource.message))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun postDeleteSubOrderTasks(subOrderId: Int) {
        withContext(Dispatchers.IO) {
            repository.run {
                _subOrder.value.subOrderTasks.map {
                    it.subOrderId = subOrderId
                    it
                }.let { it ->
                    it.filter { it.toBeDeleted }.let {
                        if (it.isNotEmpty()) deleteTasks(it).consumeEach { event ->
                            event.getContentIfNotHandled()?.let { resource ->
                                when (resource.status) {
                                    Status.LOADING -> updateLoadingState(Pair(true, null))
                                    Status.SUCCESS -> updateLoadingState(Pair(false, null))
                                    Status.ERROR -> updateLoadingState(Pair(false, resource.message))
                                }
                            }
                        }
                    }
                    it.filter { it.isNewRecord }.let {
                        if (it.isNotEmpty()) insertTasks(it).consumeEach { event ->
                            event.getContentIfNotHandled()?.let { resource ->
                                when (resource.status) {
                                    Status.LOADING -> updateLoadingState(Pair(true, null))
                                    Status.SUCCESS -> updateLoadingState(Pair(false, null))
                                    Status.ERROR -> updateLoadingState(Pair(false, resource.message))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
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

fun checkIfPossibleToSave(record: DomainOrder): Boolean {
    if (record.orderTypeId == NoRecord.num) return false
    if (record.reasonId == NoRecord.num) return false
    if (record.customerId == NoRecord.num) return false
    if (record.orderedById == NoRecord.num) return false

    return true
}

fun checkIfPossibleToSave(record: Triple<DomainOrder, DomainSubOrder, Int>): Boolean {

    if (record.first.reasonId == NoRecord.num) return false

    if (record.second.orderedById == NoRecord.num) return false
    if (record.second.departmentId == NoRecord.num) return false
    if (record.second.subDepartmentId == NoRecord.num) return false
    if (record.second.channelId == NoRecord.num) return false
    if (record.second.lineId == NoRecord.num) return false
    if (record.second.operationId == NoRecord.num) return false
    if (record.second.itemVersionId == NoRecord.num) return false
    if (record.second.samplesCount == ZeroValue.num) return false

    if (record.third == ZeroValue.num) return false

    return true
}