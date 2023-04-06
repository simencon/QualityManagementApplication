package com.simenko.qmapp.ui.main

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import com.simenko.qmapp.di.main.MainScope
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.repository.QualityManagementProductsRepository
import com.simenko.qmapp.room.implementation.getDatabase
import com.simenko.qmapp.ui.common.DialogFor
import com.simenko.qmapp.ui.common.DialogInput
import com.simenko.qmapp.ui.main.investigations.InvestigationsFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import java.io.IOException
import javax.inject.Inject

private const val TAG = "QualityManagementViewMo"

@MainScope
class QualityManagementViewModel @Inject constructor(
    context: Context
) : ViewModel() {
    /**
     * Gets data from Repositories - which is live data with list
     */

    private val roomDatabase = getDatabase(context)

    private val manufacturingRepository =
        QualityManagementManufacturingRepository(roomDatabase)
    private val productsRepository =
        QualityManagementProductsRepository(roomDatabase)
    private val investigationsRepository =
        QualityManagementInvestigationsRepository(roomDatabase)
    val isLoadingInProgress = MutableLiveData(false)
    val isNetworkError = MutableLiveData(false)

    val pairedOrderTrigger: MutableLiveData<Boolean> = MutableLiveData(true)
    val pairedSubOrderTrigger: MutableLiveData<Boolean> = MutableLiveData(true)
    val pairedTaskTrigger: MutableLiveData<Boolean> = MutableLiveData(true)
    val pairedSampleTrigger: MutableLiveData<Boolean> = MutableLiveData(true)
    val pairedResultTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

    val team = manufacturingRepository.teamComplete
    val teamS: SnapshotStateList<DomainTeamMemberComplete> = mutableStateListOf()
    fun addTeamToSnapShot(list: List<DomainTeamMemberComplete>) {
        teamS.apply {
            clear()
            addAll(list)
        }
    }

    fun changeTeamMembersDetailsVisibility(itemId: Int) {
        val iterator = teamS.listIterator()

        while (iterator.hasNext()) {
            val current = iterator.next()
            if (current.teamMember.id == itemId) {
                iterator.set(current.copy(detailsVisibility = !current.detailsVisibility))
            } else {
                iterator.set(current.copy(detailsVisibility = false))
            }
        }
    }

    val departments = manufacturingRepository.departments
    val departmentsDetailed = manufacturingRepository.departmentsDetailed
    val subDepartments = manufacturingRepository.subDepartments
    val channels = manufacturingRepository.channels
    val lines = manufacturingRepository.lines
    val operations = manufacturingRepository.operations

    val inputForOrder = investigationsRepository.inputForOrder
    val investigationReasons = investigationsRepository.investigationReasons

    /**
     * Filters
     * */
    var currentTitle = MutableLiveData("")
    var showAllInvestigations = MutableLiveData(true)

    var showWithStatus = MutableLiveData<Int>(0)
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

    var showOrderNumber = MutableLiveData<String>("0")

    /**
     * Filters
     * */

    val createdRecord = MutableLiveData(CreatedRecord())

    val currentOrder = MutableLiveData(0)

    val orders = investigationsRepository.completeOrders
    val ordersS: SnapshotStateList<DomainOrderComplete> = mutableStateListOf()
    fun addOrdersToSnapShot(
        list: List<DomainOrderComplete>,
        currentStatus: Int = 0,
        lookUpNumber: String = ""
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                ordersS.apply {
                    this.clear()
                    list.forEach {
                        if (it.order.statusId == currentStatus || currentStatus == 0)
                            if (it.order.orderNumber.toString()
                                    .contains(lookUpNumber) || lookUpNumber == "0"
                            )
                                this.add(it)
                    }
                }
            }
        }
    }

    val currentSubOrder = MutableLiveData(0)

    val completeSubOrders = investigationsRepository.completeSubOrders

    val completeTasks = investigationsRepository.completeSubOrderTasks

    val currentSubOrderTask = MutableLiveData(0)

    val completeSamples = investigationsRepository.completeSamples

    val currentSample = MutableLiveData(0)

    val completeResults = investigationsRepository.completeResults

    val currentResult = MutableLiveData(0)

    fun changeOrdersDetailsVisibility(itemId: Int) {
        val iterator = ordersS.listIterator()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (iterator.hasNext()) {
                    val current = iterator.next()
                    if (current.order.id == itemId) {
                        iterator.set(current.copy(detailsVisibility = !current.detailsVisibility))
/*                        if (!current.detailsVisibility) {
                            currentOrder.value = itemId
                        } else {
                            currentOrder.value = 0
                        }*/
                    } else {
                        if (current.detailsVisibility)
                            iterator.set(current.copy(detailsVisibility = false))
                    }
                }
            }
        }
    }

    fun changeOrderDetailsVisibility(itemId: Int) {
        var select = false

        orders.value?.find { it.order.id == itemId }
            ?.let { it ->
                if (!it.detailsVisibility)
                    select = true
                else {
                    currentOrder.value = 0
                    investigationsRepository.setCurrentOrder(0)
                }
            }

        orders.value?.forEach { it.detailsVisibility = false }

        Log.d(
            TAG,
            "changeOrderDetailsVisibility: ${pairedOrderTrigger.value}, hasActiveObservers: ${pairedOrderTrigger.hasActiveObservers()}"
        )
        if (select)
            orders.value?.find { it.order.id == itemId }
                ?.let { order ->
                    currentOrder.value = itemId
                    investigationsRepository.setCurrentOrder(itemId)
                    order.detailsVisibility = !order.detailsVisibility
                    pairedOrderTrigger.value = !(pairedOrderTrigger.value as Boolean)
                }
        else
            pairedOrderTrigger.value = !(pairedOrderTrigger.value as Boolean)

        Log.d(
            TAG,
            "changeOrderDetailsVisibility: ${pairedOrderTrigger.value}, hasActiveObservers: ${pairedOrderTrigger.hasActiveObservers()}"
        )

        changeSubOrderDetailsVisibility(currentSubOrder.value ?: 0)
        changeTaskDetailsVisibility(currentSubOrderTask.value ?: 0)
        changeSampleDetailsVisibility(currentSample.value ?: 0)
        changeResultDetailsVisibility(currentResult.value ?: 0)
    }

    fun changeSubOrderDetailsVisibility(itemId: Int) {
        var select = false

        completeSubOrders.value?.find { it.subOrder.id == itemId }
            ?.let { it ->
                if (!it.detailsVisibility)
                    select = true
                else {
                    currentSubOrder.value = 0
                    investigationsRepository.setCurrentSubOrder(0)
                }
            }

        completeSubOrders.value?.forEach { it.detailsVisibility = false }

        if (select)
            completeSubOrders.value?.find { it.subOrder.id == itemId }
                ?.let { subOrder ->
                    currentSubOrder.value = itemId
                    investigationsRepository.setCurrentSubOrder(itemId)

                    subOrder.detailsVisibility = !subOrder.detailsVisibility
                    pairedSubOrderTrigger.value = !(pairedSubOrderTrigger.value as Boolean)
                }
        else
            pairedSubOrderTrigger.value = !(pairedSubOrderTrigger.value as Boolean)

        changeTaskDetailsVisibility(currentSubOrderTask.value ?: 0)
        changeSampleDetailsVisibility(currentSample.value ?: 0)
        changeResultDetailsVisibility(currentResult.value ?: 0)
    }

    fun changeTaskDetailsVisibility(itemId: Int) {
        var select = false

        completeTasks.value?.find { it.subOrderTask.id == itemId }
            ?.let { it ->
                if (!it.detailsVisibility)
                    select = true
                else {
                    currentSubOrderTask.value = 0
                    investigationsRepository.setCurrentTask(0)
                }
            }

        completeTasks.value?.forEach { it.detailsVisibility = false }

        if (select)
            completeTasks.value?.find { it.subOrderTask.id == itemId }
                ?.let { subOrderTask ->
                    currentSubOrderTask.value = itemId
                    investigationsRepository.setCurrentTask(itemId)

                    subOrderTask.detailsVisibility = !subOrderTask.detailsVisibility
                    pairedTaskTrigger.value = !(pairedTaskTrigger.value as Boolean)
                }
        else
            pairedTaskTrigger.value = !(pairedTaskTrigger.value as Boolean)

        changeSampleDetailsVisibility(currentSample.value ?: 0)
        changeResultDetailsVisibility(currentResult.value ?: 0)
    }

    fun changeSampleDetailsVisibility(itemId: Int) {
        var select = false

        completeSamples.value?.find { it.sample.id == itemId && it.sampleResult.taskId == currentSubOrderTask.value }
            ?.let { it ->
                if (!it.detailsVisibility)
                    select = true
                else {
                    currentSample.value = 0
                    investigationsRepository.setCurrentSample(0)
                }
            }

        completeSamples.value?.forEach { it.detailsVisibility = false }

        if (select)
            completeSamples.value?.find { it.sample.id == itemId && it.sampleResult.taskId == currentSubOrderTask.value }
                ?.let { sample ->
                    currentSample.value = itemId
                    investigationsRepository.setCurrentSample(itemId)

                    sample.detailsVisibility = !sample.detailsVisibility
                    pairedSampleTrigger.value = !(pairedSampleTrigger.value as Boolean)
                }
        else
            pairedSampleTrigger.value = !(pairedSampleTrigger.value as Boolean)

        changeResultDetailsVisibility(currentResult.value ?: 0)
    }

    fun changeResultDetailsVisibility(itemId: Int) {
        var select = false

        completeResults.value?.find { it.result.id == itemId }
            ?.let { it ->
                if (!it.detailsVisibility)
                    select = true
                else {
                    currentResult.value = 0
                    investigationsRepository.setCurrentResult(0)
                }
            }

        completeResults.value?.forEach { it.detailsVisibility = false }

        if (select)
            completeResults.value?.find { it.result.id == itemId }
                ?.let { result ->
                    currentResult.value = itemId
                    investigationsRepository.setCurrentResult(itemId)

                    result.detailsVisibility = !result.detailsVisibility
                    pairedResultTrigger.value = !(pairedResultTrigger.value as Boolean)
                }
        else
            pairedResultTrigger.value = !(pairedResultTrigger.value as Boolean)
    }

    fun changeCompleteOrdersExpandState(itemId: Int) {

        val iterator = ordersS.listIterator()
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

    fun changeCompleteSubOrdersExpandState(item: DomainSubOrderComplete) {
        completeSubOrders.value?.find { it.subOrder.id == item.subOrder.id }?.let { subOrder ->
            subOrder.isExpanded = !subOrder.isExpanded
            pairedSubOrderTrigger.value = !(pairedSubOrderTrigger.value as Boolean)
        }
    }

    fun changeCompleteSubOrderTasksExpandState(item: DomainSubOrderTaskComplete) {
        completeTasks.value?.find { it.subOrderTask.id == item.subOrderTask.id }
            ?.let { subOrderTask ->
                subOrderTask.isExpanded = !subOrderTask.isExpanded
                pairedTaskTrigger.value = !(pairedTaskTrigger.value as Boolean)
            }
    }

    /**
     *
     */
    var isStatusDialogVisible = MutableLiveData(false)

    val dialogInput = MutableLiveData(DialogInput(0, DialogFor.ORDER, null))

    val investigationStatuses = investigationsRepository.investigationStatuses
    val investigationStatusesMediator: MediatorLiveData<Pair<List<DomainOrdersStatus>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainOrdersStatus>?, Boolean?>>().apply {
            addSource(investigationStatuses) { value = Pair(it, pairedOrderTrigger.value) }
            addSource(pairedOrderTrigger) { value = Pair(investigationStatuses.value, it) }
        }

    val productTolerances = productsRepository.productTolerances
    val componentTolerances = productsRepository.componentTolerances
    val componentInStageTolerances = productsRepository.componentInStageTolerances

    val itemsTolerances = productsRepository.itemsTolerances

    val metrixes = productsRepository.metrixes

    fun editSubOrder(subOrder: DomainSubOrder) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {

                    completeTasks.value!!
                        .filter { it.subOrderTask.subOrderId == subOrder.id }
                        .map { it.subOrderTask }
                        .forEach {
                            it.statusId = subOrder.statusId
                            it.completedById = subOrder.completedById
                            editTask(it, this)
                        }

                    syncSubOrder(subOrder)

                    val order = orders.value!!.find {
                        it.order.id == subOrder.orderId
                    }!!.order
                    syncOrder(order)
                }
                pairedOrderTrigger.value = !pairedOrderTrigger.value!!
                pairedSubOrderTrigger.value = !pairedSubOrderTrigger.value!!
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

                    val subOrder = completeSubOrders.value!!
                        .find { it.subOrder.id == subOrderTask.subOrderId }!!.subOrder
                    syncSubOrder(subOrder)

                    val order = orders.value!!
                        .find { it.order.id == subOrder.orderId }!!.order
                    syncOrder(order)
                }
                pairedOrderTrigger.value = !pairedOrderTrigger.value!!
                pairedSubOrderTrigger.value = !pairedSubOrderTrigger.value!!
                pairedTaskTrigger.value = !pairedTaskTrigger.value!!
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
                    val subOrder =
                        completeSubOrders.value?.find { sIt -> sIt.subOrder.id == subOrderTask.subOrderId }?.subOrder!!
                    val metrixesToRecord: List<DomainMetrix?>? =
                        when (subOrder.itemPreffix.substring(0, 1)) {
                            "p" -> {
                                productTolerances.value?.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    ?.map { pfIt -> metrixes.value?.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                            "c" -> {
                                componentTolerances.value?.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    ?.map { pfIt -> metrixes.value?.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                            "s" -> {
                                componentInStageTolerances.value?.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    ?.map { pfIt -> metrixes.value?.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                            else -> {
                                componentTolerances.value?.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    ?.map { pfIt -> metrixes.value?.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                        }

                    completeSamples.value?.filter { sIt -> sIt.sample.subOrderId == subOrder.id }
                        ?.distinctBy { sfIt -> sfIt.sample.id }?.forEach { sdIt ->
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
                        val channel3 =
                            investigationsRepository.getCreatedRecord(
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

    /**
     *
     * */
    fun printCurrentValues() {
        Log.d(
            TAG, "printCurrentValues: \n" +
                    "currentOrderId = ${currentOrder.value}\n" +
                    "currentSubOrderId = ${currentSubOrder.value}\n" +
                    "currentTaskId = ${currentSubOrderTask.value}\n" +
                    "currentResultId = ${currentResult.value}\n"
        )
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

    fun syncSubOrders() {
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

    fun syncSamples() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

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

    fun deleteOrder(order: DomainOrderComplete) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                investigationsRepository.deleteOrder(order.order)
                syncOrders()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
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