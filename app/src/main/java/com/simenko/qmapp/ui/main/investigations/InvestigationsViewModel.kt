package com.simenko.qmapp.ui.main.investigations

import androidx.lifecycle.*
import com.simenko.qmapp.di.OrderIdParameter
import com.simenko.qmapp.di.IsProcessControlOnlyParameter
import com.simenko.qmapp.di.SubOrderIdParameter
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
import com.simenko.qmapp.ui.main.main.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.BaseFilter
import com.simenko.qmapp.utils.InvStatuses
import com.simenko.qmapp.utils.InvestigationsUtils.filterByStatusAndNumber
import com.simenko.qmapp.utils.InvestigationsUtils.filterSubOrderByStatusAndNumber
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
    @IsProcessControlOnlyParameter val isPcOnly: Boolean?,
    @OrderIdParameter private val orderId: Int,
    @SubOrderIdParameter private val subOrderId: Int
) : ViewModel() {
    private val _isLoadingInProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _createdRecord: MutableStateFlow<Pair<Event<Int>, Event<Int>>> = MutableStateFlow(Pair(Event(NoRecord.num), Event(NoRecord.num)))
    private val _currentOrderVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _currentSubOrderVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(if (isPcOnly == true) Page.PROCESS_CONTROL else Page.INVESTIGATIONS, mainPageState)
            .setOnSearchClickAction { if (isPcOnly == true) setSubOrdersFilter(it) else setOrdersFilter(it) }
            .setOnTabSelectAction { if (isPcOnly == true) setSubOrdersFilter(BaseFilter(statusId = it.num)) else setOrdersFilter(BaseFilter(statusId = it.num)) }
            .setOnFabClickAction { if (isPcOnly == true) onAddProcessControlClick() else onAddInvClick() }
            .setOnPullRefreshAction { this.uploadNewInvestigations() }
            .setOnUpdateLoadingExtraAction { _isLoadingInProgress.value = it.first }
            .build()
        _createdRecord.value = Pair(Event(orderId), Event(subOrderId))
        setCurrentOrderVisibility(dId = SelectedNumber(orderId))
        setCurrentSubOrderVisibility(dId = SelectedNumber(subOrderId))
    }

    private val tabIndexesMap = mapOf(Pair(FirstTabId.num, 0), Pair(SecondTabId.num, 1), Pair(ThirdTabId.num, 2), Pair(FourthTabId.num, 3))
    val selectedTabIndex
        get() = if (isPcOnly == true) tabIndexesMap[_currentSubOrdersFilter.value.statusId] ?: NoRecord.num else tabIndexesMap[_currentOrdersFilter.value.statusId] ?: NoRecord.num

    /**
     * Navigation -------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddInvClick() {
        appNavigator.tryNavigateTo(route = Route.Main.OrderAddEdit.withArgs(NoRecordStr.str))
    }

    fun onEditInvClick(orderId: Int) {
        appNavigator.tryNavigateTo(route = Route.Main.OrderAddEdit.withArgs(orderId.toString()))
    }

    fun onAddSubOrderClick(orderId: Int) {
        appNavigator.tryNavigateTo(Route.Main.SubOrderAddEdit.withArgs(orderId.toString(), NoRecordStr.str, FalseStr.str))
    }

    fun onEditSubOrderClick(record: Pair<Int, Int>) {
        appNavigator.tryNavigateTo(Route.Main.SubOrderAddEdit.withArgs(record.first.toString(), record.second.toString(), FalseStr.str))
    }

    private fun onAddProcessControlClick() {
        appNavigator.tryNavigateTo(route = Route.Main.SubOrderAddEdit.withArgs(NoRecordStr.str, NoRecordStr.str, TrueStr.str))
    }

    fun onEditProcessControlClick(record: Pair<Int, Int>) {
        appNavigator.tryNavigateTo(Route.Main.SubOrderAddEdit.withArgs(record.first.toString(), record.second.toString(), TrueStr.str))
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
        performerId: Int? = null
    ) {
        _dialogInput.value = DialogInput(currentOrder, currentSubOrder, currentSubOrderTask, performerId)
        _isStatusUpdateDialogVisible.value = true
    }

    private val _invStatuses = repository.investigationStatuses()

    private val _selectedStatus = MutableStateFlow(NoRecord)

    fun selectStatus(statusId: SelectedNumber) {
        _selectedStatus.value = statusId
    }

    val invStatuses: StateFlow<List<DomainOrdersStatus>> =
        _invStatuses.flatMapLatest { statuses ->
            _selectedStatus.flatMapLatest { selectedStatus ->
                val cpy = mutableListOf<DomainOrdersStatus>()
                statuses.forEach {
                    cpy.add(it.copy(isSelected = it.id == selectedStatus.num))
                }
                flow { emit(cpy) }
            }
        }
            .flowOn(Dispatchers.IO)
            .conflate()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _employees: Flow<List<DomainEmployeeComplete>> = manufacturingRepository.employeesComplete
    val employees: StateFlow<List<DomainEmployeeComplete>> = _employees.flatMapLatest { team ->
        flow { emit(team) }
    }
        .flowOn(Dispatchers.IO)
        .conflate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    /**
     * Handling scrolling to just created record-------------------------------------------------
     * */
    private val _isScrollingEnabled = MutableStateFlow(false)
    val enableScrollToCreatedRecord: () -> Unit = { _isScrollingEnabled.value = true }

    val scrollToRecord: StateFlow<Pair<Event<Int>, Event<Int>>?> = _createdRecord.flatMapLatest { record ->
        _isScrollingEnabled.flatMapLatest { isScrollingEnabled ->
            if (isScrollingEnabled) flow { emit(record) } else flow { emit(null) }
        }
    }
        .flowOn(Dispatchers.Default)
        .conflate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val channel = Channel<Job>(capacity = Channel.UNLIMITED).apply { viewModelScope.launch { consumeEach { it.join() } } }

    /**
     * Operations with orders ______________________________________________________________
     * */

    private val _lastVisibleItemKey = MutableStateFlow<Any>(0)

    fun setLastVisibleItemKey(key: Any) {
        _lastVisibleItemKey.value = key
    }

    //    ToDo - change it to default when functionality done for ProcessControlOnly
    private val _currentOrdersRange =
