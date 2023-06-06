package com.simenko.qmapp.ui.main.investigations

import android.util.Log
import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.common.DialogInput
import com.simenko.qmapp.ui.main.CreatedRecord
import com.simenko.qmapp.utils.InvestigationsUtils.filterByStatusAndNumber
import com.simenko.qmapp.utils.InvestigationsUtils.filterSubOrderByStatusAndNumber
import com.simenko.qmapp.utils.InvestigationsUtils.getDetailedOrdersRange
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import com.simenko.qmapp.utils.OrdersFilter
import com.simenko.qmapp.utils.SubOrdersFilter
import com.simenko.qmapp.works.SyncPeriods
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.time.Instant
import javax.inject.Inject

private const val TAG = "InvestigationsViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InvestigationsViewModel @Inject constructor(
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository
) : ViewModel() {

    companion object {
        fun getStatus(status: String): SelectedNumber {
            return when (status) {
                InvestigationsFragment.TargetInv.ALL.name -> NoRecord
                InvestigationsFragment.TargetInv.TO_DO.name -> SelectedNumber(1)
                InvestigationsFragment.TargetInv.IN_PROGRESS.name -> SelectedNumber(2)
                InvestigationsFragment.TargetInv.DONE.name -> SelectedNumber(3)
                else -> NoRecord
            }
        }
    }

    private val _isLoadingInProgress = MutableStateFlow(false)
    val isLoadingInProgress: LiveData<Boolean> = _isLoadingInProgress.asLiveData()
    private val _isNetworkError = MutableLiveData(false)
    val isNetworkError: LiveData<Boolean> = _isNetworkError

    fun onNetworkErrorShown() {
        _isLoadingInProgress.value = false
        _isNetworkError.value = false
    }

    private val _isReportDialogVisible = MutableLiveData(false)
    val isReportDialogVisible: LiveData<Boolean> = _isReportDialogVisible
    fun hideReportDialog() {
        _isReportDialogVisible.value = false
    }

    private val _dialogInput = MutableLiveData(DialogInput())
    val dialogInput: LiveData<DialogInput> = _dialogInput
    fun statusDialog(
        currentOrder: DomainOrderComplete? = null,
        currentSubOrder: DomainSubOrderComplete? = null,
        currentSubOrderTask: DomainSubOrderTaskComplete? = null,
        performerId: Int? = null
    ) {
        _dialogInput.value =
            DialogInput(currentOrder, currentSubOrder, currentSubOrderTask, performerId)
        _isReportDialogVisible.value = true
    }

    private val _invStatusListSF = repository.investigationStatuses()

    private val _selectedStatus = MutableStateFlow(NoRecord)

    fun selectStatus(statusId: SelectedNumber) {
        _selectedStatus.value = statusId
    }

    val invStatusListSF: StateFlow<List<DomainOrdersStatus>> =
        _invStatusListSF.flatMapLatest { statuses ->
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

    /**
     * Handling scrolling to just created record-------------------------------------------------
     * */
    private val _createdRecord = MutableStateFlow(CreatedRecord())
    val createdRecord: StateFlow<CreatedRecord> = _createdRecord.flatMapLatest { record ->
        _ordersSF.flatMapLatest { ordersList ->
            _subOrdersSF.flatMapLatest { subOrdersList ->

                val recordToEmit =
                    if (
                        (record.orderId != NoRecord.num) && (record.subOrderId != NoRecord.num) &&
                        (ordersList.find { it.order.id == record.orderId } != null) && (subOrdersList.find { it.subOrder.id == record.subOrderId } != null)
                    )
                        record
                    else if (
                        (record.orderId != NoRecord.num) && (record.subOrderId == NoRecord.num) &&
                        (ordersList.find { it.order.id == record.orderId } != null)
                    )
                        record
                    else if (
                        (record.orderId == NoRecord.num) && (record.subOrderId != NoRecord.num) &&
                        (subOrdersList.find { it.subOrder.id == record.subOrderId } != null)
                    )
                        record
                    else
                        CreatedRecord()

                flow { emit(recordToEmit) }
            }
        }
    }
        .flowOn(Dispatchers.Default)
        .conflate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CreatedRecord())

    fun setCreatedRecord(
        orderId: Int = NoRecord.num,
        subOrderId: Int = NoRecord.num
    ) {
        _createdRecord.value = CreatedRecord(
            if (orderId != NoRecord.num) orderId else _createdRecord.value.orderId,
            if (subOrderId != NoRecord.num) subOrderId else _createdRecord.value.subOrderId
        )
    }

    fun resetCreatedOrderId() {
        _createdRecord.value = CreatedRecord(NoRecord.num, _createdRecord.value.subOrderId)
    }

    fun resetCreatedSubOrderId() {
        _createdRecord.value = CreatedRecord(_createdRecord.value.orderId, NoRecord.num)
    }
    /**
     * Handling scrolling to just created record-------------------------------------------------
     * */

    /**
     * Operations with orders ______________________________________________________________
     * */

    private val _lastVisibleItemKey = MutableStateFlow<Any>(0)

    fun setLastVisibleItemKey(key: Any) {
        Log.d(TAG, "setLastVisibleItemKey: $key")
        _lastVisibleItemKey.value = key
    }

    private val _currentOrdersRange = MutableStateFlow(Pair(0L, 0L))

    private val _ordersSF: Flow<List<DomainOrderComplete>> =
        _lastVisibleItemKey.flatMapLatest { key ->
            repository.ordersListByLastVisibleId(key as Int)
        }

    /**
     * Visibility operations
     * */
    private val _currentOrderVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    fun setCurrentOrderVisibility(
        dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord
    ) {
        _currentOrderVisibility.value = _currentOrderVisibility.value.setVisibility(dId, aId)
    }

    /**
     * Filtering operations
     * */
    private val _currentOrdersFilter = MutableStateFlow(OrdersFilter())
    fun setCurrentOrdersFilter(
        type: SelectedNumber = NoRecord,
        status: SelectedNumber = NoRecord,
        number: SelectedString = NoSelectedString
    ) {
        _currentOrdersFilter.value = OrdersFilter(
            type.num,
            status.num,
            if (number != NoSelectedString) number.str else _currentOrdersFilter.value.orderNumber
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
                        Log.d(TAG, "_currentOrdersRange = ${_currentOrdersRange.value}")
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
            try {
                _isLoadingInProgress.value = true

                repository.deleteOrder(orderId)
                syncOrders()

                _isLoadingInProgress.value = false
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    fun syncOrders() {
        viewModelScope.launch {
            try {
                _isLoadingInProgress.value = true

                repository.syncOrders(_currentOrdersRange.value)
                repository.syncSubOrders(_currentOrdersRange.value)
                repository.syncSubOrderTasks(_currentOrdersRange.value)
                repository.syncSamples(_currentOrdersRange.value)
                repository.syncResults(_currentOrdersRange.value)

                _isLoadingInProgress.value = false
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    private fun syncOrder(order: DomainOrder) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val channel = repository.getRecord(
                        this,
                        order
                    )
                    channel.consumeEach {}
                }
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
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
    private val _currentSubOrderVisibility =
        MutableStateFlow(Pair(NoRecord, NoRecord))

    fun setCurrentSubOrderVisibility(
        dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord
    ) {
        _currentSubOrderVisibility.value = _currentSubOrderVisibility.value.setVisibility(dId, aId)
    }

    /**
     * Filtering operations
     * */
    private val _currentSubOrdersFilter = MutableStateFlow(SubOrdersFilter())
    fun setCurrentSubOrdersFilter(
        type: SelectedNumber = NoRecord,
        status: SelectedNumber = NoRecord,
        number: SelectedString = NoSelectedString
    ) {
        _currentSubOrdersFilter.value = SubOrdersFilter(
            type.num,
            status.num,
            if (number != NoSelectedString) number.str else _currentSubOrdersFilter.value.orderNumber
        )
    }

    val showSubOrderWithOrderType: LiveData<SelectedNumber> =
        _currentSubOrdersFilter.flatMapLatest {
            flow { emit(SelectedNumber(it.typeId)) }
        }.asLiveData()

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
            try {
                _isLoadingInProgress.value = true

                repository.deleteSubOrder(subOrderId)
                syncSubOrders()

                _isLoadingInProgress.value = false
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    private fun syncSubOrders() {
        viewModelScope.launch {
            try {
                _isLoadingInProgress.value = true

                repository.syncSubOrders(_currentOrdersRange.value)
                repository.syncSubOrderTasks(_currentOrdersRange.value)
                repository.syncSamples(_currentOrdersRange.value)
                repository.syncResults(_currentOrdersRange.value)

                _isLoadingInProgress.value = false
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    private fun syncSubOrder(subOrder: DomainSubOrder) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val channel = repository.getRecord(
                        this,
                        subOrder
                    )
                    channel.consumeEach {
                    }
                }
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
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
        _currentOrdersRange.flatMapLatest { ordersRange ->
            repository.tasksRangeList(ordersRange)
        }

    /**
     * Visibility operations
     * */
    private val _currentTaskVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    fun setCurrentTaskVisibility(
        dId: SelectedNumber = NoRecord,
        aId: SelectedNumber = NoRecord
    ) {
        _currentTaskVisibility.value = _currentTaskVisibility.value.setVisibility(dId, aId)
    }

    val currentTaskDetails: LiveData<SelectedNumber> = _currentTaskVisibility.flatMapLatest {
        flow { emit(it.first) }
    }.asLiveData()

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
            try {
                _isLoadingInProgress.value = true

                repository.deleteSubOrderTask(taskId)
                syncTasks()

                _isLoadingInProgress.value = false
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    fun syncTasks() {
        viewModelScope.launch {
            try {
                _isLoadingInProgress.value = true

                repository.syncSubOrderTasks(_currentOrdersRange.value)
                repository.syncResults(_currentOrdersRange.value)

                _isLoadingInProgress.value = false
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
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
        _currentSubOrderVisibility.flatMapLatest { subOrderId ->
            repository.resultsRangeList(subOrderId.first.num)
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

    fun deleteResultsBasedOnTask(task: DomainSubOrderTask) {
        viewModelScope.launch {
            try {
                _isLoadingInProgress.value = true

                repository.deleteResults(charId = task.id)
                syncResults()

                _isLoadingInProgress.value = false
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    fun syncResults() {
        viewModelScope.launch {
            try {
                _isLoadingInProgress.value = true

                repository.syncResults(_currentOrdersRange.value)

                _isLoadingInProgress.value = false
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    fun editSubOrder(subOrder: DomainSubOrder) {
        viewModelScope.launch {
            try {
                _isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    runBlocking {
                        repository.getTasksBySubOrderId(subOrder.id).forEach {
                            it.statusId = subOrder.statusId
                            it.completedById = subOrder.completedById
                            editTask(it, this)
                        }
                        syncSubOrder(subOrder)

                        val order = repository.getOrderById(subOrder.orderId)
                        order?.let { syncOrder(it) }
                    }
                }
                hideReportDialog()
                _isNetworkError.value = false
                _isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    fun editSubOrderTask(subOrderTask: DomainSubOrderTask) {
        viewModelScope.launch {
            try {
                _isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    runBlocking {

                        editTask(subOrderTask, this)

                        val subOrder = repository.getSubOrderById(subOrderTask.subOrderId)
                        syncSubOrder(subOrder)

                        val order = repository.getOrderById(subOrder.orderId)
                        order?.let { syncOrder(it) }

                    }
                }
                hideReportDialog()
                _isNetworkError.value = false
                _isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    private suspend fun editTask(subOrderTask: DomainSubOrderTask, coroutineScope: CoroutineScope) {
        val listOfResults: MutableList<DomainResult> = mutableListOf()

        /**
         * 1.Get latest status task
         * 2.Compare with new status
         * 3.If change is "To Do"/"Rejected" -> "Done" = Collect/Post new results and change status
         * 4.If change is "Done" -> "To Do" = Delete all results
         * 5.If change is "Done" -> "Rejected" = Do nothing, just change the status
         * 6.If change is "To Do" <-> "Rejected" = Do nothing, just change the status
         * */
        val channel1 = repository.getRecord(
            coroutineScope,
            subOrderTask
        )
        channel1.consumeEach {
            if (it.statusId == 1 || it.statusId == 4) {
                if (subOrderTask.statusId == 3)
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
                        val metrixesToRecord =
                            productsRepository.getMetricsByPrefixVersionIdActualityCharId(
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
                                        id = 0,
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

                        val channel3 = repository.getCreatedRecords(
                            coroutineScope,
                            listOfResults
                        )
                        channel3.consumeEach {
                        }
                    }
                }
            } else if (it.statusId == 3) {
                if (subOrderTask.statusId == 1) {
                    /**
                     * Delete all results and change status
                     * */
                    deleteResultsBasedOnTask(subOrderTask)
                }
            }
        }

        val channel2 = repository.updateRecord(
            coroutineScope,
            subOrderTask
        )

        channel2.consumeEach {
        }
    }

    fun editResult(result: DomainResult) {
        viewModelScope.launch {
            try {
                _isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    val channel = repository.updateRecord(
                        this,
                        result
                    )
                    channel.consumeEach {
                    }
                }
                hideReportDialog()
                _isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    fun uploadNewInvestigations() {
        viewModelScope.launch {
            if (!_isLoadingInProgress.value)
                try {
                    withContext(Dispatchers.IO) {
                        repository.uploadNewInvestigations().collect { event ->
                            event.getContentIfNotHandled()?.let { resource ->
                                when(resource.status) {
                                    Status.LOADING -> {
                                        withContext(Dispatchers.Main) {
                                            _isLoadingInProgress.value = true
                                        }
                                    }
                                    Status.SUCCESS -> {
                                        resource.data?.let {data ->
                                            if (data) {
                                                withContext(Dispatchers.Main) {
                                                    _isLoadingInProgress.value = false
                                                }
                                            }
                                        }
                                        this.cancel()
                                    }
                                    Status.ERROR -> {}
                                }
                            }
                        }
                        setLastVisibleItemKey(repository.latestLocalOrderId())
                    }

                } catch (networkError: IOException) {
                    delay(500)
                    _isNetworkError.value = true
                }
        }
    }

    private fun uploadOlderInvestigations(earliestOrderDate: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.uploadOldInvestigations(earliestOrderDate).collect { event->
                        event.getContentIfNotHandled()?.let {resource ->
                            when (resource.status) {
                                Status.LOADING -> {
                                    withContext(Dispatchers.Main) {
                                        _isLoadingInProgress.value = true
                                    }
                                }
                                Status.SUCCESS-> {
                                    resource.data?.let {data ->
                                        if (data) {
                                            setLastVisibleItemKey(ordersSF.value[ordersSF.value.lastIndex - 1].order.id)
                                            withContext(Dispatchers.Main) {
                                                _isLoadingInProgress.value = false
                                            }
                                        }
                                    }
                                    this.cancel()
                                }
                                Status.ERROR -> {}
                            }
                        }
                    }
                }
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    fun refreshMasterDataFromRepository() {
        viewModelScope.launch {
            try {
                _isLoadingInProgress.value = true

                manufacturingRepository.refreshPositionLevels()
                manufacturingRepository.refreshTeamMembers()
                manufacturingRepository.refreshCompanies()
                manufacturingRepository.refreshDepartments()
                manufacturingRepository.refreshSubDepartments()
                manufacturingRepository.refreshManufacturingChannels()
                manufacturingRepository.refreshManufacturingLines()
                manufacturingRepository.refreshManufacturingOperations()
                manufacturingRepository.refreshOperationsFlows()

                productsRepository.refreshElementIshModels()
                productsRepository.refreshIshSubCharacteristics()
                productsRepository.refreshManufacturingProjects()
                productsRepository.refreshCharacteristics()
                productsRepository.refreshMetrixes()
                productsRepository.refreshKeys()
                productsRepository.refreshProductBases()
                productsRepository.refreshProducts()
                productsRepository.refreshComponents()
                productsRepository.refreshComponentInStages()
                productsRepository.refreshVersionStatuses()
                productsRepository.refreshProductVersions()
                productsRepository.refreshComponentVersions()
                productsRepository.refreshComponentInStageVersions()
                productsRepository.refreshProductTolerances()
                productsRepository.refreshComponentTolerances()
                productsRepository.refreshComponentInStageTolerances()
                productsRepository.refreshProductsToLines()
                productsRepository.refreshComponentsToLines()
                productsRepository.refreshComponentInStagesToLines()

                repository.insertInputForOrder()
                repository.insertOrdersStatuses()
                repository.insertInvestigationReasons()
                repository.insertInvestigationTypes()
                repository.insertResultsDecryptions()

                _isLoadingInProgress.value = false

            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    fun syncUploadedInvestigations() {
        viewModelScope.launch {
            val thisMoment = Instant.now()

            val onDemandRange = Pair(
                thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli(),
                thisMoment.toEpochMilli()
            )

            try {
                _isLoadingInProgress.value = true

                repository.syncInvEntitiesByTimeRange(onDemandRange)

                _isLoadingInProgress.value = false
                _isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                _isNetworkError.value = true
            }
        }
    }

    init {
        viewModelScope.launch {
            setLastVisibleItemKey(repository.latestLocalOrderId())
        }
    }
}