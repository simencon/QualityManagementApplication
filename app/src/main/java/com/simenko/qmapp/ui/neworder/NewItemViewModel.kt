package com.simenko.qmapp.ui.neworder

import androidx.lifecycle.*
import androidx.navigation.NavHostController
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.AddEditMode
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.main.setMainActivityResult
import com.simenko.qmapp.ui.neworder.assemblers.checkIfPossibleToSave
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TAG = "NewItemViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewItemViewModel @Inject constructor(
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository
) : ViewModel() {
    private lateinit var navController: NavHostController
    fun initNavController(controller: NavHostController) {
        this.navController = controller
    }

    private lateinit var mainActivityViewModel: MainActivityViewModel
    fun initMainActivityViewModel(viewModel: MainActivityViewModel) {
        this.mainActivityViewModel = viewModel
    }

    fun setAddEditMode(mode: AddEditMode) {
        mainActivityViewModel.setAddEditMode(mode)
    }

    val pairedTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

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
    }

    private val _currentSubOrder: MutableStateFlow<DomainSubOrderShort> = MutableStateFlow(
        DomainSubOrderShort(DomainSubOrder().copy(statusId = InvStatuses.TO_DO.statusId), DomainOrder().copy(statusId = InvStatuses.TO_DO.statusId))
    )
    val currentSubOrderSF: StateFlow<DomainSubOrderShort> = _currentOrder.flatMapLatest { order ->
        _currentSubOrder.flatMapLatest { subOrder ->
            _currentSubOrder.value = subOrder.copy(order = order)
            flow { emit(_currentSubOrder.value) }
        }
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
        _currentSubOrder.value = _currentSubOrder.value.copy(
            subOrder = _currentSubOrder.value.subOrder.copy(
                departmentId = NoRecord.num,
                subDepartmentId = NoRecord.num,
                orderedById = NoRecord.num,
                channelId = NoRecord.num,
                lineId = NoRecord.num
            )
        )
        _currentOrder.value = _currentOrder.value.copy(reasonId = id, customerId = NoRecord.num, orderedById = NoRecord.num)
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
    }


    private val _inputForOrder: Flow<List<DomainInputForOrder>> = repository.inputForOrder


    private val _subOrderDepartments: Flow<List<DomainDepartment>> = manufacturingRepository.getDepartments
    val subOrderDepartments: StateFlow<List<DomainDepartment>> = _subOrderDepartments.flatMapLatest { departments ->
        _currentSubOrder.flatMapLatest { subOrder ->
            _currentOrder.flatMapLatest { currentOrder ->
                _inputForOrder.flatMapLatest { master ->
                    if (currentOrder.reasonId != NoRecord.num) {
                        val ids = master.sortedBy { it.depOrder }.map { it.depId }.toSet()
                        val cpy = mutableListOf<DomainDepartment>()
                        ids.forEach { id ->
                            departments.findLast { it.id == id }?.let {
                                cpy.add(it.copy(isSelected = it.id == subOrder.subOrder.departmentId))
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
        _currentSubOrder.value = _currentSubOrder.value.copy(
            subOrder = _currentSubOrder.value.subOrder.copy(
                departmentId = id,
                subDepartmentId = NoRecord.num,
                orderedById = NoRecord.num,
                channelId = NoRecord.num,
                lineId = NoRecord.num
            )
        )
    }


    private val _subOrderSubDepartments: Flow<List<DomainSubDepartment>> = manufacturingRepository.subDepartments
    val subOrderSubDepartments: StateFlow<List<DomainSubDepartment>> = _subOrderSubDepartments.flatMapLatest { subDepartments ->
        _currentSubOrder.flatMapLatest { subOrder ->
            _inputForOrder.flatMapLatest { master ->
                if (subOrder.subOrder.departmentId != NoRecord.num) {
                    val ids = master.filter { it.depId == subOrder.subOrder.departmentId }.sortedBy { it.subDepOrder }.map { it.subDepId }.toSet()
                    val cpy = mutableListOf<DomainSubDepartment>()
                    ids.forEach { id ->
                        subDepartments.findLast { it.id == id }?.let {
                            cpy.add(it.copy(isSelected = it.id == subOrder.subOrder.departmentId))
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
        _currentSubOrder.value = _currentSubOrder.value.copy(
            subOrder = _currentSubOrder.value.subOrder.copy(
                subDepartmentId = id,
                orderedById = NoRecord.num,
                channelId = NoRecord.num,
                lineId = NoRecord.num
            )
        )
    }


    private val _subOrderPlacers: Flow<List<DomainTeamMember>> = manufacturingRepository.getTeamMembers
    val subOrderPlacers: StateFlow<List<DomainTeamMember>> = _subOrderPlacers.flatMapLatest { placers ->
        _currentSubOrder.flatMapLatest { subOrder ->
            if (subOrder.subOrder.subDepartmentId != NoRecord.num) {
                val cpy = mutableListOf<DomainTeamMember>()
                placers.filter { it.departmentId == subOrder.subOrder.departmentId }
                    .forEach { cpy.add(it.copy(isSelected = it.id == subOrder.subOrder.subDepartmentId)) }
                flow { emit(cpy) }
            } else {
                flow { emit(emptyList<DomainTeamMember>()) }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectSubOrderPlacer(id: Int) {
        _currentSubOrder.value = _currentSubOrder.value.copy(
            subOrder = _currentSubOrder.value.subOrder.copy(
                orderedById = id,
                channelId = NoRecord.num,
                lineId = NoRecord.num
            )
        )
    }


    private val _subOrderChannels: Flow<List<DomainManufacturingChannel>> = manufacturingRepository.channels
    val subOrderChannels: StateFlow<List<DomainManufacturingChannel>> = _subOrderChannels.flatMapLatest { channels ->
        _currentSubOrder.flatMapLatest { subOrder ->
            _inputForOrder.flatMapLatest { master ->
                if (subOrder.subOrder.orderedById != NoRecord.num) {
                    val ids = master.filter { it.subDepId == subOrder.subOrder.subDepartmentId }.sortedBy { it.channelOrder }.map { it.chId }.toSet()
                    val cpy = mutableListOf<DomainManufacturingChannel>()
                    ids.forEach { id ->
                        channels.findLast { it.id == id }?.let {
                            cpy.add(it.copy(isSelected = it.id == subOrder.subOrder.channelId))
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
        _currentSubOrder.value = _currentSubOrder.value.copy(subOrder = _currentSubOrder.value.subOrder.copy(channelId = id, lineId = NoRecord.num))
    }


    private val _subOrderLines: Flow<List<DomainManufacturingLine>> = manufacturingRepository.lines
    val subOrderLines: StateFlow<List<DomainManufacturingLine>> = _subOrderLines.flatMapLatest { lines ->
        _currentSubOrder.flatMapLatest { subOrder ->
            _inputForOrder.flatMapLatest { master ->
                if (subOrder.subOrder.channelId != NoRecord.num) {
                    val ids = master.filter { it.chId == subOrder.subOrder.channelId }.sortedBy { it.lineOrder }.map { it.lineId }.toSet()
                    val cpy = mutableListOf<DomainManufacturingLine>()
                    ids.forEach { id ->
                        lines.findLast { it.id == id }?.let {
                            cpy.add(it.copy(isSelected = it.id == subOrder.subOrder.lineId))
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
        _currentSubOrder.value = _currentSubOrder.value.copy(subOrder = _currentSubOrder.value.subOrder.copy(lineId = id))
    }


    private val _subOrderItemVersions: Flow<List<DomainItemVersionComplete>> = productsRepository.itemVersionsComplete
    val subOrderItemVersions: StateFlow<List<DomainItemVersionComplete>> = _subOrderItemVersions.flatMapLatest { itemVersions ->
        _currentSubOrder.flatMapLatest { subOrder ->
            _inputForOrder.flatMapLatest { master ->
                if (subOrder.subOrder.lineId != NoRecord.num) {
                    val ids = master.filter { it.lineId == subOrder.subOrder.lineId }
                        .sortedBy { it.itemDesignation }.map { it.itemPrefix + it.itemVersionId }.toSet()
                    val cpy = mutableListOf<DomainItemVersionComplete>()
                    ids.forEach { id ->
                        itemVersions.findLast { it.itemVersion.fId == id }?.let {
                            cpy.add(it.copy(isSelected = it.itemVersion.fId == (subOrder.subOrder.itemPreffix + subOrder.subOrder.itemVersionId)))
                        }
                    }
                    flow { emit(cpy) }
                } else {
                    flow { emit(emptyList<DomainItemVersionComplete>()) }
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectSubOrderItemVersion(id: Triple<Char, Int, Int>) {
        _currentSubOrder.value = _currentSubOrder.value.copy(
            subOrder = _currentSubOrder.value.subOrder.copy(itemPreffix = id.first.toString(), itemTypeId = id.second, itemVersionId = id.third)
        )
    }




    private val _subOrderOperations: Flow<List<DomainManufacturingOperation>> = manufacturingRepository.operations
    val subOrderOperations: StateFlow<List<DomainManufacturingOperation>> = _subOrderOperations.flatMapLatest { operations ->
        _currentSubOrder.flatMapLatest { subOrder ->
            _inputForOrder.flatMapLatest { master ->
                if (subOrder.subOrder.itemVersionId != NoRecord.num) {
                    val ids = master.filter { (it.itemPrefix + it.itemVersionId) == (subOrder.subOrder.itemPreffix + subOrder.subOrder.itemVersionId) }
                        .sortedBy { it.operationOrder }.map { it.operationId }.toSet()
                    val cpy = mutableListOf<DomainManufacturingOperation>()
                    ids.forEach { id ->
                        operations.findLast { it.id == id }?.let {
                            cpy.add(it.copy(isSelected = it.id == subOrder.subOrder.operationId))
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
        _currentSubOrder.value = _currentSubOrder.value.copy(subOrder = _currentSubOrder.value.subOrder.copy(operationId = id))
    }

    val inputForOrder = repository.inputForOrder

    val operationsFlows = manufacturingRepository.operationsFlows.asLiveData()

    val characteristics = productsRepository.characteristics
    val characteristicsMutable = MutableLiveData<MutableList<DomainCharacteristic>>(mutableListOf())
    val characteristicsMediator: MediatorLiveData<Pair<MutableList<DomainCharacteristic>?, Boolean?>> =
        MediatorLiveData<Pair<MutableList<DomainCharacteristic>?, Boolean?>>().apply {
            addSource(characteristicsMutable) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(characteristicsMutable.value, it) }
        }


    val inputForOrderMediator: MediatorLiveData<Pair<List<DomainInputForOrder>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainInputForOrder>?, Boolean?>>().apply {
            addSource(inputForOrder.asLiveData()) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(inputForOrder.asLiveData().value, it) }
        }

    fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                mainActivityViewModel.updateLoadingState(Pair(true, null))

                repository.syncInvestigationTypes()
                repository.syncInvestigationReasons()
                repository.syncInputForOrder()
                manufacturingRepository.syncDepartments()
                manufacturingRepository.syncTeamMembers()

                productsRepository.refreshKeys()
                productsRepository.refreshComponents()
                productsRepository.refreshVersionStatuses()
                productsRepository.refreshComponentVersions()

                mainActivityViewModel.updateLoadingState(Pair(false, null))
            } catch (e: Exception) {
                mainActivityViewModel.updateLoadingState(Pair(false, e.message))
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
                        Status.LOADING -> mainActivityViewModel.updateLoadingState(Pair(true, null))
                        Status.SUCCESS -> {
                            postDeleteSubOrderTasks(resource.data!!.id, subOrder)
                            postDeleteSamples(resource.data.id, subOrder)
                            mainActivityViewModel.updateLoadingState(Pair(false, null))
                            setMainActivityResult(activity, activity.addEditModeEnum, resource.data.orderId, resource.data.id)
                            activity.finish()
                        }

                        Status.ERROR -> mainActivityViewModel.updateLoadingState(Pair(false, resource.message))
                    }
                }
            }
        }
    }

    fun postOrder() {
        if (checkIfPossibleToSave(_currentOrder.value) != null)
            viewModelScope.launch(Dispatchers.IO) {
                with(repository) { insertOrder(_currentOrder.value) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> {
                                mainActivityViewModel.updateLoadingState(Pair(true, null))
                            }

                            Status.SUCCESS -> {
                                mainActivityViewModel.updateLoadingState(Pair(false, null))
                                setAddEditMode(AddEditMode.NO_MODE)
                                withContext(Dispatchers.Main) {
                                    navController.navigate(Screen.Main.Inv.withArgs(AllInv.str, resource.data?.id.toString(), NoRecordStr.str)) {
                                        popUpTo(0)
                                    }
                                }
                            }

                            Status.ERROR -> {
                                mainActivityViewModel.updateLoadingState(Pair(false, resource.message))
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        else
            mainActivityViewModel.updateLoadingState(Pair(false, "Fill in all field before save!"))
    }

    fun editOrder() {
        if (checkIfPossibleToSave(_currentOrder.value) != null)
            viewModelScope.launch(Dispatchers.IO) {
                repository.run { updateOrder(_currentOrder.value) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainActivityViewModel.updateLoadingState(Pair(true, null))

                            Status.SUCCESS -> {
                                mainActivityViewModel.updateLoadingState(Pair(false, null))
                                setAddEditMode(AddEditMode.NO_MODE)
                                withContext(Dispatchers.Main) {
                                    navController.navigate(Screen.Main.Inv.withArgs(AllInv.str, resource.data?.id.toString(), NoRecordStr.str)) {
                                        popUpTo(0)
                                    }
                                }
                            }

                            Status.ERROR -> mainActivityViewModel.updateLoadingState(Pair(false, resource.message))
                        }
                    }
                }
            }
        else
            mainActivityViewModel.updateLoadingState(Pair(false, "Fill in all field before save!"))
    }

    fun postNewOrderWithSubOrder(activity: NewItemActivity, subOrder: DomainSubOrderShort) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run { insertOrder(subOrder.order) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainActivityViewModel.updateLoadingState(Pair(true, null))

                        Status.SUCCESS -> {
                            subOrder.subOrder.orderId = resource.data!!.id
                            postNewSubOrder(activity, subOrder)
                        }

                        Status.ERROR -> mainActivityViewModel.updateLoadingState(Pair(false, resource.message))
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
                        Status.LOADING -> mainActivityViewModel.updateLoadingState(Pair(true, null))

                        Status.SUCCESS -> {
                            postDeleteSubOrderTasks(resource.data!!.id, subOrder)
                            postDeleteSamples(resource.data.id, subOrder)
                            mainActivityViewModel.updateLoadingState(Pair(false, null))
                            setMainActivityResult(activity, activity.addEditModeEnum, resource.data.orderId, resource.data.id)
                            activity.finish()
                        }

                        Status.ERROR -> mainActivityViewModel.updateLoadingState(Pair(false, resource.message))
                    }
                }
            }

            syncTasks()
            syncSamples()
            syncResults()
        }
    }

    fun syncTasks() {
        viewModelScope.launch {
            try {
                mainActivityViewModel.updateLoadingState(Pair(true, null))

                repository.syncSubOrderTasks(Pair(_currentOrder.value.createdDate, _currentOrder.value.createdDate))
                repository.syncResults(Pair(_currentOrder.value.createdDate, _currentOrder.value.createdDate))

                mainActivityViewModel.updateLoadingState(Pair(false, null))
            } catch (e: Exception) {
                mainActivityViewModel.updateLoadingState(Pair(false, e.message))
            }
        }
    }

    fun syncSamples() {
        viewModelScope.launch {
            try {
                mainActivityViewModel.updateLoadingState(Pair(true, null))

                repository.syncSamples(Pair(_currentOrder.value.createdDate, _currentOrder.value.createdDate))
                repository.syncResults(Pair(_currentOrder.value.createdDate, _currentOrder.value.createdDate))

                mainActivityViewModel.updateLoadingState(Pair(false, null))
            } catch (e: Exception) {
                mainActivityViewModel.updateLoadingState(Pair(false, e.message))
            }
        }
    }

    fun syncResults() {
        viewModelScope.launch {
            try {
                mainActivityViewModel.updateLoadingState(Pair(true, null))

                repository.syncResults(Pair(_currentOrder.value.createdDate, _currentOrder.value.createdDate))

                mainActivityViewModel.updateLoadingState(Pair(false, null))
            } catch (e: Exception) {
                mainActivityViewModel.updateLoadingState(Pair(false, e.message))
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