package com.simenko.qmapp.ui.main.investigations

import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.dialogs.DialogInput
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.content.InvestigationsActions
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.main.main.content.ProcessControlActions
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.BaseFilter
import com.simenko.qmapp.utils.EmployeesFilter
import com.simenko.qmapp.utils.InvStatuses
import com.simenko.qmapp.utils.InvestigationsUtils.getDetailedOrdersRange
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import com.simenko.qmapp.utils.OrdersFilter
import com.simenko.qmapp.utils.SubOrdersFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.time.Instant
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InvestigationsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository,
) : ViewModel() {

    private val _isLoadingInProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _createdRecord: MutableStateFlow<Pair<Event<ID>, Event<ID>>> = MutableStateFlow(Pair(Event(NoRecord.num), Event(NoRecord.num)))
    private val _lastVisibleItemKey = MutableStateFlow(0L)
    private val _ordersVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _subOrdersVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _tasksVisibility: MutableStateFlow<Pair<SelectedNumber, SelectedNumber>> = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _samplesVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _resultsVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null

    fun onEntered(isPcOnly: Boolean, orderId: ID, subOrderId: ID) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mainPageHandler == null) {
                setLastVisibleItemKey(if (orderId == NoRecord.num) repository.latestLocalOrderId() else orderId)
                _createdRecord.value = Pair(Event(orderId), Event(subOrderId))
                _ordersVisibility.value = Pair(SelectedNumber(orderId), NoRecord)
                _subOrdersVisibility.value = Pair(SelectedNumber(subOrderId), NoRecord)
            }

            mainPageHandler = MainPageHandler.Builder(if (isPcOnly) Page.PROCESS_CONTROL else Page.INVESTIGATIONS, mainPageState)
                .setOnSearchClickAction { if (isPcOnly) setSubOrdersFilter(it) else setOrdersFilter(it) }
                .setOnTabSelectAction { if (isPcOnly) setSubOrdersFilter(BaseFilter(statusId = it.num)) else setOrdersFilter(BaseFilter(statusId = it.num)) }
                .setOnFabClickAction { if (isPcOnly) onAddProcessControlClick() else onAddInvClick() }
                .setOnPullRefreshAction { uploadNewInvestigations() }
                .setOnUpdateLoadingExtraAction { _isLoadingInProgress.value = it.first }
                .setOnActionItemClickAction {
                    launch {
                        _syncInvestigationsEvent.emit(it == InvestigationsActions.SYNC_INVESTIGATIONS || it == ProcessControlActions.SYNC_INVESTIGATIONS)
                    }
                }
                .build()
                .apply {
                    val selectedTabIndex = if (isPcOnly) tabIndexesMap[_currentSubOrdersFilter.value.statusId] ?: NoRecord.num.toInt() else tabIndexesMap[_currentOrdersFilter.value.statusId] ?: NoRecord.num.toInt()
                    setupMainPage.invoke(selectedTabIndex, true)
                }
        }
    }

    private var _syncInvestigationsEvent = MutableSharedFlow<Boolean>()
    val syncInvestigationsEvent = _syncInvestigationsEvent.asSharedFlow()

    private val tabIndexesMap = mapOf(Pair(FirstTabId.num, 0), Pair(SecondTabId.num, 1), Pair(ThirdTabId.num, 2), Pair(FourthTabId.num, 3))

    /**
     * Navigation -------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddInvClick() {
        appNavigator.tryNavigateTo(route = Route.Main.AllInvestigations.OrderAddEdit(NoRecord.num))
    }

    fun onEditInvClick(orderId: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.AllInvestigations.OrderAddEdit(orderId))
    }

    fun onAddSubOrderClick(orderId: ID) {
        appNavigator.tryNavigateTo(Route.Main.AllInvestigations.SubOrderAddEdit(orderId = orderId, subOrderId = NoRecord.num))
    }

    fun onEditSubOrderClick(record: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(Route.Main.AllInvestigations.SubOrderAddEdit(orderId = record.first, subOrderId = record.second))
    }

    private fun onAddProcessControlClick() {
        appNavigator.tryNavigateTo(route = Route.Main.ProcessControl.SubOrderAddEdit(orderId = NoRecord.num, subOrderId = NoRecord.num))
    }

    fun onEditProcessControlClick(record: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(Route.Main.ProcessControl.SubOrderAddEdit(orderId = record.first, subOrderId = record.second))
    }

    private val _isStatusUpdateDialogVisible = MutableLiveData(false)
    val isStatusUpdateDialogVisible: LiveData<Boolean> = _isStatusUpdateDialogVisible
    fun hideStatusUpdateDialog() {
        _isStatusUpdateDialogVisible.value = false
    }

    private val _dialogInput = MutableLiveData(DialogInput())
    val dialogInput: LiveData<DialogInput> = _dialogInput
    fun showStatusUpdateDialog(
        currentOrder: DomainOrderComplete? = null,
        currentSubOrder: DomainSubOrderComplete? = null,
        currentSubOrderTask: DomainSubOrderTaskComplete? = null,
        performerId: ID? = null
    ) {
        _dialogInput.value = DialogInput(currentOrder, currentSubOrder, currentSubOrderTask, performerId)
        _isStatusUpdateDialogVisible.value = true
    }

    private val _invStatuses = repository.investigationStatuses()

    private val _selectedStatus = MutableStateFlow(NoRecord)

    fun selectStatus(statusId: SelectedNumber) {
        _selectedStatus.value = statusId
    }

    val invStatuses = _invStatuses.flatMapLatest { statuses ->
        _selectedStatus.flatMapLatest { selectedStatus ->
            val cpy = statuses.map { it.copy(isSelected = it.id == selectedStatus.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    private val _employees = manufacturingRepository.employeesComplete(EmployeesFilter())
    val employees: Flow<List<DomainEmployeeComplete>> = _employees.flatMapLatest { team ->
        flow { emit(team) }
    }.flowOn(Dispatchers.IO)

    /**
     * Handling scrolling to just created record-------------------------------------------------
     * */
    private val _isComposed = MutableStateFlow(false)
    val setIsComposed: (Boolean) -> Unit = { _isComposed.value = it }

    val scrollToRecord = _createdRecord.flatMapLatest { record ->
        _isComposed.flatMapLatest { isComposed ->
            if (isComposed) flow { emit(record) } else flow { emit(null) }
        }
    }.flowOn(Dispatchers.Default)

    val channel = Channel<Job>(capacity = Channel.UNLIMITED).apply { viewModelScope.launch { consumeEach { it.join() } } }

    /**
     * Operations with orders ______________________________________________________________
     * */
    fun setLastVisibleItemKey(key: ID) {
        _lastVisibleItemKey.value = key
    }

    //    ToDo - change it to default when functionality done for ProcessControlOnly
    private val _currentOrdersRange =
