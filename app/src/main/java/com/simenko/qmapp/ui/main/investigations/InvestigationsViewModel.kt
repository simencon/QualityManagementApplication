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
import com.simenko.qmapp.ui.main.team.SelectedRecord
import com.simenko.qmapp.ui.main.team.NoSelectedRecord
import com.simenko.qmapp.ui.main.team.NoSelectedString
import com.simenko.qmapp.ui.main.team.SelectedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

private const val TAG = "InvestigationsViewModel"

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

    var showAllInvestigations = MutableLiveData(true)

    var isStatusDialogVisible = MutableLiveData(false)
    val dialogInput = MutableLiveData(DialogInput(0, DialogFor.ORDER, null))
    fun statusDialog(recordId: Int, dialogFor: DialogFor, performerId: Int?) {
        dialogInput.value = DialogInput(recordId, dialogFor, performerId)
        isStatusDialogVisible.value = true
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

    val metrixes: SnapshotStateList<DomainMetrix> = mutableStateListOf()
    fun addMetrixesToSnapShot() {
        viewModelScope.launch {
            productsRepository.metrixes().collect() { it ->
                metrixes.apply {
                    this.clear()
                    this.addAll(it)
                }
            }
        }
    }

    val productTolerances: SnapshotStateList<DomainProductTolerance> = mutableStateListOf()
    fun addProductTolerancesToSnapShot() {
        viewModelScope.launch {
            productsRepository.productTolerances().collect() { it ->
                productTolerances.apply {
                    this.clear()
                    this.addAll(it)
                }
            }
        }
    }

    val componentTolerances: SnapshotStateList<DomainComponentTolerance> = mutableStateListOf()
    fun addComponentTolerancesToSnapShot() {
        viewModelScope.launch {
            productsRepository.componentTolerances().collect() { it ->
                componentTolerances.apply {
                    this.clear()
                    this.addAll(it)
                }
            }
        }
    }

    val componentInStageTolerances: SnapshotStateList<DomainComponentInStageTolerance> =
        mutableStateListOf()

    fun addComponentInStageTolerancesToSnapShot() {
        viewModelScope.launch {
            productsRepository.componentInStageTolerances().collect() { it ->
                componentInStageTolerances.apply {
                    this.clear()
                    this.addAll(it)
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

    val currentOrder = MutableLiveData(0)
    val orders: SnapshotStateList<DomainOrderComplete> = mutableStateListOf()

    private val _orderF = repository.completeOrders()

    private val _currentOrderDetails = MutableStateFlow<SelectedRecord>(NoSelectedRecord)
    private val _currentOrderActions = MutableStateFlow<SelectedRecord>(NoSelectedRecord)

    fun setOrderDetailsVisibility(id: Int) {
        if (_currentOrderDetails.value.num != id) {
            _currentOrderDetails.value = SelectedRecord(id)
            repository.setCurrentOrder(id)
        } else {
            _currentOrderDetails.value = NoSelectedRecord
            repository.setCurrentOrder(-1)
        }
    }

    fun setOrderActionsVisibility(id: Int) {
        if (_currentOrderActions.value.num != id)
            _currentOrderActions.value = SelectedRecord(id)
        else
            _currentOrderActions.value = NoSelectedRecord
    }

    private val _showWithStatus = MutableStateFlow(NoSelectedRecord)
    private val _showOrderNumber = MutableStateFlow(NoSelectedString)

    fun setCurrentStatusToShow(status: String) {
        when (status) {
            InvestigationsFragment.TargetInv.ALL.name -> {
                _showWithStatus.value = NoSelectedRecord
            }
            InvestigationsFragment.TargetInv.TO_DO.name -> {
                _showWithStatus.value = SelectedRecord(1)
            }
            InvestigationsFragment.TargetInv.IN_PROGRESS.name -> {
                _showWithStatus.value = SelectedRecord(2)
            }
            InvestigationsFragment.TargetInv.DONE.name -> {
                _showWithStatus.value = SelectedRecord(3)
            }
            else -> {
                _showWithStatus.value = NoSelectedRecord
            }
        }
    }

    fun setCurrentOrderToShow(orderNumber: String) {
        _showOrderNumber.value = SelectedString(orderNumber)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val ordersSF: Flow<List<DomainOrderComplete>> =
        _currentOrderDetails.flatMapLatest { details ->
            _currentOrderActions.flatMapLatest { actions ->
                _showWithStatus.flatMapLatest { status ->
                    _showOrderNumber.flatMapLatest { number ->
                        changeSubOrderDetailsVisibility(currentSubOrder.value ?: 0)
                        changeTaskDetailsVisibility(currentSubOrderTask.value ?: 0)
                        changeSampleDetailsVisibility(currentSample.value ?: 0)
                        changeResultDetailsVisibility(currentResult.value ?: 0)

                        _orderF.map {
                            it.changeVisibility(details.num, actions.num)
                                .filterByStatusAndNumber(status.num, number.str)
                        }
                    }
                }


            }
        }.flowOn(Dispatchers.IO).conflate()

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

    val currentSubOrder = MutableLiveData(0)
    val subOrders: SnapshotStateList<DomainSubOrderComplete> = mutableStateListOf()

    fun addSubOrdersToSnapShot(
        currentStatus: Int = 0,
        lookUpNumber: String = ""
    ) {
        viewModelScope.launch {
            repository.completeSubOrders().collect() { it ->
                subOrders.apply {
                    this.clear()
                    it.forEach { itF ->
                        if (itF.subOrder.statusId == currentStatus || currentStatus == 0)
                            if (itF.orderShort.order.orderNumber.toString()
                                    .contains(lookUpNumber) || lookUpNumber == "0"
                            )
                                this.add(itF)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun changeSubOrderDetailsVisibility(itemId: Int) {
        val iterator = subOrders.listIterator()
        viewModelScope.launch {
            val channel = CoroutineScope(Dispatchers.IO).produce<Int> {
                var currentItem = 0
                while (iterator.hasNext()) {
                    val current = iterator.next()
                    if (current.subOrder.id == itemId) {
                        iterator.set(current.copy(detailsVisibility = !current.detailsVisibility))
                        if (!current.detailsVisibility) {
                            currentItem = itemId
                        }
                    } else {
                        if (current.detailsVisibility)
                            iterator.set(current.copy(detailsVisibility = false))
                    }
                }
                send(currentItem)
            }
            channel.consumeEach {
                repository.setCurrentSubOrder(it)
                currentSubOrder.value = it
            }
        }

        changeTaskDetailsVisibility(currentSubOrderTask.value ?: 0)
        changeSampleDetailsVisibility(currentSample.value ?: 0)
        changeResultDetailsVisibility(currentResult.value ?: 0)
    }

    fun changeCompleteSubOrdersExpandState(itemId: Int) {
        val iterator = subOrders.listIterator()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (iterator.hasNext()) {
                    val current = iterator.next()
                    if (current.subOrder.id == itemId) {
                        iterator.set(current.copy(isExpanded = !current.isExpanded))
                    } else {
                        if (current.isExpanded)
                            iterator.set(current.copy(isExpanded = false))
                    }
                }
            }
        }
    }

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

    val currentSubOrderTask = MutableLiveData(0)
    val tasks: SnapshotStateList<DomainSubOrderTaskComplete> = mutableStateListOf()

    fun addTasksToSnapShot() {
        viewModelScope.launch {
            repository.completeSubOrderTasks().collect() { it ->
                tasks.apply {
                    this.clear()
                    addAll(it)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun changeTaskDetailsVisibility(itemId: Int) {
        val iterator = tasks.listIterator()
        viewModelScope.launch {
            val channel = CoroutineScope(Dispatchers.IO).produce<Int> {
                var currentItem = 0
                while (iterator.hasNext()) {
                    val current = iterator.next()
                    if (current.subOrderTask.id == itemId) {
                        iterator.set(current.copy(detailsVisibility = !current.detailsVisibility))
                        if (!current.detailsVisibility) {
                            currentItem = itemId
                        }
                    } else {
                        if (current.detailsVisibility)
                            iterator.set(current.copy(detailsVisibility = false))
                    }
                }
                send(currentItem)
            }
            channel.consumeEach {
                repository.setCurrentTask(it)
                currentSubOrderTask.value = it
            }
        }

        changeSampleDetailsVisibility(currentSample.value ?: 0)
        changeResultDetailsVisibility(currentResult.value ?: 0)
    }

    fun changeCompleteSubOrderTasksExpandState(itemId: Int) {
        val iterator = tasks.listIterator()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (iterator.hasNext()) {
                    val current = iterator.next()
                    if (current.subOrderTask.id == itemId) {
                        iterator.set(current.copy(isExpanded = !current.isExpanded))
                    } else {
                        if (current.isExpanded)
                            iterator.set(current.copy(isExpanded = false))
                    }
                }
            }
        }
    }

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

    val currentSample = MutableLiveData(0)
    val samples: SnapshotStateList<DomainSampleComplete> = mutableStateListOf()

    fun addSamplesToSnapShot() {
        viewModelScope.launch {
            repository.completeSamples().collect() { it ->
                samples.apply {
                    this.clear()
                    addAll(it)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun changeSampleDetailsVisibility(itemId: Int) {
        val iterator = samples.listIterator()
        viewModelScope.launch {
            val channel = CoroutineScope(Dispatchers.IO).produce<Int> {
                var currentItem = 0
                while (iterator.hasNext()) {
                    val current = iterator.next()
                    if (current.sample.id == itemId) {
                        iterator.set(current.copy(detailsVisibility = !current.detailsVisibility))
                        if (!current.detailsVisibility) {
                            currentItem = itemId
                        }
                    } else {
                        if (current.detailsVisibility)
                            iterator.set(current.copy(detailsVisibility = false))
                    }
                }
                send(currentItem)
            }
            channel.consumeEach {
                repository.setCurrentSample(it)
                currentSample.value = it
            }
        }

        changeResultDetailsVisibility(currentResult.value ?: 0)
    }

    val currentResult = MutableLiveData(0)
    val results: SnapshotStateList<DomainResultComplete> = mutableStateListOf()

    fun addResultsToSnapShot() {
        viewModelScope.launch {
            repository.completeResults().collect() { it ->
                results.apply {
                    this.clear()
                    addAll(it)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun changeResultDetailsVisibility(itemId: Int) {
        val iterator = results.listIterator()
        viewModelScope.launch {
            val channel = CoroutineScope(Dispatchers.IO).produce<Int> {
                var currentItem = 0
                while (iterator.hasNext()) {
                    val current = iterator.next()
                    if (current.result.id == itemId) {
                        iterator.set(current.copy(detailsVisibility = !current.detailsVisibility))
                        if (!current.detailsVisibility) {
                            currentItem = itemId
                        }
                    } else {
                        if (current.detailsVisibility)
                            iterator.set(current.copy(detailsVisibility = false))
                    }
                }
                send(currentItem)
            }
            channel.consumeEach {
                repository.setCurrentResult(it)
                currentResult.value = it
            }
        }
    }

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

                    tasks
                        .filter { it.subOrderTask.subOrderId == subOrder.id }
                        .map { it.subOrderTask }
                        .forEach {
                            it.statusId = subOrder.statusId
                            it.completedById = subOrder.completedById
                            editTask(it, this)
                        }

                    syncSubOrder(subOrder)

                    val order = orders.find {
                        it.order.id == subOrder.orderId
                    }!!.order
                    syncOrder(order)
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

                    editTask(subOrderTask, this)

                    val subOrder = subOrders
                        .find { it.subOrder.id == subOrderTask.subOrderId }!!.subOrder
                    syncSubOrder(subOrder)

                    val order = orders
                        .find { it.order.id == subOrder.orderId }!!.order
                    syncOrder(order)
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
                    val subOrder =
                        subOrders.find { sIt -> sIt.subOrder.id == subOrderTask.subOrderId }?.subOrder!!
                    val metrixesToRecord: List<DomainMetrix?>? =
                        when (subOrder.itemPreffix.substring(0, 1)) {
                            "p" -> {
                                productTolerances.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    .map { pfIt -> metrixes.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                            "c" -> {
                                componentTolerances.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    .map { pfIt -> metrixes.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                            "s" -> {
                                componentInStageTolerances.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    .map { pfIt -> metrixes.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                            else -> {
                                componentTolerances.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    .map { pfIt -> metrixes.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                        }

                    samples.filter { sIt -> sIt.sample.subOrderId == subOrder.id }
                        .distinctBy { sfIt -> sfIt.sample.id }.forEach { sdIt ->
                            metrixesToRecord?.forEach { mIt ->
                                if (mIt != null) {
                                    listOfResults.add(
                                        DomainResult(
                                            id = 0,
                                            sampleId = sdIt.sample.id,
                                            metrixId = mIt.id,
                                            result = null,
                                            isOk = true,
                                            resultDecryptionId = 1,
                                            taskId = subOrderTask.id
                                        )
                                    )
                                }
                            }
                        }

                    listOfResults.forEach { dResult ->
                        val channel3 = repository.getCreatedRecord(
                            coroutineScope,
                            dResult
                        )
                        channel3.consumeEach { }
                    }

                    /*val channel3 =
                        investigationsRepository.getCreatedRecords(
                            coroutineScope,
                            listOfResults
                            )
                    channel3.consumeEach {
                        nResultsIt.forEach { }
                    }*/
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