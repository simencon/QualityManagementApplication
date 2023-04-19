package com.simenko.qmapp.ui.main.investigations

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.repository.QualityManagementProductsRepository
import com.simenko.qmapp.room.implementation.getDatabase
import com.simenko.qmapp.ui.common.DialogFor
import com.simenko.qmapp.ui.common.DialogInput
import com.simenko.qmapp.ui.main.CreatedRecord
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import java.io.IOException
import javax.inject.Inject

class InvestigationsViewModel @Inject constructor(
    context: Context
) : ViewModel() {
    private val roomDatabase = getDatabase(context)

    private val manufacturingRepository =
        QualityManagementManufacturingRepository(roomDatabase)
    private val productsRepository =
        QualityManagementProductsRepository(roomDatabase)
    private val investigationsRepository =
        QualityManagementInvestigationsRepository(roomDatabase)

    val isLoadingInProgress = MutableLiveData(false)
    val isNetworkError = MutableLiveData(false)

    var showAllInvestigations = MutableLiveData(true)

    var showWithStatus = MutableLiveData<Int>(0)
    var showOrderNumber = MutableLiveData<String>("0")

    fun onNetworkErrorShown() {
        isLoadingInProgress.value = false
        isNetworkError.value = false
    }

    fun setCurrentStatusToShow(status: String) {
        when (status) {
            InvestigationsFragment.TargetInv.ALL.name -> {
                showWithStatus.value = 0
            }
            InvestigationsFragment.TargetInv.TO_DO.name -> {
                showWithStatus.value = 1
            }
            InvestigationsFragment.TargetInv.IN_PROGRESS.name -> {
                showWithStatus.value = 2
            }
            InvestigationsFragment.TargetInv.DONE.name -> {
                showWithStatus.value = 3
            }
            else -> {
                showWithStatus.value = 0
            }
        }
    }

    var isStatusDialogVisible = MutableLiveData(false)
    val dialogInput = MutableLiveData(DialogInput(0, DialogFor.ORDER, null))
    fun statusDialog(recordId: Int, dialogFor: DialogFor, performerId: Int?) {
        dialogInput.value = DialogInput(recordId, dialogFor, performerId)
        isStatusDialogVisible.value = true
    }

    val investigationStatuses: SnapshotStateList<DomainOrdersStatus> = mutableStateListOf()
    fun addStatusesToSnapShot() {
        viewModelScope.launch {
            investigationsRepository.investigationStatuses().collect() { it ->
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

    val componentInStageTolerances: SnapshotStateList<DomainComponentInStageTolerance> = mutableStateListOf()
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

    fun addOrdersToSnapShot(
        currentStatus: Int = 0,
        lookUpNumber: String = ""
    ) {
        viewModelScope.launch {
            investigationsRepository.completeOrders().collect() { it ->
                orders.apply {
                    this.clear()
                    it.forEach { itF ->
                        if (itF.order.statusId == currentStatus || currentStatus == 0)
                            if (itF.order.orderNumber.toString()
                                    .contains(lookUpNumber) || lookUpNumber == "0"
                            )
                                this.add(itF)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun changeOrdersDetailsVisibility(itemId: Int) {
        val iterator = orders.listIterator()
        viewModelScope.launch {
            val channel  = CoroutineScope(Dispatchers.IO).produce<Int> {
                var currentItem = 0
                while (iterator.hasNext()) {
                    val current = iterator.next()
                    if (current.order.id == itemId) {
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
                investigationsRepository.setCurrentOrder(it)
                currentOrder.value = it
            }
        }

        changeSubOrderDetailsVisibility(currentSubOrder.value ?: 0)
        changeTaskDetailsVisibility(currentSubOrderTask.value ?: 0)
        changeSampleDetailsVisibility(currentSample.value ?: 0)
        changeResultDetailsVisibility(currentResult.value ?: 0)
    }

    fun changeCompleteOrdersExpandState(itemId: Int) {

        val iterator = orders.listIterator()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (iterator.hasNext()) {
                    val current = iterator.next()
                    if (current.order.id == itemId) {
                        iterator.set(current.copy(isExpanded = !current.isExpanded))
                    } else {
                        if (current.isExpanded)
                            iterator.set(current.copy(isExpanded = false))
                    }
                }
            }
        }
    }

    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                investigationsRepository.deleteOrder(orderId)
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

                investigationsRepository.refreshOrders()
                investigationsRepository.refreshSubOrders()
                investigationsRepository.refreshSubOrderTasks()
                investigationsRepository.refreshSamples()
                investigationsRepository.refreshResults()

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
                    val channel = investigationsRepository.getRecord(
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
            investigationsRepository.completeSubOrders().collect() { it ->
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
                investigationsRepository.setCurrentSubOrder(it)
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

                investigationsRepository.deleteSubOrder(subOrder.subOrder)
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

                investigationsRepository.refreshSubOrders()
                investigationsRepository.refreshSubOrderTasks()
                investigationsRepository.refreshSamples()
                investigationsRepository.refreshResults()

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
                    val channel = investigationsRepository.getRecord(
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
            investigationsRepository.completeSubOrderTasks().collect() { it ->
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
                investigationsRepository.setCurrentTask(it)
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

                investigationsRepository.deleteSubOrderTask(task.subOrderTask)
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

                investigationsRepository.refreshSubOrderTasks()
                investigationsRepository.refreshResults()

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
            investigationsRepository.completeSamples().collect() { it ->
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
                investigationsRepository.setCurrentSample(it)
                currentSample.value = it
            }
        }

        changeResultDetailsVisibility(currentResult.value ?: 0)
    }

    val currentResult = MutableLiveData(0)
    val results: SnapshotStateList<DomainResultComplete> = mutableStateListOf()

    fun addResultsToSnapShot() {
        viewModelScope.launch {
            investigationsRepository.completeResults().collect() { it ->
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
                investigationsRepository.setCurrentResult(it)
                currentResult.value = it
            }
        }
    }

    fun deleteResultsBasedOnTask(task: DomainSubOrderTask) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                investigationsRepository.deleteResults(charId = task.id)
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

                investigationsRepository.refreshResults()

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
        val channel1 = investigationsRepository.getRecord(
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
                    val subOrder = subOrders.find { sIt -> sIt.subOrder.id == subOrderTask.subOrderId }?.subOrder!!
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
                        val channel3 = investigationsRepository.getCreatedRecord(
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

        val channel2 = investigationsRepository.updateRecord(
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
                    val channel = investigationsRepository.updateRecord(
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

                investigationsRepository.refreshInputForOrder()
                investigationsRepository.refreshOrdersStatuses()
                investigationsRepository.refreshInvestigationReasons()
                investigationsRepository.refreshInvestigationTypes()
                investigationsRepository.refreshOrders()
                investigationsRepository.refreshSubOrders()
                investigationsRepository.refreshSubOrderTasks()
                investigationsRepository.refreshSamples()
                investigationsRepository.refreshResultsDecryptions()
                investigationsRepository.refreshResults()
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }
}