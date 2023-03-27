package com.simenko.qmapp.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.simenko.qmapp.di.main.MainScope
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.repository.QualityManagementProductsRepository
import com.simenko.qmapp.room.implementation.getDatabase
import com.simenko.qmapp.ui.common.DialogFor
import com.simenko.qmapp.ui.common.DialogInput
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

    val pairedTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

    val teamMembers = manufacturingRepository.teamMembers
    val teamMembersMediator: MediatorLiveData<Pair<List<DomainTeamMember>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainTeamMember>?, Boolean?>>().apply {
            addSource(teamMembers) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(teamMembers.value, it) }
        }

    fun changeTeamMembersDetailsVisibility(item: DomainTeamMember) {
        teamMembers.value?.find { it.id == item.id }?.let { member ->
            member.detailsVisibility = !member.detailsVisibility
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
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

    val currentOrder = MutableLiveData(0)

    private val completeOrders = investigationsRepository.completeOrders
    val completeOrdersMediator: MediatorLiveData<Pair<List<DomainOrderComplete>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainOrderComplete>?, Boolean?>>().apply {
            addSource(completeOrders) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(completeOrders.value, it) }
        }

    val itemVersionsComplete = productsRepository.itemVersionsComplete

    val currentSubOrder = MutableLiveData(0)

    private val completeSubOrders = investigationsRepository.completeSubOrders
    val completeSubOrdersMediator: MediatorLiveData<Pair<List<DomainSubOrderComplete>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainSubOrderComplete>?, Boolean?>>().apply {
            addSource(completeSubOrders) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(completeSubOrders.value, it) }
        }

    val completeTasks =
        investigationsRepository.completeSubOrderTasks
    val completeSubOrderTasksMediator: MediatorLiveData<Pair<List<DomainSubOrderTaskComplete>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainSubOrderTaskComplete>?, Boolean?>>().apply {
            addSource(completeTasks) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(completeTasks.value, it) }
        }

    val currentSubOrderTask = MutableLiveData(0)

    private val completeSamples = investigationsRepository.completeSamples
    val samplesMediator: MediatorLiveData<Pair<List<DomainSampleComplete>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainSampleComplete>?, Boolean?>>().apply {
            addSource(completeSamples) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(completeSamples.value, it) }
        }

    val currentSample = MutableLiveData(0)

    private val completeResults = investigationsRepository.completeResults
    val completeResultsMediator: MediatorLiveData<Pair<List<DomainResultComplete>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainResultComplete>?, Boolean?>>().apply {
            addSource(completeResults) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(completeResults.value, it) }
        }

    val currentResult = MutableLiveData(0)

    fun changeOrderDetailsVisibility(itemId: Int) {
        var select = false

        completeOrders.value?.find { it.order.id == itemId }
            ?.let { it ->
                if (!it.detailsVisibility)
                    select = true
                else {
                    currentOrder.value = 0
                    investigationsRepository.setCurrentOrder(0)
                }
            }

        completeOrders.value?.forEach { it.detailsVisibility = false }

        if (select)
            completeOrders.value?.find { it.order.id == itemId }
                ?.let { order ->
                    currentOrder.value = itemId
                    investigationsRepository.setCurrentOrder(itemId)
                    order.detailsVisibility = !order.detailsVisibility
                    pairedTrigger.value = !(pairedTrigger.value as Boolean)
                }
        else
            pairedTrigger.value = !(pairedTrigger.value as Boolean)

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
                    pairedTrigger.value = !(pairedTrigger.value as Boolean)
                }
        else
            pairedTrigger.value = !(pairedTrigger.value as Boolean)

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
                    pairedTrigger.value = !(pairedTrigger.value as Boolean)
                }
        else
            pairedTrigger.value = !(pairedTrigger.value as Boolean)

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
                    pairedTrigger.value = !(pairedTrigger.value as Boolean)
                }
        else
            pairedTrigger.value = !(pairedTrigger.value as Boolean)

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
                    pairedTrigger.value = !(pairedTrigger.value as Boolean)
                }
        else
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
    }

    fun changeCompleteOrdersExpandState(item: DomainOrderComplete) {
        completeOrders.value?.find { it.order.id == item.order.id }?.let { order ->
            order.isExpanded = !order.isExpanded
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
        }
    }

    fun changeCompleteSubOrdersExpandState(item: DomainSubOrderComplete) {
        completeSubOrders.value?.find { it.subOrder.id == item.subOrder.id }?.let { subOrder ->
            subOrder.isExpanded = !subOrder.isExpanded
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
        }
    }

    fun changeCompleteSubOrderTasksExpandState(item: DomainSubOrderTaskComplete) {
        completeTasks.value?.find { it.subOrderTask.id == item.subOrderTask.id }
            ?.let { subOrderTask ->
                subOrderTask.isExpanded = !subOrderTask.isExpanded
                pairedTrigger.value = !(pairedTrigger.value as Boolean)
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
            addSource(investigationStatuses) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(investigationStatuses.value, it) }
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

                    val order = completeOrders.value!!
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

    fun editSubOrderTask(subOrderTask: DomainSubOrderTask) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {

                    editTask(subOrderTask, this)

                    val subOrder = completeSubOrders.value!!
                        .find { it.subOrder.id == subOrderTask.subOrderId }!!.subOrder
                    syncSubOrder(subOrder)

                    val order = completeOrders.value!!
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

                    val channel3 =
                        investigationsRepository.getCreatedRecords(
                            coroutineScope,
                            listOfResults
                            )
                    channel3.consumeEach { nResultsIt ->
                        nResultsIt.forEach { nResultIt ->
                            Log.d(TAG, "editSubOrderTask: $nResultIt")
                        }
                    }

                    Log.d(TAG, "editSubOrderTask: Collect/Post new results")
                }
            } else if (it.statusId == 3) {
                if (subOrderTask.statusId == 1) {
                    /**
                     * Delete all results and change status
                     * */
                    deleteResultsBasedOnTask(subOrderTask)
                    Log.d(TAG, "editSubOrderTask: Delete all results")
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