//        MutableStateFlow(Pair(NoRecord.num.toLong(), NoRecord.num.toLong()))
        MutableStateFlow(Pair(1691991128021L, Instant.now().toEpochMilli()))

    private val _ordersSF: Flow<List<DomainOrderComplete>> =
        _lastVisibleItemKey.flatMapLatest { key ->
            repository.ordersListByLastVisibleId(key as Int)
        }

    /**
     * Visibility operations
     * */
    fun setCurrentOrderVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _currentOrderVisibility.value = _currentOrderVisibility.value.setVisibility(dId, aId)
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
    val ordersSF: StateFlow<List<DomainOrderComplete>> =
        _isLoadingInProgress.flatMapLatest { isLoading ->
            _ordersSF.flatMapLatest { orders ->
                _currentOrderVisibility.flatMapLatest { visibility ->
                    _currentOrdersFilter.flatMapLatest { filter ->
                        if (visibility.first == NoRecord) {
                            setCurrentSubOrderVisibility(dId = _currentSubOrderVisibility.value.first)
                            setCurrentTaskVisibility(dId = _currentTaskVisibility.value.first)
                            setCurrentSampleVisibility(dId = _currentSampleVisibility.value.first)
                        }

                        val cyp = mutableListOf<DomainOrderComplete>()
                        orders
                            .filterByStatusAndNumber(filter)
                            .forEach {
                                cyp.add(
                                    it.copy(
                                        detailsVisibility = it.order.id == visibility.first.num,
                                        isExpanded = it.order.id == visibility.second.num
                                    )
                                )
                            }
                        _currentOrdersRange.value = cyp.getDetailedOrdersRange()
                        if (!isLoading)
                            uploadOlderInvestigations(_currentOrdersRange.value.first)
                        flow {
                            emit(
                                cyp
                            )
                        }
                    }
                }
            }
        }
            .flowOn(Dispatchers.IO)
            .conflate()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    /**
     * REST operations
     * */
    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteOrder(orderId).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler.updateLoadingState(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler.updateLoadingState(Pair(false, null))
                                Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
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
    private val _subOrdersSF: Flow<List<DomainSubOrderComplete>> =
        _currentOrdersRange.flatMapLatest { ordersRange ->
            repository.subOrdersRangeList(ordersRange)
        }

    /**
     * Visibility operations
     * */
    fun setCurrentSubOrderVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _currentSubOrderVisibility.value = _currentSubOrderVisibility.value.setVisibility(dId, aId)
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
    val subOrdersSF: StateFlow<List<DomainSubOrderComplete>> =
        _subOrdersSF.flatMapLatest { subOrders ->
            _currentSubOrderVisibility.flatMapLatest { visibility ->
                _currentSubOrdersFilter.flatMapLatest { filter ->

                    if (visibility.first == NoRecord) {
                        setCurrentTaskVisibility(dId = _currentTaskVisibility.value.first)
                        setCurrentSampleVisibility(dId = _currentSampleVisibility.value.first)
                    }

                    val cyp = mutableListOf<DomainSubOrderComplete>()
                    subOrders
                        .filterSubOrderByStatusAndNumber(filter)
                        .forEach {
                            cyp.add(
                                it.copy(
                                    detailsVisibility = it.subOrder.id == visibility.first.num,
                                    isExpanded = it.subOrder.id == visibility.second.num
                                )
                            )
                        }

                    flow { emit(cyp) }
                }
            }
        }
            .flowOn(Dispatchers.IO)
            .conflate()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    /**
     * REST operations
     * */
    fun deleteSubOrder(subOrderId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteSubOrder(subOrderId).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler.updateLoadingState(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler.updateLoadingState(Pair(false, null))
                                Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
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
    private val _tasksSF: Flow<List<DomainSubOrderTaskComplete>> =
        _currentSubOrderVisibility.flatMapLatest { subOrdersIds ->
            repository.tasksRangeList(subOrdersIds.first.num)
        }

    /**
     * Visibility operations
     * */
    private val _currentTaskVisibility: MutableStateFlow<Pair<SelectedNumber, SelectedNumber>> = MutableStateFlow(Pair(NoRecord, NoRecord))
    fun setCurrentTaskVisibility(
        dId: SelectedNumber = NoRecord,
        aId: SelectedNumber = NoRecord
    ) {
        _currentTaskVisibility.value = _currentTaskVisibility.value.setVisibility(dId, aId)
    }

    val currentTaskDetails: StateFlow<SelectedNumber>
        get() = _currentTaskVisibility.flatMapLatest {
            flow { emit(it.first) }
        }
            .flowOn(Dispatchers.IO)
            .conflate()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _currentTaskVisibility.value.first)

    /**
     * The result flow
     * */
    val tasksSF: StateFlow<List<DomainSubOrderTaskComplete>> =
        _tasksSF.flatMapLatest { tasks ->
            _currentTaskVisibility.flatMapLatest { visibility ->
                val cyp = mutableListOf<DomainSubOrderTaskComplete>()
                tasks.forEach {
                    cyp.add(
                        it.copy(
                            detailsVisibility = it.subOrderTask.id == visibility.first.num,
                            isExpanded = it.subOrderTask.id == visibility.second.num
                        )
                    )
                }
                flow { emit(cyp) }
            }
        }
            .flowOn(Dispatchers.IO)
            .conflate()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    /**
     * REST operations
     * */
    fun deleteSubOrderTask(taskId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteSubOrderTask(taskId).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler.updateLoadingState(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler.updateLoadingState(Pair(false, null))
                                Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
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
                mainPageHandler.updateLoadingState(Pair(true, null))

                repository.syncSubOrderTasks(_currentOrdersRange.value)
                repository.syncResults(_currentOrdersRange.value)

                mainPageHandler.updateLoadingState(Pair(false, null))
            } catch (e: IOException) {
                mainPageHandler.updateLoadingState(Pair(false, e.message))
            }
        }
    }

    /**
     * End of operations with tasks _______________________________________________________
     * */

    /**
     * Operations with samples __________________________________________________________
     * */
    private val _samplesSF: Flow<List<DomainSampleComplete>> =
        _currentSubOrderVisibility.flatMapLatest { subOrderId ->
            repository.samplesRangeList(subOrderId.first.num)
        }

    /**
     * Visibility operations
     * */
    private val _currentSampleVisibility =
        MutableStateFlow(Pair(NoRecord, NoRecord))

    fun setCurrentSampleVisibility(
        dId: SelectedNumber = NoRecord,
        aId: SelectedNumber = NoRecord
    ) {
        _currentSampleVisibility.value = _currentSampleVisibility.value.setVisibility(dId, aId)
    }

    val currentSampleDetails: LiveData<SelectedNumber> = _currentSampleVisibility.flatMapLatest {
        flow { emit(it.first) }
    }.asLiveData()

    /**
     * The result flow
     * */
    val samplesSF: StateFlow<List<DomainSampleComplete>> =
        _samplesSF.flatMapLatest { samples ->
            _currentSampleVisibility.flatMapLatest { visibility ->
                val cpy = mutableListOf<DomainSampleComplete>()
                samples.forEach {
                    cpy.add(
                        it.copy(
                            detailsVisibility = it.sample.id == visibility.first.num
                        )
                    )
                }
                flow { emit(cpy) }
            }
        }
            .flowOn(Dispatchers.IO)
            .conflate()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

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
    private val _resultsSF: Flow<List<DomainResultComplete>> =
        _currentTaskVisibility.flatMapLatest { taskIds ->
            _currentSampleVisibility.flatMapLatest { sampleIds ->
                repository.resultsRangeList(taskIds.first.num, sampleIds.first.num)
            }
        }

    /**
     * Visibility operations
     * */
    private val _currentResultVisibility =
        MutableStateFlow(Pair(NoRecord, NoRecord))

    fun setCurrentResultVisibility(
        dId: SelectedNumber = NoRecord,
        aId: SelectedNumber = NoRecord
    ) {
        _currentResultVisibility.value = _currentResultVisibility.value.setVisibility(dId, aId)
    }

    /**
     * The result flow
     * */
    val resultsSF: StateFlow<List<DomainResultComplete>> =
        _resultsSF.flatMapLatest { results ->
            _currentResultVisibility.flatMapLatest { visibility ->
                val cpy = mutableListOf<DomainResultComplete>()

                results.forEach {
                    cpy.add(
                        it.copy(
                            detailsVisibility = it.result.id == visibility.first.num
                        )
                    )
                }

                flow { emit(cpy) }
            }
        }
            .flowOn(Dispatchers.IO)
            .conflate()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

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
                                Status.LOADING -> mainPageHandler.updateLoadingState(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler.updateLoadingState(Pair(false, null))
                                Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
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
                mainPageHandler.updateLoadingState(Pair(true, null))
                withContext(Dispatchers.IO) {
                    runBlocking {
                        repository.getTasksBySubOrderId(subOrder.id).forEach {
                            it.statusId = subOrder.statusId
                            it.completedById = subOrder.completedById
                            editTask(it)
                        }
                        repository.run { getSubOrder(subOrder) }.consumeEach { }

                        val order = repository.getOrderById(subOrder.orderId)
                        repository.run { getOrder(order) }.consumeEach {}
                    }
                }
                hideStatusUpdateDialog()
                mainPageHandler.updateLoadingState(Pair(false, null))
            } catch (e: IOException) {
                mainPageHandler.updateLoadingState(Pair(false, e.message))
            }
        }
    }

    fun editSubOrderTask(subOrderTask: DomainSubOrderTask) {
        viewModelScope.launch {
            try {
                mainPageHandler.updateLoadingState(Pair(true, null))
                withContext(Dispatchers.IO) {
                    runBlocking {

                        editTask(subOrderTask)

                        val subOrder = repository.getSubOrderById(subOrderTask.subOrderId)
                        repository.run { getSubOrder(subOrder) }.consumeEach { }

                        val order = repository.getOrderById(subOrder.orderId)
                        repository.run { getOrder(order) }.consumeEach { }

                    }
                }
                hideStatusUpdateDialog()
                mainPageHandler.updateLoadingState(Pair(false, null))
            } catch (e: IOException) {
                mainPageHandler.updateLoadingState(Pair(false, e.message))
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
                                            val subOrder = repository.getSubOrderById(subOrderTask.subOrderId)

                                            /**
                                             * second - extract list of metrixes to record
                                             * */
                                            val metrixesToRecord = productsRepository.getMetricsByPrefixVersionIdActualityCharId(
                                                prefix = subOrder.itemPreffix.substring(0, 1),
                                                versionId = subOrder.itemVersionId,
                                                actual = true,
                                                charId = subOrderTask.charId
                                            )
                                            /**
                                             * third - generate the final list of result to record
                                             * */
                                            repository.getSamplesBySubOrderId(subOrder.id).forEach { sdIt ->
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
                                                        Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
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
                                            Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
                                        }
                                    }
                                }
                            }
                        }

                        Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
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
                        Status.LOADING -> mainPageHandler.updateLoadingState(Pair(true, null))
                        Status.SUCCESS -> mainPageHandler.updateLoadingState(Pair(false, null))
                        Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
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
                        Status.LOADING -> mainPageHandler.updateLoadingState(Pair(true, null))
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
                                                mainPageHandler.updateLoadingState(Pair(false, null))
                                            }

                                            Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
                                        }
                                    }
                                }
                            } ?: mainPageHandler.updateLoadingState(Pair(false, null))
                        }

                        Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
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
                        Status.LOADING -> mainPageHandler.updateLoadingState(Pair(true, null))
                        Status.SUCCESS -> {
                            resource.data?.let {
                                if (it.isNotEmpty()) setLastVisibleItemKey(ordersSF.value[ordersSF.value.lastIndex - 1].order.id)
                            }
                            mainPageHandler.updateLoadingState(Pair(false, null))
                        }

                        Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
                    }
                }
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            setLastVisibleItemKey(repository.latestLocalOrderId())
        }
    }
}