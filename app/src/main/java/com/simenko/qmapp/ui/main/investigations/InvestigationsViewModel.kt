package com.simenko.qmapp.ui.main.investigations

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.common.DialogFor
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

    private val _showProcessControlOnly = MutableLiveData(true)

    val showProcessControlOnly: LiveData<Boolean>
        get() = _showProcessControlOnly

    fun setProcessControlOnly(value: Boolean) {
        _showProcessControlOnly.value = value
    }

    fun getProcessControlOnly(): Boolean = _showProcessControlOnly.value ?: false

    var isStatusDialogVisible = MutableLiveData(false)
    val dialogInput = MutableLiveData(DialogInput(0, DialogFor.ORDER, null))
    fun statusDialog(recordId: Int, dialogFor: DialogFor, performerId: Int?) {
        dialogInput.value = DialogInput(recordId, dialogFor, performerId)
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
            manufacturingRepository.teamComplete().collect() {
                team.apply {
                    clear()
                    addAll(it)
                }
            }
        }
    }

    val createdRecord = MutableLiveData(CreatedRecord())

    /**
     * Operations with orders ______________________________________________________________
     * */
    private val _ordersF = repository.completeOrders()
    val orders = _ordersF.asLiveData()

    /**
     * Visibility operations
     * */
    private val _currentOrderDetails = MutableStateFlow<SelectedNumber>(NoSelectedRecord)
    private val _currentOrderActions = MutableStateFlow<SelectedNumber>(NoSelectedRecord)
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
    private val _showWithStatus = MutableStateFlow<SelectedNumber>(NoSelectedRecord)
    private val _showOrderNumber = MutableStateFlow<SelectedString>(NoSelectedString)

    fun setOrderStatusToShow(status: String) {
        _showWithStatus.value = getStatus(status)
    }

    fun setOrderNumberToShow(orderNumber: String) {
        _showOrderNumber.value = SelectedString(orderNumber)
    }

    /**
     * The result flow
     * */
    val ordersSF: Flow<List<DomainOrderComplete>> =
        _currentOrderDetails.flatMapLatest { details ->
            _currentOrderActions.flatMapLatest { actions ->
                _showWithStatus.flatMapLatest { status ->
                    _showOrderNumber.flatMapLatest { number ->
                        setSubOrderDetailsVisibility(_currentSubOrderDetails.value.num)
                        setTaskDetailsVisibility(_currentTaskDetails.value.num)
                        setSampleDetailsVisibility(_currentSampleDetails.value.num)
                        setResultDetailsVisibility(_currentResultDetails.value.num)

                        _ordersF.map {
                            it.changeOrderVisibility(details.num, actions.num)
                                .filterByStatusAndNumber(status.num, number.str)
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO).conflate()

    /**
     * REST operations
     * */
    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.deleteOrder(orderId)
                syncOrders()

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
                    channel.consumeEach { }
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
    private val _subOrdersF = repository.completeSubOrders()
    val subOrders = _subOrdersF.asLiveData()

    /**
     * Visibility operations
     * */
    private val _currentSubOrderDetails = MutableStateFlow(NoSelectedRecord)
    private val _currentSubOrderActions = MutableStateFlow(NoSelectedRecord)
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
    private val _showSubOrderWithStatus = MutableStateFlow(NoSelectedRecord)
    private val _showSubOrderNumber = MutableStateFlow(NoSelectedString)
    fun setSubOrderStatusToShow(status: String) {
        _showSubOrderWithStatus.value = getStatus(status)
    }

    fun setSubOrderNumberToShow(orderNumber: String) {
        _showSubOrderNumber.value = SelectedString(orderNumber)
    }

    /**
     * The result flow
     * */
    val subOrdersSF: Flow<List<DomainSubOrderComplete>> =
        _currentSubOrderDetails.flatMapLatest { details ->
            _currentSubOrderActions.flatMapLatest { actions ->
                _showSubOrderWithStatus.flatMapLatest { status ->
                    _showSubOrderNumber.flatMapLatest { number ->
                        setTaskDetailsVisibility(_currentTaskDetails.value.num)
                        setSampleDetailsVisibility(_currentSampleDetails.value.num)
                        setResultDetailsVisibility(_currentResultDetails.value.num)

                        _subOrdersF.map {
                            it.changeSubOrderVisibility(details.num, actions.num)
                                .filterSubOrderByStatusAndNumber(status.num, number.str)
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO).conflate()

    /**
     * REST operations
     * */
    fun deleteSubOrder(subOrder: DomainSubOrderComplete) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.deleteSubOrder(subOrder.subOrder)
                syncSubOrders()

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
                    channel.consumeEach { }
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
    val tasks = _tasksF.asLiveData()

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
    fun deleteSubOrderTask(task: DomainSubOrderTaskComplete) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.deleteSubOrderTask(task.subOrderTask)
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
                isLoadingInProgress.value = false

            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }
}