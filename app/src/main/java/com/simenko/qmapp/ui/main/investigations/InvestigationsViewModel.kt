package com.simenko.qmapp.ui.main.investigations

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.common.DialogInput
import com.simenko.qmapp.ui.main.CreatedRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

private const val TAG = "InvestigationsViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InvestigationsViewModel @Inject constructor(
    private val manufacturingRepository: ManufacturingRepository,
    private val productsRepository: ProductsRepository,
    private val repository: InvestigationsRepository
) : ViewModel() {

    val isLoadingInProgress = MutableLiveData(false)
    val isNetworkError = MutableLiveData(false)
    fun onNetworkErrorShown() {
        isLoadingInProgress.value = false
        isNetworkError.value = false
    }

    var isStatusDialogVisible = MutableLiveData(false)
    val dialogInput = MutableLiveData(DialogInput())
    fun statusDialog(
        currentOrder: DomainOrderComplete? = null,
        currentSubOrder: DomainSubOrderComplete? = null,
        currentSubOrderTask: DomainSubOrderTaskComplete? = null,
        performerId: Int? = null
    ) {
        dialogInput.value =
            DialogInput(currentOrder, currentSubOrder, currentSubOrderTask, performerId)
        isStatusDialogVisible.value = true
    }

    private fun getStatus(status: String): SelectedNumber {
        return when (status) {
            InvestigationsFragment.TargetInv.ALL.name -> NoSelectedRecord
            InvestigationsFragment.TargetInv.TO_DO.name -> SelectedNumber(1)
            InvestigationsFragment.TargetInv.IN_PROGRESS.name -> SelectedNumber(2)
            InvestigationsFragment.TargetInv.DONE.name -> SelectedNumber(3)
            else -> NoSelectedRecord
        }
    }

    val investigationStatuses: SnapshotStateList<DomainOrdersStatus> = mutableStateListOf()
    fun addStatusesToSnapShot() {
        viewModelScope.launch {
            repository.investigationStatuses().collect() { it ->
                investigationStatuses.apply {
                    this.clear()
                    this.addAll(it)
                }
            }
        }
    }

    fun selectStatus(itemId: Int) {
        val iterator = investigationStatuses.listIterator()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (iterator.hasNext()) {
                    val current = iterator.next()
                    if (current.id == itemId) {
                        iterator.set(current.copy(isSelected = true))
                    } else {
                        if (current.isSelected)
                            iterator.set(current.copy(isSelected = false))
                    }
                }
            }
        }
    }

    val team: SnapshotStateList<DomainTeamMemberComplete> = mutableStateListOf()

    fun addTeamToSnapShot() {
        viewModelScope.launch {
            team.apply {
                clear()
                addAll(manufacturingRepository.teamCompleteList())
            }
        }
    }

    /**
     * Handling scrolling to just created record-------------------------------------------------
     * */
    private val _createdRecord = MutableStateFlow(CreatedRecord())
    val createdRecord: StateFlow<CreatedRecord> = _createdRecord.flatMapLatest { record ->
        _needToUpdateOrdersFromRoom.flatMapLatest { updateOrders ->
            if (updateOrders) {
                flow { emit(CreatedRecord()) }
            } else {
                flow { emit(record) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CreatedRecord())

    fun setCreatedRecordId(
        orderId: Int = NoSelectedRecord.num,
        subOrderId: Int = NoSelectedRecord.num
    ) {
        if (orderId != NoSelectedRecord.num && subOrderId != NoSelectedRecord.num) {
            _createdRecord.value = CreatedRecord(orderId, subOrderId)
            updateOrdersFromRoom()
            updateSubOrdersFromRoom()
        } else if (orderId != NoSelectedRecord.num) {
            _createdRecord.value = CreatedRecord(orderId, _createdRecord.value.subOrderId)
            updateOrdersFromRoom()
        } else if (subOrderId != NoSelectedRecord.num) {
            _createdRecord.value = CreatedRecord(_createdRecord.value.orderId, subOrderId)
            updateSubOrdersFromRoom()
        }
    }

    fun resetCreatedOrderId() {
        _createdRecord.value = CreatedRecord(NoSelectedRecord.num, _createdRecord.value.subOrderId)
    }

    fun resetCreatedSubOrderId() {
        _createdRecord.value = CreatedRecord(_createdRecord.value.orderId, NoSelectedRecord.num)
    }
    /**
     * Handling scrolling to just created record-------------------------------------------------
     * */

    /**
     * Operations with orders ______________________________________________________________
     * */
    private val _ordersSF = MutableStateFlow<List<DomainOrderComplete>>(listOf())

    /**
     * Visibility operations
     * */
    private val _needToUpdateOrdersFromRoom = MutableStateFlow(false)
    private val _currentOrderDetails = MutableStateFlow<SelectedNumber>(NoSelectedRecord)
    private val _currentOrderActions = MutableStateFlow<SelectedNumber>(NoSelectedRecord)

    private fun updateOrdersFromRoom() {
        _needToUpdateOrdersFromRoom.value = true
    }

    fun setOrderDetailsVisibility(id: Int) {
        if (_currentOrderDetails.value.num != id) {
            _currentOrderDetails.value = SelectedNumber(id)
            repository.setCurrentOrder(id)
        } else {
            _currentOrderDetails.value = NoSelectedRecord
            repository.setCurrentOrder(NoSelectedRecord.num)
        }
    }

    fun setOrderActionsVisibility(id: Int) {
        if (_currentOrderActions.value.num != id)
            _currentOrderActions.value = SelectedNumber(id)
        else
            _currentOrderActions.value = NoSelectedRecord
    }

    /**
     * Filtering operations
     * */
    private val _showWithType = MutableStateFlow<SelectedNumber>(NoSelectedRecord)
    private val _showWithStatus = MutableStateFlow<SelectedNumber>(NoSelectedRecord)
    private val _showOrderNumber = MutableStateFlow<SelectedString>(NoSelectedString)

    fun setOrderTypeToShow(type: SelectedNumber) {
        _showWithType.value = type
    }

    fun setOrderStatusToShow(status: String) {
        _showWithStatus.value = getStatus(status)
    }

    fun setOrderNumberToShow(orderNumber: String) {
        _showOrderNumber.value = SelectedString(orderNumber)
    }

    /**
     * The result flow
     * */
    val ordersSF: StateFlow<List<DomainOrderComplete>> =
        _ordersSF.flatMapLatest { orders ->
            _needToUpdateOrdersFromRoom.flatMapLatest { updateOrders ->
                _currentOrderDetails.flatMapLatest { details ->
                    _currentOrderActions.flatMapLatest { actions ->
                        _showWithType.flatMapLatest { type ->
                            _showWithStatus.flatMapLatest { status ->
                                _showOrderNumber.flatMapLatest { number ->
                                    if (updateOrders) {
                                        _ordersSF.value = repository.ordersCompleteList()
                                        _needToUpdateOrdersFromRoom.value = false
                                    }

                                    setSubOrderDetailsVisibility(_currentSubOrderDetails.value.num)
                                    setTaskDetailsVisibility(_currentTaskDetails.value.num)
                                    setSampleDetailsVisibility(_currentSampleDetails.value.num)
                                    setResultDetailsVisibility(_currentResultDetails.value.num)

                                    val cyp = mutableListOf<DomainOrderComplete>()
                                    orders.forEach {
                                        cyp.add(it.copy())
                                    }

                                    flow {
                                        emit(
                                            cyp.changeOrderVisibility(
                                                details.num, actions.num
                                            ).filterByStatusAndNumber(
                                                type.num, status.num, number.str
                                            )
                                        )
                                    }
                                }
                            }
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
                isLoadingInProgress.value = true

                repository.deleteOrder(orderId)
                syncOrders()

                updateOrdersFromRoom()
                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun syncOrders() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.refreshOrders()
                repository.refreshSubOrders()
                repository.refreshSubOrderTasks()
                repository.refreshSamples()
                repository.refreshResults()

                updateOrdersFromRoom()
                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
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
                    channel.consumeEach {
                        updateOrdersFromRoom()
                    }
                }
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    /**
     * End of operations with orders _______________________________________________________
     * */

    /**
     * Operations with sub orders __________________________________________________________
     * */
    private val _subOrdersSF = MutableStateFlow<List<DomainSubOrderComplete>>(listOf())

    /**
     * Visibility operations
     * */
    private val _needToUpdateSubOrdersFromRoom = MutableStateFlow(false)
    private val _currentSubOrderDetails = MutableStateFlow(NoSelectedRecord)
    private val _currentSubOrderActions = MutableStateFlow(NoSelectedRecord)
    private fun updateSubOrdersFromRoom() {
        _needToUpdateSubOrdersFromRoom.value = true
    }

    fun setSubOrderDetailsVisibility(id: Int) {
        if (_currentSubOrderDetails.value.num != id) {
            _currentSubOrderDetails.value = SelectedNumber(id)
            repository.setCurrentSubOrder(id)
        } else {
            _currentSubOrderDetails.value = NoSelectedRecord
            repository.setCurrentSubOrder(NoSelectedRecord.num)
        }
    }

    fun setSubOrderActionsVisibility(id: Int) {
        if (_currentSubOrderActions.value.num != id)
            _currentSubOrderActions.value = SelectedNumber(id)
        else
            _currentSubOrderActions.value = NoSelectedRecord
    }

    /**
     * Filtering operations
     * */
    private val _showSubOrderWithOrderType = MutableStateFlow(NoSelectedRecord)
    val showSubOrderWithOrderType: LiveData<SelectedNumber>
        get() = _showSubOrderWithOrderType.asLiveData()

    private val _showSubOrderWithStatus = MutableStateFlow(NoSelectedRecord)
    private val _showSubOrderWithOrderNumber = MutableStateFlow(NoSelectedString)

    fun setSubOrderWithOrderTypeToShow(type: SelectedNumber) {
        _showSubOrderWithOrderType.value = type
    }

    fun setSubOrderStatusToShow(status: String) {
        _showSubOrderWithStatus.value = getStatus(status)
    }

    fun setSubOrderNumberToShow(orderNumber: String) {
        _showSubOrderWithOrderNumber.value = SelectedString(orderNumber)
    }

    /**
     * The result flow
     * */
    val subOrdersSF: Flow<List<DomainSubOrderComplete>> =
        _subOrdersSF.flatMapLatest { subOrders ->
            _needToUpdateSubOrdersFromRoom.flatMapLatest { updateSubOrders ->
                _currentSubOrderDetails.flatMapLatest { details ->
                    _currentSubOrderActions.flatMapLatest { actions ->
                        _showSubOrderWithOrderType.flatMapLatest { type ->
                            _showSubOrderWithStatus.flatMapLatest { status ->
                                _showSubOrderWithOrderNumber.flatMapLatest { number ->
                                    if (updateSubOrders) {
                                        _subOrdersSF.value = repository.subOrdersCompleteList()
                                        _needToUpdateSubOrdersFromRoom.value = false
                                    }

                                    setTaskDetailsVisibility(_currentTaskDetails.value.num)
                                    setSampleDetailsVisibility(_currentSampleDetails.value.num)
                                    setResultDetailsVisibility(_currentResultDetails.value.num)

                                    val cyp = mutableListOf<DomainSubOrderComplete>()
                                    subOrders.forEach {
                                        cyp.add(it.copy())
                                    }

                                    flow {
                                        emit(
                                            cyp.changeSubOrderVisibility(
                                                details.num, actions.num
                                            ).filterSubOrderByStatusAndNumber(
                                                type.num, status.num, number.str
                                            )
                                        )
                                    }
                                }
                            }
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
    fun deleteSubOrder(subOrderId: Int) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.deleteSubOrder(subOrderId)
                syncSubOrders()

                updateSubOrdersFromRoom()
                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    private fun syncSubOrders() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.refreshSubOrders()
                repository.refreshSubOrderTasks()
                repository.refreshSamples()
                repository.refreshResults()

                updateSubOrdersFromRoom()
                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
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
                        updateSubOrdersFromRoom()
                    }
                }
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    /**
     * End of operations with orders _______________________________________________________
     * */

    /**
     * Operations with tasks __________________________________________________________
     * */
    private val _tasksF = repository.completeSubOrderTasks()

    /**
     * Visibility operations
     * */
    private val _currentTaskDetails = MutableStateFlow(NoSelectedRecord)
    val currentTaskDetails: LiveData<SelectedNumber>
        get() = _currentTaskDetails.asLiveData()

    private val _currentTaskActions = MutableStateFlow(NoSelectedRecord)
    fun setTaskDetailsVisibility(id: Int) {
        if (_currentTaskDetails.value.num != id) {
            _currentTaskDetails.value = SelectedNumber(id)
            repository.setCurrentTask(id)
        } else {
            _currentTaskDetails.value = NoSelectedRecord
            repository.setCurrentTask(NoSelectedRecord.num)
        }
    }

    fun setTaskActionsVisibility(id: Int) {
        if (_currentTaskActions.value.num != id)
            _currentTaskActions.value = SelectedNumber(id)
        else
            _currentTaskActions.value = NoSelectedRecord
    }

    /**
     * The result flow
     * */
    val tasksSF: Flow<List<DomainSubOrderTaskComplete>> =
        _currentTaskDetails.flatMapLatest { details ->
            _currentTaskActions.flatMapLatest { actions ->
                setSampleDetailsVisibility(_currentSampleDetails.value.num)
                setResultDetailsVisibility(_currentResultDetails.value.num)

                _tasksF.map {
                    it.changeTaskVisibility(details.num, actions.num)
                }
            }
        }.flowOn(Dispatchers.IO).conflate()

    /**
     * REST operations
     * */
    fun deleteSubOrderTask(taskId: Int) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.deleteSubOrderTask(taskId)
                syncTasks()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun syncTasks() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.refreshSubOrderTasks()
                repository.refreshResults()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    /**
     * End of operations with tasks _______________________________________________________
     * */

    /**
     * Operations with samples __________________________________________________________
     * */
    private val _samplesF = repository.completeSamples()

    /**
     * Visibility operations
     * */
    private val _currentSampleDetails = MutableStateFlow(NoSelectedRecord)
    val currentSampleDetails: LiveData<SelectedNumber>
        get() = _currentSampleDetails.asLiveData()

    fun setSampleDetailsVisibility(id: Int) {
        if (_currentSampleDetails.value.num != id) {
            _currentSampleDetails.value = SelectedNumber(id)
            repository.setCurrentSample(id)
        } else {
            _currentSampleDetails.value = NoSelectedRecord
            repository.setCurrentTask(NoSelectedRecord.num)
        }
    }

    /**
     * The result flow
     * */
    val samplesSF: Flow<List<DomainSampleComplete>> =
        _currentSampleDetails.flatMapLatest { details ->
            setResultDetailsVisibility(_currentResultDetails.value.num)

            _samplesF.map {
                it.changeSampleVisibility(details.num)
            }
        }.flowOn(Dispatchers.IO).conflate()

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
    private val _resultsF = repository.completeResults()

    /**
     * Visibility operations
     * */
    private val _currentResultDetails = MutableStateFlow(NoSelectedRecord)
    fun setResultDetailsVisibility(id: Int) {
        if (_currentResultDetails.value.num != id) {
            _currentResultDetails.value = SelectedNumber(id)
            repository.setCurrentResult(id)
        } else {
            _currentResultDetails.value = NoSelectedRecord
            repository.setCurrentResult(NoSelectedRecord.num)
        }
    }

    /**
     * The result flow
     * */
    val resultsSF: Flow<List<DomainResultComplete>> =
        _currentSampleDetails.flatMapLatest { details ->
            _resultsF.map {
                it.changeResultVisibility(details.num)
            }
        }.flowOn(Dispatchers.IO).conflate()

    /**
     * End of operations with results _______________________________________________________
     * */

    fun deleteResultsBasedOnTask(task: DomainSubOrderTask) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.deleteResults(charId = task.id)
                syncResults()

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

                repository.refreshResults()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun editSubOrder(subOrder: DomainSubOrder) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    runBlocking {
                        repository.getTasksBySubOrderId(subOrder.id).forEach {
                            it.statusId = subOrder.statusId
                            it.completedById = subOrder.completedById
                            editTask(it, this)
                        }
                        syncSubOrder(subOrder)

                        val order = repository.getOrderById(subOrder.orderId)
                        syncOrder(order)
                    }
                }
                isStatusDialogVisible.value = false
                isNetworkError.value = false
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun editSubOrderTask(subOrderTask: DomainSubOrderTask) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    runBlocking {

                        editTask(subOrderTask, this)

                        val subOrder = repository.getSubOrderById(subOrderTask.subOrderId)
                        syncSubOrder(subOrder)

                        val order = repository.getOrderById(subOrder.orderId)
                        syncOrder(order)

                    }
                }
                isStatusDialogVisible.value = false
                isNetworkError.value = false
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
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
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    val channel = repository.updateRecord(
                        this,
                        result
                    )
                    channel.consumeEach {
                    }
                }
                isStatusDialogVisible.value = false
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

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

                repository.refreshInputForOrder()
                repository.refreshOrdersStatuses()
                repository.refreshInvestigationReasons()
                repository.refreshInvestigationTypes()
                repository.refreshOrders()
                repository.refreshSubOrders()
                repository.refreshSubOrderTasks()
                repository.refreshSamples()
                repository.refreshResultsDecryptions()
                repository.refreshResults()

                updateOrdersFromRoom()
                isLoadingInProgress.value = false

            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
//            ToDo: this not works because init is called once the MainActivity started ...
                delay(200L)
                updateOrdersFromRoom()
                updateSubOrdersFromRoom()
            }
        }
    }
}