//        MutableStateFlow(Pair(NoRecord.num.toLong(), NoRecord.num.toLong()))
        MutableStateFlow(Pair(1691991128021L, Instant.now().toEpochMilli()))

    private val _orders: StateFlow<List<DomainOrderComplete>> = _lastVisibleItemKey.flatMapLatest { key ->
        _currentOrdersFilter.flatMapLatest { filter ->
            repository.ordersListByLastVisibleId(key as ID, filter)
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    /**
     * Visibility operations
     * */
    fun setOrdersVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _ordersVisibility.value = _ordersVisibility.value.setVisibility(dId, aId)
    }

    /**
     * Filtering operations
     * */
    private val _currentOrdersFilter = MutableStateFlow(OrdersFilter())
    private fun setOrdersFilter(filter: BaseFilter) {
        val current = _currentOrdersFilter.value
        _currentOrdersFilter.value = _currentOrdersFilter.value.copy(
            typeId = filter.typeId ?: current.typeId,
            statusId = filter.statusId ?: current.statusId,
            stringToSearch = filter.stringToSearch ?: current.stringToSearch,
        )
    }

    /**
     * The result flow
     * */
    val orders = _isLoadingInProgress.flatMapLatest { isLoading ->
        _orders.flatMapLatest { orders ->
            _ordersVisibility.flatMapLatest { visibility ->
                if (visibility.first == NoRecord) {
                    setSubOrdersVisibility(dId = _subOrdersVisibility.value.first)
                    setTasksVisibility(dId = _tasksVisibility.value.first)
                    setSamplesVisibility(dId = _samplesVisibility.value.first)
                }
                val cyp = orders.map { it.copy(detailsVisibility = it.order.id == visibility.first.num, isExpanded = it.order.id == visibility.second.num) }
                _currentOrdersRange.value = cyp.getDetailedOrdersRange()
                if (!isLoading) uploadOlderInvestigations(_currentOrdersRange.value.first)
                flow { emit(cyp) }
            }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * REST operations
     * */
    fun deleteOrder(orderId: ID) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteOrder(orderId).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * End of operations with orders _______________________________________________________
     * */

    /**
     * Operations with sub orders __________________________________________________________
     * */
    private val _subOrdersSF: Flow<List<DomainSubOrderComplete>> = _currentOrdersRange.flatMapLatest { ordersRange ->
        _currentSubOrdersFilter.flatMapLatest { filter ->
            repository.subOrdersRangeList(ordersRange, filter)
        }
    }

    /**
     * Visibility operations
     * */
    fun setSubOrdersVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _subOrdersVisibility.value = _subOrdersVisibility.value.setVisibility(dId, aId)
    }

    /**
     * Filtering operations
     * */
    private val _currentSubOrdersFilter = MutableStateFlow(SubOrdersFilter())
    fun setSubOrdersFilter(filter: BaseFilter) {
        val current = _currentSubOrdersFilter.value
        _currentSubOrdersFilter.value = _currentSubOrdersFilter.value.copy(
            typeId = filter.typeId ?: current.typeId,
            statusId = filter.statusId ?: current.statusId,
            stringToSearch = filter.stringToSearch ?: current.stringToSearch,
        )
    }

    /**
     * The result flow
     * */
    val subOrdersSF = _subOrdersSF.flatMapLatest { subOrders ->
        _subOrdersVisibility.flatMapLatest { visibility ->
            if (visibility.first == NoRecord) {
                setTasksVisibility(dId = _tasksVisibility.value.first)
                setSamplesVisibility(dId = _samplesVisibility.value.first)
            }
            val cyp = subOrders.map { it.copy(detailsVisibility = it.subOrder.id == visibility.first.num, isExpanded = it.subOrder.id == visibility.second.num) }
            flow { emit(cyp) }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * REST operations
     * */
    fun onDeleteSubOrderClick(subOrderId: ID) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteSubOrder(subOrderId).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * End of operations with orders _______________________________________________________
     * */

    /**
     * Operations with tasks __________________________________________________________
     * */
    private val _tasks: Flow<List<DomainSubOrderTaskComplete>> = _subOrdersVisibility.flatMapLatest { subOrdersIds ->
        repository.tasksRangeList(subOrdersIds.first.num)
    }

    /**
     * Visibility operations
     * */
    fun setTasksVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _tasksVisibility.value = _tasksVisibility.value.setVisibility(dId, aId)
    }

    val tasksVisibility get() = _tasksVisibility.asStateFlow()

    val isSecondRowVisible = _tasksVisibility.flatMapLatest {
        flow { emit(it.first != NoRecord) }
    }

    /**
     * The result flow
     * */
    val tasks = _tasks.flatMapLatest { tasks ->
        _tasksVisibility.flatMapLatest { visibility ->
            val cyp = tasks.map { it.copy(detailsVisibility = it.subOrderTask.id == visibility.first.num, isExpanded = it.subOrderTask.id == visibility.second.num) }
            flow { emit(cyp) }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * REST operations
     * */
    fun deleteSubOrderTask(taskId: ID) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteSubOrderTask(taskId).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun syncTasks() {
        viewModelScope.launch {
            try {
                mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))

                repository.syncSubOrderTasks(_currentOrdersRange.value)
                repository.syncResults(_currentOrdersRange.value)

                mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
            } catch (e: IOException) {
                mainPageHandler?.updateLoadingState?.invoke(Pair(false, e.message))
            }
        }
    }

    /**
     * End of operations with tasks _______________________________________________________
     * */

    /**
     * Operations with samples __________________________________________________________
     * */
    private val _samples = _subOrdersVisibility.flatMapLatest { subOrderId ->
        repository.samplesRangeList(subOrderId.first.num)
    }

    /**
     * Visibility operations
     * */
    fun setSamplesVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _samplesVisibility.value = _samplesVisibility.value.setVisibility(dId, aId)
    }

    val samplesVisibility get() = _samplesVisibility.asStateFlow()

    /**
     * The result flow
     * */
    val samples = _samples.flatMapLatest { samples ->
        _samplesVisibility.flatMapLatest { visibility ->
            val cpy = samples.map { it.copy(detailsVisibility = it.sample.id == visibility.first.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * REST operations
     *
     * "no operations for now"
     * */

    /**
     * End of operations with samples _______________________________________________________
     * */

    /**
     * Operations with results __________________________________________________________
     * */
    private val _results: Flow<List<DomainResultComplete>> = _tasksVisibility.flatMapLatest { taskIds ->
        _samplesVisibility.flatMapLatest { sampleIds ->
            repository.resultsRangeList(taskIds.first.num, sampleIds.first.num)
        }
    }

    /**
     * Visibility operations
     * */
    fun setResultsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _resultsVisibility.value = _resultsVisibility.value.setVisibility(dId, aId)
    }

    /**
     * The result flow
     * */
    val results = _results.flatMapLatest { results ->
        _resultsVisibility.flatMapLatest { visibility ->
            val cpy = results.map { it.copy(detailsVisibility = it.result.id == visibility.first.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * End of operations with results _______________________________________________________
     * */

    private fun deleteResultsBasedOnTask(task: DomainSubOrderTask) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteResults(task.id).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun editSubOrder(subOrder: DomainSubOrder) {
        viewModelScope.launch {
            try {
                mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                withContext(Dispatchers.IO) {
                    runBlocking {
                        repository.tasksBySubOrderId(subOrder.id).forEach {
                            it.statusId = subOrder.statusId
                            it.completedById = subOrder.completedById
                            editTask(it)
                        }
                        repository.run { getSubOrder(subOrder) }.consumeEach { }

                        val order = repository.orderById(subOrder.orderId)
                        repository.run { getOrder(order) }.consumeEach {}
                    }
                }
                hideStatusUpdateDialog()
                mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
            } catch (e: IOException) {
                mainPageHandler?.updateLoadingState?.invoke(Pair(false, e.message))
            }
        }
    }

    fun editSubOrderTask(subOrderTask: DomainSubOrderTask) {
        viewModelScope.launch {
            try {
                mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                withContext(Dispatchers.IO) {
                    runBlocking {

                        editTask(subOrderTask)

                        val subOrder = repository.subOrderById(subOrderTask.subOrderId)
                        repository.run { getSubOrder(subOrder) }.consumeEach { }

                        val order = repository.orderById(subOrder.orderId)
                        repository.run { getOrder(order) }.consumeEach { }

                    }
                }
                hideStatusUpdateDialog()
                mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
            } catch (e: IOException) {
                mainPageHandler?.updateLoadingState?.invoke(Pair(false, e.message))
            }
        }
    }

    private suspend fun editTask(subOrderTask: DomainSubOrderTask) {
        withContext(Dispatchers.IO) {
            val listOfResults: MutableList<DomainResult> = mutableListOf()
            /**
             * 1.Get latest status task
             * 2.Compare with new status
             * 3.If change is "To Do"/"Rejected" -> "Done" = Collect/Post new results and change status
             * 4.If change is "Done" -> "To Do" = Delete all results
             * 5.If change is "Done" -> "Rejected" = Do nothing, just change the status
             * 6.If change is "To Do" <-> "Rejected" = Do nothing, just change the status
             * */
            repository.run { getTask(subOrderTask) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> {}
                        Status.SUCCESS -> {
                            resource.data!!.let {
                                if (it.statusId == InvStatuses.TO_DO.statusId || it.statusId == InvStatuses.REJECTED.statusId) {
                                    if (subOrderTask.statusId == InvStatuses.DONE.statusId)
                                    /**
                                     * Collect/Post new results and change status
                                     * */
                                    {
                                        runBlocking {
                                            /**
                                             * first find subOrder
                                             * */
                                            val subOrder = repository.subOrderById(subOrderTask.subOrderId)

                                            /**
                                             * second - extract list of metrixes to record
                                             * */
                                            val metrixesToRecord = productsRepository.metricsByPrefixVersionIdActualityCharId(
                                                subOrder.itemPreffix.substring(0, 1),
                                                subOrder.itemVersionId,
                                                true,
                                                subOrderTask.charId
                                            )
                                            /**
                                             * third - generate the final list of result to record
                                             * */
                                            repository.samplesBySubOrderId(subOrder.id).forEach { sdIt ->
                                                metrixesToRecord.forEach { mIt ->
                                                    listOfResults.add(
                                                        DomainResult(
                                                            sampleId = sdIt.id,
                                                            metrixId = mIt.id,
                                                            result = null,
                                                            isOk = true,
                                                            resultDecryptionId = 1,
                                                            taskId = subOrderTask.id
                                                        )
                                                    )
                                                }
                                            }

                                            repository.run { insertResults(listOfResults) }.consumeEach { event ->
                                                event.getContentIfNotHandled()?.let { resource ->
                                                    when (resource.status) {
                                                        Status.LOADING -> {}
                                                        Status.SUCCESS -> {}
                                                        Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else if (it.statusId == InvStatuses.DONE.statusId) {
                                    if (subOrderTask.statusId == InvStatuses.TO_DO.statusId) {
                                        /**
                                         * Delete all results and change status
                                         * */
                                        deleteResultsBasedOnTask(subOrderTask)
                                    }
                                }
                                repository.run { updateTask(subOrderTask) }.consumeEach { event ->
                                    event.getContentIfNotHandled()?.let { resource ->
                                        when (resource.status) {
                                            Status.LOADING -> {}
                                            Status.SUCCESS -> {}
                                            Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                                        }
                                    }
                                }
                            }
                        }

                        Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                    }
                }
            }
        }
    }

    fun editResult(result: DomainResult) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run { updateResult(result) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                        Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                        Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                    }
                }
            }
            withContext(Dispatchers.Main) { hideStatusUpdateDialog() }
        }
    }

    private fun uploadNewInvestigations() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run { getRemoteLatestOrderDate() }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                        Status.SUCCESS -> {
                            resource.data?.also {
                                repository.run { uploadNewInvestigations(it.toLong()) }.consumeEach { event ->
                                    event.getContentIfNotHandled()?.let { resource ->
                                        when (resource.status) {
                                            Status.LOADING -> {}
                                            Status.SUCCESS -> {
                                                resource.data?.let {
                                                    if (it.isNotEmpty()) setLastVisibleItemKey(repository.latestLocalOrderId())
                                                }
                                                mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                                            }

                                            Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                                        }
                                    }
                                }
                            } ?: mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                        }

                        Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                    }
                }
            }
        }
    }

    private fun uploadOlderInvestigations(earliestOrderDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run { uploadOldInvestigations(earliestOrderDate) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                        Status.SUCCESS -> {
                            resource.data?.let {
                                if (it.isNotEmpty()) setLastVisibleItemKey(_orders.value[_orders.value.lastIndex - 1].order.id)
                            }
                            mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                        }

                        Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                    }
                }
            }
        }
    }
}